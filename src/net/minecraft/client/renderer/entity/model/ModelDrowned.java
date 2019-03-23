package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelDrowned extends ModelZombie {
   public ModelDrowned(float p_i48915_1_, float p_i48915_2_, int p_i48915_3_, int p_i48915_4_) {
      super(p_i48915_1_, p_i48915_2_, p_i48915_3_, p_i48915_4_);
      this.bipedRightArm = new ModelRenderer(this, 32, 48);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, p_i48915_1_);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i48915_2_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 16, 48);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i48915_1_);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i48915_2_, 0.0F);
   }

   public ModelDrowned(float p_i49398_1_, boolean p_i49398_2_) {
      super(p_i49398_1_, 0.0F, 64, p_i49398_2_ ? 32 : 64);
   }

   public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
      this.rightArmPose = ModelBiped.ArmPose.EMPTY;
      this.leftArmPose = ModelBiped.ArmPose.EMPTY;
      ItemStack itemstack = p_78086_1_.getHeldItem(EnumHand.MAIN_HAND);
      if (itemstack.getItem() == Items.TRIDENT && ((EntityDrowned)p_78086_1_).isArmsRaised()) {
         if (p_78086_1_.getPrimaryHand() == EnumHandSide.RIGHT) {
            this.rightArmPose = ModelBiped.ArmPose.THROW_SPEAR;
         } else {
            this.leftArmPose = ModelBiped.ArmPose.THROW_SPEAR;
         }
      }

      super.setLivingAnimations(p_78086_1_, p_78086_2_, p_78086_3_, p_78086_4_);
   }

   public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
      super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
      if (this.leftArmPose == ModelBiped.ArmPose.THROW_SPEAR) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      if (this.rightArmPose == ModelBiped.ArmPose.THROW_SPEAR) {
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.field_205061_a > 0.0F) {
         this.bipedRightArm.rotateAngleX = this.func_205060_a(this.bipedRightArm.rotateAngleX, -2.5132742F, this.field_205061_a) + this.field_205061_a * 0.35F * MathHelper.sin(0.1F * p_78087_3_);
         this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, -2.5132742F, this.field_205061_a) - this.field_205061_a * 0.35F * MathHelper.sin(0.1F * p_78087_3_);
         this.bipedRightArm.rotateAngleZ = this.func_205060_a(this.bipedRightArm.rotateAngleZ, -0.15F, this.field_205061_a);
         this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 0.15F, this.field_205061_a);
         this.bipedLeftLeg.rotateAngleX -= this.field_205061_a * 0.55F * MathHelper.sin(0.1F * p_78087_3_);
         this.bipedRightLeg.rotateAngleX += this.field_205061_a * 0.55F * MathHelper.sin(0.1F * p_78087_3_);
         this.bipedHead.rotateAngleX = 0.0F;
      }

   }
}
