package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderIronGolem extends RenderLiving<EntityIronGolem> {
   private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation("textures/entity/iron_golem.png");

   public RenderIronGolem(RenderManager p_i46133_1_) {
      super(p_i46133_1_, new ModelIronGolem(), 0.5F);
      this.addLayer(new LayerIronGolemFlower(this));
   }

   protected ResourceLocation getEntityTexture(EntityIronGolem p_110775_1_) {
      return IRON_GOLEM_TEXTURES;
   }

   protected void applyRotations(EntityIronGolem p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
      super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
      if (!((double)p_77043_1_.limbSwingAmount < 0.01D)) {
         float f = 13.0F;
         float f1 = p_77043_1_.limbSwing - p_77043_1_.limbSwingAmount * (1.0F - p_77043_4_) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         GlStateManager.rotatef(6.5F * f2, 0.0F, 0.0F, 1.0F);
      }
   }
}
