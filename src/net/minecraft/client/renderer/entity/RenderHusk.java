package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHusk extends RenderZombie {
   private static final ResourceLocation HUSK_ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/husk.png");

   public RenderHusk(RenderManager p_i47204_1_) {
      super(p_i47204_1_);
   }

   protected void preRenderCallback(EntityZombie p_77041_1_, float p_77041_2_) {
      float f = 1.0625F;
      GlStateManager.scalef(1.0625F, 1.0625F, 1.0625F);
      super.preRenderCallback(p_77041_1_, p_77041_2_);
   }

   protected ResourceLocation getEntityTexture(EntityZombie p_110775_1_) {
      return HUSK_ZOMBIE_TEXTURES;
   }
}
