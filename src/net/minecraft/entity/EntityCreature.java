package net.minecraft.entity;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   private BlockPos homePosition = BlockPos.ORIGIN;
   private float maximumHomeDistance = -1.0F;

   protected EntityCreature(EntityType<?> p_i48575_1_, World p_i48575_2_) {
      super(p_i48575_1_, p_i48575_2_);
   }

   public float getBlockPathWeight(BlockPos p_180484_1_) {
      return this.getBlockPathWeight(p_180484_1_, this.world);
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReaderBase p_205022_2_) {
      return 0.0F;
   }

   public boolean func_205020_a(IWorld p_205020_1_, boolean p_205020_2_) {
      return super.func_205020_a(p_205020_1_, p_205020_2_) && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ), p_205020_1_) >= 0.0F;
   }

   public boolean hasPath() {
      return !this.navigator.noPath();
   }

   public boolean isWithinHomeDistanceCurrentPosition() {
      return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
   }

   public boolean isWithinHomeDistanceFromPosition(BlockPos p_180485_1_) {
      if (this.maximumHomeDistance == -1.0F) {
         return true;
      } else {
         return this.homePosition.distanceSq(p_180485_1_) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
      }
   }

   public void setHomePosAndDistance(BlockPos p_175449_1_, int p_175449_2_) {
      this.homePosition = p_175449_1_;
      this.maximumHomeDistance = (float)p_175449_2_;
   }

   public BlockPos getHomePosition() {
      return this.homePosition;
   }

   public float getMaximumHomeDistance() {
      return this.maximumHomeDistance;
   }

   public void detachHome() {
      this.maximumHomeDistance = -1.0F;
   }

   public boolean hasHome() {
      return this.maximumHomeDistance != -1.0F;
   }

   protected void updateLeashedState() {
      super.updateLeashedState();
      if (this.getLeashed() && this.getLeashHolder() != null && this.getLeashHolder().world == this.world) {
         Entity entity = this.getLeashHolder();
         this.setHomePosAndDistance(new BlockPos((int)entity.posX, (int)entity.posY, (int)entity.posZ), 5);
         float f = this.getDistance(entity);
         if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
            if (f > 10.0F) {
               this.clearLeashed(true, true);
            }

            return;
         }

         this.onLeashDistance(f);
         if (f > 10.0F) {
            this.clearLeashed(true, true);
            this.tasks.disableControlFlag(1);
         } else if (f > 6.0F) {
            double d0 = (entity.posX - this.posX) / (double)f;
            double d1 = (entity.posY - this.posY) / (double)f;
            double d2 = (entity.posZ - this.posZ) / (double)f;
            this.motionX += d0 * Math.abs(d0) * 0.4D;
            this.motionY += d1 * Math.abs(d1) * 0.4D;
            this.motionZ += d2 * Math.abs(d2) * 0.4D;
         } else {
            this.tasks.enableControlFlag(1);
            float f1 = 2.0F;
            Vec3d vec3d = (new Vec3d(entity.posX - this.posX, entity.posY - this.posY, entity.posZ - this.posZ)).normalize().scale((double)Math.max(f - 2.0F, 0.0F));
            this.getNavigator().tryMoveToXYZ(this.posX + vec3d.x, this.posY + vec3d.y, this.posZ + vec3d.z, this.followLeashSpeed());
         }
      }

   }

   protected double followLeashSpeed() {
      return 1.0D;
   }

   protected void onLeashDistance(float p_142017_1_) {
   }
}
