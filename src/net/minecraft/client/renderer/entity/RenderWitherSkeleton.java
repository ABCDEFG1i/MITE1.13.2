package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderWitherSkeleton extends RenderSkeleton {
   private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public RenderWitherSkeleton(RenderManager p_i47188_1_) {
      super(p_i47188_1_);
   }

   protected ResourceLocation getEntityTexture(AbstractSkeleton p_110775_1_) {
      return WITHER_SKELETON_TEXTURES;
   }

   protected void preRenderCallback(AbstractSkeleton p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
   }
}
