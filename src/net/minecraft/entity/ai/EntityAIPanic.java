package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public class EntityAIPanic extends EntityAIBase {
   protected final EntityCreature creature;
   protected double speed;
   protected double randPosX;
   protected double randPosY;
   protected double randPosZ;

   public EntityAIPanic(EntityCreature p_i1645_1_, double p_i1645_2_) {
      this.creature = p_i1645_1_;
      this.speed = p_i1645_2_;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
         return false;
      } else {
         if (this.creature.isBurning()) {
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 5, 4);
            if (blockpos != null) {
               this.randPosX = (double)blockpos.getX();
               this.randPosY = (double)blockpos.getY();
               this.randPosZ = (double)blockpos.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean findRandomPosition() {
      Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.creature, 5, 4);
      if (vec3d == null) {
         return false;
      } else {
         this.randPosX = vec3d.x;
         this.randPosY = vec3d.y;
         this.randPosZ = vec3d.z;
         return true;
      }
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
   }

   public boolean shouldContinueExecuting() {
      return !this.creature.getNavigator().noPath();
   }

   @Nullable
   protected BlockPos getRandPos(IBlockReader p_188497_1_, Entity p_188497_2_, int p_188497_3_, int p_188497_4_) {
      BlockPos blockpos = new BlockPos(p_188497_2_);
      int i = blockpos.getX();
      int j = blockpos.getY();
      int k = blockpos.getZ();
      float f = (float)(p_188497_3_ * p_188497_3_ * p_188497_4_ * 2);
      BlockPos blockpos1 = null;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l = i - p_188497_3_; l <= i + p_188497_3_; ++l) {
         for(int i1 = j - p_188497_4_; i1 <= j + p_188497_4_; ++i1) {
            for(int j1 = k - p_188497_3_; j1 <= k + p_188497_3_; ++j1) {
               blockpos$mutableblockpos.setPos(l, i1, j1);
               if (p_188497_1_.getFluidState(blockpos$mutableblockpos).isTagged(FluidTags.WATER)) {
                  float f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));
                  if (f1 < f) {
                     f = f1;
                     blockpos1 = new BlockPos(blockpos$mutableblockpos);
                  }
               }
            }
         }
      }

      return blockpos1;
   }
}
