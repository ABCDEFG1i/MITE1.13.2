package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonToggle extends GuiButton {
   protected ResourceLocation resourceLocation;
   protected boolean stateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public GuiButtonToggle(int p_i47389_1_, int p_i47389_2_, int p_i47389_3_, int p_i47389_4_, int p_i47389_5_, boolean p_i47389_6_) {
      super(p_i47389_1_, p_i47389_2_, p_i47389_3_, p_i47389_4_, p_i47389_5_, "");
      this.stateTriggered = p_i47389_6_;
   }

   public void initTextureValues(int p_191751_1_, int p_191751_2_, int p_191751_3_, int p_191751_4_, ResourceLocation p_191751_5_) {
      this.xTexStart = p_191751_1_;
      this.yTexStart = p_191751_2_;
      this.xDiffTex = p_191751_3_;
      this.yDiffTex = p_191751_4_;
      this.resourceLocation = p_191751_5_;
   }

   public void setStateTriggered(boolean p_191753_1_) {
      this.stateTriggered = p_191753_1_;
   }

   public boolean isStateTriggered() {
      return this.stateTriggered;
   }

   public void setPosition(int p_191752_1_, int p_191752_2_) {
      this.x = p_191752_1_;
      this.y = p_191752_2_;
   }

   public void render(int p_194828_1_, int p_194828_2_, float p_194828_3_) {
      if (this.visible) {
         this.hovered = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getTextureManager().bindTexture(this.resourceLocation);
         GlStateManager.disableDepthTest();
         int i = this.xTexStart;
         int j = this.yTexStart;
         if (this.stateTriggered) {
            i += this.xDiffTex;
         }

         if (this.hovered) {
            j += this.yDiffTex;
         }

         this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
         GlStateManager.enableDepthTest();
      }
   }
}
