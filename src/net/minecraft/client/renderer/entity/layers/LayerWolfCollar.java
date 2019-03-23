package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerWolfCollar implements LayerRenderer<EntityWolf> {
   private static final ResourceLocation WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
   private final RenderWolf wolfRenderer;

   public LayerWolfCollar(RenderWolf p_i46104_1_) {
      this.wolfRenderer = p_i46104_1_;
   }

   public void doRenderLayer(EntityWolf p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (p_177141_1_.isTamed() && !p_177141_1_.isInvisible()) {
         this.wolfRenderer.bindTexture(WOLF_COLLAR);
         float[] afloat = p_177141_1_.getCollarColor().getColorComponentValues();
         GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
         this.wolfRenderer.getMainModel().render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
