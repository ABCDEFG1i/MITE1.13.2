package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderAreaEffectCloud extends Render<EntityAreaEffectCloud> {
   public RenderAreaEffectCloud(RenderManager p_i46554_1_) {
      super(p_i46554_1_);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityAreaEffectCloud p_110775_1_) {
      return null;
   }
}
