package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelChicken;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChicken extends RenderLiving<EntityChicken> {
   private static final ResourceLocation CHICKEN_TEXTURES = new ResourceLocation("textures/entity/chicken.png");

   public RenderChicken(RenderManager p_i47211_1_) {
      super(p_i47211_1_, new ModelChicken(), 0.3F);
   }

   protected ResourceLocation getEntityTexture(EntityChicken p_110775_1_) {
      return CHICKEN_TEXTURES;
   }

   protected float handleRotationFloat(EntityChicken p_77044_1_, float p_77044_2_) {
      float f = p_77044_1_.oFlap + (p_77044_1_.wingRotation - p_77044_1_.oFlap) * p_77044_2_;
      float f1 = p_77044_1_.oFlapSpeed + (p_77044_1_.destPos - p_77044_1_.oFlapSpeed) * p_77044_2_;
      return (MathHelper.sin(f) + 1.0F) * f1;
   }
}
