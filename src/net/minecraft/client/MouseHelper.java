package net.minecraft.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class MouseHelper {
   private final Minecraft minecraft;
   private boolean leftDown;
   private boolean middleDown;
   private boolean rightDown;
   private double mouseX;
   private double mouseY;
   private int field_212148_g;
   private int activeButton = -1;
   private boolean field_198043_h = true;
   private int touchScreenCounter;
   private double eventTime;
   private final MouseSmoother xSmoother = new MouseSmoother();
   private final MouseSmoother ySmoother = new MouseSmoother();
   private double xVelocity;
   private double yVelocity;
   private double accumulatedScrollDelta;
   private double field_198050_o = Double.MIN_VALUE;
   private boolean mouseGrabbed;

   public MouseHelper(Minecraft p_i47672_1_) {
      this.minecraft = p_i47672_1_;
   }

   private void mouseButtonCallback(long p_198023_1_, int p_198023_3_, int p_198023_4_, int p_198023_5_) {
      if (p_198023_1_ == this.minecraft.mainWindow.getHandle()) {
         boolean flag = p_198023_4_ == 1;
         if (Minecraft.IS_RUNNING_ON_MAC && p_198023_3_ == 0) {
            if (flag) {
               if ((p_198023_5_ & 2) == 2) {
                  p_198023_3_ = 1;
                  ++this.field_212148_g;
               }
            } else if (this.field_212148_g > 0) {
               p_198023_3_ = 1;
               --this.field_212148_g;
            }
         }

         if (flag) {
            if (this.minecraft.gameSettings.touchscreen && this.touchScreenCounter++ > 0) {
               return;
            }

            this.activeButton = p_198023_3_;
            this.eventTime = GLFW.glfwGetTime();
         } else if (this.activeButton != -1) {
            if (this.minecraft.gameSettings.touchscreen && --this.touchScreenCounter > 0) {
               return;
            }

            this.activeButton = -1;
         }

         boolean[] aboolean = new boolean[]{false};
         if (this.minecraft.currentScreen == null) {
            if (!this.mouseGrabbed && flag) {
               this.grabMouse();
            }
         } else {
            double d0 = this.mouseX * (double)this.minecraft.mainWindow.getScaledWidth() / (double)this.minecraft.mainWindow.getWidth();
            double d1 = this.mouseY * (double)this.minecraft.mainWindow.getScaledHeight() / (double)this.minecraft.mainWindow.getHeight();
            final int pFinal =  p_198023_3_;
            if (flag) {
               GuiScreen.runOrMakeCrashReport(() -> {
                  aboolean[0] = this.minecraft.currentScreen.mouseClicked(d0, d1, pFinal);
               }, "mouseClicked event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
            } else {
               GuiScreen.runOrMakeCrashReport(() -> {
                  aboolean[0] = this.minecraft.currentScreen.mouseReleased(d0, d1, pFinal);
               }, "mouseReleased event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
            }
         }

         if (!aboolean[0] && (this.minecraft.currentScreen == null || this.minecraft.currentScreen.allowUserInput)) {
            if (p_198023_3_ == 0) {
               this.leftDown = flag;
            } else if (p_198023_3_ == 2) {
               this.middleDown = flag;
            } else if (p_198023_3_ == 1) {
               this.rightDown = flag;
            }

            KeyBinding.setKeyBindState(InputMappings.Type.MOUSE.getOrMakeInput(p_198023_3_), flag);
            if (flag) {
               if (this.minecraft.player.isSpectator() && p_198023_3_ == 2) {
                  this.minecraft.ingameGUI.getSpectatorGui().onMiddleClick();
               } else {
                  KeyBinding.onTick(InputMappings.Type.MOUSE.getOrMakeInput(p_198023_3_));
               }
            }
         }

      }
   }

   private void scrollCallback(long p_198020_1_, double p_198020_3_, double p_198020_5_) {
      if (p_198020_1_ == Minecraft.getInstance().mainWindow.getHandle()) {
         double d0 = p_198020_5_ * this.minecraft.gameSettings.mouseWheelSensitivity;
         if (this.minecraft.currentScreen != null) {
            this.minecraft.currentScreen.mouseScrolled(d0);
         } else if (this.minecraft.player != null) {
            if (this.accumulatedScrollDelta != 0.0D && Math.signum(d0) != Math.signum(this.accumulatedScrollDelta)) {
               this.accumulatedScrollDelta = 0.0D;
            }

            this.accumulatedScrollDelta += d0;
            double d1 = (double)((int)this.accumulatedScrollDelta);
            if (d1 == 0.0D) {
               return;
            }

            this.accumulatedScrollDelta -= d1;
            if (this.minecraft.player.isSpectator()) {
               if (this.minecraft.ingameGUI.getSpectatorGui().isMenuActive()) {
                  this.minecraft.ingameGUI.getSpectatorGui().func_195621_a(-d1);
               } else {
                  double d2 = MathHelper.clamp((double)this.minecraft.player.capabilities.getFlySpeed() + d1 * (double)0.005F, 0.0D, (double)0.2F);
                  this.minecraft.player.capabilities.setFlySpeed(d2);
               }
            } else {
               this.minecraft.player.inventory.changeCurrentItem(d1);
            }
         }
      }

   }

   public void registerCallbacks(long p_198029_1_) {
      GLFW.glfwSetCursorPosCallback(p_198029_1_, this::cursorPosCallback);
      GLFW.glfwSetMouseButtonCallback(p_198029_1_, this::mouseButtonCallback);
      GLFW.glfwSetScrollCallback(p_198029_1_, this::scrollCallback);
   }

   private void cursorPosCallback(long p_198022_1_, double p_198022_3_, double p_198022_5_) {
      if (p_198022_1_ == Minecraft.getInstance().mainWindow.getHandle()) {
         if (this.field_198043_h) {
            this.mouseX = p_198022_3_;
            this.mouseY = p_198022_5_;
            this.field_198043_h = false;
         }

         IGuiEventListener iguieventlistener = this.minecraft.currentScreen;
         if (this.activeButton != -1 && this.eventTime > 0.0D && iguieventlistener != null) {
            double d0 = p_198022_3_ * (double)this.minecraft.mainWindow.getScaledWidth() / (double)this.minecraft.mainWindow.getWidth();
            double d1 = p_198022_5_ * (double)this.minecraft.mainWindow.getScaledHeight() / (double)this.minecraft.mainWindow.getHeight();
            double d2 = (p_198022_3_ - this.mouseX) * (double)this.minecraft.mainWindow.getScaledWidth() / (double)this.minecraft.mainWindow.getWidth();
            double d3 = (p_198022_5_ - this.mouseY) * (double)this.minecraft.mainWindow.getScaledHeight() / (double)this.minecraft.mainWindow.getHeight();
            GuiScreen.runOrMakeCrashReport(() -> {
               iguieventlistener.mouseDragged(d0, d1, this.activeButton, d2, d3);
            }, "mouseDragged event handler", iguieventlistener.getClass().getCanonicalName());
         }

         this.minecraft.profiler.startSection("mouse");
         if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
            this.xVelocity += p_198022_3_ - this.mouseX;
            this.yVelocity += p_198022_5_ - this.mouseY;
         }

         this.updatePlayerLook();
         this.mouseX = p_198022_3_;
         this.mouseY = p_198022_5_;
         this.minecraft.profiler.endSection();
      }
   }

   public void updatePlayerLook() {
      double d0 = GLFW.glfwGetTime();
      double d1 = d0 - this.field_198050_o;
      this.field_198050_o = d0;
      if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
         double d4 = this.minecraft.gameSettings.mouseSensitivity * (double)0.6F + (double)0.2F;
         double d5 = d4 * d4 * d4 * 8.0D;
         double d2;
         double d3;
         if (this.minecraft.gameSettings.smoothCamera) {
            double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
            double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
            d2 = d6;
            d3 = d7;
         } else {
            this.xSmoother.reset();
            this.ySmoother.reset();
            d2 = this.xVelocity * d5;
            d3 = this.yVelocity * d5;
         }

         this.xVelocity = 0.0D;
         this.yVelocity = 0.0D;
         int i = 1;
         if (this.minecraft.gameSettings.invertMouse) {
            i = -1;
         }

         this.minecraft.getTutorial().onMouseMove(d2, d3);
         if (this.minecraft.player != null) {
            this.minecraft.player.rotateTowards(d2, d3 * (double)i);
         }

      } else {
         this.xVelocity = 0.0D;
         this.yVelocity = 0.0D;
      }
   }

   public boolean isLeftDown() {
      return this.leftDown;
   }

   public boolean isRightDown() {
      return this.rightDown;
   }

   public double getMouseX() {
      return this.mouseX;
   }

   public double getMouseY() {
      return this.mouseY;
   }

   public void func_198021_g() {
      this.field_198043_h = true;
   }

   public boolean isMouseGrabbed() {
      return this.mouseGrabbed;
   }

   public void grabMouse() {
      if (this.minecraft.isGameFocused()) {
         if (!this.mouseGrabbed) {
            if (!Minecraft.IS_RUNNING_ON_MAC) {
               KeyBinding.updateKeyBindState();
            }

            this.mouseGrabbed = true;
            this.mouseX = (double)(this.minecraft.mainWindow.getWidth() / 2);
            this.mouseY = (double)(this.minecraft.mainWindow.getHeight() / 2);
            GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), this.mouseX, this.mouseY);
            GLFW.glfwSetInputMode(this.minecraft.mainWindow.getHandle(), 208897, 212995);
            this.minecraft.displayGuiScreen((GuiScreen)null);
            this.minecraft.leftClickCounter = 10000;
         }
      }
   }

   public void ungrabMouse() {
      if (this.mouseGrabbed) {
         this.mouseGrabbed = false;
         GLFW.glfwSetInputMode(this.minecraft.mainWindow.getHandle(), 208897, 212993);
         this.mouseX = (double)(this.minecraft.mainWindow.getWidth() / 2);
         this.mouseY = (double)(this.minecraft.mainWindow.getHeight() / 2);
         GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), this.mouseX, this.mouseY);
      }
   }
}
