package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHorseArmorBase extends ModelBase {
   protected ModelRenderer field_199049_a;
   protected ModelRenderer field_199050_b;
   private final ModelRenderer field_199051_c;
   private final ModelRenderer field_199052_d;
   private final ModelRenderer field_199053_e;
   private final ModelRenderer field_199054_f;
   private final ModelRenderer field_199055_g;
   private final ModelRenderer[] field_199056_h;
   private final ModelRenderer[] field_209234_i;

   public ModelHorseArmorBase() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.field_199049_a = new ModelRenderer(this, 0, 32);
      this.field_199049_a.addBox(-5.0F, -8.0F, -17.0F, 10, 10, 22, 0.05F);
      this.field_199049_a.setRotationPoint(0.0F, 11.0F, 5.0F);
      this.field_199050_b = new ModelRenderer(this, 0, 35);
      this.field_199050_b.addBox(-2.05F, -6.0F, -2.0F, 4, 12, 7);
      this.field_199050_b.rotateAngleX = ((float)Math.PI / 6F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 13);
      modelrenderer.addBox(-3.0F, -11.0F, -2.0F, 6, 5, 7);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 56, 36);
      modelrenderer1.addBox(-1.0F, -11.0F, 5.01F, 2, 16, 2);
      ModelRenderer modelrenderer2 = new ModelRenderer(this, 0, 25);
      modelrenderer2.addBox(-2.0F, -11.0F, -7.0F, 4, 5, 5);
      this.field_199050_b.addChild(modelrenderer);
      this.field_199050_b.addChild(modelrenderer1);
      this.field_199050_b.addChild(modelrenderer2);
      this.func_199047_a(this.field_199050_b);
      this.field_199051_c = new ModelRenderer(this, 48, 21);
      this.field_199051_c.mirror = true;
      this.field_199051_c.addBox(-3.0F, -1.01F, -1.0F, 4, 11, 4);
      this.field_199051_c.setRotationPoint(4.0F, 14.0F, 7.0F);
      this.field_199052_d = new ModelRenderer(this, 48, 21);
      this.field_199052_d.addBox(-1.0F, -1.01F, -1.0F, 4, 11, 4);
      this.field_199052_d.setRotationPoint(-4.0F, 14.0F, 7.0F);
      this.field_199053_e = new ModelRenderer(this, 48, 21);
      this.field_199053_e.mirror = true;
      this.field_199053_e.addBox(-3.0F, -1.01F, -1.9F, 4, 11, 4);
      this.field_199053_e.setRotationPoint(4.0F, 6.0F, -12.0F);
      this.field_199054_f = new ModelRenderer(this, 48, 21);
      this.field_199054_f.addBox(-1.0F, -1.01F, -1.9F, 4, 11, 4);
      this.field_199054_f.setRotationPoint(-4.0F, 6.0F, -12.0F);
      this.field_199055_g = new ModelRenderer(this, 42, 36);
      this.field_199055_g.addBox(-1.5F, 0.0F, 0.0F, 3, 14, 4);
      this.field_199055_g.setRotationPoint(0.0F, -5.0F, 2.0F);
      this.field_199055_g.rotateAngleX = ((float)Math.PI / 6F);
      this.field_199049_a.addChild(this.field_199055_g);
      ModelRenderer modelrenderer3 = new ModelRenderer(this, 26, 0);
      modelrenderer3.addBox(-5.0F, -8.0F, -9.0F, 10, 9, 9, 0.5F);
      this.field_199049_a.addChild(modelrenderer3);
      ModelRenderer modelrenderer4 = new ModelRenderer(this, 29, 5);
      modelrenderer4.addBox(2.0F, -9.0F, -6.0F, 1, 2, 2);
      this.field_199050_b.addChild(modelrenderer4);
      ModelRenderer modelrenderer5 = new ModelRenderer(this, 29, 5);
      modelrenderer5.addBox(-3.0F, -9.0F, -6.0F, 1, 2, 2);
      this.field_199050_b.addChild(modelrenderer5);
      ModelRenderer modelrenderer6 = new ModelRenderer(this, 32, 2);
      modelrenderer6.addBox(3.1F, -6.0F, -8.0F, 0, 3, 16);
      modelrenderer6.rotateAngleX = (-(float)Math.PI / 6F);
      this.field_199050_b.addChild(modelrenderer6);
      ModelRenderer modelrenderer7 = new ModelRenderer(this, 32, 2);
      modelrenderer7.addBox(-3.1F, -6.0F, -8.0F, 0, 3, 16);
      modelrenderer7.rotateAngleX = (-(float)Math.PI / 6F);
      this.field_199050_b.addChild(modelrenderer7);
      ModelRenderer modelrenderer8 = new ModelRenderer(this, 1, 1);
      modelrenderer8.addBox(-3.0F, -11.0F, -1.9F, 6, 5, 6, 0.2F);
      this.field_199050_b.addChild(modelrenderer8);
      ModelRenderer modelrenderer9 = new ModelRenderer(this, 19, 0);
      modelrenderer9.addBox(-2.0F, -11.0F, -4.0F, 4, 5, 2, 0.2F);
      this.field_199050_b.addChild(modelrenderer9);
      this.field_199056_h = new ModelRenderer[]{modelrenderer3, modelrenderer4, modelrenderer5, modelrenderer8, modelrenderer9};
      this.field_209234_i = new ModelRenderer[]{modelrenderer6, modelrenderer7};
   }

   protected void func_199047_a(ModelRenderer p_199047_1_) {
      ModelRenderer modelrenderer = new ModelRenderer(this, 19, 16);
      modelrenderer.addBox(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 19, 16);
      modelrenderer1.addBox(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      p_199047_1_.addChild(modelrenderer);
      p_199047_1_.addChild(modelrenderer1);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      AbstractHorse abstracthorse = (AbstractHorse)p_78088_1_;
      boolean flag = abstracthorse.isChild();
      float f = abstracthorse.getHorseSize();
      boolean flag1 = abstracthorse.isHorseSaddled();
      boolean flag2 = abstracthorse.isBeingRidden();

      for(ModelRenderer modelrenderer : this.field_199056_h) {
         modelrenderer.showModel = flag1;
      }

      for(ModelRenderer modelrenderer1 : this.field_209234_i) {
         modelrenderer1.showModel = flag2 && flag1;
      }

      if (flag) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(f, 0.5F + f * 0.5F, f);
         GlStateManager.translatef(0.0F, 0.95F * (1.0F - f), 0.0F);
      }

      this.field_199051_c.render(p_78088_7_);
      this.field_199052_d.render(p_78088_7_);
      this.field_199053_e.render(p_78088_7_);
      this.field_199054_f.render(p_78088_7_);
      if (flag) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(f, f, f);
         GlStateManager.translatef(0.0F, 2.3F * (1.0F - f), 0.0F);
      }

      this.field_199049_a.render(p_78088_7_);
      if (flag) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float f1 = f + 0.1F * f;
         GlStateManager.scalef(f1, f1, f1);
         GlStateManager.translatef(0.0F, 2.25F * (1.0F - f1), 0.1F * (1.4F - f1));
      }

      this.field_199050_b.render(p_78088_7_);
      if (flag) {
         GlStateManager.popMatrix();
      }

   }

   public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
      super.setLivingAnimations(p_78086_1_, p_78086_2_, p_78086_3_, p_78086_4_);
      float f = this.func_199048_a(p_78086_1_.prevRenderYawOffset, p_78086_1_.renderYawOffset, p_78086_4_);
      float f1 = this.func_199048_a(p_78086_1_.prevRotationYawHead, p_78086_1_.rotationYawHead, p_78086_4_);
      float f2 = p_78086_1_.prevRotationPitch + (p_78086_1_.rotationPitch - p_78086_1_.prevRotationPitch) * p_78086_4_;
      float f3 = f1 - f;
      float f4 = f2 * ((float)Math.PI / 180F);
      if (f3 > 20.0F) {
         f3 = 20.0F;
      }

      if (f3 < -20.0F) {
         f3 = -20.0F;
      }

      if (p_78086_3_ > 0.2F) {
         f4 += MathHelper.cos(p_78086_2_ * 0.4F) * 0.15F * p_78086_3_;
      }

      AbstractHorse abstracthorse = (AbstractHorse)p_78086_1_;
      float f5 = abstracthorse.getGrassEatingAmount(p_78086_4_);
      float f6 = abstracthorse.getRearingAmount(p_78086_4_);
      float f7 = 1.0F - f6;
      float f8 = abstracthorse.getMouthOpennessAngle(p_78086_4_);
      boolean flag = abstracthorse.tailCounter != 0;
      float f9 = (float)p_78086_1_.ticksExisted + p_78086_4_;
      this.field_199050_b.rotationPointY = 4.0F;
      this.field_199050_b.rotationPointZ = -12.0F;
      this.field_199049_a.rotateAngleX = 0.0F;
      this.field_199050_b.rotateAngleX = ((float)Math.PI / 6F) + f4;
      this.field_199050_b.rotateAngleY = f3 * ((float)Math.PI / 180F);
      float f10 = abstracthorse.isInWater() ? 0.2F : 1.0F;
      float f11 = MathHelper.cos(f10 * p_78086_2_ * 0.6662F + (float)Math.PI);
      float f12 = f11 * 0.8F * p_78086_3_;
      float f13 = (1.0F - Math.max(f6, f5)) * (((float)Math.PI / 6F) + f4 + f8 * MathHelper.sin(f9) * 0.05F);
      this.field_199050_b.rotateAngleX = f6 * (0.2617994F + f4) + f5 * (2.1816616F + MathHelper.sin(f9) * 0.05F) + f13;
      this.field_199050_b.rotateAngleY = f6 * f3 * ((float)Math.PI / 180F) + (1.0F - Math.max(f6, f5)) * this.field_199050_b.rotateAngleY;
      this.field_199050_b.rotationPointY = f6 * -4.0F + f5 * 11.0F + (1.0F - Math.max(f6, f5)) * this.field_199050_b.rotationPointY;
      this.field_199050_b.rotationPointZ = f6 * -4.0F + f5 * -12.0F + (1.0F - Math.max(f6, f5)) * this.field_199050_b.rotationPointZ;
      this.field_199049_a.rotateAngleX = f6 * (-(float)Math.PI / 4F) + f7 * this.field_199049_a.rotateAngleX;
      float f14 = 0.2617994F * f6;
      float f15 = MathHelper.cos(f9 * 0.6F + (float)Math.PI);
      this.field_199053_e.rotationPointY = 2.0F * f6 + 14.0F * f7;
      this.field_199053_e.rotationPointZ = -6.0F * f6 - 10.0F * f7;
      this.field_199054_f.rotationPointY = this.field_199053_e.rotationPointY;
      this.field_199054_f.rotationPointZ = this.field_199053_e.rotationPointZ;
      float f16 = ((-(float)Math.PI / 3F) + f15) * f6 + f12 * f7;
      float f17 = ((-(float)Math.PI / 3F) - f15) * f6 - f12 * f7;
      this.field_199051_c.rotateAngleX = f14 - f11 * 0.5F * p_78086_3_ * f7;
      this.field_199052_d.rotateAngleX = f14 + f11 * 0.5F * p_78086_3_ * f7;
      this.field_199053_e.rotateAngleX = f16;
      this.field_199054_f.rotateAngleX = f17;
      this.field_199055_g.rotateAngleX = ((float)Math.PI / 6F) + p_78086_3_ * 0.75F;
      this.field_199055_g.rotationPointY = -5.0F + p_78086_3_;
      this.field_199055_g.rotationPointZ = 2.0F + p_78086_3_ * 2.0F;
      if (flag) {
         this.field_199055_g.rotateAngleY = MathHelper.cos(f9 * 0.7F);
      } else {
         this.field_199055_g.rotateAngleY = 0.0F;
      }

   }

   private float func_199048_a(float p_199048_1_, float p_199048_2_, float p_199048_3_) {
      float f;
      for(f = p_199048_2_ - p_199048_1_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_199048_1_ + p_199048_3_ * f;
   }
}
