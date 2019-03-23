package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonImage extends GuiButton {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffText;

   public GuiButtonImage(int p_i47392_1_, int p_i47392_2_, int p_i47392_3_, int p_i47392_4_, int p_i47392_5_, int p_i47392_6_, int p_i47392_7_, int p_i47392_8_, ResourceLocation p_i47392_9_) {
      super(p_i47392_1_, p_i47392_2_, p_i47392_3_, p_i47392_4_, p_i47392_5_, "");
      this.xTexStart = p_i47392_6_;
      this.yTexStart = p_i47392_7_;
      this.yDiffText = p_i47392_8_;
      this.resourceLocation = p_i47392_9_;
   }

   public void setPosition(int p_191746_1_, int p_191746_2_) {
      this.x = p_191746_1_;
      this.y = p_191746_2_;
   }

   public void render(int p_194828_1_, int p_194828_2_, float p_194828_3_) {
      if (this.visible) {
         this.hovered = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getTextureManager().bindTexture(this.resourceLocation);
         GlStateManager.disableDepthTest();
         int i = this.yTexStart;
         if (this.hovered) {
            i += this.yDiffText;
         }

         this.drawTexturedModalRect(this.x, this.y, this.xTexStart, i, this.width, this.height);
         GlStateManager.enableDepthTest();
      }
   }
}
