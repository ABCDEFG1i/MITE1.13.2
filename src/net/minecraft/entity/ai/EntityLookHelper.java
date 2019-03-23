package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class EntityLookHelper {
   protected final EntityLiving entity;
   protected float deltaLookYaw;
   protected float deltaLookPitch;
   protected boolean isLooking;
   protected double posX;
   protected double posY;
   protected double posZ;

   public EntityLookHelper(EntityLiving p_i1613_1_) {
      this.entity = p_i1613_1_;
   }

   public void setLookPositionWithEntity(Entity p_75651_1_, float p_75651_2_, float p_75651_3_) {
      this.posX = p_75651_1_.posX;
      if (p_75651_1_ instanceof EntityLivingBase) {
         this.posY = p_75651_1_.posY + (double)p_75651_1_.getEyeHeight();
      } else {
         this.posY = (p_75651_1_.getEntityBoundingBox().minY + p_75651_1_.getEntityBoundingBox().maxY) / 2.0D;
      }

      this.posZ = p_75651_1_.posZ;
      this.deltaLookYaw = p_75651_2_;
      this.deltaLookPitch = p_75651_3_;
      this.isLooking = true;
   }

   public void setLookPosition(double p_75650_1_, double p_75650_3_, double p_75650_5_, float p_75650_7_, float p_75650_8_) {
      this.posX = p_75650_1_;
      this.posY = p_75650_3_;
      this.posZ = p_75650_5_;
      this.deltaLookYaw = p_75650_7_;
      this.deltaLookPitch = p_75650_8_;
      this.isLooking = true;
   }

   public void tick() {
      this.entity.rotationPitch = 0.0F;
      if (this.isLooking) {
         this.isLooking = false;
         double d0 = this.posX - this.entity.posX;
         double d1 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
         double d2 = this.posZ - this.entity.posZ;
         double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
         float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         float f1 = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
         this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
      } else {
         this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
      }

      float f2 = MathHelper.wrapDegrees(this.entity.rotationYawHead - this.entity.renderYawOffset);
      if (!this.entity.getNavigator().noPath()) {
         if (f2 < -75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
         }

         if (f2 > 75.0F) {
            this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
         }
      }

   }

   protected float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
      float f = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);
      if (f > p_75652_3_) {
         f = p_75652_3_;
      }

      if (f < -p_75652_3_) {
         f = -p_75652_3_;
      }

      return p_75652_1_ + f;
   }

   public boolean getIsLooking() {
      return this.isLooking;
   }

   public double getLookPosX() {
      return this.posX;
   }

   public double getLookPosY() {
      return this.posY;
   }

   public double getLookPosZ() {
      return this.posZ;
   }
}
