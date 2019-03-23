package net.minecraft.client.gui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Gui {
   public static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STAT_ICONS = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");
   protected float zLevel;

   protected void drawHorizontalLine(int p_73730_1_, int p_73730_2_, int p_73730_3_, int p_73730_4_) {
      if (p_73730_2_ < p_73730_1_) {
         int i = p_73730_1_;
         p_73730_1_ = p_73730_2_;
         p_73730_2_ = i;
      }

      drawRect(p_73730_1_, p_73730_3_, p_73730_2_ + 1, p_73730_3_ + 1, p_73730_4_);
   }

   protected void drawVerticalLine(int p_73728_1_, int p_73728_2_, int p_73728_3_, int p_73728_4_) {
      if (p_73728_3_ < p_73728_2_) {
         int i = p_73728_2_;
         p_73728_2_ = p_73728_3_;
         p_73728_3_ = i;
      }

      drawRect(p_73728_1_, p_73728_2_ + 1, p_73728_1_ + 1, p_73728_3_, p_73728_4_);
   }

   public static void drawRect(int p_73734_0_, int p_73734_1_, int p_73734_2_, int p_73734_3_, int p_73734_4_) {
      if (p_73734_0_ < p_73734_2_) {
         int i = p_73734_0_;
         p_73734_0_ = p_73734_2_;
         p_73734_2_ = i;
      }

      if (p_73734_1_ < p_73734_3_) {
         int j = p_73734_1_;
         p_73734_1_ = p_73734_3_;
         p_73734_3_ = j;
      }

      float f3 = (float)(p_73734_4_ >> 24 & 255) / 255.0F;
      float f = (float)(p_73734_4_ >> 16 & 255) / 255.0F;
      float f1 = (float)(p_73734_4_ >> 8 & 255) / 255.0F;
      float f2 = (float)(p_73734_4_ & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(f, f1, f2, f3);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)p_73734_0_, (double)p_73734_3_, 0.0D).endVertex();
      bufferbuilder.pos((double)p_73734_2_, (double)p_73734_3_, 0.0D).endVertex();
      bufferbuilder.pos((double)p_73734_2_, (double)p_73734_1_, 0.0D).endVertex();
      bufferbuilder.pos((double)p_73734_0_, (double)p_73734_1_, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   protected void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_) {
      float f = (float)(p_73733_5_ >> 24 & 255) / 255.0F;
      float f1 = (float)(p_73733_5_ >> 16 & 255) / 255.0F;
      float f2 = (float)(p_73733_5_ >> 8 & 255) / 255.0F;
      float f3 = (float)(p_73733_5_ & 255) / 255.0F;
      float f4 = (float)(p_73733_6_ >> 24 & 255) / 255.0F;
      float f5 = (float)(p_73733_6_ >> 16 & 255) / 255.0F;
      float f6 = (float)(p_73733_6_ >> 8 & 255) / 255.0F;
      float f7 = (float)(p_73733_6_ & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)p_73733_3_, (double)p_73733_2_, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
      bufferbuilder.pos((double)p_73733_1_, (double)p_73733_2_, (double)this.zLevel).color(f1, f2, f3, f).endVertex();
      bufferbuilder.pos((double)p_73733_1_, (double)p_73733_4_, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
      bufferbuilder.pos((double)p_73733_3_, (double)p_73733_4_, (double)this.zLevel).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
      GlStateManager.enableTexture2D();
   }

   public void drawCenteredString(FontRenderer p_73732_1_, String p_73732_2_, int p_73732_3_, int p_73732_4_, int p_73732_5_) {
      p_73732_1_.drawStringWithShadow(p_73732_2_, (float)(p_73732_3_ - p_73732_1_.getStringWidth(p_73732_2_) / 2), (float)p_73732_4_, p_73732_5_);
   }

   public void drawString(FontRenderer p_73731_1_, String p_73731_2_, int p_73731_3_, int p_73731_4_, int p_73731_5_) {
      p_73731_1_.drawStringWithShadow(p_73731_2_, (float)p_73731_3_, (float)p_73731_4_, p_73731_5_);
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(float p_175174_1_, float p_175174_2_, int p_175174_3_, int p_175174_4_, int p_175174_5_, int p_175174_6_) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(p_175174_1_ + 0.0F), (double)(p_175174_2_ + (float)p_175174_6_), (double)this.zLevel).tex((double)((float)(p_175174_3_ + 0) * 0.00390625F), (double)((float)(p_175174_4_ + p_175174_6_) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(p_175174_1_ + (float)p_175174_5_), (double)(p_175174_2_ + (float)p_175174_6_), (double)this.zLevel).tex((double)((float)(p_175174_3_ + p_175174_5_) * 0.00390625F), (double)((float)(p_175174_4_ + p_175174_6_) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(p_175174_1_ + (float)p_175174_5_), (double)(p_175174_2_ + 0.0F), (double)this.zLevel).tex((double)((float)(p_175174_3_ + p_175174_5_) * 0.00390625F), (double)((float)(p_175174_4_ + 0) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(p_175174_1_ + 0.0F), (double)(p_175174_2_ + 0.0F), (double)this.zLevel).tex((double)((float)(p_175174_3_ + 0) * 0.00390625F), (double)((float)(p_175174_4_ + 0) * 0.00390625F)).endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(int p_175175_1_, int p_175175_2_, TextureAtlasSprite p_175175_3_, int p_175175_4_, int p_175175_5_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(p_175175_1_ + 0), (double)(p_175175_2_ + p_175175_5_), (double)this.zLevel).tex((double)p_175175_3_.getMinU(), (double)p_175175_3_.getMaxV()).endVertex();
      bufferbuilder.pos((double)(p_175175_1_ + p_175175_4_), (double)(p_175175_2_ + p_175175_5_), (double)this.zLevel).tex((double)p_175175_3_.getMaxU(), (double)p_175175_3_.getMaxV()).endVertex();
      bufferbuilder.pos((double)(p_175175_1_ + p_175175_4_), (double)(p_175175_2_ + 0), (double)this.zLevel).tex((double)p_175175_3_.getMaxU(), (double)p_175175_3_.getMinV()).endVertex();
      bufferbuilder.pos((double)(p_175175_1_ + 0), (double)(p_175175_2_ + 0), (double)this.zLevel).tex((double)p_175175_3_.getMinU(), (double)p_175175_3_.getMinV()).endVertex();
      tessellator.draw();
   }

   public static void drawModalRectWithCustomSizedTexture(int p_146110_0_, int p_146110_1_, float p_146110_2_, float p_146110_3_, int p_146110_4_, int p_146110_5_, float p_146110_6_, float p_146110_7_) {
      float f = 1.0F / p_146110_6_;
      float f1 = 1.0F / p_146110_7_;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)p_146110_0_, (double)(p_146110_1_ + p_146110_5_), 0.0D).tex((double)(p_146110_2_ * f), (double)((p_146110_3_ + (float)p_146110_5_) * f1)).endVertex();
      bufferbuilder.pos((double)(p_146110_0_ + p_146110_4_), (double)(p_146110_1_ + p_146110_5_), 0.0D).tex((double)((p_146110_2_ + (float)p_146110_4_) * f), (double)((p_146110_3_ + (float)p_146110_5_) * f1)).endVertex();
      bufferbuilder.pos((double)(p_146110_0_ + p_146110_4_), (double)p_146110_1_, 0.0D).tex((double)((p_146110_2_ + (float)p_146110_4_) * f), (double)(p_146110_3_ * f1)).endVertex();
      bufferbuilder.pos((double)p_146110_0_, (double)p_146110_1_, 0.0D).tex((double)(p_146110_2_ * f), (double)(p_146110_3_ * f1)).endVertex();
      tessellator.draw();
   }

   public static void drawScaledCustomSizeModalRect(int p_152125_0_, int p_152125_1_, float p_152125_2_, float p_152125_3_, int p_152125_4_, int p_152125_5_, int p_152125_6_, int p_152125_7_, float p_152125_8_, float p_152125_9_) {
      float f = 1.0F / p_152125_8_;
      float f1 = 1.0F / p_152125_9_;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)p_152125_0_, (double)(p_152125_1_ + p_152125_7_), 0.0D).tex((double)(p_152125_2_ * f), (double)((p_152125_3_ + (float)p_152125_5_) * f1)).endVertex();
      bufferbuilder.pos((double)(p_152125_0_ + p_152125_6_), (double)(p_152125_1_ + p_152125_7_), 0.0D).tex((double)((p_152125_2_ + (float)p_152125_4_) * f), (double)((p_152125_3_ + (float)p_152125_5_) * f1)).endVertex();
      bufferbuilder.pos((double)(p_152125_0_ + p_152125_6_), (double)p_152125_1_, 0.0D).tex((double)((p_152125_2_ + (float)p_152125_4_) * f), (double)(p_152125_3_ * f1)).endVertex();
      bufferbuilder.pos((double)p_152125_0_, (double)p_152125_1_, 0.0D).tex((double)(p_152125_2_ * f), (double)(p_152125_3_ * f1)).endVertex();
      tessellator.draw();
   }
}
