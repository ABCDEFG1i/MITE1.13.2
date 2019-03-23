package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiOptionSlider extends GuiButton {
   private double sliderValue = 1.0D;
   public boolean dragging;
   private final GameSettings.Options options;
   private final double minValue;
   private final double maxValue;

   public GuiOptionSlider(int p_i45016_1_, int p_i45016_2_, int p_i45016_3_, GameSettings.Options p_i45016_4_) {
      this(p_i45016_1_, p_i45016_2_, p_i45016_3_, p_i45016_4_, 0.0D, 1.0D);
   }

   public GuiOptionSlider(int p_i47662_1_, int p_i47662_2_, int p_i47662_3_, GameSettings.Options p_i47662_4_, double p_i47662_5_, double p_i47662_7_) {
      this(p_i47662_1_, p_i47662_2_, p_i47662_3_, 150, 20, p_i47662_4_, p_i47662_5_, p_i47662_7_);
   }

   public GuiOptionSlider(int p_i47663_1_, int p_i47663_2_, int p_i47663_3_, int p_i47663_4_, int p_i47663_5_, GameSettings.Options p_i47663_6_, double p_i47663_7_, double p_i47663_9_) {
      super(p_i47663_1_, p_i47663_2_, p_i47663_3_, p_i47663_4_, p_i47663_5_, "");
      this.options = p_i47663_6_;
      this.minValue = p_i47663_7_;
      this.maxValue = p_i47663_9_;
      Minecraft minecraft = Minecraft.getInstance();
      this.sliderValue = p_i47663_6_.func_198008_a(minecraft.gameSettings.getOptionFloatValue(p_i47663_6_));
      this.displayString = minecraft.gameSettings.getKeyBinding(p_i47663_6_);
   }

   protected int getHoverState(boolean p_146114_1_) {
      return 0;
   }

   protected void renderBg(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_) {
      if (this.visible) {
         if (this.dragging) {
            this.sliderValue = (double)((float)(p_146119_2_ - (this.x + 4)) / (float)(this.width - 8));
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0D, 1.0D);
         }

         if (this.dragging || this.options == GameSettings.Options.FULLSCREEN_RESOLUTION) {
            double d0 = this.options.func_198004_b(this.sliderValue);
            p_146119_1_.gameSettings.setOptionFloatValue(this.options, d0);
            this.sliderValue = this.options.func_198008_a(d0);
            this.displayString = p_146119_1_.gameSettings.getKeyBinding(this.options);
         }

         p_146119_1_.getTextureManager().bindTexture(BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
         this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
      }
   }

   public final void onClick(double p_194829_1_, double p_194829_3_) {
      this.sliderValue = (p_194829_1_ - (double)(this.x + 4)) / (double)(this.width - 8);
      this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0D, 1.0D);
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.gameSettings.setOptionFloatValue(this.options, this.options.func_198004_b(this.sliderValue));
      this.displayString = minecraft.gameSettings.getKeyBinding(this.options);
      this.dragging = true;
   }

   public void onRelease(double p_194831_1_, double p_194831_3_) {
      this.dragging = false;
   }
}
