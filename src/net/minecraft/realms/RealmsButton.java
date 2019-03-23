package net.minecraft.realms;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RealmsButton {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private final GuiButtonRealmsProxy proxy;

   public RealmsButton(int p_i1177_1_, int p_i1177_2_, int p_i1177_3_, String p_i1177_4_) {
      this.proxy = new GuiButtonRealmsProxy(this, p_i1177_1_, p_i1177_2_, p_i1177_3_, p_i1177_4_) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            RealmsButton.this.onClick(p_194829_1_, p_194829_3_);
         }
      };
   }

   public RealmsButton(int p_i1178_1_, int p_i1178_2_, int p_i1178_3_, int p_i1178_4_, int p_i1178_5_, String p_i1178_6_) {
      this.proxy = new GuiButtonRealmsProxy(this, p_i1178_1_, p_i1178_2_, p_i1178_3_, p_i1178_6_, p_i1178_4_, p_i1178_5_) {
         public void onClick(double p_194829_1_, double p_194829_3_) {
            RealmsButton.this.onClick(p_194829_1_, p_194829_3_);
         }
      };
   }

   public GuiButton getProxy() {
      return this.proxy;
   }

   public int id() {
      return this.proxy.id();
   }

   public boolean active() {
      return this.proxy.active();
   }

   public void active(boolean p_active_1_) {
      this.proxy.active(p_active_1_);
   }

   public void msg(String p_msg_1_) {
      this.proxy.msg(p_msg_1_);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int y() {
      return this.proxy.y();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.proxy.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
      this.proxy.drawTexturedModalRect(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_);
   }

   public void renderBg(int p_renderBg_1_, int p_renderBg_2_) {
   }

   public int getYImage(boolean p_getYImage_1_) {
      return this.proxy.getYImage(p_getYImage_1_);
   }

   public abstract void onClick(double p_onClick_1_, double p_onClick_3_);

   public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
   }
}
