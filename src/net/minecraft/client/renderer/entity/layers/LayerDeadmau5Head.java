package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerDeadmau5Head implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer playerRenderer;

   public LayerDeadmau5Head(RenderPlayer p_i46119_1_) {
      this.playerRenderer = p_i46119_1_;
   }

   public void doRenderLayer(AbstractClientPlayer p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if ("deadmau5".equals(p_177141_1_.getName().getString()) && p_177141_1_.hasSkin() && !p_177141_1_.isInvisible()) {
         this.playerRenderer.bindTexture(p_177141_1_.getLocationSkin());

         for(int i = 0; i < 2; ++i) {
            float f = p_177141_1_.prevRotationYaw + (p_177141_1_.rotationYaw - p_177141_1_.prevRotationYaw) * p_177141_4_ - (p_177141_1_.prevRenderYawOffset + (p_177141_1_.renderYawOffset - p_177141_1_.prevRenderYawOffset) * p_177141_4_);
            float f1 = p_177141_1_.prevRotationPitch + (p_177141_1_.rotationPitch - p_177141_1_.prevRotationPitch) * p_177141_4_;
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.375F * (float)(i * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.375F, 0.0F);
            GlStateManager.rotatef(-f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(-f, 0.0F, 1.0F, 0.0F);
            float f2 = 1.3333334F;
            GlStateManager.scalef(1.3333334F, 1.3333334F, 1.3333334F);
            this.playerRenderer.getMainModel().renderDeadmau5Head(0.0625F);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
