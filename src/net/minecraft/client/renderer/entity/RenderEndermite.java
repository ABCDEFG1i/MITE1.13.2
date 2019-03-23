package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelEnderMite;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEndermite extends RenderLiving<EntityEndermite> {
   private static final ResourceLocation ENDERMITE_TEXTURES = new ResourceLocation("textures/entity/endermite.png");

   public RenderEndermite(RenderManager p_i46181_1_) {
      super(p_i46181_1_, new ModelEnderMite(), 0.3F);
   }

   protected float getDeathMaxRotation(EntityEndermite p_77037_1_) {
      return 180.0F;
   }

   protected ResourceLocation getEntityTexture(EntityEndermite p_110775_1_) {
      return ENDERMITE_TEXTURES;
   }
}
