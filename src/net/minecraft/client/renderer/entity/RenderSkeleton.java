package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSkeleton extends RenderBiped<AbstractSkeleton> {
   private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public RenderSkeleton(RenderManager p_i46143_1_) {
      super(p_i46143_1_, new ModelSkeleton(), 0.5F);
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerBipedArmor(this) {
         protected void initArmor() {
            this.modelLeggings = new ModelSkeleton(0.5F, true);
            this.modelArmor = new ModelSkeleton(1.0F, true);
         }
      });
   }

   protected ResourceLocation getEntityTexture(AbstractSkeleton p_110775_1_) {
      return SKELETON_TEXTURES;
   }
}
