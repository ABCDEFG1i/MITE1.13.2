package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderLiving<T extends EntityLiving> extends RenderLivingBase<T> {
   public RenderLiving(RenderManager p_i46153_1_, ModelBase p_i46153_2_, float p_i46153_3_) {
      super(p_i46153_1_, p_i46153_2_, p_i46153_3_);
   }

   protected boolean canRenderName(T p_177070_1_) {
      return super.canRenderName(p_177070_1_) && (p_177070_1_.getAlwaysRenderNameTagForRender() || p_177070_1_.hasCustomName() && p_177070_1_ == this.renderManager.pointedEntity);
   }

   public boolean shouldRender(T p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      if (super.shouldRender(p_177071_1_, p_177071_2_, p_177071_3_, p_177071_5_, p_177071_7_)) {
         return true;
      } else if (p_177071_1_.getLeashed() && p_177071_1_.getLeashHolder() != null) {
         Entity entity = p_177071_1_.getLeashHolder();
         return p_177071_2_.isBoundingBoxInFrustum(entity.getRenderBoundingBox());
      } else {
         return false;
      }
   }

   public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      if (!this.renderOutlines) {
         this.renderLeash(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      }

   }

   public void setLightmap(T p_177105_1_) {
      int i = p_177105_1_.getBrightnessForRender();
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
   }

   private double interpolateValue(double p_110828_1_, double p_110828_3_, double p_110828_5_) {
      return p_110828_1_ + (p_110828_3_ - p_110828_1_) * p_110828_5_;
   }

   protected void renderLeash(T p_110827_1_, double p_110827_2_, double p_110827_4_, double p_110827_6_, float p_110827_8_, float p_110827_9_) {
      Entity entity = p_110827_1_.getLeashHolder();
      if (entity != null) {
         p_110827_4_ = p_110827_4_ - (1.6D - (double)p_110827_1_.height) * 0.5D;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         double d0 = this.interpolateValue((double)entity.prevRotationYaw, (double)entity.rotationYaw, (double)(p_110827_9_ * 0.5F)) * (double)((float)Math.PI / 180F);
         double d1 = this.interpolateValue((double)entity.prevRotationPitch, (double)entity.rotationPitch, (double)(p_110827_9_ * 0.5F)) * (double)((float)Math.PI / 180F);
         double d2 = Math.cos(d0);
         double d3 = Math.sin(d0);
         double d4 = Math.sin(d1);
         if (entity instanceof EntityHanging) {
            d2 = 0.0D;
            d3 = 0.0D;
            d4 = -1.0D;
         }

         double d5 = Math.cos(d1);
         double d6 = this.interpolateValue(entity.prevPosX, entity.posX, (double)p_110827_9_) - d2 * 0.7D - d3 * 0.5D * d5;
         double d7 = this.interpolateValue(entity.prevPosY + (double)entity.getEyeHeight() * 0.7D, entity.posY + (double)entity.getEyeHeight() * 0.7D, (double)p_110827_9_) - d4 * 0.5D - 0.25D;
         double d8 = this.interpolateValue(entity.prevPosZ, entity.posZ, (double)p_110827_9_) - d3 * 0.7D + d2 * 0.5D * d5;
         double d9 = this.interpolateValue((double)p_110827_1_.prevRenderYawOffset, (double)p_110827_1_.renderYawOffset, (double)p_110827_9_) * (double)((float)Math.PI / 180F) + (Math.PI / 2D);
         d2 = Math.cos(d9) * (double)p_110827_1_.width * 0.4D;
         d3 = Math.sin(d9) * (double)p_110827_1_.width * 0.4D;
         double d10 = this.interpolateValue(p_110827_1_.prevPosX, p_110827_1_.posX, (double)p_110827_9_) + d2;
         double d11 = this.interpolateValue(p_110827_1_.prevPosY, p_110827_1_.posY, (double)p_110827_9_);
         double d12 = this.interpolateValue(p_110827_1_.prevPosZ, p_110827_1_.posZ, (double)p_110827_9_) + d3;
         p_110827_2_ = p_110827_2_ + d2;
         p_110827_6_ = p_110827_6_ + d3;
         double d13 = (double)((float)(d6 - d10));
         double d14 = (double)((float)(d7 - d11));
         double d15 = (double)((float)(d8 - d12));
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         int i = 24;
         double d16 = 0.025D;
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

         for(int j = 0; j <= 24; ++j) {
            float f = 0.5F;
            float f1 = 0.4F;
            float f2 = 0.3F;
            if (j % 2 == 0) {
               f *= 0.7F;
               f1 *= 0.7F;
               f2 *= 0.7F;
            }

            float f3 = (float)j / 24.0F;
            bufferbuilder.pos(p_110827_2_ + d13 * (double)f3 + 0.0D, p_110827_4_ + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F), p_110827_6_ + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
            bufferbuilder.pos(p_110827_2_ + d13 * (double)f3 + 0.025D, p_110827_4_ + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F) + 0.025D, p_110827_6_ + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
         }

         tessellator.draw();
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

         for(int k = 0; k <= 24; ++k) {
            float f4 = 0.5F;
            float f5 = 0.4F;
            float f6 = 0.3F;
            if (k % 2 == 0) {
               f4 *= 0.7F;
               f5 *= 0.7F;
               f6 *= 0.7F;
            }

            float f7 = (float)k / 24.0F;
            bufferbuilder.pos(p_110827_2_ + d13 * (double)f7 + 0.0D, p_110827_4_ + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F) + 0.025D, p_110827_6_ + d15 * (double)f7).color(f4, f5, f6, 1.0F).endVertex();
            bufferbuilder.pos(p_110827_2_ + d13 * (double)f7 + 0.025D, p_110827_4_ + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F), p_110827_6_ + d15 * (double)f7 + 0.025D).color(f4, f5, f6, 1.0F).endVertex();
         }

         tessellator.draw();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableCull();
      }
   }
}
