package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PathNavigateClimber extends PathNavigateGround {
   private BlockPos targetPosition;

   public PathNavigateClimber(EntityLiving p_i45874_1_, World p_i45874_2_) {
      super(p_i45874_1_, p_i45874_2_);
   }

   public Path getPathToPos(BlockPos p_179680_1_) {
      this.targetPosition = p_179680_1_;
      return super.getPathToPos(p_179680_1_);
   }

   public Path getPathToEntityLiving(Entity p_75494_1_) {
      this.targetPosition = new BlockPos(p_75494_1_);
      return super.getPathToEntityLiving(p_75494_1_);
   }

   public boolean tryMoveToEntityLiving(Entity p_75497_1_, double p_75497_2_) {
      Path path = this.getPathToEntityLiving(p_75497_1_);
      if (path != null) {
         return this.setPath(path, p_75497_2_);
      } else {
         this.targetPosition = new BlockPos(p_75497_1_);
         this.speed = p_75497_2_;
         return true;
      }
   }

   public void tick() {
      if (!this.noPath()) {
         super.tick();
      } else {
         if (this.targetPosition != null) {
            double d0 = (double)(this.entity.width * this.entity.width);
            if (!(this.entity.getDistanceSqToCenter(this.targetPosition) < d0) && (!(this.entity.posY > (double)this.targetPosition.getY()) || !(this.entity.getDistanceSqToCenter(new BlockPos(this.targetPosition.getX(), MathHelper.floor(this.entity.posY), this.targetPosition.getZ())) < d0))) {
               this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speed);
            } else {
               this.targetPosition = null;
            }
         }

      }
   }
}
