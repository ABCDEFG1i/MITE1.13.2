package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelSkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerStrayClothing implements LayerRenderer<EntityStray> {
   private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final RenderLivingBase<?> renderer;
   private final ModelSkeleton layerModel = new ModelSkeleton(0.25F, true);

   public LayerStrayClothing(RenderLivingBase<?> p_i47183_1_) {
      this.renderer = p_i47183_1_;
   }

   public void doRenderLayer(EntityStray p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      this.layerModel.setModelAttributes(this.renderer.getMainModel());
      this.layerModel.setLivingAnimations(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderer.bindTexture(STRAY_CLOTHES_TEXTURES);
      this.layerModel.render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
