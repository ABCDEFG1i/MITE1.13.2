package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderAbstractHorse<T extends AbstractHorse> extends RenderLiving<AbstractHorse> {
   private final float scale;

   public RenderAbstractHorse(RenderManager p_i48145_1_, ModelBase p_i48145_2_, float p_i48145_3_) {
      super(p_i48145_1_, p_i48145_2_, 0.75F);
      this.scale = p_i48145_3_;
   }

   protected void preRenderCallback(AbstractHorse p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
      super.preRenderCallback(p_77041_1_, p_77041_2_);
   }
}
