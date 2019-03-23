package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelMagmaCube extends ModelBase {
   private final ModelRenderer[] segments = new ModelRenderer[8];
   private final ModelRenderer core;

   public ModelMagmaCube() {
      for(int i = 0; i < this.segments.length; ++i) {
         int j = 0;
         int k = i;
         if (i == 2) {
            j = 24;
            k = 10;
         } else if (i == 3) {
            j = 24;
            k = 19;
         }

         this.segments[i] = new ModelRenderer(this, j, k);
         this.segments[i].addBox(-4.0F, (float)(16 + i), -4.0F, 8, 1, 8);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }

   public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
      EntityMagmaCube entitymagmacube = (EntityMagmaCube)p_78086_1_;
      float f = entitymagmacube.prevSquishFactor + (entitymagmacube.squishFactor - entitymagmacube.prevSquishFactor) * p_78086_4_;
      if (f < 0.0F) {
         f = 0.0F;
      }

      for(int i = 0; i < this.segments.length; ++i) {
         this.segments[i].rotationPointY = (float)(-(4 - i)) * f * 1.7F;
      }

   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.core.render(p_78088_7_);

      for(ModelRenderer modelrenderer : this.segments) {
         modelrenderer.render(p_78088_7_);
      }

   }
}
