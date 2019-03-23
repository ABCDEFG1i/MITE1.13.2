package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerPhantomEyes;
import net.minecraft.client.renderer.entity.model.ModelPhantom;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPhantom extends RenderLiving<EntityPhantom> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public RenderPhantom(RenderManager p_i48829_1_) {
      super(p_i48829_1_, new ModelPhantom(), 0.75F);
      this.addLayer(new LayerPhantomEyes(this));
   }

   protected ResourceLocation getEntityTexture(EntityPhantom p_110775_1_) {
      return PHANTOM_LOCATION;
   }

   protected void preRenderCallback(EntityPhantom p_77041_1_, float p_77041_2_) {
      int i = p_77041_1_.func_203032_dq();
      float f = 1.0F + 0.15F * (float)i;
      GlStateManager.scalef(f, f, f);
      GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
   }

   protected void applyRotations(EntityPhantom p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      GlStateManager.rotatef(p_77043_1_.rotationPitch, 1.0F, 0.0F, 0.0F);
   }
}
