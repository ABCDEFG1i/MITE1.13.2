package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelParrot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderParrot extends RenderLiving<EntityParrot> {
   public static final ResourceLocation[] PARROT_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png")};

   public RenderParrot(RenderManager p_i47375_1_) {
      super(p_i47375_1_, new ModelParrot(), 0.3F);
   }

   protected ResourceLocation getEntityTexture(EntityParrot p_110775_1_) {
      return PARROT_TEXTURES[p_110775_1_.getVariant()];
   }

   public float handleRotationFloat(EntityParrot p_77044_1_, float p_77044_2_) {
      return this.getCustomBob(p_77044_1_, p_77044_2_);
   }

   private float getCustomBob(EntityParrot p_192861_1_, float p_192861_2_) {
      float f = p_192861_1_.oFlap + (p_192861_1_.flap - p_192861_1_.oFlap) * p_192861_2_;
      float f1 = p_192861_1_.oFlapSpeed + (p_192861_1_.flapSpeed - p_192861_1_.oFlapSpeed) * p_192861_2_;
      return (MathHelper.sin(f) + 1.0F) * f1;
   }
}
