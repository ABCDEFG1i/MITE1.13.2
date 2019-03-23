package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerLlamaDecor;
import net.minecraft.client.renderer.entity.model.ModelLlama;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderLlama extends RenderLiving<EntityLlama> {
   private static final ResourceLocation[] LLAMA_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/creamy.png"), new ResourceLocation("textures/entity/llama/white.png"), new ResourceLocation("textures/entity/llama/brown.png"), new ResourceLocation("textures/entity/llama/gray.png")};

   public RenderLlama(RenderManager p_i47203_1_) {
      super(p_i47203_1_, new ModelLlama(0.0F), 0.7F);
      this.addLayer(new LayerLlamaDecor(this));
   }

   protected ResourceLocation getEntityTexture(EntityLlama p_110775_1_) {
      return LLAMA_TEXTURES[p_110775_1_.getVariant()];
   }
}
