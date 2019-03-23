package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelBlaze;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBlaze extends RenderLiving<EntityBlaze> {
   private static final ResourceLocation BLAZE_TEXTURES = new ResourceLocation("textures/entity/blaze.png");

   public RenderBlaze(RenderManager p_i46191_1_) {
      super(p_i46191_1_, new ModelBlaze(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityBlaze p_110775_1_) {
      return BLAZE_TEXTURES;
   }
}
