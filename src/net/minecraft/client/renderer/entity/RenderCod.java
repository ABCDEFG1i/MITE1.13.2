package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelCod;
import net.minecraft.entity.passive.EntityCod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCod extends RenderLiving<EntityCod> {
   private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

   public RenderCod(RenderManager p_i48864_1_) {
      super(p_i48864_1_, new ModelCod(), 0.2F);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityCod p_110775_1_) {
      return COD_LOCATION;
   }

   protected void applyRotations(EntityCod p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      float f = 4.3F * MathHelper.sin(0.6F * p_77043_2_);
      GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
      if (!p_77043_1_.isInWater()) {
         GlStateManager.translatef(0.1F, 0.1F, -0.1F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
