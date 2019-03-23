package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSpider extends ModelBase {
   private final ModelRenderer spiderHead;
   private final ModelRenderer spiderNeck;
   private final ModelRenderer spiderBody;
   private final ModelRenderer spiderLeg1;
   private final ModelRenderer spiderLeg2;
   private final ModelRenderer spiderLeg3;
   private final ModelRenderer spiderLeg4;
   private final ModelRenderer spiderLeg5;
   private final ModelRenderer spiderLeg6;
   private final ModelRenderer spiderLeg7;
   private final ModelRenderer spiderLeg8;

   public ModelSpider() {
      float f = 0.0F;
      int i = 15;
      this.spiderHead = new ModelRenderer(this, 32, 4);
      this.spiderHead.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
      this.spiderHead.setRotationPoint(0.0F, 15.0F, -3.0F);
      this.spiderNeck = new ModelRenderer(this, 0, 0);
      this.spiderNeck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
      this.spiderNeck.setRotationPoint(0.0F, 15.0F, 0.0F);
      this.spiderBody = new ModelRenderer(this, 0, 12);
      this.spiderBody.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
      this.spiderBody.setRotationPoint(0.0F, 15.0F, 9.0F);
      this.spiderLeg1 = new ModelRenderer(this, 18, 0);
      this.spiderLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg1.setRotationPoint(-4.0F, 15.0F, 2.0F);
      this.spiderLeg2 = new ModelRenderer(this, 18, 0);
      this.spiderLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg2.setRotationPoint(4.0F, 15.0F, 2.0F);
      this.spiderLeg3 = new ModelRenderer(this, 18, 0);
      this.spiderLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg3.setRotationPoint(-4.0F, 15.0F, 1.0F);
      this.spiderLeg4 = new ModelRenderer(this, 18, 0);
      this.spiderLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg4.setRotationPoint(4.0F, 15.0F, 1.0F);
      this.spiderLeg5 = new ModelRenderer(this, 18, 0);
      this.spiderLeg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg5.setRotationPoint(-4.0F, 15.0F, 0.0F);
      this.spiderLeg6 = new ModelRenderer(this, 18, 0);
      this.spiderLeg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg6.setRotationPoint(4.0F, 15.0F, 0.0F);
      this.spiderLeg7 = new ModelRenderer(this, 18, 0);
      this.spiderLeg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg7.setRotationPoint(-4.0F, 15.0F, -1.0F);
      this.spiderLeg8 = new ModelRenderer(this, 18, 0);
      this.spiderLeg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.spiderLeg8.setRotationPoint(4.0F, 15.0F, -1.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.spiderHead.render(p_78088_7_);
      this.spiderNeck.render(p_78088_7_);
      this.spiderBody.render(p_78088_7_);
      this.spiderLeg1.render(p_78088_7_);
      this.spiderLeg2.render(p_78088_7_);
      this.spiderLeg3.render(p_78088_7_);
      this.spiderLeg4.render(p_78088_7_);
      this.spiderLeg5.render(p_78088_7_);
      this.spiderLeg6.render(p_78088_7_);
      this.spiderLeg7.render(p_78088_7_);
      this.spiderLeg8.render(p_78088_7_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      this.spiderHead.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.spiderHead.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      float f = ((float)Math.PI / 4F);
      this.spiderLeg1.rotateAngleZ = (-(float)Math.PI / 4F);
      this.spiderLeg2.rotateAngleZ = ((float)Math.PI / 4F);
      this.spiderLeg3.rotateAngleZ = -0.58119464F;
      this.spiderLeg4.rotateAngleZ = 0.58119464F;
      this.spiderLeg5.rotateAngleZ = -0.58119464F;
      this.spiderLeg6.rotateAngleZ = 0.58119464F;
      this.spiderLeg7.rotateAngleZ = (-(float)Math.PI / 4F);
      this.spiderLeg8.rotateAngleZ = ((float)Math.PI / 4F);
      float f1 = -0.0F;
      float f2 = ((float)Math.PI / 8F);
      this.spiderLeg1.rotateAngleY = ((float)Math.PI / 4F);
      this.spiderLeg2.rotateAngleY = (-(float)Math.PI / 4F);
      this.spiderLeg3.rotateAngleY = ((float)Math.PI / 8F);
      this.spiderLeg4.rotateAngleY = (-(float)Math.PI / 8F);
      this.spiderLeg5.rotateAngleY = (-(float)Math.PI / 8F);
      this.spiderLeg6.rotateAngleY = ((float)Math.PI / 8F);
      this.spiderLeg7.rotateAngleY = (-(float)Math.PI / 4F);
      this.spiderLeg8.rotateAngleY = ((float)Math.PI / 4F);
      float f3 = -(MathHelper.cos(p_78087_1_ * 0.6662F * 2.0F + 0.0F) * 0.4F) * p_78087_2_;
      float f4 = -(MathHelper.cos(p_78087_1_ * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * p_78087_2_;
      float f5 = -(MathHelper.cos(p_78087_1_ * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * p_78087_2_;
      float f6 = -(MathHelper.cos(p_78087_1_ * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * p_78087_2_;
      float f7 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662F + 0.0F) * 0.4F) * p_78087_2_;
      float f8 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662F + (float)Math.PI) * 0.4F) * p_78087_2_;
      float f9 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * p_78087_2_;
      float f10 = Math.abs(MathHelper.sin(p_78087_1_ * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * p_78087_2_;
      this.spiderLeg1.rotateAngleY += f3;
      this.spiderLeg2.rotateAngleY += -f3;
      this.spiderLeg3.rotateAngleY += f4;
      this.spiderLeg4.rotateAngleY += -f4;
      this.spiderLeg5.rotateAngleY += f5;
      this.spiderLeg6.rotateAngleY += -f5;
      this.spiderLeg7.rotateAngleY += f6;
      this.spiderLeg8.rotateAngleY += -f6;
      this.spiderLeg1.rotateAngleZ += f7;
      this.spiderLeg2.rotateAngleZ += -f7;
      this.spiderLeg3.rotateAngleZ += f8;
      this.spiderLeg4.rotateAngleZ += -f8;
      this.spiderLeg5.rotateAngleZ += f9;
      this.spiderLeg6.rotateAngleZ += -f9;
      this.spiderLeg7.rotateAngleZ += f10;
      this.spiderLeg8.rotateAngleZ += -f10;
   }
}
