package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonRealmsProxy extends GuiButton {
   private final RealmsButton realmsButton;

   public GuiButtonRealmsProxy(RealmsButton p_i46321_1_, int p_i46321_2_, int p_i46321_3_, int p_i46321_4_, String p_i46321_5_) {
      super(p_i46321_2_, p_i46321_3_, p_i46321_4_, p_i46321_5_);
      this.realmsButton = p_i46321_1_;
   }

   public GuiButtonRealmsProxy(RealmsButton p_i1090_1_, int p_i1090_2_, int p_i1090_3_, int p_i1090_4_, String p_i1090_5_, int p_i1090_6_, int p_i1090_7_) {
      super(p_i1090_2_, p_i1090_3_, p_i1090_4_, p_i1090_6_, p_i1090_7_, p_i1090_5_);
      this.realmsButton = p_i1090_1_;
   }

   public int id() {
      return this.id;
   }

   public boolean active() {
      return this.enabled;
   }

   public void active(boolean p_207706_1_) {
      this.enabled = p_207706_1_;
   }

   public void msg(String p_207705_1_) {
      super.displayString = p_207705_1_;
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int y() {
      return this.y;
   }

   public void onClick(double p_194829_1_, double p_194829_3_) {
      this.realmsButton.onClick(p_194829_1_, p_194829_3_);
   }

   public void onRelease(double p_194831_1_, double p_194831_3_) {
      this.realmsButton.onRelease(p_194831_1_, p_194831_3_);
   }

   public void renderBg(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_) {
      this.realmsButton.renderBg(p_146119_2_, p_146119_3_);
   }

   public RealmsButton getRealmsButton() {
      return this.realmsButton;
   }

   public int getHoverState(boolean p_146114_1_) {
      return this.realmsButton.getYImage(p_146114_1_);
   }

   public int getYImage(boolean p_154312_1_) {
      return super.getHoverState(p_154312_1_);
   }

   public int getHeight() {
      return this.height;
   }
}
