package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelChicken extends ModelBase {
   private final ModelRenderer head;
   private final ModelRenderer body;
   private final ModelRenderer rightLeg;
   private final ModelRenderer leftLeg;
   private final ModelRenderer rightWing;
   private final ModelRenderer leftWing;
   private final ModelRenderer bill;
   private final ModelRenderer chin;

   public ModelChicken() {
      int i = 16;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
      this.head.setRotationPoint(0.0F, 15.0F, -4.0F);
      this.bill = new ModelRenderer(this, 14, 0);
      this.bill.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
      this.bill.setRotationPoint(0.0F, 15.0F, -4.0F);
      this.chin = new ModelRenderer(this, 14, 4);
      this.chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
      this.chin.setRotationPoint(0.0F, 15.0F, -4.0F);
      this.body = new ModelRenderer(this, 0, 9);
      this.body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
      this.body.setRotationPoint(0.0F, 16.0F, 0.0F);
      this.rightLeg = new ModelRenderer(this, 26, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
      this.rightLeg.setRotationPoint(-2.0F, 19.0F, 1.0F);
      this.leftLeg = new ModelRenderer(this, 26, 0);
      this.leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
      this.leftLeg.setRotationPoint(1.0F, 19.0F, 1.0F);
      this.rightWing = new ModelRenderer(this, 24, 13);
      this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
      this.rightWing.setRotationPoint(-4.0F, 13.0F, 0.0F);
      this.leftWing = new ModelRenderer(this, 24, 13);
      this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
      this.leftWing.setRotationPoint(4.0F, 13.0F, 0.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * p_78088_7_, 2.0F * p_78088_7_);
         this.head.render(p_78088_7_);
         this.bill.render(p_78088_7_);
         this.chin.render(p_78088_7_);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * p_78088_7_, 0.0F);
         this.body.render(p_78088_7_);
         this.rightLeg.render(p_78088_7_);
         this.leftLeg.render(p_78088_7_);
         this.rightWing.render(p_78088_7_);
         this.leftWing.render(p_78088_7_);
         GlStateManager.popMatrix();
      } else {
         this.head.render(p_78088_7_);
         this.bill.render(p_78088_7_);
         this.chin.render(p_78088_7_);
         this.body.render(p_78088_7_);
         this.rightLeg.render(p_78088_7_);
         this.leftLeg.render(p_78088_7_);
         this.rightWing.render(p_78088_7_);
         this.leftWing.render(p_78088_7_);
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      this.head.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.head.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.bill.rotateAngleX = this.head.rotateAngleX;
      this.bill.rotateAngleY = this.head.rotateAngleY;
      this.chin.rotateAngleX = this.head.rotateAngleX;
      this.chin.rotateAngleY = this.head.rotateAngleY;
      this.body.rotateAngleX = ((float)Math.PI / 2F);
      this.rightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
      this.leftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
      this.rightWing.rotateAngleZ = p_78087_3_;
      this.leftWing.rotateAngleZ = -p_78087_3_;
   }
}
