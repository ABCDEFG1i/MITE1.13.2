package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSnowmanHead;
import net.minecraft.client.renderer.entity.model.ModelSnowMan;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSnowMan extends RenderLiving<EntitySnowman> {
   private static final ResourceLocation SNOW_MAN_TEXTURES = new ResourceLocation("textures/entity/snow_golem.png");

   public RenderSnowMan(RenderManager p_i46140_1_) {
      super(p_i46140_1_, new ModelSnowMan(), 0.5F);
      this.addLayer(new LayerSnowmanHead(this));
   }

   protected ResourceLocation getEntityTexture(EntitySnowman p_110775_1_) {
      return SNOW_MAN_TEXTURES;
   }

   public ModelSnowMan getMainModel() {
      return (ModelSnowMan)super.getMainModel();
   }
}
