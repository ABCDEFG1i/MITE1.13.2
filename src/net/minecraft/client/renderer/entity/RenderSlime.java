package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerSlimeGel;
import net.minecraft.client.renderer.entity.model.ModelSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSlime extends RenderLiving<EntitySlime> {
   private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");

   public RenderSlime(RenderManager p_i47193_1_) {
      super(p_i47193_1_, new ModelSlime(16), 0.25F);
      this.addLayer(new LayerSlimeGel(this));
   }

   public void doRender(EntitySlime p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.shadowSize = 0.25F * (float)p_76986_1_.getSlimeSize();
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected void preRenderCallback(EntitySlime p_77041_1_, float p_77041_2_) {
      float f = 0.999F;
      GlStateManager.scalef(0.999F, 0.999F, 0.999F);
      float f1 = (float)p_77041_1_.getSlimeSize();
      float f2 = (p_77041_1_.prevSquishFactor + (p_77041_1_.squishFactor - p_77041_1_.prevSquishFactor) * p_77041_2_) / (f1 * 0.5F + 1.0F);
      float f3 = 1.0F / (f2 + 1.0F);
      GlStateManager.scalef(f3 * f1, 1.0F / f3 * f1, f3 * f1);
   }

   protected ResourceLocation getEntityTexture(EntitySlime p_110775_1_) {
      return SLIME_TEXTURES;
   }
}
