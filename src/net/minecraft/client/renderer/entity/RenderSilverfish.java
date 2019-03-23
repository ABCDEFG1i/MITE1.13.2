package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSilverfish extends RenderLiving<EntitySilverfish> {
   private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

   public RenderSilverfish(RenderManager p_i46144_1_) {
      super(p_i46144_1_, new ModelSilverfish(), 0.3F);
   }

   protected float getDeathMaxRotation(EntitySilverfish p_77037_1_) {
      return 180.0F;
   }

   protected ResourceLocation getEntityTexture(EntitySilverfish p_110775_1_) {
      return SILVERFISH_TEXTURES;
   }
}
