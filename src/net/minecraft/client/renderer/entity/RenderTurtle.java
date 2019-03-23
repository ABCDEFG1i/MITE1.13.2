package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.ModelTurtle;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTurtle extends RenderLiving<EntityTurtle> {
   private static final ResourceLocation field_203091_a = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public RenderTurtle(RenderManager p_i48827_1_) {
      super(p_i48827_1_, new ModelTurtle(0.0F), 0.35F);
   }

   public void doRender(EntityTurtle p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      if (p_76986_1_.isChild()) {
         this.shadowSize *= 0.5F;
      }

      super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityTurtle p_110775_1_) {
      return field_203091_a;
   }
}
