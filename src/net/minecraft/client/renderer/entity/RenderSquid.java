package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSquid;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSquid extends RenderLiving<EntitySquid> {
   private static final ResourceLocation SQUID_TEXTURES = new ResourceLocation("textures/entity/squid.png");

   public RenderSquid(RenderManager p_i47192_1_) {
      super(p_i47192_1_, new ModelSquid(), 0.7F);
   }

   protected ResourceLocation getEntityTexture(EntitySquid p_110775_1_) {
      return SQUID_TEXTURES;
   }

   protected void applyRotations(EntitySquid p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      float f = p_77043_1_.prevSquidPitch + (p_77043_1_.squidPitch - p_77043_1_.prevSquidPitch) * p_77043_4_;
      float f1 = p_77043_1_.prevSquidYaw + (p_77043_1_.squidYaw - p_77043_1_.prevSquidYaw) * p_77043_4_;
      GlStateManager.translatef(0.0F, 0.5F, 0.0F);
      GlStateManager.rotatef(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(f, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, -1.2F, 0.0F);
   }

   protected float handleRotationFloat(EntitySquid p_77044_1_, float p_77044_2_) {
      return p_77044_1_.lastTentacleAngle + (p_77044_1_.tentacleAngle - p_77044_1_.lastTentacleAngle) * p_77044_2_;
   }
}
