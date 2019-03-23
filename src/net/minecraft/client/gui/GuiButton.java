package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButton extends Gui implements IGuiEventListener {
   protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
   public int width = 200;
   public int height = 20;
   public int x;
   public int y;
   public String displayString;
   public int id;
   public boolean enabled = true;
   public boolean visible = true;
   protected boolean hovered;
   private boolean isDragging;

   public GuiButton(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
      this(p_i1020_1_, p_i1020_2_, p_i1020_3_, 200, 20, p_i1020_4_);
   }

   public GuiButton(int p_i46323_1_, int p_i46323_2_, int p_i46323_3_, int p_i46323_4_, int p_i46323_5_, String p_i46323_6_) {
      this.id = p_i46323_1_;
      this.x = p_i46323_2_;
      this.y = p_i46323_3_;
      this.width = p_i46323_4_;
      this.height = p_i46323_5_;
      this.displayString = p_i46323_6_;
   }

   protected int getHoverState(boolean p_146114_1_) {
      int i = 1;
      if (!this.enabled) {
         i = 0;
      } else if (p_146114_1_) {
         i = 2;
      }

      return i;
   }

   public void render(int p_194828_1_, int p_194828_2_, float p_194828_3_) {
      if (this.visible) {
         Minecraft minecraft = Minecraft.getInstance();
         FontRenderer fontrenderer = minecraft.fontRenderer;
         minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
         int i = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
         this.renderBg(minecraft, p_194828_1_, p_194828_2_);
         int j = 14737632;
         if (!this.enabled) {
            j = 10526880;
         } else if (this.hovered) {
            j = 16777120;
         }

         this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
      }
   }

   protected void renderBg(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_) {
   }

   public void onClick(double p_194829_1_, double p_194829_3_) {
      this.isDragging = true;
   }

   public void onRelease(double p_194831_1_, double p_194831_3_) {
      this.isDragging = false;
   }

   protected void onDrag(double p_194827_1_, double p_194827_3_, double p_194827_5_, double p_194827_7_) {
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         boolean flag = this.isPressable(p_mouseClicked_1_, p_mouseClicked_3_);
         if (flag) {
            this.playPressSound(Minecraft.getInstance().getSoundHandler());
            this.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
            return true;
         }
      }

      return false;
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (p_mouseReleased_5_ == 0) {
         this.onRelease(p_mouseReleased_1_, p_mouseReleased_3_);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ == 0) {
         this.onDrag(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_6_, p_mouseDragged_8_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isPressable(double p_199400_1_, double p_199400_3_) {
      return this.enabled && this.visible && p_199400_1_ >= (double)this.x && p_199400_3_ >= (double)this.y && p_199400_1_ < (double)(this.x + this.width) && p_199400_3_ < (double)(this.y + this.height);
   }

   public boolean isMouseOver() {
      return this.hovered;
   }

   public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_) {
   }

   public void playPressSound(SoundHandler p_146113_1_) {
      p_146113_1_.play(SimpleSound.func_184371_a(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int p_175211_1_) {
      this.width = p_175211_1_;
   }
}
