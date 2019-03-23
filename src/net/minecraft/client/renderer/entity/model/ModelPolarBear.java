package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPolarBear extends ModelQuadruped {
   public ModelPolarBear() {
      super(12, 0.0F);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
      this.head.setRotationPoint(0.0F, 10.0F, -16.0F);
      this.head.setTextureOffset(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
      this.head.setTextureOffset(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      ModelRenderer modelrenderer = this.head.setTextureOffset(26, 0);
      modelrenderer.mirror = true;
      modelrenderer.addBox(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      this.body = new ModelRenderer(this);
      this.body.setTextureOffset(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
      this.body.setTextureOffset(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
      this.body.setRotationPoint(-2.0F, 9.0F, 12.0F);
      int i = 10;
      this.leg1 = new ModelRenderer(this, 50, 22);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.leg1.setRotationPoint(-3.5F, 14.0F, 6.0F);
      this.leg2 = new ModelRenderer(this, 50, 22);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.leg2.setRotationPoint(3.5F, 14.0F, 6.0F);
      this.leg3 = new ModelRenderer(this, 50, 40);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.leg3.setRotationPoint(-2.5F, 14.0F, -7.0F);
      this.leg4 = new ModelRenderer(this, 50, 40);
      this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.leg4.setRotationPoint(2.5F, 14.0F, -7.0F);
      --this.leg1.rotationPointX;
      ++this.leg2.rotationPointX;
      this.leg1.rotationPointZ += 0.0F;
      this.leg2.rotationPointZ += 0.0F;
      --this.leg3.rotationPointX;
      ++this.leg4.rotationPointX;
      --this.leg3.rotationPointZ;
      --this.leg4.rotationPointZ;
      this.childZOffset += 2.0F;
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      if (this.isChild) {
         float f = 2.0F;
         this.childYOffset = 16.0F;
         this.childZOffset = 4.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, 0.6666667F, 0.6666667F);
         GlStateManager.translatef(0.0F, this.childYOffset * p_78088_7_, this.childZOffset * p_78088_7_);
         this.head.render(p_78088_7_);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * p_78088_7_, 0.0F);
         this.body.render(p_78088_7_);
         this.leg1.render(p_78088_7_);
         this.leg2.render(p_78088_7_);
         this.leg3.render(p_78088_7_);
         this.leg4.render(p_78088_7_);
         GlStateManager.popMatrix();
      } else {
         this.head.render(p_78088_7_);
         this.body.render(p_78088_7_);
         this.leg1.render(p_78088_7_);
         this.leg2.render(p_78088_7_);
         this.leg3.render(p_78088_7_);
         this.leg4.render(p_78088_7_);
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      float f = p_78087_3_ - (float)p_78087_7_.ticksExisted;
      float f1 = ((EntityPolarBear)p_78087_7_).getStandingAnimationScale(f);
      f1 = f1 * f1;
      float f2 = 1.0F - f1;
      this.body.rotateAngleX = ((float)Math.PI / 2F) - f1 * (float)Math.PI * 0.35F;
      this.body.rotationPointY = 9.0F * f2 + 11.0F * f1;
      this.leg3.rotationPointY = 14.0F * f2 - 6.0F * f1;
      this.leg3.rotationPointZ = -8.0F * f2 - 4.0F * f1;
      this.leg3.rotateAngleX -= f1 * (float)Math.PI * 0.45F;
      this.leg4.rotationPointY = this.leg3.rotationPointY;
      this.leg4.rotationPointZ = this.leg3.rotationPointZ;
      this.leg4.rotateAngleX -= f1 * (float)Math.PI * 0.45F;
      this.head.rotationPointY = 10.0F * f2 - 12.0F * f1;
      this.head.rotationPointZ = -16.0F * f2 - 3.0F * f1;
      this.head.rotateAngleX += f1 * (float)Math.PI * 0.15F;
   }
}
