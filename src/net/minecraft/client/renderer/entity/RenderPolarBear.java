package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelPolarBear;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPolarBear extends RenderLiving<EntityPolarBear> {
   private static final ResourceLocation POLAR_BEAR_TEXTURE = new ResourceLocation("textures/entity/bear/polarbear.png");

   public RenderPolarBear(RenderManager p_i47197_1_) {
      super(p_i47197_1_, new ModelPolarBear(), 0.7F);
   }

   protected ResourceLocation getEntityTexture(EntityPolarBear p_110775_1_) {
      return POLAR_BEAR_TEXTURE;
   }

   public void doRender(EntityPolarBear p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected void preRenderCallback(EntityPolarBear p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
      super.preRenderCallback(p_77041_1_, p_77041_2_);
   }
}
