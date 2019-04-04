package net.minecraft.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiDirtMessageScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenLoading;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IGuiEventListenerDeferred;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

@OnlyIn(Dist.CLIENT)
public class Minecraft implements IThreadListener, ISnooperInfo, IGuiEventListenerDeferred {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.EnumOS.OSX;
   public static final ResourceLocation DEFAULT_FONT_RENDERER_NAME = new ResourceLocation("default");
   public static final ResourceLocation standardGalacticFontRenderer = new ResourceLocation("alt");
   public static byte[] memoryReserve = new byte[10485760];
   private static int cachedMaximumTextureSize = -1;
   private final File fileResourcepacks;
   private final PropertyMap profileProperties;
   private final GameConfiguration.DisplayInformation displayInfo;
   private ServerData currentServerData;
   public TextureManager textureManager;
   private static Minecraft instance;
   private final DataFixer dataFixer;
   public PlayerControllerMP playerController;
   private VirtualScreen virtualScreen;
   public MainWindow mainWindow;
   private boolean hasCrashed;
   private CrashReport crashReporter;
   private boolean connectedToRealms;
   private final Timer timer = new Timer(20.0F, 0L);
   private final Snooper snooper = new Snooper("client", this, Util.milliTime());
   public WorldClient world;
   public WorldRenderer renderGlobal;
   private RenderManager renderManager;
   private ItemRenderer itemRenderer;
   private FirstPersonRenderer firstPersonRenderer;
   public EntityPlayerSP player;
   @Nullable
   private Entity renderViewEntity;
   @Nullable
   public Entity pointedEntity;
   public ParticleManager effectRenderer;
   private final SearchTreeManager searchTreeManager = new SearchTreeManager();
   private final Session session;
   private boolean isGamePaused;
   private float renderPartialTicksPaused;
   public FontRenderer fontRenderer;
   @Nullable
   public GuiScreen currentScreen;
   public GameRenderer entityRenderer;
   public DebugRenderer debugRenderer;
   int leftClickCounter;
   @Nullable
   private IntegratedServer integratedServer;
   public GuiIngame ingameGUI;
   public boolean skipRenderWorld;
   public RayTraceResult objectMouseOver;
   public GameSettings gameSettings;
   private CreativeSettings creativeSettings;
   public MouseHelper mouseHelper;
   public KeyboardListener keyboardListener;
   public final File gameDir;
   private final File fileAssets;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private ISaveFormat saveLoader;
   private static int debugFPS;
   private int rightClickDelayTimer;
   private String serverName;
   private int serverPort;
   private int joinPlayerCounter;
   public final FrameTimer frameTimer = new FrameTimer();
   private long startNanoTime = Util.nanoTime();
   private final boolean jvm64bit;
   private final boolean isDemo;
   @Nullable
   private NetworkManager networkManager;
   private boolean integratedServerIsRunning;
   public final Profiler profiler = new Profiler();
   private IReloadableResourceManager resourceManager;
   private final DownloadingPackFinder packFinder;
   private final ResourcePackList<ResourcePackInfoClient> resourcePackRepository;
   private LanguageManager languageManager;
   private BlockColors blockColors;
   private ItemColors itemColors;
   private Framebuffer framebuffer;
   private TextureMap textureMap;
   private SoundHandler soundHandler;
   private MusicTicker musicTicker;
   private FontResourceManager fontResourceMananger;
   private final MinecraftSessionService sessionService;
   private SkinManager skinManager;
   private final Queue<FutureTask<?>> scheduledTasks = Queues.newConcurrentLinkedQueue();
   private final Thread thread = Thread.currentThread();
   private ModelManager modelManager;
   private BlockRendererDispatcher blockRenderDispatcher;
   private final GuiToast toastGui;
   private volatile boolean running = true;
   public String debug = "";
   public boolean renderChunksMany = true;
   private long debugUpdateTime;
   private int fpsCounter;
   private final Tutorial tutorial;
   boolean isWindowFocused;
   private String debugProfilerName = "root";

   public Minecraft(GameConfiguration p_i45547_1_) {
      this.displayInfo = p_i45547_1_.displayInfo;
      instance = this;
      this.gameDir = p_i45547_1_.folderInfo.gameDir;
      this.fileAssets = p_i45547_1_.folderInfo.assetsDir;
      this.fileResourcepacks = p_i45547_1_.folderInfo.resourcePacksDir;
      this.launchedVersion = p_i45547_1_.gameInfo.version;
      this.versionType = p_i45547_1_.gameInfo.versionType;
      this.profileProperties = p_i45547_1_.userInfo.profileProperties;
      this.packFinder = new DownloadingPackFinder(new File(this.gameDir, "server-resource-packs"), p_i45547_1_.folderInfo.getAssetsIndex());
      this.resourcePackRepository = new ResourcePackList<>((p_211818_0_, p_211818_1_, p_211818_2_, p_211818_3_, p_211818_4_, p_211818_5_) -> {
         Supplier<IResourcePack> supplier;
         if (p_211818_4_.getPackFormat() < 4) {
            supplier = () -> {
               return new LegacyResourcePackWrapper(p_211818_2_.get(), LegacyResourcePackWrapper.NEW_TO_LEGACY_MAP);
            };
         } else {
            supplier = p_211818_2_;
         }

         return new ResourcePackInfoClient(p_211818_0_, p_211818_1_, supplier, p_211818_3_, p_211818_4_, p_211818_5_);
      });
      this.resourcePackRepository.addPackFinder(this.packFinder);
      this.resourcePackRepository.addPackFinder(new FolderPackFinder(this.fileResourcepacks));
      this.proxy = p_i45547_1_.userInfo.proxy == null ? Proxy.NO_PROXY : p_i45547_1_.userInfo.proxy;
      this.sessionService = (new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.session = p_i45547_1_.userInfo.session;
      LOGGER.info("Setting user: {}", this.session.getUsername());
      LOGGER.debug("(Session ID is {})", this.session.getSessionID());
      this.isDemo = p_i45547_1_.gameInfo.isDemo;
      this.jvm64bit = isJvm64bit();
      this.integratedServer = null;
      if (p_i45547_1_.serverInfo.serverName != null) {
         this.serverName = p_i45547_1_.serverInfo.serverName;
         this.serverPort = p_i45547_1_.serverInfo.serverPort;
      }

      Bootstrap.register();
      TextComponentKeybind.displaySupplierFunction = KeyBinding::getDisplayString;
      this.dataFixer = DataFixesManager.getDataFixer();
      this.toastGui = new GuiToast(this);
      this.tutorial = new Tutorial(this);
   }

   public void run() {
      this.running = true;

      try {
         this.init();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
         crashreport.makeCategory("Initialization");
         this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
         return;
      }

      while(true) {
         try {
            while(this.running) {

            if (!this.hasCrashed || this.crashReporter == null) {
               try {
                  this.runGameLoop(true);
               } catch (OutOfMemoryError var9) {
                  this.freeMemory();
                  this.displayGuiScreen(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }
            else
            this.displayCrashReport(this.crashReporter);
            }

         } catch (ReportedException reportedexception) {
            this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
            this.freeMemory();
            LOGGER.fatal("Reported exception thrown!", reportedexception);
            this.displayCrashReport(reportedexception.getCrashReport());
            break;
         } catch (Throwable throwable1) {
            CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
            this.freeMemory();
            LOGGER.fatal("Unreported exception thrown!", throwable1);
            this.displayCrashReport(crashreport1);
            break;
         } finally {
            this.shutdownMinecraftApplet();
         }

         return;
      }

   }

   private void init() {
      this.gameSettings = new GameSettings(this, this.gameDir);
      this.creativeSettings = new CreativeSettings(this.gameDir, this.dataFixer);
      this.startTimerHackThread();
      LOGGER.info("LWJGL Version: {}", Version.getVersion());
      GameConfiguration.DisplayInformation gameconfiguration$displayinformation = this.displayInfo;
      if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
         gameconfiguration$displayinformation = new GameConfiguration.DisplayInformation(this.gameSettings.overrideWidth, this.gameSettings.overrideHeight, gameconfiguration$displayinformation.fullscreenWidth, gameconfiguration$displayinformation.fullscreenHeight, gameconfiguration$displayinformation.fullscreen);
      }

      this.checkForGLFWInitError();
      this.virtualScreen = new VirtualScreen(this);
      this.mainWindow = this.virtualScreen.createMainWindow(gameconfiguration$displayinformation, this.gameSettings.fullscreenResolution);
      OpenGlHelper.init();
      GlDebugTextUtils.setDebugVerbosity(this.gameSettings.glDebugVerbosity);
      this.framebuffer = new Framebuffer(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight(), true);
      this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.resourceManager = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES);
      this.languageManager = new LanguageManager(this.gameSettings.language);
      this.resourceManager.addReloadListener(this.languageManager);
      this.gameSettings.fillResourcePackList(this.resourcePackRepository);
      this.refreshResources();
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.addReloadListener(this.textureManager);
      this.mainWindow.updateSize();
      this.displayGuiScreen(new GuiScreenLoading());
      this.initMainWindow();
      this.skinManager = new SkinManager(this.textureManager, new File(this.fileAssets, "skins"), this.sessionService);
      this.saveLoader = new AnvilSaveConverter(this.gameDir.toPath().resolve("saves"), this.gameDir.toPath().resolve("backups"), this.dataFixer);
      this.soundHandler = new SoundHandler(this.resourceManager, this.gameSettings);
      this.resourceManager.addReloadListener(this.soundHandler);
      this.musicTicker = new MusicTicker(this);
      this.fontResourceMananger = new FontResourceManager(this.textureManager, this.getForceUnicodeFont());
      this.resourceManager.addReloadListener(this.fontResourceMananger);
      this.fontRenderer = this.fontResourceMananger.getFontRenderer(DEFAULT_FONT_RENDERER_NAME);
      if (this.gameSettings.language != null) {
         this.fontRenderer.setBidiFlag(this.languageManager.isCurrentLanguageBidirectional());
      }

      this.resourceManager.addReloadListener(new GrassColorReloadListener());
      this.resourceManager.addReloadListener(new FoliageColorReloadListener());
      this.mainWindow.setRenderPhase("Startup");
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0D);
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(GlStateManager.CullFace.BACK);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.mainWindow.setRenderPhase("Post startup");
      this.textureMap = new TextureMap("textures");
      this.textureMap.setMipmapLevels(this.gameSettings.mipmapLevels);
      this.textureManager.loadTickableTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, this.textureMap);
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureMap.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
      this.modelManager = new ModelManager(this.textureMap);
      this.resourceManager.addReloadListener(this.modelManager);
      this.blockColors = BlockColors.init();
      this.itemColors = ItemColors.init(this.blockColors);
      this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
      this.renderManager = new RenderManager(this.textureManager, this.itemRenderer);
      this.firstPersonRenderer = new FirstPersonRenderer(this);
      this.resourceManager.addReloadListener(this.itemRenderer);
      this.entityRenderer = new GameRenderer(this, this.resourceManager);
      this.resourceManager.addReloadListener(this.entityRenderer);
      this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.func_174954_c(), this.blockColors);
      this.resourceManager.addReloadListener(this.blockRenderDispatcher);
      this.renderGlobal = new WorldRenderer(this);
      this.resourceManager.addReloadListener(this.renderGlobal);
      this.populateSearchTreeManager();
      this.resourceManager.addReloadListener(this.searchTreeManager);
      GlStateManager.viewport(0, 0, this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
      this.effectRenderer = new ParticleManager(this.world, this.textureManager);
      this.ingameGUI = new GuiIngame(this);
      if (this.serverName != null) {
         this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
      } else {
         this.displayGuiScreen(new GuiMainMenu());
      }

      this.debugRenderer = new DebugRenderer(this);
      GLFW.glfwSetErrorCallback(this::disableVSyncAfterGlError).free();
      if (this.gameSettings.fullScreen && !this.mainWindow.isFullscreen()) {
         this.mainWindow.toggleFullscreen();
      }

      this.mainWindow.updateVsyncFromGameSettings();
      this.mainWindow.setLogOnGlError();
      this.renderGlobal.func_174966_b();
   }

   private void checkForGLFWInitError() {
      MainWindow.func_211162_a((p_211108_0_, p_211108_1_) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", p_211108_0_, p_211108_1_));
      });
      List<String> list = Lists.newArrayList();
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback((p_211100_1_, p_211100_2_) -> {
         list.add(String.format("GLFW error during init: [0x%X]%s", p_211100_1_, p_211100_2_));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(list));
      } else {
         Util.nanoTimeSupplier = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };

         for(String s : list) {
            LOGGER.error("GLFW error collected during initialization: {}", s);
         }

         GLFW.glfwSetErrorCallback(glfwerrorcallback).free();
      }
   }

   public void populateSearchTreeManager() {
      SearchTree<ItemStack> searchtree = new SearchTree<>((p_193988_0_) -> {
         return p_193988_0_.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).stream().map((p_211817_0_) -> {
            return TextFormatting.getTextWithoutFormattingCodes(p_211817_0_.getString()).trim();
         }).filter((p_200241_0_) -> {
            return !p_200241_0_.isEmpty();
         }).collect(Collectors.toList());
      }, (p_193985_0_) -> {
         return Collections.singleton(IRegistry.field_212630_s.func_177774_c(p_193985_0_.getItem()));
      });
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Item item : IRegistry.field_212630_s) {
         item.fillItemGroup(ItemGroup.SEARCH, nonnulllist);
      }

      nonnulllist.forEach(searchtree::add);
      SearchTree<RecipeList> searchtree1 = new SearchTree<>((p_193990_0_) -> {
         return p_193990_0_.getRecipes().stream().flatMap((p_200240_0_) -> {
            return p_200240_0_.getRecipeOutput().getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).stream();
         }).map((p_200235_0_) -> {
            return TextFormatting.getTextWithoutFormattingCodes(p_200235_0_.getString()).trim();
         }).filter((p_200234_0_) -> {
            return !p_200234_0_.isEmpty();
         }).collect(Collectors.toList());
      }, (p_193992_0_) -> {
         return p_193992_0_.getRecipes().stream().map((p_200237_0_) -> {
            return IRegistry.field_212630_s.func_177774_c(p_200237_0_.getRecipeOutput().getItem());
         }).collect(Collectors.toList());
      });
      this.searchTreeManager.register(SearchTreeManager.ITEMS, searchtree);
      this.searchTreeManager.register(SearchTreeManager.RECIPES, searchtree1);
   }

   private void disableVSyncAfterGlError(int p_195545_1_, long p_195545_2_) {
      this.gameSettings.enableVsync = false;
      this.gameSettings.saveOptions();
   }

   private static boolean isJvm64bit() {
      String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for(String s : astring) {
         String s1 = System.getProperty(s);
         if (s1 != null && s1.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public Framebuffer getFramebuffer() {
      return this.framebuffer;
   }

   public String getVersion() {
      return this.launchedVersion;
   }

   public String getVersionType() {
      return this.versionType;
   }

   private void startTimerHackThread() {
      Thread thread = new Thread("Timer hack thread") {
         public void run() {
            while(Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }

         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void crashed(CrashReport p_71404_1_) {
      this.hasCrashed = true;
      this.crashReporter = p_71404_1_;
   }

   public void displayCrashReport(CrashReport p_71377_1_) {
      File file1 = new File(getInstance().gameDir, "crash-reports");
      File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.printToSYSOUT(p_71377_1_.getCompleteReport());
      if (p_71377_1_.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + p_71377_1_.getFile());
         System.exit(-1);
      } else if (p_71377_1_.saveToFile(file2)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }

   }

   public boolean getForceUnicodeFont() {
      return this.gameSettings.forceUnicodeFont;
   }

   public void refreshResources() {
      this.resourcePackRepository.reloadPacksFromFinders();
      List<IResourcePack> list = this.resourcePackRepository.getPackInfos().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
      if (this.integratedServer != null) {
         this.integratedServer.reload();
      }

      try {
         this.resourceManager.reload(list);
      } catch (RuntimeException runtimeexception) {
         LOGGER.info("Caught error stitching, removing all assigned resourcepacks", runtimeexception);
         this.resourcePackRepository.func_198985_a(Collections.emptyList());
         List<IResourcePack> list1 = this.resourcePackRepository.getPackInfos().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
         this.resourceManager.reload(list1);
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.incompatibleResourcePacks.clear();
         this.gameSettings.saveOptions();
      }

      this.languageManager.parseLanguageMetadata(list);
      if (this.renderGlobal != null) {
         this.renderGlobal.func_72712_a();
      }

   }

   private void initMainWindow() {
      this.mainWindow.setupOverlayRendering();
      this.currentScreen.render(0, 0, 0.0F);
      this.mainWindow.update(false);
   }

   public void draw(int p_181536_1_, int p_181536_2_, int p_181536_3_, int p_181536_4_, int p_181536_5_, int p_181536_6_, int p_181536_7_, int p_181536_8_, int p_181536_9_, int p_181536_10_) {
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      bufferbuilder.pos((double)p_181536_1_, (double)(p_181536_2_ + p_181536_6_), 0.0D).tex((double)((float)p_181536_3_ * 0.00390625F), (double)((float)(p_181536_4_ + p_181536_6_) * 0.00390625F)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
      bufferbuilder.pos((double)(p_181536_1_ + p_181536_5_), (double)(p_181536_2_ + p_181536_6_), 0.0D).tex((double)((float)(p_181536_3_ + p_181536_5_) * 0.00390625F), (double)((float)(p_181536_4_ + p_181536_6_) * 0.00390625F)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
      bufferbuilder.pos((double)(p_181536_1_ + p_181536_5_), (double)p_181536_2_, 0.0D).tex((double)((float)(p_181536_3_ + p_181536_5_) * 0.00390625F), (double)((float)p_181536_4_ * 0.00390625F)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
      bufferbuilder.pos((double)p_181536_1_, (double)p_181536_2_, 0.0D).tex((double)((float)p_181536_3_ * 0.00390625F), (double)((float)p_181536_4_ * 0.00390625F)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
      Tessellator.getInstance().draw();
   }

   public ISaveFormat getSaveLoader() {
      return this.saveLoader;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.currentScreen;
   }

   public void displayGuiScreen(@Nullable GuiScreen p_147108_1_) {
      if (this.currentScreen != null) {
         this.currentScreen.onGuiClosed();
      }

      if (p_147108_1_ == null && this.world == null) {
         p_147108_1_ = new GuiMainMenu();
      } else if (p_147108_1_ == null && this.player.getHealth() <= 0.0F) {
         p_147108_1_ = new GuiGameOver(null);
      }

      if (p_147108_1_ instanceof GuiMainMenu || p_147108_1_ instanceof GuiMultiplayer) {
         this.gameSettings.showDebugInfo = false;
         this.ingameGUI.getChatGUI().clearChatMessages(true);
      }

      this.currentScreen = p_147108_1_;
      if (p_147108_1_ != null) {
         this.mouseHelper.ungrabMouse();
         KeyBinding.unPressAllKeys();
         p_147108_1_.setWorldAndResolution(this, this.mainWindow.getScaledWidth(), this.mainWindow.getScaledHeight());
         this.skipRenderWorld = false;
      } else {
         this.soundHandler.resume();
         this.mouseHelper.grabMouse();
      }

   }

   public void shutdownMinecraftApplet() {
      try {
         LOGGER.info("Stopping!");

         try {
            this.loadWorld(null);
         } catch (Throwable var5) {
         }

         if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
         }

         this.textureMap.clear();
         this.fontRenderer.close();
         this.entityRenderer.close();
         this.renderGlobal.close();
         this.soundHandler.unloadSounds();
      } finally {
         this.virtualScreen.close();
         this.mainWindow.close();
         if (!this.hasCrashed) {
            System.exit(0);
         }

      }

      System.gc();
   }

   private void runGameLoop(boolean p_195542_1_) {
      this.mainWindow.setRenderPhase("Pre render");
      long i = Util.nanoTime();
      this.profiler.startSection("root");
      if (GLFW.glfwWindowShouldClose(this.mainWindow.getHandle())) {
         this.shutdown();
      }

      if (p_195542_1_) {
         this.timer.updateTimer(Util.milliTime());
         this.profiler.startSection("scheduledExecutables");

         FutureTask<?> futuretask;
         while((futuretask = this.scheduledTasks.poll()) != null) {
            Util.runTask(futuretask, LOGGER);
         }

         this.profiler.endSection();
      }

      long l = Util.nanoTime();
      if (p_195542_1_) {
         this.profiler.startSection("tick");

         for(int j = 0; j < Math.min(10, this.timer.elapsedTicks); ++j) {
            this.runTick();
         }
      }

      this.mouseHelper.updatePlayerLook();
      this.mainWindow.setRenderPhase("Render");
      GLFW.glfwPollEvents();
      long i1 = Util.nanoTime() - l;
      this.profiler.endStartSection("sound");
      this.soundHandler.setListener(this.player, this.timer.renderPartialTicks);
      this.profiler.endSection();
      this.profiler.startSection("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640);
      this.framebuffer.bindFramebuffer(true);
      this.profiler.startSection("display");
      GlStateManager.enableTexture2D();
      this.profiler.endSection();
      if (!this.skipRenderWorld) {
         this.profiler.endStartSection("gameRenderer");
         this.entityRenderer.func_195458_a(this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks, i, p_195542_1_);
         this.profiler.endStartSection("toasts");
         this.toastGui.render();
         this.profiler.endSection();
      }

      this.profiler.endSection();
      if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
         this.profiler.startProfiling(this.timer.elapsedTicks);
         this.drawProfiler();
      } else {
         this.profiler.stopProfiling();
      }

      this.framebuffer.unbindFramebuffer();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.framebuffer.framebufferRender(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.entityRenderer.func_152430_c(this.timer.renderPartialTicks);
      GlStateManager.popMatrix();
      this.profiler.startSection("root");
      this.mainWindow.update(true);
      Thread.yield();
      this.mainWindow.setRenderPhase("Post render");
      ++this.fpsCounter;
      boolean flag = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.integratedServer.getPublic();
      if (this.isGamePaused != flag) {
         if (this.isGamePaused) {
            this.renderPartialTicksPaused = this.timer.renderPartialTicks;
         } else {
            this.timer.renderPartialTicks = this.renderPartialTicksPaused;
         }

         this.isGamePaused = flag;
      }

      long k = Util.nanoTime();
      this.frameTimer.addFrame(k - this.startNanoTime);
      this.startNanoTime = k;

      while(Util.milliTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", debugFPS, RenderChunk.renderChunksUpdated, RenderChunk.renderChunksUpdated == 1 ? "" : "s", (double)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate, this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : "");
         RenderChunk.renderChunksUpdated = 0;
         this.debugUpdateTime += 1000L;
         this.fpsCounter = 0;
         this.snooper.addMemoryStatsToSnooper();
         if (!this.snooper.isSnooperRunning()) {
            this.snooper.start();
         }
      }

      this.profiler.endSection();
   }

   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.renderGlobal.func_72728_f();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         this.loadWorld(null, new GuiDirtMessageScreen(I18n.format("menu.savingLevel")));
      } catch (Throwable var2) {
      }

      System.gc();
   }

   void updateDebugProfilerName(int p_71383_1_) {
      List<Profiler.Result> list = this.profiler.getProfilingData(this.debugProfilerName);
      if (!list.isEmpty()) {
         Profiler.Result profiler$result = list.remove(0);
         if (p_71383_1_ == 0) {
            if (!profiler$result.profilerName.isEmpty()) {
               int i = this.debugProfilerName.lastIndexOf(46);
               if (i >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, i);
               }
            }
         } else {
            --p_71383_1_;
            if (p_71383_1_ < list.size() && !"unspecified".equals((list.get(p_71383_1_)).profilerName)) {
               if (!this.debugProfilerName.isEmpty()) {
                  this.debugProfilerName = this.debugProfilerName + ".";
               }

               this.debugProfilerName = this.debugProfilerName + (list.get(p_71383_1_)).profilerName;
            }
         }

      }
   }

   private void drawProfiler() {
      if (this.profiler.isProfiling()) {
         List<Profiler.Result> list = this.profiler.getProfilingData(this.debugProfilerName);
         Profiler.Result profiler$result = list.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, (double)this.mainWindow.getFramebufferWidth(), (double)this.mainWindow.getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GlStateManager.lineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         int i = 160;
         int j = this.mainWindow.getFramebufferWidth() - 160 - 10;
         int k = this.mainWindow.getFramebufferHeight() - 320;
         GlStateManager.enableBlend();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         bufferbuilder.pos((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)((float)j - 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)((float)j + 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         bufferbuilder.pos((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         tessellator.draw();
         GlStateManager.disableBlend();
         double d0 = 0.0D;

         for(int l = 0; l < list.size(); ++l) {
            Profiler.Result profiler$result1 = list.get(l);
            int i1 = MathHelper.floor(profiler$result1.usePercentage / 4.0D) + 1;
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int j1 = profiler$result1.getColor();
            int k1 = j1 >> 16 & 255;
            int l1 = j1 >> 8 & 255;
            int i2 = j1 & 255;
            bufferbuilder.pos((double)j, (double)k, 0.0D).color(k1, l1, i2, 255).endVertex();

            for(int j2 = i1; j2 >= 0; --j2) {
               float f = (float)((d0 + profiler$result1.usePercentage * (double)j2 / (double)i1) * (double)((float)Math.PI * 2F) / 100.0D);
               float f1 = MathHelper.sin(f) * 160.0F;
               float f2 = MathHelper.cos(f) * 160.0F * 0.5F;
               bufferbuilder.pos((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
            }

            tessellator.draw();
            bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for(int i3 = i1; i3 >= 0; --i3) {
               float f3 = (float)((d0 + profiler$result1.usePercentage * (double)i3 / (double)i1) * (double)((float)Math.PI * 2F) / 100.0D);
               float f4 = MathHelper.sin(f3) * 160.0F;
               float f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
               bufferbuilder.pos((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
               bufferbuilder.pos((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
            }

            tessellator.draw();
            d0 += profiler$result1.usePercentage;
         }

         DecimalFormat decimalformat = new DecimalFormat("##0.00");
         decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         GlStateManager.enableTexture2D();
         String s = "";
         if (!"unspecified".equals(profiler$result.profilerName)) {
            s = s + "[0] ";
         }

         if (profiler$result.profilerName.isEmpty()) {
            s = s + "ROOT ";
         } else {
            s = s + profiler$result.profilerName + ' ';
         }

         int l2 = 16777215;
         this.fontRenderer.drawStringWithShadow(s, (float)(j - 160), (float)(k - 80 - 16), 16777215);
         s = decimalformat.format(profiler$result.totalUsePercentage) + "%";
         this.fontRenderer.drawStringWithShadow(s, (float)(j + 160 - this.fontRenderer.getStringWidth(s)), (float)(k - 80 - 16), 16777215);

         for(int k2 = 0; k2 < list.size(); ++k2) {
            Profiler.Result profiler$result2 = list.get(k2);
            StringBuilder stringbuilder = new StringBuilder();
            if ("unspecified".equals(profiler$result2.profilerName)) {
               stringbuilder.append("[?] ");
            } else {
               stringbuilder.append("[").append(k2 + 1).append("] ");
            }

            String s1 = stringbuilder.append(profiler$result2.profilerName).toString();
            this.fontRenderer.drawStringWithShadow(s1, (float)(j - 160), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
            s1 = decimalformat.format(profiler$result2.usePercentage) + "%";
            this.fontRenderer.drawStringWithShadow(s1, (float)(j + 160 - 50 - this.fontRenderer.getStringWidth(s1)), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
            s1 = decimalformat.format(profiler$result2.totalUsePercentage) + "%";
            this.fontRenderer.drawStringWithShadow(s1, (float)(j + 160 - this.fontRenderer.getStringWidth(s1)), (float)(k + 80 + k2 * 8 + 20), profiler$result2.getColor());
         }

      }
   }

   public void shutdown() {
      this.running = false;
   }

   public void displayInGameMenu() {
      if (this.currentScreen == null) {
         this.displayGuiScreen(new GuiIngameMenu());
         if (this.isSingleplayer() && !this.integratedServer.getPublic()) {
            this.soundHandler.pause();
         }

      }
   }

   private void sendClickBlockToController(boolean p_147115_1_) {
      if (!p_147115_1_) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.player.isHandActive()) {
         if (p_147115_1_ && this.objectMouseOver != null && this.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = this.objectMouseOver.getBlockPos();
            if (!this.world.getBlockState(blockpos).isAir() && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
               this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
               this.player.swingArm(EnumHand.MAIN_HAND);
            }

         } else {
            this.playerController.resetBlockRemoving();
         }
      }
   }

   private void clickMouse() {
      if (this.leftClickCounter <= 0) {
         if (this.objectMouseOver == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.isNotCreative()) {
               this.leftClickCounter = 10;
            }

         } else if (!this.player.isRowingBoat()) {
            switch(this.objectMouseOver.type) {
            case ENTITY:
               this.playerController.attackEntity(this.player, this.objectMouseOver.entity);
               break;
            case BLOCK:
               BlockPos blockpos = this.objectMouseOver.getBlockPos();
               if (!this.world.getBlockState(blockpos).isAir()) {
                  this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                  break;
               }
            case MISS:
               if (this.playerController.isNotCreative()) {
                  this.leftClickCounter = 10;
               }

               this.player.resetCooldown();
            }

            this.player.swingArm(EnumHand.MAIN_HAND);
         }
      }
   }

   private void rightClickMouse() {
      if (!this.playerController.getIsHittingBlock()) {
         this.rightClickDelayTimer = 4;
         if (!this.player.isRowingBoat()) {
            if (this.objectMouseOver == null) {
               LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            for(EnumHand enumhand : EnumHand.values()) {
               ItemStack itemstack = this.player.getHeldItem(enumhand);
               if (this.objectMouseOver != null) {
                  switch(this.objectMouseOver.type) {
                  case ENTITY:
                     if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entity, this.objectMouseOver, enumhand) == EnumActionResult.SUCCESS) {
                        return;
                     }

                     if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entity, enumhand) == EnumActionResult.SUCCESS) {
                        return;
                     }
                     break;
                  case BLOCK:
                     BlockPos blockpos = this.objectMouseOver.getBlockPos();
                     if (!this.world.getBlockState(blockpos).isAir()) {
                        int i = itemstack.getCount();
                        EnumActionResult enumactionresult = this.playerController.processRightClickBlock(this.player, this.world, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec, enumhand);
                        if (enumactionresult == EnumActionResult.SUCCESS) {
                           this.player.swingArm(enumhand);
                           if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.playerController.isInCreativeMode())) {
                              this.entityRenderer.field_78516_c.resetEquippedProgress(enumhand);
                           }

                           return;
                        }

                        if (enumactionresult == EnumActionResult.FAIL) {
                           return;
                        }
                     }
                  }
               }

               if (!itemstack.isEmpty() && this.playerController.processRightClick(this.player, this.world, enumhand) == EnumActionResult.SUCCESS) {
                  this.entityRenderer.field_78516_c.resetEquippedProgress(enumhand);
                  return;
               }
            }

         }
      }
   }

   public MusicTicker getMusicTicker() {
      return this.musicTicker;
   }

   public void runTick() {
      if (this.rightClickDelayTimer > 0) {
         --this.rightClickDelayTimer;
      }

      this.profiler.startSection("gui");
      if (!this.isGamePaused) {
         this.ingameGUI.tick();
      }

      this.profiler.endSection();
      this.entityRenderer.func_78473_a(1.0F);
      this.tutorial.onMouseHover(this.world, this.objectMouseOver);
      this.profiler.startSection("gameMode");
      if (!this.isGamePaused && this.world != null) {
         this.playerController.tick();
      }

      this.profiler.endStartSection("textures");
      if (this.world != null) {
         this.textureManager.tick();
      }

      if (this.currentScreen == null && this.player != null) {
         if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof GuiGameOver)) {
            this.displayGuiScreen(null);
         } else if (this.player.isPlayerSleeping() && this.world != null) {
            this.displayGuiScreen(new GuiSleepMP());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.player.isPlayerSleeping()) {
         this.displayGuiScreen(null);
      }

      if (this.currentScreen != null) {
         this.leftClickCounter = 10000;
      }

      if (this.currentScreen != null) {
         GuiScreen.runOrMakeCrashReport(() -> {
            this.currentScreen.tick();
         }, "Ticking screen", this.currentScreen.getClass().getCanonicalName());
      }

      if (this.currentScreen == null || this.currentScreen.allowUserInput) {
         this.profiler.endStartSection("GLFW events");
         GLFW.glfwPollEvents();
         this.processKeyBinds();
         if (this.leftClickCounter > 0) {
            --this.leftClickCounter;
         }
      }

      if (this.world != null) {
         if (this.player != null) {
            ++this.joinPlayerCounter;
            if (this.joinPlayerCounter == 30) {
               this.joinPlayerCounter = 0;
               this.world.joinEntityInSurroundings(this.player);
            }
         }

         this.profiler.endStartSection("gameRenderer");
         if (!this.isGamePaused) {
            this.entityRenderer.func_78464_a();
         }

         this.profiler.endStartSection("levelRenderer");
         if (!this.isGamePaused) {
            this.renderGlobal.func_72734_e();
         }

         this.profiler.endStartSection("level");
         if (!this.isGamePaused) {
            if (this.world.getLastLightningBolt() > 0) {
               this.world.setLastLightningBolt(this.world.getLastLightningBolt() - 1);
            }

            this.world.tickEntities();
         }
      } else if (this.entityRenderer.func_147702_a()) {
         this.entityRenderer.func_181022_b();
      }

      if (!this.isGamePaused) {
         this.musicTicker.tick();
         this.soundHandler.tick();
      }

      if (this.world != null) {
         if (!this.isGamePaused) {
            this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
            this.tutorial.tick();

            try {
               this.world.tick(() -> {
                  return true;
               });
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception in world tick");
               if (this.world == null) {
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected level");
                  crashreportcategory.addCrashSection("Problem", "Level is null!");
               } else {
                  this.world.addWorldInfoToCrashReport(crashreport);
               }

               throw new ReportedException(crashreport);
            }
         }

         this.profiler.endStartSection("animateTick");
         if (!this.isGamePaused && this.world != null) {
            this.world.animateTick(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY), MathHelper.floor(this.player.posZ));
         }

         this.profiler.endStartSection("particles");
         if (!this.isGamePaused) {
            this.effectRenderer.tick();
         }
      } else if (this.networkManager != null) {
         this.profiler.endStartSection("pendingConnection");
         this.networkManager.tick();
      }

      this.profiler.endStartSection("keyboard");
      this.keyboardListener.tick();
      this.profiler.endSection();
   }

   private void processKeyBinds() {
      for(; this.gameSettings.keyBindTogglePerspective.isPressed(); this.renderGlobal.func_174979_m()) {
         ++this.gameSettings.thirdPersonView;
         if (this.gameSettings.thirdPersonView > 2) {
            this.gameSettings.thirdPersonView = 0;
         }

         if (this.gameSettings.thirdPersonView == 0) {
            this.entityRenderer.func_175066_a(this.getRenderViewEntity());
         } else if (this.gameSettings.thirdPersonView == 1) {
            this.entityRenderer.func_175066_a(null);
         }
      }

      while(this.gameSettings.keyBindSmoothCamera.isPressed()) {
         this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
      }

      for(int i = 0; i < 9; ++i) {
         boolean flag = this.gameSettings.keyBindSaveToolbar.isKeyDown();
         boolean flag1 = this.gameSettings.keyBindLoadToolbar.isKeyDown();
         if (this.gameSettings.keyBindsHotbar[i].isPressed()) {
            if (this.player.isSpectator()) {
               this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
            } else if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag) {
               this.player.inventory.currentItem = i;
            } else {
               GuiContainerCreative.handleHotbarSnapshots(this, i, flag1, flag);
            }
         }
      }

      while(this.gameSettings.keyBindInventory.isPressed()) {
         if (this.playerController.isRidingHorse()) {
            this.player.sendHorseInventory();
         } else {
            this.tutorial.openInventory();
            this.displayGuiScreen(new GuiInventory(this.player));
         }
      }

      while(this.gameSettings.keyBindAdvancements.isPressed()) {
         this.displayGuiScreen(new GuiScreenAdvancements(this.player.connection.getAdvancementManager()));
      }

      while(this.gameSettings.keyBindSwapHands.isPressed()) {
         if (!this.player.isSpectator()) {
            this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
         }
      }

      while(this.gameSettings.keyBindDrop.isPressed()) {
         if (!this.player.isSpectator()) {
            this.player.dropItem(GuiScreen.isCtrlKeyDown());
         }
      }

      boolean flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
      if (flag2) {
         while(this.gameSettings.keyBindChat.isPressed()) {
            this.displayGuiScreen(new GuiChat());
         }

         if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed()) {
            this.displayGuiScreen(new GuiChat("/"));
         }
      }

      if (this.player.isHandActive()) {
         if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
            this.playerController.onStoppedUsingItem(this.player);
         }

         while(this.gameSettings.keyBindAttack.isPressed()) {
         }

         while(this.gameSettings.keyBindUseItem.isPressed()) {
         }

         while(this.gameSettings.keyBindPickBlock.isPressed()) {
         }
      } else {
         while(this.gameSettings.keyBindAttack.isPressed()) {
            this.clickMouse();
         }

         while(this.gameSettings.keyBindUseItem.isPressed()) {
            this.rightClickMouse();
         }

         while(this.gameSettings.keyBindPickBlock.isPressed()) {
            this.middleClickMouse();
         }
      }

      if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive()) {
         this.rightClickMouse();
      }

      this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.mouseHelper.isMouseGrabbed());
   }

   public void launchIntegratedServer(String p_71371_1_, String p_71371_2_, @Nullable WorldSettings p_71371_3_) {
      this.loadWorld(null);
      System.gc();
      ISaveHandler isavehandler = this.saveLoader.getSaveLoader(p_71371_1_, null);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      if (worldinfo == null && p_71371_3_ != null) {
         worldinfo = new WorldInfo(p_71371_3_, p_71371_1_);
         isavehandler.saveWorldInfo(worldinfo);
      }

      if (p_71371_3_ == null) {
         p_71371_3_ = new WorldSettings(worldinfo);
      }

      try {
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
         TileEntitySkull.setProfileCache(playerprofilecache);
         TileEntitySkull.setSessionService(minecraftsessionservice);
         PlayerProfileCache.setOnlineMode(false);
         this.integratedServer = new IntegratedServer(this, p_71371_1_, p_71371_2_, p_71371_3_, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
         this.integratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
         crashreportcategory.addCrashSection("Level ID", p_71371_1_);
         crashreportcategory.addCrashSection("Level Name", p_71371_2_);
         throw new ReportedException(crashreport);
      }

      GuiScreenWorking guiscreenworking = new GuiScreenWorking();
      this.displayGuiScreen(guiscreenworking);
      guiscreenworking.func_200210_a(new TextComponentTranslation("menu.loadingLevel"));

      while(!this.integratedServer.serverIsInRunLoop()) {
         ITextComponent itextcomponent = this.integratedServer.getUserMessage();
         if (itextcomponent != null) {
            ITextComponent itextcomponent1 = this.integratedServer.getCurrentTask();
            if (itextcomponent1 != null) {
               guiscreenworking.func_200209_c(itextcomponent1);
               guiscreenworking.setLoadingProgress(this.integratedServer.getPercentDone());
            } else {
               guiscreenworking.func_200209_c(itextcomponent);
            }
         } else {
            guiscreenworking.func_200209_c(new TextComponentString(""));
         }

         this.runGameLoop(false);

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var10) {
         }

         if (this.hasCrashed && this.crashReporter != null) {
            this.displayCrashReport(this.crashReporter);
            return;
         }
      }

      SocketAddress socketaddress = this.integratedServer.getNetworkSystem().addLocalEndpoint();
      NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
      networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, null, (p_209507_0_) -> {
      }));
      networkmanager.sendPacket(new CPacketHandshake(socketaddress.toString(), 0, EnumConnectionState.LOGIN));
      networkmanager.sendPacket(new CPacketLoginStart(this.getSession().getProfile()));
      this.networkManager = networkmanager;
   }

   public void loadWorld(@Nullable WorldClient p_71403_1_) {
      GuiScreenWorking guiscreenworking = new GuiScreenWorking();
      if (p_71403_1_ != null) {
         guiscreenworking.func_200210_a(new TextComponentTranslation("connect.joining"));
      }

      this.loadWorld(p_71403_1_, guiscreenworking);
   }

   public void loadWorld(@Nullable WorldClient p_205055_1_, GuiScreen p_205055_2_) {
      if (p_205055_1_ == null) {
         NetHandlerPlayClient nethandlerplayclient = this.getConnection();
         if (nethandlerplayclient != null) {
            this.scheduledTasks.clear();
            nethandlerplayclient.cleanup();
         }

         this.integratedServer = null;
         this.entityRenderer.func_190564_k();
         this.playerController = null;
         NarratorChatListener.INSTANCE.clear();
      }

      this.musicTicker.stopMusic();
      this.soundHandler.stop();
      this.renderViewEntity = null;
      this.networkManager = null;
      this.displayGuiScreen(p_205055_2_);
      this.runGameLoop(false);
      if (p_205055_1_ == null && this.world != null) {
         this.packFinder.clearResourcePack();
         this.ingameGUI.resetPlayersOverlayFooterHeader();
         this.setServerData(null);
         this.integratedServerIsRunning = false;
      }

      this.world = p_205055_1_;
      if (this.renderGlobal != null) {
         this.renderGlobal.func_72732_a(p_205055_1_);
      }

      if (this.effectRenderer != null) {
         this.effectRenderer.clearEffects(p_205055_1_);
      }

      TileEntityRendererDispatcher.instance.setWorld(p_205055_1_);
      if (p_205055_1_ != null) {
         if (!this.integratedServerIsRunning) {
            AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
            TileEntitySkull.setProfileCache(playerprofilecache);
            TileEntitySkull.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setOnlineMode(false);
         }

         if (this.player == null) {
            this.player = this.playerController.createPlayer(p_205055_1_, new StatisticsManager(), new RecipeBookClient(p_205055_1_.getRecipeManager()));
            this.playerController.flipPlayer(this.player);
            if (this.integratedServer != null) {
               this.integratedServer.func_211527_b(this.player.getUniqueID());
            }
         }

         this.player.preparePlayerToSpawn();
         p_205055_1_.spawnEntity(this.player);
         this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
         this.playerController.setPlayerCapabilities(this.player);
         this.renderViewEntity = this.player;
      } else {
         this.player = null;
      }

      System.gc();
   }

   public void func_212315_a(DimensionType p_212315_1_) {
      this.world.setInitialSpawnLocation();
      this.world.removeAllEntities();
      int i = 0;
      String s = null;
      if (this.player != null) {
         i = this.player.getEntityId();
         this.world.removeEntity(this.player);
         s = this.player.getServerBrand();
      }

      this.renderViewEntity = null;
      EntityPlayerSP entityplayersp = this.player;
      this.player = this.playerController.createPlayer(this.world, this.player == null ? new StatisticsManager() : this.player.getStatFileWriter(), this.player == null ? new RecipeBookClient(new RecipeManager()) : this.player.getRecipeBook());
      this.player.getDataManager().setEntryValues(entityplayersp.getDataManager().getAll());
      this.player.dimension = p_212315_1_;
      this.renderViewEntity = this.player;
      this.player.preparePlayerToSpawn();
      this.player.setServerBrand(s);
      this.world.spawnEntity(this.player);
      this.playerController.flipPlayer(this.player);
      this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
      this.player.setEntityId(i);
      this.playerController.setPlayerCapabilities(this.player);
      this.player.setReducedDebug(entityplayersp.hasReducedDebug());
      if (this.currentScreen instanceof GuiGameOver) {
         this.displayGuiScreen(null);
      }

   }

   public final boolean isDemo() {
      return this.isDemo;
   }

   @Nullable
   public NetHandlerPlayClient getConnection() {
      return this.player == null ? null : this.player.connection;
   }

   public static boolean isGuiEnabled() {
      return instance == null || !instance.gameSettings.hideGUI;
   }

   public static boolean isFancyGraphicsEnabled() {
      return instance != null && instance.gameSettings.fancyGraphics;
   }

   public static boolean isAmbientOcclusionEnabled() {
      return instance != null && instance.gameSettings.ambientOcclusion != 0;
   }

   private void middleClickMouse() {
      if (this.objectMouseOver != null && this.objectMouseOver.type != RayTraceResult.Type.MISS) {
         boolean flag = this.player.capabilities.isCreativeMode;
         TileEntity tileentity = null;
         ItemStack itemstack;
         if (this.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = this.objectMouseOver.getBlockPos();
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (iblockstate.isAir()) {
               return;
            }

            itemstack = block.getItem(this.world, blockpos, iblockstate);
            if (itemstack.isEmpty()) {
               return;
            }

            if (flag && GuiScreen.isCtrlKeyDown() && block.hasTileEntity()) {
               tileentity = this.world.getTileEntity(blockpos);
            }
         } else {
            if (this.objectMouseOver.type != RayTraceResult.Type.ENTITY || this.objectMouseOver.entity == null || !flag) {
               return;
            }

            if (this.objectMouseOver.entity instanceof EntityPainting) {
               itemstack = new ItemStack(Items.PAINTING);
            } else if (this.objectMouseOver.entity instanceof EntityLeashKnot) {
               itemstack = new ItemStack(Items.LEAD);
            } else if (this.objectMouseOver.entity instanceof EntityItemFrame) {
               EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entity;
               ItemStack itemstack1 = entityitemframe.getDisplayedItem();
               if (itemstack1.isEmpty()) {
                  itemstack = new ItemStack(Items.ITEM_FRAME);
               } else {
                  itemstack = itemstack1.copy();
               }
            } else if (this.objectMouseOver.entity instanceof EntityMinecart) {
               EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entity;
               Item item;
               switch(entityminecart.getMinecartType()) {
               case FURNACE:
                  item = Items.FURNACE_MINECART;
                  break;
               case CHEST:
                  item = Items.CHEST_MINECART;
                  break;
               case TNT:
                  item = Items.TNT_MINECART;
                  break;
               case HOPPER:
                  item = Items.HOPPER_MINECART;
                  break;
               case COMMAND_BLOCK:
                  item = Items.COMMAND_BLOCK_MINECART;
                  break;
               default:
                  item = Items.MINECART;
               }

               itemstack = new ItemStack(item);
            } else if (this.objectMouseOver.entity instanceof EntityBoat) {
               itemstack = new ItemStack(((EntityBoat)this.objectMouseOver.entity).getItemBoat());
            } else if (this.objectMouseOver.entity instanceof EntityArmorStand) {
               itemstack = new ItemStack(Items.ARMOR_STAND);
            } else if (this.objectMouseOver.entity instanceof EntityEnderCrystal) {
               itemstack = new ItemStack(Items.END_CRYSTAL);
            } else {
               ItemSpawnEgg itemspawnegg = ItemSpawnEgg.getEgg(this.objectMouseOver.entity.getType());
               if (itemspawnegg == null) {
                  return;
               }

               itemstack = new ItemStack(itemspawnegg);
            }
         }

         if (itemstack.isEmpty()) {
            String s = "";
            if (this.objectMouseOver.type == RayTraceResult.Type.BLOCK) {
               s = IRegistry.field_212618_g.func_177774_c(this.world.getBlockState(this.objectMouseOver.getBlockPos()).getBlock()).toString();
            } else if (this.objectMouseOver.type == RayTraceResult.Type.ENTITY) {
               s = IRegistry.field_212629_r.func_177774_c(this.objectMouseOver.entity.getType()).toString();
            }

            LOGGER.warn("Picking on: [{}] {} gave null item", this.objectMouseOver.type, s);
         } else {
            InventoryPlayer inventoryplayer = this.player.inventory;
            if (tileentity != null) {
               this.storeTEInStack(itemstack, tileentity);
            }

            int i = inventoryplayer.getSlotFor(itemstack);
            if (flag) {
               inventoryplayer.setPickedItemStack(itemstack);
               this.playerController.sendSlotPacket(this.player.getHeldItem(EnumHand.MAIN_HAND), 36 + inventoryplayer.currentItem);
            } else if (i != -1) {
               if (InventoryPlayer.isHotbar(i)) {
                  inventoryplayer.currentItem = i;
               } else {
                  this.playerController.pickItem(i);
               }
            }

         }
      }
   }

   public ItemStack storeTEInStack(ItemStack p_184119_1_, TileEntity p_184119_2_) {
      NBTTagCompound nbttagcompound = p_184119_2_.writeToNBT(new NBTTagCompound());
      if (p_184119_1_.getItem() instanceof ItemSkull && nbttagcompound.hasKey("Owner")) {
         NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
         p_184119_1_.getOrCreateTag().setTag("SkullOwner", nbttagcompound2);
         return p_184119_1_;
      } else {
         p_184119_1_.setTagInfo("BlockEntityTag", nbttagcompound);
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         NBTTagList nbttaglist = new NBTTagList();
         nbttaglist.add(new NBTTagString("(+NBT)"));
         nbttagcompound1.setTag("Lore", nbttaglist);
         p_184119_1_.setTagInfo("display", nbttagcompound1);
         return p_184119_1_;
      }
   }

   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport p_71396_1_) {
      CrashReportCategory crashreportcategory = p_71396_1_.getCategory();
      crashreportcategory.addDetail("Launched Version", () -> {
         return this.launchedVersion;
      });
      crashreportcategory.addDetail("LWJGL", Version::getVersion);
      crashreportcategory.addDetail("OpenGL", () -> {
         return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
      });
      crashreportcategory.addDetail("GL Caps", OpenGlHelper::getLogText);
      crashreportcategory.addDetail("Using VBOs", () -> {
         return this.gameSettings.useVbo ? "Yes" : "No";
      });
      crashreportcategory.addDetail("Is Modded", () -> {
         String s = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(s)) {
            return "Definitely; Client brand changed to '" + s + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      crashreportcategory.addCrashSection("Type", "Client (map_client.txt)");
      crashreportcategory.addDetail("Resource Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(String s : this.gameSettings.resourcePacks) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(s);
            if (this.gameSettings.incompatibleResourcePacks.contains(s)) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      crashreportcategory.addDetail("Current Language", () -> {
         return this.languageManager.getCurrentLanguage().toString();
      });
      crashreportcategory.addDetail("Profiler Position", () -> {
         return this.profiler.isProfiling() ? this.profiler.getNameOfLastSection() : "N/A (disabled)";
      });
      crashreportcategory.addDetail("CPU", OpenGlHelper::getCpu);
      if (this.world != null) {
         this.world.addWorldInfoToCrashReport(p_71396_1_);
      }

      return p_71396_1_;
   }

   public static Minecraft getInstance() {
      return instance;
   }

   public ListenableFuture<Object> scheduleResourcesRefresh() {
      return this.addScheduledTask(this::refreshResources);
   }

   public void addServerStatsToSnooper(Snooper p_70000_1_) {
      p_70000_1_.addClientStat("fps", debugFPS);
      p_70000_1_.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
      long i = GLFW.glfwGetWindowMonitor(this.mainWindow.getHandle());
      if (i == 0L) {
         i = GLFW.glfwGetPrimaryMonitor();
      }

      p_70000_1_.addClientStat("display_frequency", GLFW.glfwGetVideoMode(i).refreshRate());
      p_70000_1_.addClientStat("display_type", this.mainWindow.isFullscreen() ? "fullscreen" : "windowed");
      p_70000_1_.addClientStat("run_time", (Util.milliTime() - p_70000_1_.getMinecraftStartTimeMillis()) / 60L * 1000L);
      p_70000_1_.addClientStat("current_action", this.getCurrentAction());
      p_70000_1_.addClientStat("language", this.gameSettings.language == null ? "en_us" : this.gameSettings.language);
      String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      p_70000_1_.addClientStat("endianness", s);
      p_70000_1_.addClientStat("subtitles", this.gameSettings.showSubtitles);
      p_70000_1_.addClientStat("touch", this.gameSettings.touchscreen ? "touch" : "mouse");
      int j = 0;

      for(ResourcePackInfoClient resourcepackinfoclient : this.resourcePackRepository.getPackInfos()) {
         if (!resourcepackinfoclient.func_195797_g() && !resourcepackinfoclient.func_195798_h()) {
            p_70000_1_.addClientStat("resource_pack[" + j++ + "]", resourcepackinfoclient.getName());
         }
      }

      p_70000_1_.addClientStat("resource_packs", j);
      if (this.integratedServer != null && this.integratedServer.getSnooper() != null) {
         p_70000_1_.addClientStat("snooper_partner", this.integratedServer.getSnooper().getUniqueID());
      }

   }

   private String getCurrentAction() {
      if (this.integratedServer != null) {
         return this.integratedServer.getPublic() ? "hosting_lan" : "singleplayer";
      } else if (this.currentServerData != null) {
         return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
      } else {
         return "out_of_game";
      }
   }

   public static int getGLMaximumTextureSize() {
      if (cachedMaximumTextureSize == -1) {
         for(int i = 16384; i > 0; i >>= 1) {
            GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
            int j = GlStateManager.glGetTexLevelParameteri(32868, 0, 4096);
            if (j != 0) {
               cachedMaximumTextureSize = i;
               return i;
            }
         }
      }

      return cachedMaximumTextureSize;
   }

   public boolean isSnooperEnabled() {
      return this.gameSettings.snooperEnabled;
   }

   public void setServerData(ServerData p_71351_1_) {
      this.currentServerData = p_71351_1_;
   }

   @Nullable
   public ServerData getCurrentServerData() {
      return this.currentServerData;
   }

   public boolean isIntegratedServerRunning() {
      return this.integratedServerIsRunning;
   }

   public boolean isSingleplayer() {
      return this.integratedServerIsRunning && this.integratedServer != null;
   }

   @Nullable
   public IntegratedServer getIntegratedServer() {
      return this.integratedServer;
   }

   public static void stopIntegratedServer() {
      if (instance != null) {
         IntegratedServer integratedserver = instance.getIntegratedServer();
         if (integratedserver != null) {
            integratedserver.stopServer();
         }

      }
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public Session getSession() {
      return this.session;
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.profileProperties.putAll(gameprofile.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public IResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ResourcePackInfoClient> getResourcePackList() {
      return this.resourcePackRepository;
   }

   public DownloadingPackFinder getPackFinder() {
      return this.packFinder;
   }

   public File getFileResourcePacks() {
      return this.fileResourcepacks;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public TextureMap getTextureMap() {
      return this.textureMap;
   }

   public boolean isJava64bit() {
      return this.jvm64bit;
   }

   public boolean isGamePaused() {
      return this.isGamePaused;
   }

   public SoundHandler getSoundHandler() {
      return this.soundHandler;
   }

   public MusicTicker.MusicType getAmbientMusicType() {
      if (this.currentScreen instanceof GuiWinGame) {
         return MusicTicker.MusicType.CREDITS;
      } else if (this.player == null) {
         return MusicTicker.MusicType.MENU;
      } else if (this.player.world.dimension instanceof NetherDimension) {
         return MusicTicker.MusicType.NETHER;
      } else if (this.player.world.dimension instanceof EndDimension) {
         return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
      } else {
         Biome.Category biome$category = this.player.world.getBiome(new BlockPos(this.player.posX, this.player.posY, this.player.posZ)).getBiomeCategory();
         if (!this.musicTicker.isPlaying(MusicTicker.MusicType.UNDER_WATER) && (!this.player.canSwim() || this.musicTicker.isPlaying(MusicTicker.MusicType.GAME) || biome$category != Biome.Category.OCEAN && biome$category != Biome.Category.RIVER)) {
            return this.player.capabilities.isCreativeMode && this.player.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
         } else {
            return MusicTicker.MusicType.UNDER_WATER;
         }
      }
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   @Nullable
   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public void setRenderViewEntity(Entity p_175607_1_) {
      this.renderViewEntity = p_175607_1_;
      this.entityRenderer.func_175066_a(p_175607_1_);
   }

   public <V> ListenableFuture<V> addScheduledTask(Callable<V> p_152343_1_) {
      Validate.notNull(p_152343_1_);
      if (this.isCallingFromMinecraftThread()) {
         try {
            return Futures.immediateFuture(p_152343_1_.call());
         } catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
         }
      } else {
         ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(p_152343_1_);
         this.scheduledTasks.add(listenablefuturetask);
         return listenablefuturetask;
      }
   }

   public ListenableFuture<Object> addScheduledTask(Runnable p_152344_1_) {
      Validate.notNull(p_152344_1_);
      return this.addScheduledTask(Executors.callable(p_152344_1_));
   }

   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.thread;
   }

   public BlockRendererDispatcher getBlockRendererDispatcher() {
      return this.blockRenderDispatcher;
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public FirstPersonRenderer getFirstPersonRenderer() {
      return this.firstPersonRenderer;
   }

   public <T> ISearchTree<T> getSearchTree(SearchTreeManager.Key<T> p_193987_1_) {
      return this.searchTreeManager.get(p_193987_1_);
   }

   public static int getDebugFPS() {
      return debugFPS;
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   public void setConnectedToRealms(boolean p_181537_1_) {
      this.connectedToRealms = p_181537_1_;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public float getRenderPartialTicks() {
      return this.timer.renderPartialTicks;
   }

   public float getTickLength() {
      return this.timer.elapsedPartialTicks;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   public boolean isReducedDebug() {
      return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
   }

   public GuiToast getToastGui() {
      return this.toastGui;
   }

   public Tutorial getTutorial() {
      return this.tutorial;
   }

   public boolean isGameFocused() {
      return this.isWindowFocused;
   }

   public CreativeSettings getCreativeSettings() {
      return this.creativeSettings;
   }

   public ModelManager func_209506_al() {
      return this.modelManager;
   }

   public FontResourceManager getFontResourceManager() {
      return this.fontResourceMananger;
   }
}
