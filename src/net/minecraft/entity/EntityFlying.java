package net.minecraft.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
   protected EntityFlying(EntityType<?> p_i48578_1_, World p_i48578_2_) {
      super(p_i48578_1_, p_i48578_2_);
   }

   public void fall(float p_180430_1_, float p_180430_2_) {
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, IBlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   public void travel(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      if (this.isInWater()) {
         this.moveRelative(p_191986_1_, p_191986_2_, p_191986_3_, 0.02F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.8F;
         this.motionY *= (double)0.8F;
         this.motionZ *= (double)0.8F;
      } else if (this.isInLava()) {
         this.moveRelative(p_191986_1_, p_191986_2_, p_191986_3_, 0.02F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.5D;
         this.motionY *= 0.5D;
         this.motionZ *= 0.5D;
      } else {
         float f = 0.91F;
         if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().getSlipperiness() * 0.91F;
         }

         float f1 = 0.16277137F / (f * f * f);
         this.moveRelative(p_191986_1_, p_191986_2_, p_191986_3_, this.onGround ? 0.1F * f1 : 0.02F);
         f = 0.91F;
         if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().getSlipperiness() * 0.91F;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)f;
         this.motionY *= (double)f;
         this.motionZ *= (double)f;
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d1 = this.posX - this.prevPosX;
      double d0 = this.posZ - this.prevPosZ;
      float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public boolean isOnLadder() {
      return false;
   }
}
