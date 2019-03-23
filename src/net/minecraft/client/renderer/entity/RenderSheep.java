package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.client.renderer.entity.model.ModelSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSheep extends RenderLiving<EntitySheep> {
   private static final ResourceLocation SHEARED_SHEEP_TEXTURES = new ResourceLocation("textures/entity/sheep/sheep.png");

   public RenderSheep(RenderManager p_i47195_1_) {
      super(p_i47195_1_, new ModelSheep(), 0.7F);
      this.addLayer(new LayerSheepWool(this));
   }

   protected ResourceLocation getEntityTexture(EntitySheep p_110775_1_) {
      return SHEARED_SHEEP_TEXTURES;
   }
}
