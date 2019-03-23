package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerDolphinCarriedItem;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDolphin extends RenderLiving<EntityDolphin> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public RenderDolphin(RenderManager p_i48949_1_) {
      super(p_i48949_1_, new DolphinModel(), 0.7F);
      this.addLayer(new LayerDolphinCarriedItem(this));
   }

   protected ResourceLocation getEntityTexture(EntityDolphin p_110775_1_) {
      return DOLPHIN_LOCATION;
   }

   protected void preRenderCallback(EntityDolphin p_77041_1_, float p_77041_2_) {
      float f = 1.0F;
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
   }

   protected void applyRotations(EntityDolphin p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
   }
}
