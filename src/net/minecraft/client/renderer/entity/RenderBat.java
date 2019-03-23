package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBat;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBat extends RenderLiving<EntityBat> {
   private static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

   public RenderBat(RenderManager p_i46192_1_) {
      super(p_i46192_1_, new ModelBat(), 0.25F);
   }

   protected ResourceLocation getEntityTexture(EntityBat p_110775_1_) {
      return BAT_TEXTURES;
   }

   protected void preRenderCallback(EntityBat p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(0.35F, 0.35F, 0.35F);
   }

   protected void applyRotations(EntityBat p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      if (p_77043_1_.getIsBatHanging()) {
         GlStateManager.translatef(0.0F, -0.1F, 0.0F);
      } else {
         GlStateManager.translatef(0.0F, MathHelper.cos(p_77043_2_ * 0.3F) * 0.1F, 0.0F);
      }

      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
   }
}
