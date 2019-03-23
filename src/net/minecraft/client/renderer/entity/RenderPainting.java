package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPainting extends Render<EntityPainting> {
   private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

   public RenderPainting(RenderManager p_i46150_1_) {
      super(p_i46150_1_);
   }

   public void doRender(EntityPainting p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      GlStateManager.translated(p_76986_2_, p_76986_4_, p_76986_6_);
      GlStateManager.rotatef(180.0F - p_76986_8_, 0.0F, 1.0F, 0.0F);
      GlStateManager.enableRescaleNormal();
      this.bindEntityTexture(p_76986_1_);
      PaintingType paintingtype = p_76986_1_.art;
      float f = 0.0625F;
      GlStateManager.scalef(0.0625F, 0.0625F, 0.0625F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(p_76986_1_));
      }

      this.renderPainting(p_76986_1_, paintingtype.getWidth(), paintingtype.getHeight(), paintingtype.getU(), paintingtype.getV());
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityPainting p_110775_1_) {
      return KRISTOFFER_PAINTING_TEXTURE;
   }

   private void renderPainting(EntityPainting p_77010_1_, int p_77010_2_, int p_77010_3_, int p_77010_4_, int p_77010_5_) {
      float f = (float)(-p_77010_2_) / 2.0F;
      float f1 = (float)(-p_77010_3_) / 2.0F;
      float f2 = 0.5F;
      float f3 = 0.75F;
      float f4 = 0.8125F;
      float f5 = 0.0F;
      float f6 = 0.0625F;
      float f7 = 0.75F;
      float f8 = 0.8125F;
      float f9 = 0.001953125F;
      float f10 = 0.001953125F;
      float f11 = 0.7519531F;
      float f12 = 0.7519531F;
      float f13 = 0.0F;
      float f14 = 0.0625F;

      for(int i = 0; i < p_77010_2_ / 16; ++i) {
         for(int j = 0; j < p_77010_3_ / 16; ++j) {
            float f15 = f + (float)((i + 1) * 16);
            float f16 = f + (float)(i * 16);
            float f17 = f1 + (float)((j + 1) * 16);
            float f18 = f1 + (float)(j * 16);
            this.setLightmap(p_77010_1_, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
            float f19 = (float)(p_77010_4_ + p_77010_2_ - i * 16) / 256.0F;
            float f20 = (float)(p_77010_4_ + p_77010_2_ - (i + 1) * 16) / 256.0F;
            float f21 = (float)(p_77010_5_ + p_77010_3_ - j * 16) / 256.0F;
            float f22 = (float)(p_77010_5_ + p_77010_3_ - (j + 1) * 16) / 256.0F;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            bufferbuilder.pos((double)f15, (double)f18, -0.5D).tex((double)f20, (double)f21).normal(0.0F, 0.0F, -1.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f18, -0.5D).tex((double)f19, (double)f21).normal(0.0F, 0.0F, -1.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f17, -0.5D).tex((double)f19, (double)f22).normal(0.0F, 0.0F, -1.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f17, -0.5D).tex((double)f20, (double)f22).normal(0.0F, 0.0F, -1.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f17, 0.5D).tex(0.75D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f17, 0.5D).tex(0.8125D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f18, 0.5D).tex(0.8125D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f18, 0.5D).tex(0.75D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f17, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f17, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f17, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f17, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f18, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f18, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f18, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f18, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f17, 0.5D).tex((double)0.7519531F, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f18, 0.5D).tex((double)0.7519531F, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f18, -0.5D).tex((double)0.7519531F, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f15, (double)f17, -0.5D).tex((double)0.7519531F, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f17, -0.5D).tex((double)0.7519531F, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f18, -0.5D).tex((double)0.7519531F, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f18, 0.5D).tex((double)0.7519531F, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
            bufferbuilder.pos((double)f16, (double)f17, 0.5D).tex((double)0.7519531F, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
            tessellator.draw();
         }
      }

   }

   private void setLightmap(EntityPainting p_77008_1_, float p_77008_2_, float p_77008_3_) {
      int i = MathHelper.floor(p_77008_1_.posX);
      int j = MathHelper.floor(p_77008_1_.posY + (double)(p_77008_3_ / 16.0F));
      int k = MathHelper.floor(p_77008_1_.posZ);
      EnumFacing enumfacing = p_77008_1_.facingDirection;
      if (enumfacing == EnumFacing.NORTH) {
         i = MathHelper.floor(p_77008_1_.posX + (double)(p_77008_2_ / 16.0F));
      }

      if (enumfacing == EnumFacing.WEST) {
         k = MathHelper.floor(p_77008_1_.posZ - (double)(p_77008_2_ / 16.0F));
      }

      if (enumfacing == EnumFacing.SOUTH) {
         i = MathHelper.floor(p_77008_1_.posX - (double)(p_77008_2_ / 16.0F));
      }

      if (enumfacing == EnumFacing.EAST) {
         k = MathHelper.floor(p_77008_1_.posZ + (double)(p_77008_2_ / 16.0F));
      }

      int l = this.renderManager.world.getCombinedLight(new BlockPos(i, j, k), 0);
      int i1 = l % 65536;
      int j1 = l / 65536;
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)i1, (float)j1);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
   }
}
