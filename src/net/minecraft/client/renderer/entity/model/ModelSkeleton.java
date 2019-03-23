package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSkeleton extends ModelBiped {
   public ModelSkeleton() {
      this(0.0F, false);
   }

   public ModelSkeleton(float p_i46303_1_, boolean p_i46303_2_) {
      super(p_i46303_1_, 0.0F, 64, 32);
      if (!p_i46303_2_) {
         this.bipedRightArm = new ModelRenderer(this, 40, 16);
         this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, p_i46303_1_);
         this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
         this.bipedLeftArm = new ModelRenderer(this, 40, 16);
         this.bipedLeftArm.mirror = true;
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, p_i46303_1_);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedRightLeg = new ModelRenderer(this, 0, 16);
         this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, p_i46303_1_);
         this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
         this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
         this.bipedLeftLeg.mirror = true;
         this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, p_i46303_1_);
         this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
      }

   }

   public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
      this.rightArmPose = ModelBiped.ArmPose.EMPTY;
      this.leftArmPose = ModelBiped.ArmPose.EMPTY;
      ItemStack itemstack = p_78086_1_.getHeldItem(EnumHand.MAIN_HAND);
      if (itemstack.getItem() == Items.BOW && ((AbstractSkeleton)p_78086_1_).isSwingingArms()) {
         if (p_78086_1_.getPrimaryHand() == EnumHandSide.RIGHT) {
            this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
         }
      }

      super.setLivingAnimations(p_78086_1_, p_78086_2_, p_78086_3_, p_78086_4_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      ItemStack itemstack = ((EntityLivingBase)p_78087_7_).getHeldItemMainhand();
      AbstractSkeleton abstractskeleton = (AbstractSkeleton)p_78087_7_;
      if (abstractskeleton.isSwingingArms() && (itemstack.isEmpty() || itemstack.getItem() != Items.BOW)) {
         float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
         float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
         this.bipedRightArm.rotateAngleZ = 0.0F;
         this.bipedLeftArm.rotateAngleZ = 0.0F;
         this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
         this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F);
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F);
         this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
         this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
         this.bipedRightArm.rotateAngleZ += MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
         this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(p_78087_3_ * 0.09F) * 0.05F + 0.05F;
         this.bipedRightArm.rotateAngleX += MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
         this.bipedLeftArm.rotateAngleX -= MathHelper.sin(p_78087_3_ * 0.067F) * 0.05F;
      }

   }

   public void postRenderArm(float p_187073_1_, EnumHandSide p_187073_2_) {
      float f = p_187073_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      ModelRenderer modelrenderer = this.getArmForSide(p_187073_2_);
      modelrenderer.rotationPointX += f;
      modelrenderer.postRender(p_187073_1_);
      modelrenderer.rotationPointX -= f;
   }
}
