package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelSalmon;
import net.minecraft.entity.passive.EntitySalmon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSalmon extends RenderLiving<EntitySalmon> {
   private static final ResourceLocation field_203776_a = new ResourceLocation("textures/entity/fish/salmon.png");

   public RenderSalmon(RenderManager p_i48862_1_) {
      super(p_i48862_1_, new ModelSalmon(), 0.2F);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntitySalmon p_110775_1_) {
      return field_203776_a;
   }

   protected void applyRotations(EntitySalmon p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      float f = 1.0F;
      float f1 = 1.0F;
      if (!p_77043_1_.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      float f2 = f * 4.3F * MathHelper.sin(f1 * 0.6F * p_77043_2_);
      GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, -0.4F);
      if (!p_77043_1_.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
