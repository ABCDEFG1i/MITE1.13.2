package net.minecraft.client;

import java.nio.ByteBuffer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class KeyboardListener {
   private final Minecraft mc;
   private boolean repeatEventsEnabled;
   private long debugCrashKeyPressTime = -1L;
   private long field_204871_d = -1L;
   private long field_204872_e = -1L;
   private boolean actionKeyF3;
   private final ByteBuffer field_211563_g = ByteBuffer.allocateDirect(1024);

   public KeyboardListener(Minecraft p_i47674_1_) {
      this.mc = p_i47674_1_;
   }

   private void printDebugMessage(String p_197964_1_, Object... p_197964_2_) {
      this.mc.ingameGUI.getChatGUI().printChatMessage((new TextComponentString("")).appendSibling((new TextComponentTranslation("debug.prefix")).applyTextStyles(new TextFormatting[]{TextFormatting.YELLOW, TextFormatting.BOLD})).appendText(" ").appendSibling(new TextComponentTranslation(p_197964_1_, p_197964_2_)));
   }

   private void printDebugWarning(String p_204869_1_, Object... p_204869_2_) {
      this.mc.ingameGUI.getChatGUI().printChatMessage((new TextComponentString("")).appendSibling((new TextComponentTranslation("debug.prefix")).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.BOLD})).appendText(" ").appendSibling(new TextComponentTranslation(p_204869_1_, p_204869_2_)));
   }

   private boolean processKeyF3(int p_197962_1_) {
      if (this.debugCrashKeyPressTime > 0L && this.debugCrashKeyPressTime < Util.milliTime() - 100L) {
         return true;
      } else {
         switch(p_197962_1_) {
         case 65:
            this.mc.renderGlobal.func_72712_a();
            this.printDebugMessage("debug.reload_chunks.message");
            return true;
         case 66:
            boolean flag = !this.mc.getRenderManager().isDebugBoundingBox();
            this.mc.getRenderManager().setDebugBoundingBox(flag);
            this.printDebugMessage(flag ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
         case 67:
            if (this.mc.player.hasReducedDebug()) {
               return false;
            }

            this.printDebugMessage("debug.copy_location.message");
            this.setClipboardString(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.func_212678_a(this.mc.player.world.dimension.getType()), this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ, this.mc.player.rotationYaw, this.mc.player.rotationPitch));
            return true;
         case 68:
            if (this.mc.ingameGUI != null) {
               this.mc.ingameGUI.getChatGUI().clearChatMessages(false);
            }

            return true;
         case 69:
         case 74:
         case 75:
         case 76:
         case 77:
         case 79:
         case 82:
         case 83:
         default:
            return false;
         case 70:
            this.mc.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
            this.printDebugMessage("debug.cycle_renderdistance.message", this.mc.gameSettings.renderDistanceChunks);
            return true;
         case 71:
            boolean flag1 = this.mc.debugRenderer.toggleChunkBorders();
            this.printDebugMessage(flag1 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
         case 72:
            this.mc.gameSettings.advancedItemTooltips = !this.mc.gameSettings.advancedItemTooltips;
            this.printDebugMessage(this.mc.gameSettings.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.mc.gameSettings.saveOptions();
            return true;
         case 73:
            if (!this.mc.player.hasReducedDebug()) {
               this.func_211556_a(this.mc.player.hasPermissionLevel(2), !GuiScreen.isShiftKeyDown());
            }

            return true;
         case 78:
            if (!this.mc.player.hasPermissionLevel(2)) {
               this.printDebugMessage("debug.creative_spectator.error");
            } else if (this.mc.player.isCreative()) {
               this.mc.player.sendChatMessage("/gamemode spectator");
            } else if (this.mc.player.isSpectator()) {
               this.mc.player.sendChatMessage("/gamemode creative");
            }

            return true;
         case 80:
            this.mc.gameSettings.pauseOnLostFocus = !this.mc.gameSettings.pauseOnLostFocus;
            this.mc.gameSettings.saveOptions();
            this.printDebugMessage(this.mc.gameSettings.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
         case 81:
            this.printDebugMessage("debug.help.message");
            GuiNewChat guinewchat = this.mc.ingameGUI.getChatGUI();
            guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_chunks.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.show_hitboxes.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.copy_location.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.clear_chat.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.cycle_renderdistance.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.chunk_boundaries.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.advanced_tooltips.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.inspect.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.creative_spectator.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.pause_focus.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.help.help"));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_resourcepacks.help"));
            return true;
         case 84:
            this.printDebugMessage("debug.reload_resourcepacks.message");
            this.mc.refreshResources();
            return true;
         }
      }
   }

   private void func_211556_a(boolean p_211556_1_, boolean p_211556_2_) {
      if (this.mc.objectMouseOver != null) {
         switch(this.mc.objectMouseOver.type) {
         case BLOCK:
            BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
            IBlockState iblockstate = this.mc.player.world.getBlockState(blockpos);
            if (p_211556_1_) {
               if (p_211556_2_) {
                  this.mc.player.connection.func_211523_k().queryTileEntity(blockpos, (p_211561_3_) -> {
                     this.setBlockClipboardString(iblockstate, blockpos, p_211561_3_);
                     this.printDebugMessage("debug.inspect.server.block");
                  });
               } else {
                  TileEntity tileentity = this.mc.player.world.getTileEntity(blockpos);
                  NBTTagCompound nbttagcompound1 = tileentity != null ? tileentity.writeToNBT(new NBTTagCompound()) : null;
                  this.setBlockClipboardString(iblockstate, blockpos, nbttagcompound1);
                  this.printDebugMessage("debug.inspect.client.block");
               }
            } else {
               this.setBlockClipboardString(iblockstate, blockpos, (NBTTagCompound)null);
               this.printDebugMessage("debug.inspect.client.block");
            }
            break;
         case ENTITY:
            Entity entity = this.mc.objectMouseOver.entity;
            if (entity == null) {
               return;
            }

            ResourceLocation resourcelocation = IRegistry.field_212629_r.func_177774_c(entity.getType());
            Vec3d vec3d = new Vec3d(entity.posX, entity.posY, entity.posZ);
            if (p_211556_1_) {
               if (p_211556_2_) {
                  this.mc.player.connection.func_211523_k().queryEntity(entity.getEntityId(), (p_211560_3_) -> {
                     this.setEntityClipboardString(resourcelocation, vec3d, p_211560_3_);
                     this.printDebugMessage("debug.inspect.server.entity");
                  });
               } else {
                  NBTTagCompound nbttagcompound = entity.writeToNBT(new NBTTagCompound());
                  this.setEntityClipboardString(resourcelocation, vec3d, nbttagcompound);
                  this.printDebugMessage("debug.inspect.client.entity");
               }
            } else {
               this.setEntityClipboardString(resourcelocation, vec3d, (NBTTagCompound)null);
               this.printDebugMessage("debug.inspect.client.entity");
            }
         }

      }
   }

   private void setBlockClipboardString(IBlockState p_211558_1_, BlockPos p_211558_2_, @Nullable NBTTagCompound p_211558_3_) {
      if (p_211558_3_ != null) {
         p_211558_3_.removeTag("x");
         p_211558_3_.removeTag("y");
         p_211558_3_.removeTag("z");
         p_211558_3_.removeTag("id");
      }

      String s = BlockStateParser.toString(p_211558_1_, p_211558_3_);
      String s1 = String.format(Locale.ROOT, "/setblock %d %d %d %s", p_211558_2_.getX(), p_211558_2_.getY(), p_211558_2_.getZ(), s);
      this.setClipboardString(s1);
   }

   private void setEntityClipboardString(ResourceLocation p_211557_1_, Vec3d p_211557_2_, @Nullable NBTTagCompound p_211557_3_) {
      String s;
      if (p_211557_3_ != null) {
         p_211557_3_.removeTag("UUIDMost");
         p_211557_3_.removeTag("UUIDLeast");
         p_211557_3_.removeTag("Pos");
         p_211557_3_.removeTag("Dimension");
         String s1 = p_211557_3_.toFormattedComponent().getString();
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", p_211557_1_.toString(), p_211557_2_.x, p_211557_2_.y, p_211557_2_.z, s1);
      } else {
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", p_211557_1_.toString(), p_211557_2_.x, p_211557_2_.y, p_211557_2_.z);
      }

      this.setClipboardString(s);
   }

   public void onKeyEvent(long p_197961_1_, int p_197961_3_, int p_197961_4_, int p_197961_5_, int p_197961_6_) {
      if (p_197961_1_ == this.mc.mainWindow.getHandle()) {
         if (this.debugCrashKeyPressTime > 0L) {
            if (!InputMappings.isKeyDown(67) || !InputMappings.isKeyDown(292)) {
               this.debugCrashKeyPressTime = -1L;
            }
         } else if (InputMappings.isKeyDown(67) && InputMappings.isKeyDown(292)) {
            this.actionKeyF3 = true;
            this.debugCrashKeyPressTime = Util.milliTime();
            this.field_204871_d = Util.milliTime();
            this.field_204872_e = 0L;
         }

         IGuiEventListener iguieventlistener = this.mc.currentScreen;
         if (p_197961_5_ == 1 && (!(this.mc.currentScreen instanceof GuiControls) || ((GuiControls)iguieventlistener).time <= Util.milliTime() - 20L)) {
            if (this.mc.gameSettings.keyBindFullscreen.matchesKey(p_197961_3_, p_197961_4_)) {
               this.mc.mainWindow.toggleFullscreen();
               return;
            }

            if (this.mc.gameSettings.keyBindScreenshot.matchesKey(p_197961_3_, p_197961_4_)) {
               if (GuiScreen.isCtrlKeyDown()) {
                  ;
               }

               ScreenShotHelper.func_148260_a(this.mc.gameDir, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), this.mc.getFramebuffer(), (p_212449_1_) -> {
                  this.mc.addScheduledTask(() -> {
                     this.mc.ingameGUI.getChatGUI().printChatMessage(p_212449_1_);
                  });
               });
               return;
            }
         }

         if (iguieventlistener != null) {
            boolean[] aboolean = new boolean[]{false};
            GuiScreen.runOrMakeCrashReport(() -> {
               if (p_197961_5_ != 1 && (p_197961_5_ != 2 || !this.repeatEventsEnabled)) {
                  if (p_197961_5_ == 0) {
                     aboolean[0] = iguieventlistener.keyReleased(p_197961_3_, p_197961_4_, p_197961_6_);
                  }
               } else {
                  aboolean[0] = iguieventlistener.keyPressed(p_197961_3_, p_197961_4_, p_197961_6_);
               }

            }, "keyPressed event handler", iguieventlistener.getClass().getCanonicalName());
            if (aboolean[0]) {
               return;
            }
         }

         if (this.mc.currentScreen == null || this.mc.currentScreen.allowUserInput) {
            InputMappings.Input inputmappings$input = InputMappings.getInputByCode(p_197961_3_, p_197961_4_);
            if (p_197961_5_ == 0) {
               KeyBinding.setKeyBindState(inputmappings$input, false);
               if (p_197961_3_ == 292) {
                  if (this.actionKeyF3) {
                     this.actionKeyF3 = false;
                  } else {
                     this.mc.gameSettings.showDebugInfo = !this.mc.gameSettings.showDebugInfo;
                     this.mc.gameSettings.showDebugProfilerChart = this.mc.gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown();
                     this.mc.gameSettings.showLagometer = this.mc.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
                  }
               }
            } else {
               if (p_197961_3_ == 66 && GuiScreen.isCtrlKeyDown()) {
                  this.mc.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);
                  if (iguieventlistener instanceof ScreenChatOptions) {
                     ((ScreenChatOptions)iguieventlistener).updateNarratorButton();
                  }
               }

               if (p_197961_3_ == 293 && this.mc.entityRenderer != null) {
                  this.mc.entityRenderer.func_175071_c();
               }

               boolean flag = false;
               if (this.mc.currentScreen == null) {
                  if (p_197961_3_ == 256) {
                     this.mc.displayInGameMenu();
                  }

                  flag = InputMappings.isKeyDown(292) && this.processKeyF3(p_197961_3_);
                  this.actionKeyF3 |= flag;
                  if (p_197961_3_ == 290) {
                     this.mc.gameSettings.hideGUI = !this.mc.gameSettings.hideGUI;
                  }
               }

               if (flag) {
                  KeyBinding.setKeyBindState(inputmappings$input, false);
               } else {
                  KeyBinding.setKeyBindState(inputmappings$input, true);
                  KeyBinding.onTick(inputmappings$input);
               }

               if (this.mc.gameSettings.showDebugProfilerChart) {
                  if (p_197961_3_ == 48) {
                     this.mc.updateDebugProfilerName(0);
                  }

                  for(int i = 0; i < 9; ++i) {
                     if (p_197961_3_ == 49 + i) {
                        this.mc.updateDebugProfilerName(i + 1);
                     }
                  }
               }
            }
         }

      }
   }

   private void onCharEvent(long p_197963_1_, int p_197963_3_, int p_197963_4_) {
      if (p_197963_1_ == this.mc.mainWindow.getHandle()) {
         IGuiEventListener iguieventlistener = this.mc.currentScreen;
         if (iguieventlistener != null) {
            if (Character.charCount(p_197963_3_) == 1) {
               GuiScreen.runOrMakeCrashReport(() -> {
                  iguieventlistener.charTyped((char)p_197963_3_, p_197963_4_);
               }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
            } else {
               for(char c0 : Character.toChars(p_197963_3_)) {
                  GuiScreen.runOrMakeCrashReport(() -> {
                     iguieventlistener.charTyped(c0, p_197963_4_);
                  }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
               }
            }

         }
      }
   }

   public void enableRepeatEvents(boolean p_197967_1_) {
      this.repeatEventsEnabled = p_197967_1_;
   }

   public void setupCallbacks(long p_197968_1_) {
      GLFW.glfwSetKeyCallback(p_197968_1_, this::onKeyEvent);
      GLFW.glfwSetCharModsCallback(p_197968_1_, this::onCharEvent);
   }

   public String getClipboardString() {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback((p_197966_1_, p_197966_2_) -> {
         if (p_197966_1_ != 65545) {
            this.mc.mainWindow.logGlError(p_197966_1_, p_197966_2_);
         }

      });
      String s = GLFW.glfwGetClipboardString(this.mc.mainWindow.getHandle());
      GLFW.glfwSetErrorCallback(glfwerrorcallback).free();
      return s == null ? "" : s;
   }

   private void func_211559_a(ByteBuffer p_211559_1_, String p_211559_2_) {
      MemoryUtil.memUTF8(p_211559_2_, true, p_211559_1_);
      GLFW.glfwSetClipboardString(this.mc.mainWindow.getHandle(), p_211559_1_);
   }

   public void setClipboardString(String p_197960_1_) {
      int i = MemoryUtil.memLengthUTF8(p_197960_1_, true);
      if (i < this.field_211563_g.capacity()) {
         this.func_211559_a(this.field_211563_g, p_197960_1_);
         this.field_211563_g.clear();
      } else {
         ByteBuffer bytebuffer = ByteBuffer.allocateDirect(i);
         this.func_211559_a(bytebuffer, p_197960_1_);
      }

   }

   public void tick() {
      if (this.debugCrashKeyPressTime > 0L) {
         long i = Util.milliTime();
         long j = 10000L - (i - this.debugCrashKeyPressTime);
         long k = i - this.field_204871_d;
         if (j < 0L) {
            if (GuiScreen.isCtrlKeyDown()) {
               MemoryUtil.memSet(0L, 0, 1L);
            }

            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
         }

         if (k >= 1000L) {
            if (this.field_204872_e == 0L) {
               this.printDebugMessage("debug.crash.message");
            } else {
               this.printDebugWarning("debug.crash.warning", MathHelper.ceil((float)j / 1000.0F));
            }

            this.field_204871_d = i;
            ++this.field_204872_e;
         }
      }

   }
}
