package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.*;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.server.management.UserListWhitelist;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.*;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.*;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.*;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;

public abstract class MinecraftServer implements IThreadListener, ISnooperInfo, ICommandSource, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USER_CACHE_FILE = new File("usercache.json");
   private final ISaveFormat anvilConverterForAnvilFile;
   private final Snooper snooper = new Snooper("server", this, Util.milliTime());
   private final File anvilFile;
   private final List<ITickable> tickables = Lists.newArrayList();
   public final Profiler profiler = new Profiler();
   private final NetworkSystem networkSystem;
   private final ServerStatusResponse statusResponse = new ServerStatusResponse();
   private final Random random = new Random();
   private final DataFixer dataFixer;
   private String hostname;
   private int serverPort = -1;
   private final Map<DimensionType, WorldServer> worlds = Maps.newIdentityHashMap();
   private PlayerList playerList;
   private boolean serverRunning = true;
   private boolean serverStopped;
   private int tickCounter;
   protected final Proxy serverProxy;
   private ITextComponent currentTask;
   private int percentDone;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean canSpawnAnimals;
   private boolean canSpawnNPCs;
   private boolean pvpEnabled;
   private boolean allowFlight;
   private String motd;
   private int buildLimit;
   private int maxPlayerIdleMinutes;
   public final long[] tickTimeArray = new long[100];
   protected final Map<DimensionType, long[]> timeOfLastDimensionTick = Maps.newIdentityHashMap();
   private KeyPair serverKeyPair;
   private String serverOwner;
   private String folderName;
   @OnlyIn(Dist.CLIENT)
   private String worldName;
   private boolean isDemo;
   private boolean enableBonusChest;
   private String resourcePackUrl = "";
   private String resourcePackHash = "";
   private boolean serverIsRunning;
   private long timeOfLastWarning;
   private ITextComponent userMessage;
   private boolean startProfiling;
   private boolean isGamemodeForced;
   private final YggdrasilAuthenticationService authService;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepo;
   private final PlayerProfileCache profileCache;
   private long nanoTimeSinceStatusRefresh;
   public final Queue<FutureTask<?>> futureTaskQueue = Queues.newConcurrentLinkedQueue();
   private Thread serverThread;
   protected long serverTime = Util.milliTime();
   @OnlyIn(Dist.CLIENT)
   private boolean worldIconSet;
   private final IReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
   private final ResourcePackList<ResourcePackInfo> resourcePacks = new ResourcePackList<>(ResourcePackInfo::new);
   private FolderPackFinder datapackFinder;
   private final Commands commandManager;
   private final RecipeManager recipeManager = new RecipeManager();
   private final NetworkTagManager networkTagManager = new NetworkTagManager();
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   private final CustomBossEvents customBossEvents = new CustomBossEvents(this);
   private final LootTableManager lootTableManager = new LootTableManager();
   private final AdvancementManager advancementManager = new AdvancementManager();
   private final FunctionManager functionManager = new FunctionManager(this);
   private boolean whitelistEnabled;
   private boolean forceWorldUpgrade;
   private float tickTime;

   public MinecraftServer(@Nullable File p_i49697_1_, Proxy p_i49697_2_, DataFixer p_i49697_3_, Commands p_i49697_4_, YggdrasilAuthenticationService p_i49697_5_, MinecraftSessionService p_i49697_6_, GameProfileRepository p_i49697_7_, PlayerProfileCache p_i49697_8_) {
      this.serverProxy = p_i49697_2_;
      this.commandManager = p_i49697_4_;
      this.authService = p_i49697_5_;
      this.sessionService = p_i49697_6_;
      this.profileRepo = p_i49697_7_;
      this.profileCache = p_i49697_8_;
      this.anvilFile = p_i49697_1_;
      this.networkSystem = p_i49697_1_ == null ? null : new NetworkSystem(this);
      this.anvilConverterForAnvilFile = p_i49697_1_ == null ? null : new AnvilSaveConverter(p_i49697_1_.toPath(), p_i49697_1_.toPath().resolve("../backups"), p_i49697_3_);
      this.dataFixer = p_i49697_3_;
      this.resourceManager.addReloadListener(this.networkTagManager);
      this.resourceManager.addReloadListener(this.recipeManager);
      this.resourceManager.addReloadListener(this.lootTableManager);
      this.resourceManager.addReloadListener(this.functionManager);
      this.resourceManager.addReloadListener(this.advancementManager);
   }

   public abstract boolean init() throws IOException;

   public void convertMapIfNeeded(String p_71237_1_) {
      if (this.getActiveAnvilConverter().isOldMapFormat(p_71237_1_)) {
         LOGGER.info("Converting map!");
         this.setUserMessage(new TextComponentTranslation("menu.convertingLevel"));
         this.getActiveAnvilConverter().convertMapFormat(p_71237_1_, new IProgressUpdate() {
            private long startTime = Util.milliTime();

            public void func_200210_a(ITextComponent p_200210_1_) {
            }

            @OnlyIn(Dist.CLIENT)
            public void func_200211_b(ITextComponent p_200211_1_) {
            }

            public void setLoadingProgress(int p_73718_1_) {
               if (Util.milliTime() - this.startTime >= 1000L) {
                  this.startTime = Util.milliTime();
                  MinecraftServer.LOGGER.info("Converting... {}%", p_73718_1_);
               }

            }

            @OnlyIn(Dist.CLIENT)
            public void setDoneWorking() {
            }

            public void func_200209_c(ITextComponent p_200209_1_) {
            }
         });
      }

      if (this.forceWorldUpgrade) {
         LOGGER.info("Forcing world upgrade!");
         WorldInfo worldinfo = this.getActiveAnvilConverter().getWorldInfo(this.getFolderName());
         if (worldinfo != null) {
            WorldOptimizer worldoptimizer = new WorldOptimizer(this.getFolderName(), this.getActiveAnvilConverter(), worldinfo);
            ITextComponent itextcomponent = null;

            while(!worldoptimizer.func_212218_b()) {
               ITextComponent itextcomponent1 = worldoptimizer.getStatusText();
               if (itextcomponent != itextcomponent1) {
                  itextcomponent = itextcomponent1;
                  LOGGER.info(worldoptimizer.getStatusText().getString());
               }

               int i = worldoptimizer.func_212211_j();
               if (i > 0) {
                  int j = worldoptimizer.func_212208_k() + worldoptimizer.func_212209_l();
                  LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
               }

               if (this.isServerStopped()) {
                  worldoptimizer.func_212217_a();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                  }
               }
            }
         }
      }

   }

   protected synchronized void setUserMessage(ITextComponent p_200245_1_) {
      this.userMessage = p_200245_1_;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public synchronized ITextComponent getUserMessage() {
      return this.userMessage;
   }

   public void loadAllWorlds(String p_71247_1_, String p_71247_2_, long p_71247_3_, WorldType p_71247_5_, JsonElement p_71247_6_) {
      this.convertMapIfNeeded(p_71247_1_);
      this.setUserMessage(new TextComponentTranslation("menu.loadingLevel"));
      ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(p_71247_1_, this);
      this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      WorldSettings worldsettings;
      if (worldinfo == null) {
         if (this.isDemo()) {
            worldsettings = WorldServerDemo.DEMO_WORLD_SETTINGS;
         } else {
            worldsettings = new WorldSettings(p_71247_3_, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), p_71247_5_);
            worldsettings.func_205390_a(p_71247_6_);
            if (this.enableBonusChest) {
               worldsettings.enableBonusChest();
            }
         }

         worldinfo = new WorldInfo(worldsettings, p_71247_2_);
      } else {
         worldinfo.setWorldName(p_71247_2_);
         worldsettings = new WorldSettings(worldinfo);
      }

      this.func_195560_a(isavehandler.getWorldDirectory(), worldinfo);
      WorldSavedDataStorage worldsaveddatastorage = new WorldSavedDataStorage(isavehandler);
      this.func_212369_a(isavehandler, worldsaveddatastorage, worldinfo, worldsettings);
      this.setDifficultyForAllWorlds(this.getDifficulty());
      this.func_71222_d(worldsaveddatastorage);
   }

   public void func_212369_a(ISaveHandler p_212369_1_, WorldSavedDataStorage p_212369_2_, WorldInfo p_212369_3_, WorldSettings p_212369_4_) {
      if (this.isDemo()) {
         this.worlds.put(DimensionType.OVERWORLD, (new WorldServerDemo(this, p_212369_1_, p_212369_2_, p_212369_3_, DimensionType.OVERWORLD, this.profiler)).func_212251_i__());
      } else {
         this.worlds.put(DimensionType.OVERWORLD, (new WorldServer(this, p_212369_1_, p_212369_2_, p_212369_3_, DimensionType.OVERWORLD, this.profiler)).func_212251_i__());
      }

      WorldServer worldserver = this.func_71218_a(DimensionType.OVERWORLD);
      worldserver.initialize(p_212369_4_);
      worldserver.addEventListener(new ServerWorldEventHandler(this, worldserver));
      if (!this.isSinglePlayer()) {
         worldserver.getWorldInfo().setGameType(this.getGameType());
      }

      WorldServerMulti worldservermulti = (new WorldServerMulti(this, p_212369_1_, DimensionType.NETHER, worldserver, this.profiler)).func_212251_i__();
      this.worlds.put(DimensionType.NETHER, worldservermulti);
      worldservermulti.addEventListener(new ServerWorldEventHandler(this, worldservermulti));
      if (!this.isSinglePlayer()) {
         worldservermulti.getWorldInfo().setGameType(this.getGameType());
      }

      WorldServerMulti worldservermulti1 = (new WorldServerMulti(this, p_212369_1_, DimensionType.THE_END, worldserver, this.profiler)).func_212251_i__();
      this.worlds.put(DimensionType.THE_END, worldservermulti1);
      worldservermulti1.addEventListener(new ServerWorldEventHandler(this, worldservermulti1));
      if (!this.isSinglePlayer()) {
         worldservermulti1.getWorldInfo().setGameType(this.getGameType());
      }

      this.getPlayerList().func_212504_a(worldserver);
      if (p_212369_3_.getCustomBossEvents() != null) {
         this.getCustomBossEvents().read(p_212369_3_.getCustomBossEvents());
      }

   }

   public void func_195560_a(File p_195560_1_, WorldInfo p_195560_2_) {
      this.resourcePacks.addPackFinder(new ServerPackFinder());
      this.datapackFinder = new FolderPackFinder(new File(p_195560_1_, "datapacks"));
      this.resourcePacks.addPackFinder(this.datapackFinder);
      this.resourcePacks.reloadPacksFromFinders();
      List<ResourcePackInfo> list = Lists.newArrayList();

      for(String s : p_195560_2_.getEnabledDataPacks()) {
         ResourcePackInfo resourcepackinfo = this.resourcePacks.getPackInfo(s);
         if (resourcepackinfo != null) {
            list.add(resourcepackinfo);
         } else {
            LOGGER.warn("Missing data pack {}", s);
         }
      }

      this.resourcePacks.func_198985_a(list);
      this.loadDataPacks(p_195560_2_);
   }

   public void func_71222_d(WorldSavedDataStorage p_71222_1_) {
      int i = 16;
      int j = 4;
      int k = 12;
      int l = 192;
      int i1 = 625;
      this.setUserMessage(new TextComponentTranslation("menu.generatingTerrain"));
      WorldServer worldserver = this.func_71218_a(DimensionType.OVERWORLD);
      LOGGER.info("Preparing start region for dimension " + DimensionType.func_212678_a(worldserver.dimension.getType()));
      BlockPos blockpos = worldserver.getSpawnPoint();
      List<ChunkPos> list = Lists.newArrayList();
      Set<ChunkPos> set = Sets.newConcurrentHashSet();
      Stopwatch stopwatch = Stopwatch.createStarted();

      for(int j1 = -192; j1 <= 192 && this.isServerRunning(); j1 += 16) {
         for(int k1 = -192; k1 <= 192 && this.isServerRunning(); k1 += 16) {
            list.add(new ChunkPos(blockpos.getX() + j1 >> 4, blockpos.getZ() + k1 >> 4));
         }

         CompletableFuture<?> completablefuture = worldserver.getChunkProvider().loadChunks(list, (p_201701_1_) -> {
            set.add(p_201701_1_.getPos());
         });

         while(!completablefuture.isDone()) {
            try {
               completablefuture.get(1L, TimeUnit.SECONDS);
            } catch (InterruptedException interruptedexception) {
               throw new RuntimeException(interruptedexception);
            } catch (ExecutionException executionexception) {
               if (executionexception.getCause() instanceof RuntimeException) {
                  throw (RuntimeException)executionexception.getCause();
               }

               throw new RuntimeException(executionexception.getCause());
            } catch (TimeoutException var22) {
               this.setCurrentTaskAndPercentDone(new TextComponentTranslation("menu.preparingSpawn"), set.size() * 100 / 625);
            }
         }

         this.setCurrentTaskAndPercentDone(new TextComponentTranslation("menu.preparingSpawn"), set.size() * 100 / 625);
      }

      LOGGER.info("Time elapsed: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         ForcedChunksSaveData forcedchunkssavedata = p_71222_1_.func_212426_a(dimensiontype, ForcedChunksSaveData::new, "chunks");
         if (forcedchunkssavedata != null) {
            WorldServer worldserver1 = this.func_71218_a(dimensiontype);
            LongIterator longiterator = forcedchunkssavedata.func_212438_a().iterator();

            while(longiterator.hasNext()) {
               this.setCurrentTaskAndPercentDone(new TextComponentTranslation("menu.loadingForcedChunks", dimensiontype), forcedchunkssavedata.func_212438_a().size() * 100 / 625);
               long l1 = longiterator.nextLong();
               ChunkPos chunkpos = new ChunkPos(l1);
               worldserver1.getChunkProvider().func_186025_d(chunkpos.x, chunkpos.z, true, true);
            }
         }
      }

      this.clearCurrentTask();
   }

   public void setResourcePackFromWorld(String p_175584_1_, ISaveHandler p_175584_2_) {
      File file1 = new File(p_175584_2_.getWorldDirectory(), "resources.zip");
      if (file1.isFile()) {
         try {
            this.setResourcePack("level://" + URLEncoder.encode(p_175584_1_, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            LOGGER.warn("Something went wrong url encoding {}", p_175584_1_);
         }
      }

   }

   public abstract boolean canStructuresSpawn();

   public abstract GameType getGameType();

   public abstract EnumDifficulty getDifficulty();

   public abstract boolean isHardcore();

   public abstract int getOpPermissionLevel();

   public abstract boolean allowLoggingRcon();

   protected void setCurrentTaskAndPercentDone(ITextComponent p_200250_1_, int p_200250_2_) {
      this.currentTask = p_200250_1_;
      this.percentDone = p_200250_2_;
      LOGGER.info("{}: {}%", p_200250_1_.getString(), p_200250_2_);
   }

   protected void clearCurrentTask() {
      this.currentTask = null;
      this.percentDone = 0;
   }

   public void saveAllWorlds(boolean p_71267_1_) {
      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver != null) {
            if (!p_71267_1_) {
               LOGGER.info("Saving chunks for level '{}'/{}", worldserver.getWorldInfo().getWorldName(), DimensionType.func_212678_a(worldserver.dimension.getType()));
            }

            try {
               worldserver.saveAllChunks(true, null);
            } catch (SessionLockException sessionlockexception) {
               LOGGER.warn(sessionlockexception.getMessage());
            }
         }
      }

   }

   public void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getNetworkSystem() != null) {
         this.getNetworkSystem().terminateEndpoints();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAllPlayerData();
         this.playerList.removeAllPlayers();
      }

      LOGGER.info("Saving worlds");

      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver != null) {
            worldserver.disableLevelSaving = false;
         }
      }

      this.saveAllWorlds(false);

      for(WorldServer worldserver1 : this.func_212370_w()) {
         if (worldserver1 != null) {
            worldserver1.close();
         }
      }

      if (this.snooper.isSnooperRunning()) {
         this.snooper.stop();
      }

   }

   public String getServerHostname() {
      return this.hostname;
   }

   public void setHostname(String p_71189_1_) {
      this.hostname = p_71189_1_;
   }

   public boolean isServerRunning() {
      return this.serverRunning;
   }

   public void initiateShutdown() {
      this.serverRunning = false;
   }

   private boolean func_212379_aT() {
      return Util.milliTime() < this.serverTime;
   }

   public void run() {
      try {
         if (this.init()) {
            this.serverTime = Util.milliTime();
            this.statusResponse.setServerDescription(new TextComponentString(this.motd));
            this.statusResponse.setVersion(new ServerStatusResponse.Version("1.13.2", 404));
            this.applyServerIconToResponse(this.statusResponse);

            while(this.serverRunning) {
               long i = Util.milliTime() - this.serverTime;
               if (i > 2000L && this.serverTime - this.timeOfLastWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.serverTime += j * 50L;
                  this.timeOfLastWarning = this.serverTime;
               }

               this.func_71217_p(this::func_212379_aT);
               this.serverTime += 50L;

               while(this.func_212379_aT()) {
                  Thread.sleep(1L);
               }

               this.serverIsRunning = true;
            }
         } else {
            this.finalTick(null);
         }
      } catch (Throwable throwable1) {
         LOGGER.error("Encountered an unexpected exception", throwable1);
         CrashReport crashreport;
         if (throwable1 instanceof ReportedException) {
            crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
         } else {
            crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
         }

         File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         this.finalTick(crashreport);
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
         } catch (Throwable throwable) {
            LOGGER.error("Exception stopping the server", throwable);
         } finally {
            this.systemExitNow();
         }

      }

   }

   public void applyServerIconToResponse(ServerStatusResponse p_184107_1_) {
      File file1 = this.getFile("server-icon.png");
      if (!file1.exists()) {
         file1 = this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
      }

      if (file1.isFile()) {
         ByteBuf bytebuf = Unpooled.buffer();

         try {
            BufferedImage bufferedimage = ImageIO.read(file1);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
            p_184107_1_.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load server icon", exception);
         } finally {
            bytebuf.release();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWorldIconSet() {
      this.worldIconSet = this.worldIconSet || this.getWorldIconFile().isFile();
      return this.worldIconSet;
   }

   @OnlyIn(Dist.CLIENT)
   public File getWorldIconFile() {
      return this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
   }

   public File getDataDirectory() {
      return new File(".");
   }

   public void finalTick(CrashReport p_71228_1_) {
   }

   public void systemExitNow() {
   }

   public void func_71217_p(BooleanSupplier p_71217_1_) {
      long i = Util.nanoTime();
      ++this.tickCounter;
      if (this.startProfiling) {
         this.startProfiling = false;
         this.profiler.startProfiling(this.tickCounter);
      }

      this.profiler.startSection("root");
      this.func_71190_q(p_71217_1_);
      if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
         this.nanoTimeSinceStatusRefresh = i;
         this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
         int j = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

         for(int k = 0; k < agameprofile.length; ++k) {
            agameprofile[k] = this.playerList.getPlayers().get(j + k).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.statusResponse.getPlayers().setPlayers(agameprofile);
      }

      if (this.tickCounter % 900 == 0) {
         this.profiler.startSection("save");
         this.playerList.saveAllPlayerData();
         this.saveAllWorlds(true);
         this.profiler.endSection();
      }

      this.profiler.startSection("snooper");
      if (!this.snooper.isSnooperRunning() && this.tickCounter > 100) {
         this.snooper.start();
      }

      if (this.tickCounter % 6000 == 0) {
         this.snooper.addMemoryStatsToSnooper();
      }

      this.profiler.endSection();
      this.profiler.startSection("tallying");
      long l = this.tickTimeArray[this.tickCounter % 100] = Util.nanoTime() - i;
      this.tickTime = this.tickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      this.profiler.endSection();
      this.profiler.endSection();
   }

   public void func_71190_q(BooleanSupplier p_71190_1_) {
      this.profiler.startSection("jobs");

      FutureTask<?> futuretask;
      while((futuretask = this.futureTaskQueue.poll()) != null) {
         Util.runTask(futuretask, LOGGER);
      }

      this.profiler.endStartSection("commandFunctions");
      this.getFunctionManager().tick();
      this.profiler.endStartSection("levels");

      for(WorldServer worldserver : this.func_212370_w()) {
         long i = Util.nanoTime();
         if (worldserver.dimension.getType() == DimensionType.OVERWORLD || this.getAllowNether()) {
            this.profiler.startSection(() -> {
               return "dim-" + worldserver.dimension.getType().getId();
            });
            if (this.tickCounter % 20 == 0) {
               this.profiler.startSection("timeSync");
               this.playerList.func_148537_a(new SPacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.dimension.getType());
               this.profiler.endSection();
            }

            this.profiler.startSection("tick");

            try {
               worldserver.tick(p_71190_1_);
            } catch (Throwable throwable1) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
               worldserver.addWorldInfoToCrashReport(crashreport);
               throw new ReportedException(crashreport);
            }

            try {
               worldserver.tickEntities();
            } catch (Throwable throwable) {
               CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Exception ticking world entities");
               worldserver.addWorldInfoToCrashReport(crashreport1);
               throw new ReportedException(crashreport1);
            }

            this.profiler.endSection();
            this.profiler.startSection("tracker");
            worldserver.getEntityTracker().tick();
            this.profiler.endSection();
            this.profiler.endSection();
         }

         (this.timeOfLastDimensionTick.computeIfAbsent(worldserver.dimension.getType(), (p_212377_0_) -> {
            return new long[100];
         }))[this.tickCounter % 100] = Util.nanoTime() - i;
      }

      this.profiler.endStartSection("connection");
      this.getNetworkSystem().tick();
      this.profiler.endStartSection("players");
      this.playerList.tick();
      this.profiler.endStartSection("tickables");

      for(int j = 0; j < this.tickables.size(); ++j) {
         this.tickables.get(j).tick();
      }

      this.profiler.endSection();
   }

   public boolean getAllowNether() {
      return true;
   }

   public void registerTickable(ITickable p_82010_1_) {
      this.tickables.add(p_82010_1_);
   }

   public static void main(String[] p_main_0_) {
      Bootstrap.register();

      try {
         boolean flag = true;
         String s = null;
         String s1 = ".";
         String s2 = null;
         boolean flag1 = false;
         boolean flag2 = false;
         boolean flag3 = false;
         int i = -1;

         for(int j = 0; j < p_main_0_.length; ++j) {
            String s3 = p_main_0_[j];
            String s4 = j == p_main_0_.length - 1 ? null : p_main_0_[j + 1];
            boolean flag4 = false;
            if (!"nogui".equals(s3) && !"--nogui".equals(s3)) {
               if ("--port".equals(s3) && s4 != null) {
                  flag4 = true;

                  try {
                     i = Integer.parseInt(s4);
                  } catch (NumberFormatException var15) {
                  }
               } else if ("--singleplayer".equals(s3) && s4 != null) {
                  flag4 = true;
                  s = s4;
               } else if ("--universe".equals(s3) && s4 != null) {
                  flag4 = true;
                  s1 = s4;
               } else if ("--world".equals(s3) && s4 != null) {
                  flag4 = true;
                  s2 = s4;
               } else if ("--demo".equals(s3)) {
                  flag1 = true;
               } else if ("--bonusChest".equals(s3)) {
                  flag2 = true;
               } else if ("--forceUpgrade".equals(s3)) {
                  flag3 = true;
               }
            } else {
               flag = false;
            }

            if (flag4) {
               ++j;
            }
         }

         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(s1, USER_CACHE_FILE.getName()));
         final DedicatedServer dedicatedserver = new DedicatedServer(new File(s1), DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
         if (s != null) {
            dedicatedserver.setServerOwner(s);
         }

         if (s2 != null) {
            dedicatedserver.setFolderName(s2);
         }

         if (i >= 0) {
            dedicatedserver.setServerPort(i);
         }

         if (flag1) {
            dedicatedserver.setDemo(true);
         }

         if (flag2) {
            dedicatedserver.canCreateBonusChest(true);
         }

         if (flag && !GraphicsEnvironment.isHeadless()) {
            dedicatedserver.setGuiEnabled();
         }

         if (flag3) {
            dedicatedserver.setForceWorldUpgrade(true);
         }

         dedicatedserver.startServerThread();
         Thread thread = new Thread("Server Shutdown Thread") {
            public void run() {
               dedicatedserver.stopServer();
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(thread);
      } catch (Exception exception) {
         LOGGER.fatal("Failed to start the minecraft server", exception);
      }

   }

   protected void setForceWorldUpgrade(boolean p_212204_1_) {
      this.forceWorldUpgrade = p_212204_1_;
   }

   public void startServerThread() {
      this.serverThread = new Thread(this, "Server thread");
      this.serverThread.setUncaughtExceptionHandler((p_195574_0_, p_195574_1_) -> {
         LOGGER.error(p_195574_1_);
      });
      this.serverThread.start();
   }

   public File getFile(String p_71209_1_) {
      return new File(this.getDataDirectory(), p_71209_1_);
   }

   public void logInfo(String p_71244_1_) {
      LOGGER.info(p_71244_1_);
   }

   public void logWarning(String p_71236_1_) {
      LOGGER.warn(p_71236_1_);
   }

   public WorldServer func_71218_a(DimensionType p_71218_1_) {
      return this.worlds.get(p_71218_1_);
   }

   public Iterable<WorldServer> func_212370_w() {
      return this.worlds.values();
   }

   public String getMinecraftVersion() {
      return "1.13.2";
   }

   public int getCurrentPlayerCount() {
      return this.playerList.getCurrentPlayerCount();
   }

   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   public String[] getOnlinePlayerNames() {
      return this.playerList.getOnlinePlayerNames();
   }

   public boolean isDebuggingEnabled() {
      return false;
   }

   public void logSevere(String p_71201_1_) {
      LOGGER.error(p_71201_1_);
   }

   public void logDebug(String p_71198_1_) {
      if (this.isDebuggingEnabled()) {
         LOGGER.info(p_71198_1_);
      }

   }

   public String getServerModName() {
      return "vanilla";
   }

   public CrashReport addServerInfoToCrashReport(CrashReport p_71230_1_) {
      p_71230_1_.getCategory().addDetail("Profiler Position", () -> {
         return this.profiler.isProfiling() ? this.profiler.getNameOfLastSection() : "N/A (disabled)";
      });
      if (this.playerList != null) {
         p_71230_1_.getCategory().addDetail("Player Count", () -> {
            return this.playerList.getCurrentPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      p_71230_1_.getCategory().addDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

         for(ResourcePackInfo resourcepackinfo : this.resourcePacks.getPackInfos()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(resourcepackinfo.getName());
            if (!resourcepackinfo.func_195791_d().func_198968_a()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      return p_71230_1_;
   }

   public boolean isAnvilFileSet() {
      return this.anvilFile != null;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      LOGGER.info(p_145747_1_.getString());
   }

   public KeyPair getKeyPair() {
      return this.serverKeyPair;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public void setServerPort(int p_71208_1_) {
      this.serverPort = p_71208_1_;
   }

   public String getServerOwner() {
      return this.serverOwner;
   }

   public void setServerOwner(String p_71224_1_) {
      this.serverOwner = p_71224_1_;
   }

   public boolean isSinglePlayer() {
      return this.serverOwner != null;
   }

   public String getFolderName() {
      return this.folderName;
   }

   public void setFolderName(String p_71261_1_) {
      this.folderName = p_71261_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWorldName(String p_71246_1_) {
      this.worldName = p_71246_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getWorldName() {
      return this.worldName;
   }

   public void setKeyPair(KeyPair p_71253_1_) {
      this.serverKeyPair = p_71253_1_;
   }

   public void setDifficultyForAllWorlds(EnumDifficulty p_147139_1_) {
      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver.getWorldInfo().isHardcoreModeEnabled()) {
            worldserver.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
            worldserver.setAllowedSpawnTypes(true, true);
         } else if (this.isSinglePlayer()) {
            worldserver.getWorldInfo().setDifficulty(p_147139_1_);
            worldserver.setAllowedSpawnTypes(worldserver.getDifficulty() != EnumDifficulty.PEACEFUL, true);
         } else {
            worldserver.getWorldInfo().setDifficulty(p_147139_1_);
            worldserver.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
         }
      }

   }

   public boolean allowSpawnMonsters() {
      return true;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean p_71204_1_) {
      this.isDemo = p_71204_1_;
   }

   public void canCreateBonusChest(boolean p_71194_1_) {
      this.enableBonusChest = p_71194_1_;
   }

   public ISaveFormat getActiveAnvilConverter() {
      return this.anvilConverterForAnvilFile;
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String p_180507_1_, String p_180507_2_) {
      this.resourcePackUrl = p_180507_1_;
      this.resourcePackHash = p_180507_2_;
   }

   public void addServerStatsToSnooper(Snooper p_70000_1_) {
      p_70000_1_.addClientStat("whitelist_enabled", false);
      p_70000_1_.addClientStat("whitelist_count", 0);
      if (this.playerList != null) {
         p_70000_1_.addClientStat("players_current", this.getCurrentPlayerCount());
         p_70000_1_.addClientStat("players_max", this.getMaxPlayers());
         p_70000_1_.addClientStat("players_seen", this.playerList.getAvailablePlayerDat().length);
      }

      p_70000_1_.addClientStat("uses_auth", this.onlineMode);
      p_70000_1_.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
      p_70000_1_.addClientStat("run_time", (Util.milliTime() - p_70000_1_.getMinecraftStartTimeMillis()) / 60L * 1000L);
      p_70000_1_.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D));
      int i = 0;

      for(WorldServer worldserver : this.func_212370_w()) {
         if (worldserver != null) {
            WorldInfo worldinfo = worldserver.getWorldInfo();
            p_70000_1_.addClientStat("world[" + i + "][dimension]", worldserver.dimension.getType());
            p_70000_1_.addClientStat("world[" + i + "][mode]", worldinfo.getGameType());
            p_70000_1_.addClientStat("world[" + i + "][difficulty]", worldserver.getDifficulty());
            p_70000_1_.addClientStat("world[" + i + "][hardcore]", worldinfo.isHardcoreModeEnabled());
            p_70000_1_.addClientStat("world[" + i + "][generator_name]", worldinfo.getTerrainType().func_211888_a());
            p_70000_1_.addClientStat("world[" + i + "][generator_version]", worldinfo.getTerrainType().getVersion());
            p_70000_1_.addClientStat("world[" + i + "][height]", this.buildLimit);
            p_70000_1_.addClientStat("world[" + i + "][chunks_loaded]", worldserver.getChunkProvider().getLoadedChunkCount());
            ++i;
         }
      }

      p_70000_1_.addClientStat("worlds", i);
   }

   public boolean isSnooperEnabled() {
      return true;
   }

   public abstract boolean isDedicatedServer();

   public boolean isServerInOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean p_71229_1_) {
      this.onlineMode = p_71229_1_;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_190517_1_) {
      this.preventProxyConnections = p_190517_1_;
   }

   public boolean getCanSpawnAnimals() {
      return this.canSpawnAnimals;
   }

   public void setCanSpawnAnimals(boolean p_71251_1_) {
      this.canSpawnAnimals = p_71251_1_;
   }

   public boolean getCanSpawnNPCs() {
      return this.canSpawnNPCs;
   }

   public abstract boolean shouldUseNativeTransport();

   public void setCanSpawnNPCs(boolean p_71257_1_) {
      this.canSpawnNPCs = p_71257_1_;
   }

   public boolean isPVPEnabled() {
      return this.pvpEnabled;
   }

   public void setAllowPvp(boolean p_71188_1_) {
      this.pvpEnabled = p_71188_1_;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean p_71245_1_) {
      this.allowFlight = p_71245_1_;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMOTD() {
      return this.motd;
   }

   public void setMOTD(String p_71205_1_) {
      this.motd = p_71205_1_;
   }

   public int getBuildLimit() {
      return this.buildLimit;
   }

   public void setBuildLimit(int p_71191_1_) {
      this.buildLimit = p_71191_1_;
   }

   public boolean isServerStopped() {
      return this.serverStopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList p_184105_1_) {
      this.playerList = p_184105_1_;
   }

   public abstract boolean getPublic();

   public void setGameType(GameType p_71235_1_) {
      for(WorldServer worldserver : this.func_212370_w()) {
         worldserver.getWorldInfo().setGameType(p_71235_1_);
      }

   }

   public NetworkSystem getNetworkSystem() {
      return this.networkSystem;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean serverIsInRunLoop() {
      return this.serverIsRunning;
   }

   public boolean getGuiEnabled() {
      return false;
   }

   public abstract boolean func_195565_a(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_);

   public int getTickCounter() {
      return this.tickCounter;
   }

   public void enableProfiling() {
      this.startProfiling = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   public int getSpawnProtectionSize() {
      return 16;
   }

   public boolean isBlockProtected(World p_175579_1_, BlockPos p_175579_2_, EntityPlayer p_175579_3_) {
      return false;
   }

   public void setForceGamemode(boolean p_104055_1_) {
      this.isGamemodeForced = p_104055_1_;
   }

   public boolean getForceGamemode() {
      return this.isGamemodeForced;
   }

   public int getMaxPlayerIdleMinutes() {
      return this.maxPlayerIdleMinutes;
   }

   public void setPlayerIdleTimeout(int p_143006_1_) {
      this.maxPlayerIdleMinutes = p_143006_1_;
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.profileRepo;
   }

   public PlayerProfileCache getPlayerProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getServerStatusResponse() {
      return this.statusResponse;
   }

   public void refreshStatusNextTick() {
      this.nanoTimeSinceStatusRefresh = 0L;
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public <V> ListenableFuture<V> callFromMainThread(Callable<V> p_175586_1_) {
      Validate.notNull(p_175586_1_);
      if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
         ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(p_175586_1_);
         this.futureTaskQueue.add(listenablefuturetask);
         return listenablefuturetask;
      } else {
         try {
            return Futures.immediateFuture(p_175586_1_.call());
         } catch (Exception exception) {
            return Futures.immediateFailedCheckedFuture(exception);
         }
      }
   }

   public ListenableFuture<Object> addScheduledTask(Runnable p_152344_1_) {
      Validate.notNull(p_152344_1_);
      return this.callFromMainThread(Executors.callable(p_152344_1_));
   }

   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.serverThread;
   }

   public int getNetworkCompressionThreshold() {
      return 256;
   }

   public long func_211150_az() {
      return this.serverTime;
   }

   public Thread getServerThread() {
      return this.serverThread;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public int getSpawnRadius(@Nullable WorldServer p_184108_1_) {
      return p_184108_1_ != null ? p_184108_1_.getGameRules().getInt("spawnRadius") : 10;
   }

   public AdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public FunctionManager getFunctionManager() {
      return this.functionManager;
   }

   public void reload() {
      if (!this.isCallingFromMinecraftThread()) {
         this.addScheduledTask(this::reload);
      } else {
         this.getPlayerList().saveAllPlayerData();
          System.out.println("resourcePacks");
         this.resourcePacks.reloadPacksFromFinders();
          System.out.println("loadDataPacks");
         this.loadDataPacks(this.func_71218_a(DimensionType.OVERWORLD).getWorldInfo());
          System.out.println("Player reload");
         this.getPlayerList().reloadResources();
          System.out.println("Player finished");
      }
   }

   private void loadDataPacks(WorldInfo p_195568_1_) {
      List<ResourcePackInfo> list = Lists.newArrayList(this.resourcePacks.getPackInfos());

      for(ResourcePackInfo resourcepackinfo : this.resourcePacks.func_198978_b()) {
         if (!p_195568_1_.getDisabledDataPacks().contains(resourcepackinfo.getName()) && !list.contains(resourcepackinfo)) {
            LOGGER.info("Found new data pack {}, loading it automatically", resourcepackinfo.getName());
            resourcepackinfo.getPriority().func_198993_a(list, resourcepackinfo, (p_200247_0_) -> {
               return p_200247_0_;
            }, false);
         }
      }

      this.resourcePacks.func_198985_a(list);
      List<IResourcePack> list1 = Lists.newArrayList();
      this.resourcePacks.getPackInfos().forEach((p_200244_1_) -> {
         list1.add(p_200244_1_.getResourcePack());
      });
      this.resourceManager.reload(list1);
      p_195568_1_.getEnabledDataPacks().clear();
      p_195568_1_.getDisabledDataPacks().clear();
      this.resourcePacks.getPackInfos().forEach((p_195562_1_) -> {
         p_195568_1_.getEnabledDataPacks().add(p_195562_1_.getName());
      });
      this.resourcePacks.func_198978_b().forEach((p_200248_2_) -> {
         if (!this.resourcePacks.getPackInfos().contains(p_200248_2_)) {
            p_195568_1_.getDisabledDataPacks().add(p_200248_2_.getName());
         }

      });
   }

   public void kickPlayersNotWhitelisted(CommandSource p_205743_1_) {
      if (this.isWhitelistEnabled()) {
         PlayerList playerlist = p_205743_1_.getServer().getPlayerList();
         UserListWhitelist userlistwhitelist = playerlist.getWhitelistedPlayers();
         if (userlistwhitelist.isLanServer()) {
            for(EntityPlayerMP entityplayermp : Lists.newArrayList(playerlist.getPlayers())) {
               if (!userlistwhitelist.isWhitelisted(entityplayermp.getGameProfile())) {
                  entityplayermp.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.not_whitelisted"));
               }
            }

         }
      }
   }

   public IReloadableResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ResourcePackInfo> getResourcePacks() {
      return this.resourcePacks;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getCurrentTask() {
      return this.currentTask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getPercentDone() {
      return this.percentDone;
   }

   public Commands getCommandManager() {
      return this.commandManager;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, this.func_71218_a(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(this.func_71218_a(DimensionType.OVERWORLD).getSpawnPoint()), Vec2f.ZERO, this.func_71218_a(DimensionType.OVERWORLD), 4, "Server", new TextComponentString("Server"), this, null);
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public NetworkTagManager getNetworkTagManager() {
      return this.networkTagManager;
   }

   public ServerScoreboard getWorldScoreboard() {
      return this.scoreboard;
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public GameRules getGameRules() {
      return this.func_71218_a(DimensionType.OVERWORLD).getGameRules();
   }

   public CustomBossEvents getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void setWhitelistEnabled(boolean p_205741_1_) {
      this.whitelistEnabled = p_205741_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getTickTime() {
      return this.tickTime;
   }

   public int getPermissionLevel(GameProfile p_211833_1_) {
      if (this.getPlayerList().canSendCommands(p_211833_1_)) {
         UserListOpsEntry userlistopsentry = this.getPlayerList().getOppedPlayers().getEntry(p_211833_1_);
         if (userlistopsentry != null) {
            return userlistopsentry.getPermissionLevel();
         } else if (this.isSinglePlayer()) {
            if (this.getServerOwner().equals(p_211833_1_.getName())) {
               return 4;
            } else {
               return this.getPlayerList().commandsAllowedForAll() ? 4 : 0;
            }
         } else {
            return this.getOpPermissionLevel();
         }
      } else {
         return 0;
      }
   }
}
