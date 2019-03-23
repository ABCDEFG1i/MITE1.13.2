package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCaveSpider extends RenderSpider<EntityCaveSpider> {
   private static final ResourceLocation CAVE_SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public RenderCaveSpider(RenderManager p_i46189_1_) {
      super(p_i46189_1_);
      this.shadowSize *= 0.7F;
   }

   protected void preRenderCallback(EntityCaveSpider p_77041_1_, float p_77041_2_) {
      GlStateManager.scalef(0.7F, 0.7F, 0.7F);
   }

   protected ResourceLocation getEntityTexture(EntityCaveSpider p_110775_1_) {
      return CAVE_SPIDER_TEXTURES;
   }
}
