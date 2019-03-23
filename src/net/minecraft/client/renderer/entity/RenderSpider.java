package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.client.renderer.entity.model.ModelSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSpider<T extends EntitySpider> extends RenderLiving<T> {
   private static final ResourceLocation SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/spider.png");

   public RenderSpider(RenderManager p_i46139_1_) {
      super(p_i46139_1_, new ModelSpider(), 1.0F);
      this.addLayer(new LayerSpiderEyes(this));
   }

   protected float getDeathMaxRotation(T p_77037_1_) {
      return 180.0F;
   }

   protected ResourceLocation getEntityTexture(T p_110775_1_) {
      return SPIDER_TEXTURES;
   }
}
