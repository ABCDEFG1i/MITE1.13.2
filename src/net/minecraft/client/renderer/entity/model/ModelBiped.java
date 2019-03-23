package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBiped extends ModelBase {
   public ModelRenderer bipedHead;
   public ModelRenderer bipedHeadwear;
   public ModelRenderer bipedBody;
   public ModelRenderer bipedRightArm;
   public ModelRenderer bipedLeftArm;
   public ModelRenderer bipedRightLeg;
   public ModelRenderer bipedLeftLeg;
   public ModelBiped.ArmPose leftArmPose = ModelBiped.ArmPose.EMPTY;
   public ModelBiped.ArmPose rightArmPose = ModelBiped.ArmPose.EMPTY;
   public boolean isSneak;
   public float field_205061_a;

   public ModelBiped() {
      this(0.0F);
   }

   public ModelBiped(float p_i1148_1_) {
      this(p_i1148_1_, 0.0F, 64, 32);
   }

   public ModelBiped(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_) {
      this.textureWidth = p_i1149_3_;
      this.textureHeight = p_i1149_4_;
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i1149_1_);
      this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedHeadwear = new ModelRenderer(this, 32, 0);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i1149_1_ + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedBody = new ModelRenderer(this, 16, 16);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i1149_1_);
      this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 40, 16);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, p_i1149_1_);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, p_i1149_1_);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 16);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i1149_1_);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i1149_1_);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);
   }

   public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 16.0F * p_78088_7_, 0.0F);
         this.bipedHead.render(p_78088_7_);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * p_78088_7_, 0.0F);
         this.bipedBody.render(p_78088_7_);
         this.bipedRightArm.render(p_78088_7_);
         this.bipedLeftArm.render(p_78088_7_);
         this.bipedRightLeg.render(p_78088_7_);
         this.bipedLeftLeg.render(p_78088_7_);
         this.bipedHeadwear.render(p_78088_7_);
      } else {
         if (p_78088_1_.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.bipedHead.render(p_78088_7_);
         this.bipedBody.render(p_78088_7_);
         this.bipedRightArm.render(p_78088_7_);
         this.bipedLeftArm.render(p_78088_7_);
         this.bipedRightLeg.render(p_78088_7_);
         this.bipedLeftLeg.render(p_78088_7_);
         this.bipedHeadwear.render(p_78088_7_);
      }

      GlStateManager.popMatrix();
   }

   public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
      this.field_205061_a = p_78086_1_.getSwimAnimation(p_78086_4_);
      super.setLivingAnimations(p_78086_1_, p_78086_2_, p_78086_3_, p_78086_4_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      boolean flag = p_78087_7_ instanceof EntityLivingBase && ((EntityLivingBase)p_78087_7_).getTicksElytraFlying() > 4;
      boolean flag1 = p_78087_7_.isSwimming();
      this.bipedHead.rotateAngleY = p_78087_4_ * ((float)Math.PI / 180F);
      if (flag) {
         this.bipedHead.rotateAngleX = (-(float)Math.PI / 4F);
      } else if (this.field_205061_a > 0.0F) {
         if (flag1) {
            this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, (-(float)Math.PI / 4F), this.field_205061_a);
         } else {
            this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, p_78087_5_ * ((float)Math.PI / 180F), this.field_205061_a);
         }
      } else {
         this.bipedHead.rotateAngleX = p_78087_5_ * ((float)Math.PI / 180F);
      }

      this.bipedBody.rotateAngleY = 0.0F;
      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedRightArm.rotationPointX = -5.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointX = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)(p_78087_7_.motionX * p_78087_7_.motionX + p_78087_7_.motionY * p_78087_7_.motionY + p_78087_7_.motionZ * p_78087_7_.motionZ);
         f = f / 0.2F;
         f = f * f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.bipedRightArm.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 2.0F * p_78087_2_ * 0.5F / f;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 2.0F * p_78087_2_ * 0.5F / f;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_ / f;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_ / f;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      this.bipedRightLeg.rotateAngleZ = 0.0F;
      this.bipedLeftLeg.rotateAngleZ = 0.0F;
      if (this.isRiding) {
         this.bipedRightArm.rotateAngleX += (-(float)Math.PI / 5F);
         this.bipedLeftArm.rotateAngleX += (-(float)Math.PI / 5F);
         this.bipedRightLeg.rotateAngleX = -1.4137167F;
         this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
         this.bipedRightLeg.rotateAngleZ = 0.07853982F;
         this.bipedLeftLeg.rotateAngleX = -1.4137167F;
         this.bipedLeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
         this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.bipedLeftArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 6F);
         break;
      case ITEM:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedRightArm.rotateAngleY = (-(float)Math.PI / 6F);
         break;
      case ITEM:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case THROW_SPEAR:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.leftArmPose == ModelBiped.ArmPose.THROW_SPEAR && this.rightArmPose != ModelBiped.ArmPose.BLOCK && this.rightArmPose != ModelBiped.ArmPose.THROW_SPEAR && this.rightArmPose != ModelBiped.ArmPose.BOW_AND_ARROW) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      if (this.swingProgress > 0.0F) {
         EnumHandSide enumhandside = this.getMainHand(p_78087_7_);
         ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
         float f1 = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
         if (enumhandside == EnumHandSide.LEFT) {
            this.bipedBody.rotateAngleY *= -1.0F;
         }

         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
         f1 = 1.0F - this.swingProgress;
         f1 = f1 * f1;
         f1 = f1 * f1;
         f1 = 1.0F - f1;
         float f2 = MathHelper.sin(f1 * (float)Math.PI);
         float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
         modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
         modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
      }

      if (this.isSneak) {
         this.bipedBody.rotateAngleX = 0.5F;
         this.bipedRightArm.rotateAngleX += 0.4F;
         this.bipedLeftArm.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.bipedHead.rotationPointY = 1.0F;
      } else {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
      }

      this.bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
      this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
      this.bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
      if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
      } else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW && this.rightArmPose != ModelBiped.ArmPose.THROW_SPEAR && this.rightArmPose != ModelBiped.ArmPose.BLOCK) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
      }

      if (this.field_205061_a > 0.0F) {
         float f4 = p_78087_1_ % 26.0F;
         float f5 = this.swingProgress > 0.0F ? 0.0F : this.field_205061_a;
         if (f4 < 14.0F) {
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 0.0F, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = this.func_205059_b(this.bipedRightArm.rotateAngleX, 0.0F, f5);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = this.func_205059_b(this.bipedRightArm.rotateAngleY, (float)Math.PI, f5);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, (float)Math.PI + 1.8707964F * this.func_203068_a(f4) / this.func_203068_a(14.0F), this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = this.func_205059_b(this.bipedRightArm.rotateAngleZ, (float)Math.PI - 1.8707964F * this.func_203068_a(f4) / this.func_203068_a(14.0F), f5);
         } else if (f4 >= 14.0F && f4 < 22.0F) {
            float f7 = (f4 - 14.0F) / 8.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) * f7, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = this.func_205059_b(this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) * f7, f5);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = this.func_205059_b(this.bipedRightArm.rotateAngleY, (float)Math.PI, f5);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * f7, this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = this.func_205059_b(this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f7, f5);
         } else if (f4 >= 22.0F && f4 < 26.0F) {
            float f6 = (f4 - 22.0F) / 4.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f6, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = this.func_205059_b(this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f6, f5);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = this.func_205059_b(this.bipedRightArm.rotateAngleY, (float)Math.PI, f5);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = this.func_205059_b(this.bipedRightArm.rotateAngleZ, (float)Math.PI, f5);
         }

         float f8 = 0.3F;
         float f9 = 0.33333334F;
         this.bipedLeftLeg.rotateAngleX = this.func_205059_b(this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(p_78087_1_ * 0.33333334F + (float)Math.PI), this.field_205061_a);
         this.bipedRightLeg.rotateAngleX = this.func_205059_b(this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(p_78087_1_ * 0.33333334F), this.field_205061_a);
      }

      copyModelAngles(this.bipedHead, this.bipedHeadwear);
   }

   protected float func_205060_a(float p_205060_1_, float p_205060_2_, float p_205060_3_) {
      float f;
      for(f = p_205060_2_ - p_205060_1_; f < -(float)Math.PI; f += ((float)Math.PI * 2F)) {
         ;
      }

      while(f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_205060_1_ + p_205060_3_ * f;
   }

   private float func_205059_b(float p_205059_1_, float p_205059_2_, float p_205059_3_) {
      return p_205059_1_ + (p_205059_2_ - p_205059_1_) * p_205059_3_;
   }

   private float func_203068_a(float p_203068_1_) {
      return -65.0F * p_203068_1_ + p_203068_1_ * p_203068_1_;
   }

   public void setModelAttributes(ModelBase p_178686_1_) {
      super.setModelAttributes(p_178686_1_);
      if (p_178686_1_ instanceof ModelBiped) {
         ModelBiped modelbiped = (ModelBiped)p_178686_1_;
         this.leftArmPose = modelbiped.leftArmPose;
         this.rightArmPose = modelbiped.rightArmPose;
         this.isSneak = modelbiped.isSneak;
      }

   }

   public void setVisible(boolean p_178719_1_) {
      this.bipedHead.showModel = p_178719_1_;
      this.bipedHeadwear.showModel = p_178719_1_;
      this.bipedBody.showModel = p_178719_1_;
      this.bipedRightArm.showModel = p_178719_1_;
      this.bipedLeftArm.showModel = p_178719_1_;
      this.bipedRightLeg.showModel = p_178719_1_;
      this.bipedLeftLeg.showModel = p_178719_1_;
   }

   public void postRenderArm(float p_187073_1_, EnumHandSide p_187073_2_) {
      this.getArmForSide(p_187073_2_).postRender(p_187073_1_);
   }

   protected ModelRenderer getArmForSide(EnumHandSide p_187074_1_) {
      return p_187074_1_ == EnumHandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
   }

   protected EnumHandSide getMainHand(Entity p_187072_1_) {
      if (p_187072_1_ instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)p_187072_1_;
         EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
         return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
      } else {
         return EnumHandSide.RIGHT;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR;
   }
}
