package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBiped<T extends EntityLiving> extends RenderLiving<T> {
   private static final ResourceLocation DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");

   public RenderBiped(RenderManager p_i46168_1_, ModelBiped p_i46168_2_, float p_i46168_3_) {
      super(p_i46168_1_, p_i46168_2_, p_i46168_3_);
      this.addLayer(new LayerCustomHead(p_i46168_2_.bipedHead));
      this.addLayer(new LayerElytra(this));
      this.addLayer(new LayerHeldItem(this));
   }

   protected ResourceLocation getEntityTexture(T p_110775_1_) {
      return DEFAULT_RES_LOC;
   }
}
