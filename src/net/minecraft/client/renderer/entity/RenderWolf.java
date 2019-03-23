package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWolfCollar;
import net.minecraft.client.renderer.entity.model.ModelWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderWolf extends RenderLiving<EntityWolf> {
   private static final ResourceLocation WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation TAMED_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation ANRGY_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

   public RenderWolf(RenderManager p_i47187_1_) {
      super(p_i47187_1_, new ModelWolf(), 0.5F);
      this.addLayer(new LayerWolfCollar(this));
   }

   protected float handleRotationFloat(EntityWolf p_77044_1_, float p_77044_2_) {
      return p_77044_1_.getTailRotation();
   }

   public void doRender(EntityWolf p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      if (p_76986_1_.isWolfWet()) {
         float f = p_76986_1_.getBrightness() * p_76986_1_.getShadingWhileWet(p_76986_9_);
         GlStateManager.color3f(f, f, f);
      }

      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation getEntityTexture(EntityWolf p_110775_1_) {
      if (p_110775_1_.isTamed()) {
         return TAMED_WOLF_TEXTURES;
      } else {
         return p_110775_1_.isAngry() ? ANRGY_WOLF_TEXTURES : WOLF_TEXTURES;
      }
   }
}
