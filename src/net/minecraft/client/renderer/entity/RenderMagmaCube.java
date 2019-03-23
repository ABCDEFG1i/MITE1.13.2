package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelMagmaCube;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMagmaCube extends RenderLiving<EntityMagmaCube> {
   private static final ResourceLocation MAGMA_CUBE_TEXTURES = new ResourceLocation("textures/entity/slime/magmacube.png");

   public RenderMagmaCube(RenderManager p_i46159_1_) {
      super(p_i46159_1_, new ModelMagmaCube(), 0.25F);
   }

   protected ResourceLocation getEntityTexture(EntityMagmaCube p_110775_1_) {
      return MAGMA_CUBE_TEXTURES;
   }

   protected void preRenderCallback(EntityMagmaCube p_77041_1_, float p_77041_2_) {
      int i = p_77041_1_.getSlimeSize();
      float f = (p_77041_1_.prevSquishFactor + (p_77041_1_.squishFactor - p_77041_1_.prevSquishFactor) * p_77041_2_) / ((float)i * 0.5F + 1.0F);
      float f1 = 1.0F / (f + 1.0F);
      GlStateManager.scalef(f1 * (float)i, 1.0F / f1 * (float)i, f1 * (float)i);
   }
}
