package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderTropicalFish;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishA;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishB;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerTropicalFishPattern implements LayerRenderer<EntityTropicalFish> {
   private final RenderTropicalFish field_204250_a;
   private final ModelTropicalFishA modelA;
   private final ModelTropicalFishB modelB;

   public LayerTropicalFishPattern(RenderTropicalFish p_i48887_1_) {
      this.field_204250_a = p_i48887_1_;
      this.modelA = new ModelTropicalFishA(0.008F);
      this.modelB = new ModelTropicalFishB(0.008F);
   }

   public void doRenderLayer(EntityTropicalFish p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if (!p_177141_1_.isInvisible()) {
         ModelBase modelbase = p_177141_1_.getSize() == 0 ? this.modelA : this.modelB;
         this.field_204250_a.bindTexture(p_177141_1_.getPatternTexture());
         float[] afloat = p_177141_1_.func_204222_dD();
         GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
         modelbase.setModelAttributes(this.field_204250_a.getMainModel());
         modelbase.setLivingAnimations(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_);
         modelbase.render(p_177141_1_, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
