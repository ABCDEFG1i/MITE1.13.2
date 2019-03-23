package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelIllager extends ModelBase {
   private final ModelRenderer head;
   private final ModelRenderer hat;
   private final ModelRenderer body;
   private final ModelRenderer arms;
   private final ModelRenderer leg0;
   private final ModelRenderer leg1;
   private final ModelRenderer nose;
   private final ModelRenderer rightArm;
   private final ModelRenderer leftArm;

   public ModelIllager(float p_i47227_1_, float p_i47227_2_, int p_i47227_3_, int p_i47227_4_) {
      this.head = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.head.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, p_i47227_1_);
      this.hat = (new ModelRenderer(this, 32, 0)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, p_i47227_1_ + 0.45F);
      this.head.addChild(this.hat);
      this.hat.showModel = false;
      this.nose = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.nose.setRotationPoint(0.0F, p_i47227_2_ - 2.0F, 0.0F);
      this.nose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, p_i47227_1_);
      this.head.addChild(this.nose);
      this.body = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.body.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.body.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, p_i47227_1_);
      this.body.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, p_i47227_1_ + 0.5F);
      this.arms = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.arms.setRotationPoint(0.0F, 0.0F + p_i47227_2_ + 2.0F, 0.0F);
      this.arms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, p_i47227_1_);
      ModelRenderer modelrenderer = (new ModelRenderer(this, 44, 22)).setTextureSize(p_i47227_3_, p_i47227_4_);
      modelrenderer.mirror = true;
      modelrenderer.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, p_i47227_1_);
      this.arms.addChild(modelrenderer);
      this.arms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, p_i47227_1_);
      this.leg0 = (new ModelRenderer(this, 0, 22)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.leg0.setRotationPoint(-2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i47227_1_);
      this.leg1 = (new ModelRenderer(this, 0, 22)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.leg1.mirror = true;
      this.leg1.setRotationPoint(2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i47227_1_);
      this.rightArm = (new ModelRenderer(this, 40, 46)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, p_i47227_1_);
      this.rightArm.setRotationPoint(-5.0F, 2.0F + p_i47227_2_, 0.0F);
      this.leftArm = (new ModelRenderer(this, 40, 46)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, p_i47227_1_);
      this.leftArm.setRotationPoint(5.0F, 2.0F + p_i47227_2_, 0.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      this.head.render(p_78088_7_);
      this.body.render(p_78088_7_);
      this.leg0.render(p_78088_7_);
      this.leg1.render(p_78088_7_);
      AbstractIllager abstractillager = (AbstractIllager)p_78088_1_;
      if (abstractillager.getArmPose() == AbstractIllager.IllagerArmPose.CROSSED) {
         this.arms.render(p_78088_7_);
      } else {
         this.rightArm.render(p_78088_7_);
         this.leftArm.render(p_78088_7_);
      }

   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      this.head.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      this.head.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      this.arms.rotationPointY = 3.0F;
      this.arms.rotationPointZ = -1.0F;
      this.arms.rotateAngleX = -0.75F;
      this.leg0.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_ * 0.5F;
      this.leg1.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_ * 0.5F;
      this.leg0.rotateAngleY = 0.0F;
      this.leg1.rotateAngleY = 0.0F;
      AbstractIllager.IllagerArmPose abstractillager$illagerarmpose = ((AbstractIllager)p_78087_7_).getArmPose();
      if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.ATTACKING) {
         float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
         float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.rightArm.rotateAngleY = 0.15707964F;
         this.leftArm.rotateAngleY = -0.15707964F;
         if (((EntityLivingBase)p_78087_7_).getPrimaryHand() == EnumHandSide.RIGHT) {
            this.rightArm.rotateAngleX = -1.8849558F + MathHelper.cos(p_78087_3_ * 0.09F) * 0.15F;
            this.leftArm.rotateAngleX = -0.0F + MathHelper.cos(p_78087_3_ * 0.19F) * 0.5F;
            this.rightArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            this.leftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
         } else {
            this.rightArm.rotateAngleX = -0.0F + MathHelper.cos(p_78087_3_ * 0.19F) * 0.5F;
            this.leftArm.rotateAngleX = -1.8849558F + MathHelper.cos(p_78087_3_ * 0.09F) * 0.15F;
            this.rightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
            this.leftArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
         }

         this.rightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
         this.leftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
         this.rightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
         this.leftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
      } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.SPELLCASTING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(p_78087_3_ * 0.6662F) * 0.25F;
         this.leftArm.rotateAngleX = MathHelper.cos(p_78087_3_ * 0.6662F) * 0.25F;
         this.rightArm.rotateAngleZ = 2.3561945F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleY = 0.0F;
      } else if (abstractillager$illagerarmpose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
         this.rightArm.rotateAngleY = -0.1F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
         this.leftArm.rotateAngleX = -0.9424779F + this.head.rotateAngleX;
         this.leftArm.rotateAngleY = this.head.rotateAngleY - 0.4F;
         this.leftArm.rotateAngleZ = ((float)Math.PI / 2F);
      }

   }

   public ModelRenderer getArm(EnumHandSide p_191216_1_) {
      return p_191216_1_ == EnumHandSide.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelRenderer func_205062_a() {
      return this.hat;
   }
}
