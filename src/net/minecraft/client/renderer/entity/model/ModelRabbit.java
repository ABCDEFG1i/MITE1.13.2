package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRabbit extends ModelBase {
   private final ModelRenderer rabbitLeftFoot;
   private final ModelRenderer rabbitRightFoot;
   private final ModelRenderer rabbitLeftThigh;
   private final ModelRenderer rabbitRightThigh;
   private final ModelRenderer rabbitBody;
   private final ModelRenderer rabbitLeftArm;
   private final ModelRenderer rabbitRightArm;
   private final ModelRenderer rabbitHead;
   private final ModelRenderer rabbitRightEar;
   private final ModelRenderer rabbitLeftEar;
   private final ModelRenderer rabbitTail;
   private final ModelRenderer rabbitNose;
   private float jumpRotation;

   public ModelRabbit() {
      this.setTextureOffset("head.main", 0, 0);
      this.setTextureOffset("head.nose", 0, 24);
      this.setTextureOffset("head.ear1", 0, 10);
      this.setTextureOffset("head.ear2", 6, 10);
      this.rabbitLeftFoot = new ModelRenderer(this, 26, 24);
      this.rabbitLeftFoot.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.rabbitLeftFoot.setRotationPoint(3.0F, 17.5F, 3.7F);
      this.rabbitLeftFoot.mirror = true;
      this.setRotationOffset(this.rabbitLeftFoot, 0.0F, 0.0F, 0.0F);
      this.rabbitRightFoot = new ModelRenderer(this, 8, 24);
      this.rabbitRightFoot.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.rabbitRightFoot.setRotationPoint(-3.0F, 17.5F, 3.7F);
      this.rabbitRightFoot.mirror = true;
      this.setRotationOffset(this.rabbitRightFoot, 0.0F, 0.0F, 0.0F);
      this.rabbitLeftThigh = new ModelRenderer(this, 30, 15);
      this.rabbitLeftThigh.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.rabbitLeftThigh.setRotationPoint(3.0F, 17.5F, 3.7F);
      this.rabbitLeftThigh.mirror = true;
      this.setRotationOffset(this.rabbitLeftThigh, -0.34906584F, 0.0F, 0.0F);
      this.rabbitRightThigh = new ModelRenderer(this, 16, 15);
      this.rabbitRightThigh.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.rabbitRightThigh.setRotationPoint(-3.0F, 17.5F, 3.7F);
      this.rabbitRightThigh.mirror = true;
      this.setRotationOffset(this.rabbitRightThigh, -0.34906584F, 0.0F, 0.0F);
      this.rabbitBody = new ModelRenderer(this, 0, 0);
      this.rabbitBody.addBox(-3.0F, -2.0F, -10.0F, 6, 5, 10);
      this.rabbitBody.setRotationPoint(0.0F, 19.0F, 8.0F);
      this.rabbitBody.mirror = true;
      this.setRotationOffset(this.rabbitBody, -0.34906584F, 0.0F, 0.0F);
      this.rabbitLeftArm = new ModelRenderer(this, 8, 15);
      this.rabbitLeftArm.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.rabbitLeftArm.setRotationPoint(3.0F, 17.0F, -1.0F);
      this.rabbitLeftArm.mirror = true;
      this.setRotationOffset(this.rabbitLeftArm, -0.17453292F, 0.0F, 0.0F);
      this.rabbitRightArm = new ModelRenderer(this, 0, 15);
      this.rabbitRightArm.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.rabbitRightArm.setRotationPoint(-3.0F, 17.0F, -1.0F);
      this.rabbitRightArm.mirror = true;
      this.setRotationOffset(this.rabbitRightArm, -0.17453292F, 0.0F, 0.0F);
      this.rabbitHead = new ModelRenderer(this, 32, 0);
      this.rabbitHead.addBox(-2.5F, -4.0F, -5.0F, 5, 4, 5);
      this.rabbitHead.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitHead.mirror = true;
      this.setRotationOffset(this.rabbitHead, 0.0F, 0.0F, 0.0F);
      this.rabbitRightEar = new ModelRenderer(this, 52, 0);
      this.rabbitRightEar.addBox(-2.5F, -9.0F, -1.0F, 2, 5, 1);
      this.rabbitRightEar.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitRightEar.mirror = true;
      this.setRotationOffset(this.rabbitRightEar, 0.0F, -0.2617994F, 0.0F);
      this.rabbitLeftEar = new ModelRenderer(this, 58, 0);
      this.rabbitLeftEar.addBox(0.5F, -9.0F, -1.0F, 2, 5, 1);
      this.rabbitLeftEar.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitLeftEar.mirror = true;
      this.setRotationOffset(this.rabbitLeftEar, 0.0F, 0.2617994F, 0.0F);
      this.rabbitTail = new ModelRenderer(this, 52, 6);
      this.rabbitTail.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2);
      this.rabbitTail.setRotationPoint(0.0F, 20.0F, 7.0F);
      this.rabbitTail.mirror = true;
      this.setRotationOffset(this.rabbitTail, -0.3490659F, 0.0F, 0.0F);
      this.rabbitNose = new ModelRenderer(this, 32, 9);
      this.rabbitNose.addBox(-0.5F, -2.5F, -5.5F, 1, 1, 1);
      this.rabbitNose.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitNose.mirror = true;
      this.setRotationOffset(this.rabbitNose, 0.0F, 0.0F, 0.0F);
   }

   private void setRotationOffset(ModelRenderer p_178691_1_, float p_178691_2_, float p_178691_3_, float p_178691_4_) {
      p_178691_1_.rotateAngleX = p_178691_2_;
      p_178691_1_.rotateAngleY = p_178691_3_;
      p_178691_1_.rotateAngleZ = p_178691_4_;
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      if (this.isChild) {
         float f = 1.5F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.56666666F, 0.56666666F, 0.56666666F);
         GlStateManager.translatef(0.0F, 22.0F * p_78088_7_, 2.0F * p_78088_7_);
         this.rabbitHead.render(p_78088_7_);
         this.rabbitLeftEar.render(p_78088_7_);
         this.rabbitRightEar.render(p_78088_7_);
         this.rabbitNose.render(p_78088_7_);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.4F, 0.4F, 0.4F);
         GlStateManager.translatef(0.0F, 36.0F * p_78088_7_, 0.0F);
         this.rabbitLeftFoot.render(p_78088_7_);
         this.rabbitRightFoot.render(p_78088_7_);
         this.rabbitLeftThigh.render(p_78088_7_);
         this.rabbitRightThigh.render(p_78088_7_);
         this.rabbitBody.render(p_78088_7_);
         this.rabbitLeftArm.render(p_78088_7_);
         this.rabbitRightArm.render(p_78088_7_);
         this.rabbitTail.render(p_78088_7_);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6F, 0.6F, 0.6F);
         GlStateManager.translatef(0.0F, 16.0F * p_78088_7_, 0.0F);
         this.rabbitLeftFoot.render(p_78088_7_);
         this.rabbitRightFoot.render(p_78088_7_);
         this.rabbitLeftThigh.render(p_78088_7_);
         this.rabbitRightThigh.render(p_78088_7_);
         this.rabbitBody.render(p_78088_7_);
         this.rabbitLeftArm.render(p_78088_7_);
         this.rabbitRightArm.render(p_78088_7_);
         this.rabbitHead.render(p_78088_7_);
         this.rabbitRightEar.render(p_78088_7_);
         this.rabbitLeftEar.render(p_78088_7_);
         this.rabbitTail.render(p_78088_7_);
         this.rabbitNose.render(p_78088_7_);
         GlStateManager.popMatrix();
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      float f = p_78087_3_ - (float)p_78087_7_.ticksExisted;
      EntityRabbit entityrabbit = (EntityRabbit)p_78087_7_;
      this.rabbitNose.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.rabbitHead.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.rabbitRightEar.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.rabbitLeftEar.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.rabbitNose.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.rabbitHead.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.rabbitRightEar.rotateAngleY = this.rabbitNose.rotateAngleY - 0.2617994F;
      this.rabbitLeftEar.rotateAngleY = this.rabbitNose.rotateAngleY + 0.2617994F;
      this.jumpRotation = MathHelper.sin(entityrabbit.getJumpCompletion(f) * (float)Math.PI);
      this.rabbitLeftThigh.rotateAngleX = (this.jumpRotation * 50.0F - 21.0F) * ((float)Math.PI / 180F);
      this.rabbitRightThigh.rotateAngleX = (this.jumpRotation * 50.0F - 21.0F) * ((float)Math.PI / 180F);
      this.rabbitLeftFoot.rotateAngleX = this.jumpRotation * 50.0F * ((float)Math.PI / 180F);
      this.rabbitRightFoot.rotateAngleX = this.jumpRotation * 50.0F * ((float)Math.PI / 180F);
      this.rabbitLeftArm.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * ((float)Math.PI / 180F);
      this.rabbitRightArm.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * ((float)Math.PI / 180F);
   }

   public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
      super.setLivingAnimations(p_78086_1_, p_78086_2_, p_78086_3_, p_78086_4_);
      this.jumpRotation = MathHelper.sin(((EntityRabbit)p_78086_1_).getJumpCompletion(p_78086_4_) * (float)Math.PI);
   }
}
