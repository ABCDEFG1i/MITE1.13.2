package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelShulkerBullet;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShulkerBullet extends Render<EntityShulkerBullet> {
   private static final ResourceLocation SHULKER_SPARK_TEXTURE = new ResourceLocation("textures/entity/shulker/spark.png");
   private final ModelShulkerBullet model = new ModelShulkerBullet();

   public RenderShulkerBullet(RenderManager p_i46551_1_) {
      super(p_i46551_1_);
   }

   private float rotLerp(float p_188347_1_, float p_188347_2_, float p_188347_3_) {
      float f;
      for(f = p_188347_2_ - p_188347_1_; f < -180.0F; f += 360.0F) {
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_188347_1_ + p_188347_3_ * f;
   }

   public void doRender(EntityShulkerBullet p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.pushMatrix();
      float f = this.rotLerp(p_76986_1_.prevRotationYaw, p_76986_1_.rotationYaw, p_76986_9_);
      float f1 = p_76986_1_.prevRotationPitch + (p_76986_1_.rotationPitch - p_76986_1_.prevRotationPitch) * p_76986_9_;
      float f2 = (float)p_76986_1_.ticksExisted + p_76986_9_;
      GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_ + 0.15F, (float)p_76986_6_);
      GlStateManager.rotatef(MathHelper.sin(f2 * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(MathHelper.cos(f2 * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(MathHelper.sin(f2 * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
      float f3 = 0.03125F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.bindEntityTexture(p_76986_1_);
      this.model.render(p_76986_1_, 0.0F, 0.0F, 0.0F, f, f1, 0.03125F);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.scalef(1.5F, 1.5F, 1.5F);
      this.model.render(p_76986_1_, 0.0F, 0.0F, 0.0F, f, f1, 0.03125F);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityShulkerBullet p_110775_1_) {
      return SHULKER_SPARK_TEXTURE;
   }
}
