package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiButtonLanguage extends GuiButton {
   public GuiButtonLanguage(int p_i1041_1_, int p_i1041_2_, int p_i1041_3_) {
      super(p_i1041_1_, p_i1041_2_, p_i1041_3_, 20, 20, "");
   }

   public void render(int p_194828_1_, int p_194828_2_, float p_194828_3_) {
      if (this.visible) {
         Minecraft.getInstance().getTextureManager().bindTexture(GuiButton.BUTTON_TEXTURES);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
         int i = 106;
         if (flag) {
            i += this.height;
         }

         this.drawTexturedModalRect(this.x, this.y, 0, i, this.width, this.height);
      }
   }
}
