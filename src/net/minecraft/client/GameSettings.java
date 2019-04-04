package net.minecraft.client;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.datafixers.DataFixTypes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Type TYPE_LIST_STRING = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   public static final Splitter COLON_SPLITTER = Splitter.on(':');
   private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] CLOUDS_TYPES = new String[]{"options.off", "options.clouds.fast", "options.clouds.fancy"};
   private static final String[] ATTACK_INDICATORS = new String[]{"options.off", "options.attack.crosshair", "options.attack.hotbar"};
   public static final String[] NARRATOR_MODES = new String[]{"options.narrator.off", "options.narrator.all", "options.narrator.chat", "options.narrator.system"};
   public double mouseSensitivity = 0.5D;
   public boolean invertMouse;
   public int renderDistanceChunks = -1;
   public boolean viewBobbing = true;
   public boolean fboEnable = true;
   public int limitFramerate = 120;
   public int clouds = 2;
   public boolean fancyGraphics = true;
   public int ambientOcclusion = 2;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
   public boolean chatColours = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public double chatOpacity = 1.0D;
   public boolean snooperEnabled = true;
   public boolean fullScreen;
   @Nullable
   public String fullscreenResolution;
   public boolean enableVsync = true;
   public boolean useVbo = true;
   public boolean reducedDebugInfo;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
   public boolean touchscreen;
   public EnumHandSide mainHand = EnumHandSide.RIGHT;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public double chatScale = 1.0D;
   public double chatWidth = 1.0D;
   public double chatHeightUnfocused = (double)0.44366196F;
   public double chatHeightFocused = 1.0D;
   public int mipmapLevels = 4;
   private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
   public boolean useNativeTransport = true;
   public boolean entityShadows = true;
   public int attackIndicator = 1;
   public boolean enableWeakAttacks;
   public boolean showSubtitles;
   public boolean realmsNotifications = true;
   public boolean autoJump = true;
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public boolean autoSuggestions = true;
   public int biomeBlendRadius = 2;
   public double mouseWheelSensitivity = 1.0D;
   public int glDebugVerbosity = 1;
   public KeyBinding keyBindForward = new KeyBinding("key.forward", 87, "key.categories.movement");
   public KeyBinding keyBindLeft = new KeyBinding("key.left", 65, "key.categories.movement");
   public KeyBinding keyBindBack = new KeyBinding("key.back", 83, "key.categories.movement");
   public KeyBinding keyBindRight = new KeyBinding("key.right", 68, "key.categories.movement");
   public KeyBinding keyBindJump = new KeyBinding("key.jump", 32, "key.categories.movement");
   public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 340, "key.categories.movement");
   public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 341, "key.categories.movement");
   public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
   public KeyBinding keyBindSwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
   public KeyBinding keyBindDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
   public KeyBinding keyBindUseItem = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
   public KeyBinding keyBindAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
   public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
   public KeyBinding keyBindChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
   public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
   public KeyBinding keyBindCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
   public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
   public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
   public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", -1, "key.categories.misc");
   public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
   public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", -1, "key.categories.misc");
   public KeyBinding keyBindAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
   public KeyBinding[] keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
   public KeyBinding keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
   public KeyBinding keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
   public KeyBinding[] keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements}, this.keyBindsHotbar);
   protected Minecraft mc;
   private File optionsFile;
   public EnumDifficulty difficulty = EnumDifficulty.NORMAL;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public String lastServer = "";
   public boolean smoothCamera;
   public boolean debugCamEnable;
   public double fovSetting = 70.0D;
   public double gammaSetting;
   public float saturation;
   public int guiScale;
   public int particleSetting;
   public int narrator;
   public String language = "en_us";
   public boolean forceUnicodeFont;

   public GameSettings(Minecraft p_i46326_1_, File p_i46326_2_) {
      this.mc = p_i46326_1_;
      this.optionsFile = new File(p_i46326_2_, "options.txt");
      if (p_i46326_1_.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
      } else {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
      }

      this.renderDistanceChunks = p_i46326_1_.isJava64bit() ? 12 : 8;
      this.loadOptions();
   }

   public GameSettings() {
   }

   public void func_198014_a(KeyBinding p_198014_1_, InputMappings.Input p_198014_2_) {
      p_198014_1_.bind(p_198014_2_);
      this.saveOptions();
   }

   public void setOptionFloatValue(GameSettings.Options p_198016_1_, double p_198016_2_) {
      if (p_198016_1_ == GameSettings.Options.SENSITIVITY) {
         this.mouseSensitivity = p_198016_2_;
      }

      if (p_198016_1_ == GameSettings.Options.FOV) {
         this.fovSetting = p_198016_2_;
      }

      if (p_198016_1_ == GameSettings.Options.GAMMA) {
         this.gammaSetting = p_198016_2_;
      }

      if (p_198016_1_ == GameSettings.Options.FRAMERATE_LIMIT) {
         this.limitFramerate = (int)p_198016_2_;
      }

      if (p_198016_1_ == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = p_198016_2_;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (p_198016_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.chatHeightFocused = p_198016_2_;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (p_198016_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.chatHeightUnfocused = p_198016_2_;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (p_198016_1_ == GameSettings.Options.CHAT_WIDTH) {
         this.chatWidth = p_198016_2_;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (p_198016_1_ == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = p_198016_2_;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (p_198016_1_ == GameSettings.Options.MIPMAP_LEVELS) {
         int i = this.mipmapLevels;
         this.mipmapLevels = (int)p_198016_2_;
         if ((double)i != p_198016_2_) {
            this.mc.getTextureMap().setMipmapLevels(this.mipmapLevels);
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.mc.getTextureMap().setBlurMipmapDirect(false, this.mipmapLevels > 0);
            this.mc.scheduleResourcesRefresh();
         }
      }

      if (p_198016_1_ == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)p_198016_2_;
         this.mc.renderGlobal.func_174979_m();
      }

      if (p_198016_1_ == GameSettings.Options.BIOME_BLEND_RADIUS) {
         this.biomeBlendRadius = MathHelper.clamp((int)p_198016_2_, 0, 7);
         this.mc.renderGlobal.func_72712_a();
      }

      if (p_198016_1_ == GameSettings.Options.FULLSCREEN_RESOLUTION) {
         this.mc.mainWindow.setFullscreenResolution((int)p_198016_2_);
      }

      if (p_198016_1_ == GameSettings.Options.MOUSE_WHEEL_SENSITIVITY) {
         this.mouseWheelSensitivity = p_198016_2_;
      }

   }

   public void setOptionValue(GameSettings.Options p_74306_1_, int p_74306_2_) {
      if (p_74306_1_ == GameSettings.Options.RENDER_DISTANCE) {
         this.setOptionFloatValue(p_74306_1_, MathHelper.clamp((double)(this.renderDistanceChunks + p_74306_2_), p_74306_1_.getValueMin(), p_74306_1_.getValueMax()));
      }

      if (p_74306_1_ == GameSettings.Options.MAIN_HAND) {
         this.mainHand = this.mainHand.opposite();
      }

      if (p_74306_1_ == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if (p_74306_1_ == GameSettings.Options.GUI_SCALE) {
         this.guiScale = Integer.remainderUnsigned(this.guiScale + p_74306_2_, this.mc.mainWindow.getScaleFactor(0) + 1);
      }

      if (p_74306_1_ == GameSettings.Options.PARTICLES) {
         this.particleSetting = (this.particleSetting + p_74306_2_) % 3;
      }

      if (p_74306_1_ == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (p_74306_1_ == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + p_74306_2_) % 3;
      }

      if (p_74306_1_ == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.mc.getFontResourceManager().setForceUnicodeFont(this.forceUnicodeFont);
      }

      if (p_74306_1_ == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (p_74306_1_ == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.mc.renderGlobal.func_72712_a();
      }

      if (p_74306_1_ == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + p_74306_2_) % 3;
         this.mc.renderGlobal.func_72712_a();
      }

      if (p_74306_1_ == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + p_74306_2_) % 3);
      }

      if (p_74306_1_ == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if (p_74306_1_ == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (p_74306_1_ == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if (p_74306_1_ == GameSettings.Options.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if (p_74306_1_ == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (p_74306_1_ == GameSettings.Options.USE_FULLSCREEN) {
         this.fullScreen = !this.fullScreen;
         if (this.mc.mainWindow.isFullscreen() != this.fullScreen) {
            this.mc.mainWindow.toggleFullscreen();
         }
      }

      if (p_74306_1_ == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         this.mc.mainWindow.updateVsyncFromGameSettings();
      }

      if (p_74306_1_ == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.mc.renderGlobal.func_72712_a();
      }

      if (p_74306_1_ == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if (p_74306_1_ == GameSettings.Options.ENTITY_SHADOWS) {
         this.entityShadows = !this.entityShadows;
      }

      if (p_74306_1_ == GameSettings.Options.ATTACK_INDICATOR) {
         this.attackIndicator = (this.attackIndicator + p_74306_2_) % 3;
      }

      if (p_74306_1_ == GameSettings.Options.SHOW_SUBTITLES) {
         this.showSubtitles = !this.showSubtitles;
      }

      if (p_74306_1_ == GameSettings.Options.REALMS_NOTIFICATIONS) {
         this.realmsNotifications = !this.realmsNotifications;
      }

      if (p_74306_1_ == GameSettings.Options.AUTO_JUMP) {
         this.autoJump = !this.autoJump;
      }

      if (p_74306_1_ == GameSettings.Options.AUTO_SUGGESTIONS) {
         this.autoSuggestions = !this.autoSuggestions;
      }

      if (p_74306_1_ == GameSettings.Options.NARRATOR) {
         if (NarratorChatListener.INSTANCE.isActive()) {
            this.narrator = (this.narrator + p_74306_2_) % NARRATOR_MODES.length;
         } else {
            this.narrator = 0;
         }

         NarratorChatListener.INSTANCE.announceMode(this.narrator);
      }

      this.saveOptions();
   }

   public double getOptionFloatValue(GameSettings.Options p_198015_1_) {
      if (p_198015_1_ == GameSettings.Options.BIOME_BLEND_RADIUS) {
         return (double)this.biomeBlendRadius;
      } else if (p_198015_1_ == GameSettings.Options.FOV) {
         return this.fovSetting;
      } else if (p_198015_1_ == GameSettings.Options.GAMMA) {
         return this.gammaSetting;
      } else if (p_198015_1_ == GameSettings.Options.SATURATION) {
         return (double)this.saturation;
      } else if (p_198015_1_ == GameSettings.Options.SENSITIVITY) {
         return this.mouseSensitivity;
      } else if (p_198015_1_ == GameSettings.Options.CHAT_OPACITY) {
         return this.chatOpacity;
      } else if (p_198015_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         return this.chatHeightFocused;
      } else if (p_198015_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         return this.chatHeightUnfocused;
      } else if (p_198015_1_ == GameSettings.Options.CHAT_SCALE) {
         return this.chatScale;
      } else if (p_198015_1_ == GameSettings.Options.CHAT_WIDTH) {
         return this.chatWidth;
      } else if (p_198015_1_ == GameSettings.Options.FRAMERATE_LIMIT) {
         return (double)this.limitFramerate;
      } else if (p_198015_1_ == GameSettings.Options.MIPMAP_LEVELS) {
         return (double)this.mipmapLevels;
      } else if (p_198015_1_ == GameSettings.Options.RENDER_DISTANCE) {
         return (double)this.renderDistanceChunks;
      } else if (p_198015_1_ == GameSettings.Options.FULLSCREEN_RESOLUTION) {
         return (double)this.mc.mainWindow.getVideoModeIndex();
      } else {
         return p_198015_1_ == GameSettings.Options.MOUSE_WHEEL_SENSITIVITY ? this.mouseWheelSensitivity : 0.0D;
      }
   }

   public boolean getOptionOrdinalValue(GameSettings.Options p_74308_1_) {
      switch(p_74308_1_) {
      case INVERT_MOUSE:
         return this.invertMouse;
      case VIEW_BOBBING:
         return this.viewBobbing;
      case FBO_ENABLE:
         return this.fboEnable;
      case CHAT_COLOR:
         return this.chatColours;
      case CHAT_LINKS:
         return this.chatLinks;
      case CHAT_LINKS_PROMPT:
         return this.chatLinksPrompt;
      case SNOOPER_ENABLED:
         if (this.snooperEnabled) {
         }

         return false;
      case USE_FULLSCREEN:
         return this.fullScreen;
      case ENABLE_VSYNC:
         return this.enableVsync;
      case USE_VBO:
         return this.useVbo;
      case TOUCHSCREEN:
         return this.touchscreen;
      case FORCE_UNICODE_FONT:
         return this.forceUnicodeFont;
      case REDUCED_DEBUG_INFO:
         return this.reducedDebugInfo;
      case ENTITY_SHADOWS:
         return this.entityShadows;
      case SHOW_SUBTITLES:
         return this.showSubtitles;
      case REALMS_NOTIFICATIONS:
         return this.realmsNotifications;
      case ENABLE_WEAK_ATTACKS:
         return this.enableWeakAttacks;
      case AUTO_JUMP:
         return this.autoJump;
      case AUTO_SUGGESTIONS:
         return this.autoSuggestions;
      default:
         return false;
      }
   }

   private static String getTranslation(String[] p_74299_0_, int p_74299_1_) {
      if (p_74299_1_ < 0 || p_74299_1_ >= p_74299_0_.length) {
         p_74299_1_ = 0;
      }

      return I18n.format(p_74299_0_[p_74299_1_]);
   }

   public String getKeyBinding(GameSettings.Options p_74297_1_) {
      String s = I18n.format(p_74297_1_.getTranslation()) + ": ";
      if (p_74297_1_.isFloat()) {
         double d1 = this.getOptionFloatValue(p_74297_1_);
         double d0 = p_74297_1_.func_198008_a(d1);
         if (p_74297_1_ == GameSettings.Options.SENSITIVITY) {
            if (d0 == 0.0D) {
               return s + I18n.format("options.sensitivity.min");
            } else {
               return d0 == 1.0D ? s + I18n.format("options.sensitivity.max") : s + (int)(d0 * 200.0D) + "%";
            }
         } else if (p_74297_1_ == GameSettings.Options.BIOME_BLEND_RADIUS) {
            if (d0 == 0.0D) {
               return s + I18n.format("options.off");
            } else {
               int i = this.biomeBlendRadius * 2 + 1;
               return s + i + "x" + i;
            }
         } else if (p_74297_1_ == GameSettings.Options.FOV) {
            if (d1 == 70.0D) {
               return s + I18n.format("options.fov.min");
            } else {
               return d1 == 110.0D ? s + I18n.format("options.fov.max") : s + (int)d1;
            }
         } else if (p_74297_1_ == GameSettings.Options.FRAMERATE_LIMIT) {
            return d1 == p_74297_1_.valueMax ? s + I18n.format("options.framerateLimit.max") : s + I18n.format("options.framerate", (int)d1);
         } else if (p_74297_1_ == GameSettings.Options.RENDER_CLOUDS) {
            return d1 == p_74297_1_.valueMin ? s + I18n.format("options.cloudHeight.min") : s + ((int)d1 + 128);
         } else if (p_74297_1_ == GameSettings.Options.GAMMA) {
            if (d0 == 0.0D) {
               return s + I18n.format("options.gamma.min");
            } else {
               return d0 == 1.0D ? s + I18n.format("options.gamma.max") : s + "+" + (int)(d0 * 100.0D) + "%";
            }
         } else if (p_74297_1_ == GameSettings.Options.SATURATION) {
            return s + (int)(d0 * 400.0D) + "%";
         } else if (p_74297_1_ == GameSettings.Options.CHAT_OPACITY) {
            return s + (int)(d0 * 90.0D + 10.0D) + "%";
         } else if (p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
            return s + GuiNewChat.func_194816_c(d0) + "px";
         } else if (p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
            return s + GuiNewChat.func_194816_c(d0) + "px";
         } else if (p_74297_1_ == GameSettings.Options.CHAT_WIDTH) {
            return s + GuiNewChat.func_194814_b(d0) + "px";
         } else if (p_74297_1_ == GameSettings.Options.RENDER_DISTANCE) {
            return s + I18n.format("options.chunks", (int)d1);
         } else if (p_74297_1_ == GameSettings.Options.MOUSE_WHEEL_SENSITIVITY) {
            return d0 == 1.0D ? s + I18n.format("options.mouseWheelSensitivity.default") : s + "+" + (int)d0 + "." + (int)(d0 * 10.0D) % 10;
         } else if (p_74297_1_ == GameSettings.Options.MIPMAP_LEVELS) {
            return d1 == 0.0D ? s + I18n.format("options.off") : s + (int)d1;
         } else if (p_74297_1_ == GameSettings.Options.FULLSCREEN_RESOLUTION) {
            return d1 == 0.0D ? s + I18n.format("options.fullscreen.current") : s + this.mc.mainWindow.getVideoModeString((int)d1 - 1);
         } else {
            return d0 == 0.0D ? s + I18n.format("options.off") : s + (int)(d0 * 100.0D) + "%";
         }
      } else if (p_74297_1_.isBoolean()) {
         boolean flag = this.getOptionOrdinalValue(p_74297_1_);
         return flag ? s + I18n.format("options.on") : s + I18n.format("options.off");
      } else if (p_74297_1_ == GameSettings.Options.MAIN_HAND) {
         return s + this.mainHand;
      } else if (p_74297_1_ == GameSettings.Options.GUI_SCALE) {
         return s + (this.guiScale == 0 ? I18n.format("options.guiScale.auto") : this.guiScale);
      } else if (p_74297_1_ == GameSettings.Options.CHAT_VISIBILITY) {
         return s + I18n.format(this.chatVisibility.getResourceKey());
      } else if (p_74297_1_ == GameSettings.Options.PARTICLES) {
         return s + getTranslation(PARTICLES, this.particleSetting);
      } else if (p_74297_1_ == GameSettings.Options.AMBIENT_OCCLUSION) {
         return s + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
      } else if (p_74297_1_ == GameSettings.Options.RENDER_CLOUDS) {
         return s + getTranslation(CLOUDS_TYPES, this.clouds);
      } else if (p_74297_1_ == GameSettings.Options.GRAPHICS) {
         if (this.fancyGraphics) {
            return s + I18n.format("options.graphics.fancy");
         } else {
            String s1 = "options.graphics.fast";
            return s + I18n.format("options.graphics.fast");
         }
      } else if (p_74297_1_ == GameSettings.Options.ATTACK_INDICATOR) {
         return s + getTranslation(ATTACK_INDICATORS, this.attackIndicator);
      } else if (p_74297_1_ == GameSettings.Options.NARRATOR) {
         return NarratorChatListener.INSTANCE.isActive() ? s + getTranslation(NARRATOR_MODES, this.narrator) : s + I18n.format("options.narrator.notavailable");
      } else {
         return s;
      }
   }

   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.soundLevels.clear();
         List<String> list = IOUtils.readLines(new FileInputStream(this.optionsFile));
         NBTTagCompound nbttagcompound = new NBTTagCompound();

         for(String s : list) {
            try {
               Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
               nbttagcompound.setString(iterator.next(), iterator.next());
            } catch (Exception var10) {
               LOGGER.warn("Skipping bad option: {}", s);
            }
         }

         nbttagcompound = this.dataFix(nbttagcompound);

         for(String s1 : nbttagcompound.getKeySet()) {
            String s2 = nbttagcompound.getString(s1);

            try {
               if ("mouseSensitivity".equals(s1)) {
                  this.mouseSensitivity = (double)this.parseFloat(s2);
               }

               if ("fov".equals(s1)) {
                  this.fovSetting = (double)(this.parseFloat(s2) * 40.0F + 70.0F);
               }

               if ("gamma".equals(s1)) {
                  this.gammaSetting = (double)this.parseFloat(s2);
               }

               if ("saturation".equals(s1)) {
                  this.saturation = this.parseFloat(s2);
               }

               if ("invertYMouse".equals(s1)) {
                  this.invertMouse = "true".equals(s2);
               }

               if ("renderDistance".equals(s1)) {
                  this.renderDistanceChunks = Integer.parseInt(s2);
               }

               if ("guiScale".equals(s1)) {
                  this.guiScale = Integer.parseInt(s2);
               }

               if ("particles".equals(s1)) {
                  this.particleSetting = Integer.parseInt(s2);
               }

               if ("bobView".equals(s1)) {
                  this.viewBobbing = "true".equals(s2);
               }

               if ("maxFps".equals(s1)) {
                  this.limitFramerate = Integer.parseInt(s2);
               }

               if ("fboEnable".equals(s1)) {
                  this.fboEnable = "true".equals(s2);
               }

               if ("difficulty".equals(s1)) {
                  this.difficulty = EnumDifficulty.byId(Integer.parseInt(s2));
               }

               if ("fancyGraphics".equals(s1)) {
                  this.fancyGraphics = "true".equals(s2);
               }

               if ("tutorialStep".equals(s1)) {
                  this.tutorialStep = TutorialSteps.byName(s2);
               }

               if ("ao".equals(s1)) {
                  if ("true".equals(s2)) {
                     this.ambientOcclusion = 2;
                  } else if ("false".equals(s2)) {
                     this.ambientOcclusion = 0;
                  } else {
                     this.ambientOcclusion = Integer.parseInt(s2);
                  }
               }

               if ("renderClouds".equals(s1)) {
                  if ("true".equals(s2)) {
                     this.clouds = 2;
                  } else if ("false".equals(s2)) {
                     this.clouds = 0;
                  } else if ("fast".equals(s2)) {
                     this.clouds = 1;
                  }
               }

               if ("attackIndicator".equals(s1)) {
                  if ("0".equals(s2)) {
                     this.attackIndicator = 0;
                  } else if ("1".equals(s2)) {
                     this.attackIndicator = 1;
                  } else if ("2".equals(s2)) {
                     this.attackIndicator = 2;
                  }
               }

               if ("resourcePacks".equals(s1)) {
                  this.resourcePacks = JsonUtils.fromJson(GSON, s2, TYPE_LIST_STRING);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(s1)) {
                  this.incompatibleResourcePacks = JsonUtils.fromJson(GSON, s2, TYPE_LIST_STRING);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(s1)) {
                  this.lastServer = s2;
               }

               if ("lang".equals(s1)) {
                  this.language = s2;
               }

               if ("chatVisibility".equals(s1)) {
                  this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(s2));
               }

               if ("chatColors".equals(s1)) {
                  this.chatColours = "true".equals(s2);
               }

               if ("chatLinks".equals(s1)) {
                  this.chatLinks = "true".equals(s2);
               }

               if ("chatLinksPrompt".equals(s1)) {
                  this.chatLinksPrompt = "true".equals(s2);
               }

               if ("chatOpacity".equals(s1)) {
                  this.chatOpacity = (double)this.parseFloat(s2);
               }

               if ("snooperEnabled".equals(s1)) {
                  this.snooperEnabled = "true".equals(s2);
               }

               if ("fullscreen".equals(s1)) {
                  this.fullScreen = "true".equals(s2);
               }

               if ("fullscreenResolution".equals(s1)) {
                  this.fullscreenResolution = s2;
               }

               if ("enableVsync".equals(s1)) {
                  this.enableVsync = "true".equals(s2);
               }

               if ("useVbo".equals(s1)) {
                  this.useVbo = "true".equals(s2);
               }

               if ("hideServerAddress".equals(s1)) {
                  this.hideServerAddress = "true".equals(s2);
               }

               if ("advancedItemTooltips".equals(s1)) {
                  this.advancedItemTooltips = "true".equals(s2);
               }

               if ("pauseOnLostFocus".equals(s1)) {
                  this.pauseOnLostFocus = "true".equals(s2);
               }

               if ("touchscreen".equals(s1)) {
                  this.touchscreen = "true".equals(s2);
               }

               if ("overrideHeight".equals(s1)) {
                  this.overrideHeight = Integer.parseInt(s2);
               }

               if ("overrideWidth".equals(s1)) {
                  this.overrideWidth = Integer.parseInt(s2);
               }

               if ("heldItemTooltips".equals(s1)) {
                  this.heldItemTooltips = "true".equals(s2);
               }

               if ("chatHeightFocused".equals(s1)) {
                  this.chatHeightFocused = (double)this.parseFloat(s2);
               }

               if ("chatHeightUnfocused".equals(s1)) {
                  this.chatHeightUnfocused = (double)this.parseFloat(s2);
               }

               if ("chatScale".equals(s1)) {
                  this.chatScale = (double)this.parseFloat(s2);
               }

               if ("chatWidth".equals(s1)) {
                  this.chatWidth = (double)this.parseFloat(s2);
               }

               if ("mipmapLevels".equals(s1)) {
                  this.mipmapLevels = Integer.parseInt(s2);
               }

               if ("forceUnicodeFont".equals(s1)) {
                  this.forceUnicodeFont = "true".equals(s2);
               }

               if ("reducedDebugInfo".equals(s1)) {
                  this.reducedDebugInfo = "true".equals(s2);
               }

               if ("useNativeTransport".equals(s1)) {
                  this.useNativeTransport = "true".equals(s2);
               }

               if ("entityShadows".equals(s1)) {
                  this.entityShadows = "true".equals(s2);
               }

               if ("mainHand".equals(s1)) {
                  this.mainHand = "left".equals(s2) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
               }

               if ("showSubtitles".equals(s1)) {
                  this.showSubtitles = "true".equals(s2);
               }

               if ("realmsNotifications".equals(s1)) {
                  this.realmsNotifications = "true".equals(s2);
               }

               if ("enableWeakAttacks".equals(s1)) {
                  this.enableWeakAttacks = "true".equals(s2);
               }

               if ("autoJump".equals(s1)) {
                  this.autoJump = "true".equals(s2);
               }

               if ("narrator".equals(s1)) {
                  this.narrator = Integer.parseInt(s2);
               }

               if ("autoSuggestions".equals(s1)) {
                  this.autoSuggestions = "true".equals(s2);
               }

               if ("biomeBlendRadius".equals(s1)) {
                  this.biomeBlendRadius = Integer.parseInt(s2);
               }

               if ("mouseWheelSensitivity".equals(s1)) {
                  this.mouseWheelSensitivity = (double)this.parseFloat(s2);
               }

               if ("glDebugVerbosity".equals(s1)) {
                  this.glDebugVerbosity = Integer.parseInt(s2);
               }

               for(KeyBinding keybinding : this.keyBindings) {
                  if (s1.equals("key_" + keybinding.getKeyDescription())) {
                     keybinding.bind(InputMappings.getInputByName(s2));
                  }
               }

               for(SoundCategory soundcategory : SoundCategory.values()) {
                  if (s1.equals("soundCategory_" + soundcategory.getName())) {
                     this.soundLevels.put(soundcategory, this.parseFloat(s2));
                  }
               }

               for(EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
                  if (s1.equals("modelPart_" + enumplayermodelparts.getPartName())) {
                     this.setModelPartEnabled(enumplayermodelparts, "true".equals(s2));
                  }
               }
            } catch (Exception var11) {
               LOGGER.warn("Skipping bad option: {}:{}", s1, s2);
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } catch (Exception exception) {
         LOGGER.error("Failed to load options", exception);
      }

   }

   private NBTTagCompound dataFix(NBTTagCompound p_189988_1_) {
      int i = 0;

      try {
         i = Integer.parseInt(p_189988_1_.getString("version"));
      } catch (RuntimeException var4) {
      }

      return NBTUtil.func_210822_a(this.mc.getDataFixer(), DataFixTypes.OPTIONS, p_189988_1_, i);
   }

   private float parseFloat(String p_74305_1_) {
      if ("true".equals(p_74305_1_)) {
         return 1.0F;
      } else {
         return "false".equals(p_74305_1_) ? 0.0F : Float.parseFloat(p_74305_1_);
      }
   }

   public void saveOptions() {
      PrintWriter printwriter = null;

      try {
         printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
         printwriter.println("version:1631");
         printwriter.println("invertYMouse:" + this.invertMouse);
         printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printwriter.println("fov:" + (this.fovSetting - 70.0D) / 40.0D);
         printwriter.println("gamma:" + this.gammaSetting);
         printwriter.println("saturation:" + this.saturation);
         printwriter.println("renderDistance:" + this.renderDistanceChunks);
         printwriter.println("guiScale:" + this.guiScale);
         printwriter.println("particles:" + this.particleSetting);
         printwriter.println("bobView:" + this.viewBobbing);
         printwriter.println("maxFps:" + this.limitFramerate);
         printwriter.println("fboEnable:" + this.fboEnable);
         printwriter.println("difficulty:" + this.difficulty.getId());
         printwriter.println("fancyGraphics:" + this.fancyGraphics);
         printwriter.println("ao:" + this.ambientOcclusion);
         printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
         switch(this.clouds) {
         case 0:
            printwriter.println("renderClouds:false");
            break;
         case 1:
            printwriter.println("renderClouds:fast");
            break;
         case 2:
            printwriter.println("renderClouds:true");
         }

         printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
         printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
         printwriter.println("lastServer:" + this.lastServer);
         printwriter.println("lang:" + this.language);
         printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         printwriter.println("chatColors:" + this.chatColours);
         printwriter.println("chatLinks:" + this.chatLinks);
         printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
         printwriter.println("chatOpacity:" + this.chatOpacity);
         printwriter.println("snooperEnabled:" + this.snooperEnabled);
         printwriter.println("fullscreen:" + this.fullScreen);
         if (this.mc.mainWindow.getVideoMode().isPresent()) {
            printwriter.println("fullscreenResolution:" + this.mc.mainWindow.getVideoMode().get().getSettingsString());
         }

         printwriter.println("enableVsync:" + this.enableVsync);
         printwriter.println("useVbo:" + this.useVbo);
         printwriter.println("hideServerAddress:" + this.hideServerAddress);
         printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printwriter.println("touchscreen:" + this.touchscreen);
         printwriter.println("overrideWidth:" + this.overrideWidth);
         printwriter.println("overrideHeight:" + this.overrideHeight);
         printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printwriter.println("chatScale:" + this.chatScale);
         printwriter.println("chatWidth:" + this.chatWidth);
         printwriter.println("mipmapLevels:" + this.mipmapLevels);
         printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
         printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
         printwriter.println("useNativeTransport:" + this.useNativeTransport);
         printwriter.println("entityShadows:" + this.entityShadows);
         printwriter.println("mainHand:" + (this.mainHand == EnumHandSide.LEFT ? "left" : "right"));
         printwriter.println("attackIndicator:" + this.attackIndicator);
         printwriter.println("showSubtitles:" + this.showSubtitles);
         printwriter.println("realmsNotifications:" + this.realmsNotifications);
         printwriter.println("enableWeakAttacks:" + this.enableWeakAttacks);
         printwriter.println("autoJump:" + this.autoJump);
         printwriter.println("narrator:" + this.narrator);
         printwriter.println("tutorialStep:" + this.tutorialStep.getName());
         printwriter.println("autoSuggestions:" + this.autoSuggestions);
         printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
         printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);

         for(KeyBinding keybinding : this.keyBindings) {
            printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey());
         }

         for(SoundCategory soundcategory : SoundCategory.values()) {
            printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
         }

         for(EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
            printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to save options", exception);
      } finally {
         IOUtils.closeQuietly(printwriter);
      }

      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory p_186711_1_) {
      return this.soundLevels.containsKey(p_186711_1_) ? this.soundLevels.get(p_186711_1_) : 1.0F;
   }

   public void setSoundLevel(SoundCategory p_186712_1_, float p_186712_2_) {
      this.mc.getSoundHandler().setSoundLevel(p_186712_1_, p_186712_2_);
      this.soundLevels.put(p_186712_1_, p_186712_2_);
   }

   public void sendSettingsToServer() {
      if (this.mc.player != null) {
         int i = 0;

         for(EnumPlayerModelParts enumplayermodelparts : this.setModelParts) {
            i |= enumplayermodelparts.getPartMask();
         }

         this.mc.player.connection.sendPacket(new CPacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i, this.mainHand));
      }

   }

   public Set<EnumPlayerModelParts> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(EnumPlayerModelParts p_178878_1_, boolean p_178878_2_) {
      if (p_178878_2_) {
         this.setModelParts.add(p_178878_1_);
      } else {
         this.setModelParts.remove(p_178878_1_);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(EnumPlayerModelParts p_178877_1_) {
      if (this.getModelParts().contains(p_178877_1_)) {
         this.setModelParts.remove(p_178877_1_);
      } else {
         this.setModelParts.add(p_178877_1_);
      }

      this.sendSettingsToServer();
   }

   public int shouldRenderClouds() {
      return this.renderDistanceChunks >= 4 ? this.clouds : 0;
   }

   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   public void fillResourcePackList(ResourcePackList<ResourcePackInfoClient> p_198017_1_) {
      p_198017_1_.reloadPacksFromFinders();
      Set<ResourcePackInfoClient> set = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String s = iterator.next();
         ResourcePackInfoClient resourcepackinfoclient = p_198017_1_.getPackInfo(s);
         if (resourcepackinfoclient == null && !s.startsWith("file/")) {
            resourcepackinfoclient = p_198017_1_.getPackInfo("file/" + s);
         }

         if (resourcepackinfoclient == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", s);
            iterator.remove();
         } else if (!resourcepackinfoclient.func_195791_d().func_198968_a() && !this.incompatibleResourcePacks.contains(s)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", s);
            iterator.remove();
         } else if (resourcepackinfoclient.func_195791_d().func_198968_a() && this.incompatibleResourcePacks.contains(s)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", s);
            this.incompatibleResourcePacks.remove(s);
         } else {
            set.add(resourcepackinfoclient);
         }
      }

      p_198017_1_.func_198985_a(set);
   }

   @OnlyIn(Dist.CLIENT)
   public enum Options {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0D, 110.0D, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0D, 16.0D, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0D, 260.0D, 10.0F),
      FBO_ENABLE("options.fboEnable", false, true),
      RENDER_CLOUDS("options.renderClouds", false, false),
      GRAPHICS("options.graphics", false, false),
      AMBIENT_OCCLUSION("options.ao", false, false),
      GUI_SCALE("options.guiScale", false, false),
      PARTICLES("options.particles", false, false),
      CHAT_VISIBILITY("options.chat.visibility", false, false),
      CHAT_COLOR("options.chat.color", false, true),
      CHAT_LINKS("options.chat.links", false, true),
      CHAT_OPACITY("options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("options.snooper", false, true),
      FULLSCREEN_RESOLUTION("options.fullscreen.resolution", true, false, 0.0D, 0.0D, 1.0F),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0D, 4.0D, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("options.entityShadows", false, true),
      MAIN_HAND("options.mainHand", false, false),
      ATTACK_INDICATOR("options.attackIndicator", false, false),
      ENABLE_WEAK_ATTACKS("options.enableWeakAttacks", false, true),
      SHOW_SUBTITLES("options.showSubtitles", false, true),
      REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
      AUTO_JUMP("options.autoJump", false, true),
      NARRATOR("options.narrator", false, false),
      AUTO_SUGGESTIONS("options.autoSuggestCommands", false, true),
      BIOME_BLEND_RADIUS("options.biomeBlendRadius", true, false, 0.0D, 7.0D, 1.0F),
      MOUSE_WHEEL_SENSITIVITY("options.mouseWheelSensitivity", true, false, 1.0D, 10.0D, 0.5F);

      private final boolean isFloat;
      private final boolean isBoolean;
      private final String translation;
      private final float valueStep;
      private double valueMin;
      private double valueMax;

      public static GameSettings.Options byOrdinal(int p_74379_0_) {
         for(GameSettings.Options gamesettings$options : values()) {
            if (gamesettings$options.getOrdinal() == p_74379_0_) {
               return gamesettings$options;
            }
         }

         return null;
      }

      Options(String p_i1015_3_, boolean p_i1015_4_, boolean p_i1015_5_) {
         this(p_i1015_3_, p_i1015_4_, p_i1015_5_, 0.0D, 1.0D, 0.0F);
      }

      Options(String p_i47986_3_, boolean p_i47986_4_, boolean p_i47986_5_, double p_i47986_6_, double p_i47986_8_, float p_i47986_10_) {
         this.translation = p_i47986_3_;
         this.isFloat = p_i47986_4_;
         this.isBoolean = p_i47986_5_;
         this.valueMin = p_i47986_6_;
         this.valueMax = p_i47986_8_;
         this.valueStep = p_i47986_10_;
      }

      public boolean isFloat() {
         return this.isFloat;
      }

      public boolean isBoolean() {
         return this.isBoolean;
      }

      public int getOrdinal() {
         return this.ordinal();
      }

      public String getTranslation() {
         return this.translation;
      }

      public double getValueMin() {
         return this.valueMin;
      }

      public double getValueMax() {
         return this.valueMax;
      }

      public void setValueMax(float p_148263_1_) {
         this.valueMax = (double)p_148263_1_;
      }

      public double func_198008_a(double p_198008_1_) {
         return MathHelper.clamp((this.func_198011_c(p_198008_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0D, 1.0D);
      }

      public double func_198004_b(double p_198004_1_) {
         return this.func_198011_c(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp(p_198004_1_, 0.0D, 1.0D));
      }

      public double func_198011_c(double p_198011_1_) {
         p_198011_1_ = this.func_198006_d(p_198011_1_);
         return MathHelper.clamp(p_198011_1_, this.valueMin, this.valueMax);
      }

      private double func_198006_d(double p_198006_1_) {
         if (this.valueStep > 0.0F) {
            p_198006_1_ = (double)(this.valueStep * (float)Math.round(p_198006_1_ / (double)this.valueStep));
         }

         return p_198006_1_;
      }
   }
}
