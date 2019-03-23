package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerTropicalFishPattern;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishA;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishB;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTropicalFish extends RenderLiving<EntityTropicalFish> {
   private final ModelTropicalFishA field_204246_a = new ModelTropicalFishA();
   private final ModelTropicalFishB field_204247_j = new ModelTropicalFishB();

   public RenderTropicalFish(RenderManager p_i48889_1_) {
      super(p_i48889_1_, new ModelTropicalFishA(), 0.15F);
      this.addLayer(new LayerTropicalFishPattern(this));
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityTropicalFish p_110775_1_) {
      return p_110775_1_.getBodyTexture();
   }

   public void doRender(EntityTropicalFish p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.mainModel = (ModelBase)(p_76986_1_.getSize() == 0 ? this.field_204246_a : this.field_204247_j);
      float[] afloat = p_76986_1_.func_204219_dC();
      GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected void applyRotations(EntityTropicalFish p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      float f = 4.3F * MathHelper.sin(0.6F * p_77043_2_);
      GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
      if (!p_77043_1_.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
