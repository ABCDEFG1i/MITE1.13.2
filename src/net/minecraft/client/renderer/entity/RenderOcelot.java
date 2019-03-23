package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelOcelot;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderOcelot extends RenderLiving<EntityOcelot> {
   private static final ResourceLocation BLACK_OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/black.png");
   private static final ResourceLocation OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/ocelot.png");
   private static final ResourceLocation RED_OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/red.png");
   private static final ResourceLocation SIAMESE_OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/siamese.png");

   public RenderOcelot(RenderManager p_i47199_1_) {
      super(p_i47199_1_, new ModelOcelot(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityOcelot p_110775_1_) {
      switch(p_110775_1_.getTameSkin()) {
      case 0:
      default:
         return OCELOT_TEXTURES;
      case 1:
         return BLACK_OCELOT_TEXTURES;
      case 2:
         return RED_OCELOT_TEXTURES;
      case 3:
         return SIAMESE_OCELOT_TEXTURES;
      }
   }

   protected void preRenderCallback(EntityOcelot p_77041_1_, float p_77041_2_) {
      super.preRenderCallback(p_77041_1_, p_77041_2_);
      if (p_77041_1_.isTamed()) {
         GlStateManager.scalef(0.8F, 0.8F, 0.8F);
      }

   }
}
