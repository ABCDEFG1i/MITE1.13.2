package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelTurtle extends ModelQuadruped {
   private final ModelRenderer field_203078_i;

   public ModelTurtle(float p_i48834_1_) {
      super(12, p_i48834_1_);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.head = new ModelRenderer(this, 3, 0);
      this.head.addBox(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
      this.head.setRotationPoint(0.0F, 19.0F, -10.0F);
      this.body = new ModelRenderer(this);
      this.body.setTextureOffset(7, 37).addBox(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
      this.body.setTextureOffset(31, 1).addBox(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
      this.body.setRotationPoint(0.0F, 11.0F, -10.0F);
      this.field_203078_i = new ModelRenderer(this);
      this.field_203078_i.setTextureOffset(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
      this.field_203078_i.setRotationPoint(0.0F, 11.0F, -10.0F);
      int i = 1;
      this.leg1 = new ModelRenderer(this, 1, 23);
      this.leg1.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.leg1.setRotationPoint(-3.5F, 22.0F, 11.0F);
      this.leg2 = new ModelRenderer(this, 1, 12);
      this.leg2.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.leg2.setRotationPoint(3.5F, 22.0F, 11.0F);
      this.leg3 = new ModelRenderer(this, 27, 30);
      this.leg3.addBox(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.leg3.setRotationPoint(-5.0F, 21.0F, -4.0F);
      this.leg4 = new ModelRenderer(this, 27, 24);
      this.leg4.addBox(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.leg4.setRotationPoint(5.0F, 21.0F, -4.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      EntityTurtle entityturtle = (EntityTurtle)p_78088_1_;
      if (this.isChild) {
         float f = 6.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.16666667F, 0.16666667F, 0.16666667F);
         GlStateManager.translatef(0.0F, 120.0F * p_78088_7_, 0.0F);
         this.head.render(p_78088_7_);
         this.body.render(p_78088_7_);
         this.leg1.render(p_78088_7_);
         this.leg2.render(p_78088_7_);
         this.leg3.render(p_78088_7_);
         this.leg4.render(p_78088_7_);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         if (entityturtle.func_203020_dx()) {
            GlStateManager.translatef(0.0F, -0.08F, 0.0F);
         }

         this.head.render(p_78088_7_);
         this.body.render(p_78088_7_);
         GlStateManager.pushMatrix();
         this.leg1.render(p_78088_7_);
         this.leg2.render(p_78088_7_);
         GlStateManager.popMatrix();
         this.leg3.render(p_78088_7_);
         this.leg4.render(p_78088_7_);
         if (entityturtle.func_203020_dx()) {
            this.field_203078_i.render(p_78088_7_);
         }

         GlStateManager.popMatrix();
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      EntityTurtle entityturtle = (EntityTurtle)p_78087_7_;
      this.leg1.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F * 0.6F) * 0.5F * p_78087_2_;
      this.leg2.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * p_78087_2_;
      this.leg3.rotateAngleZ = MathHelper.cos(p_78087_1_ * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * p_78087_2_;
      this.leg4.rotateAngleZ = MathHelper.cos(p_78087_1_ * 0.6662F * 0.6F) * 0.5F * p_78087_2_;
      this.leg3.rotateAngleX = 0.0F;
      this.leg4.rotateAngleX = 0.0F;
      this.leg3.rotateAngleY = 0.0F;
      this.leg4.rotateAngleY = 0.0F;
      this.leg1.rotateAngleY = 0.0F;
      this.leg2.rotateAngleY = 0.0F;
      this.field_203078_i.rotateAngleX = ((float)Math.PI / 2F);
      if (!entityturtle.isInWater() && entityturtle.onGround) {
         float f = entityturtle.func_203023_dy() ? 4.0F : 1.0F;
         float f1 = entityturtle.func_203023_dy() ? 2.0F : 1.0F;
         float f2 = 5.0F;
         this.leg3.rotateAngleY = MathHelper.cos(f * p_78087_1_ * 5.0F + (float)Math.PI) * 8.0F * p_78087_2_ * f1;
         this.leg3.rotateAngleZ = 0.0F;
         this.leg4.rotateAngleY = MathHelper.cos(f * p_78087_1_ * 5.0F) * 8.0F * p_78087_2_ * f1;
         this.leg4.rotateAngleZ = 0.0F;
         this.leg1.rotateAngleY = MathHelper.cos(p_78087_1_ * 5.0F + (float)Math.PI) * 3.0F * p_78087_2_;
         this.leg1.rotateAngleX = 0.0F;
         this.leg2.rotateAngleY = MathHelper.cos(p_78087_1_ * 5.0F) * 3.0F * p_78087_2_;
         this.leg2.rotateAngleX = 0.0F;
      }

   }
}
