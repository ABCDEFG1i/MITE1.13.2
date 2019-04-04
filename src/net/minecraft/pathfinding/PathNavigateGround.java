package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGround extends PathNavigate {
   private boolean shouldAvoidSun;

   public PathNavigateGround(EntityLiving p_i45875_1_, World p_i45875_2_) {
      super(p_i45875_1_, p_i45875_2_);
   }

   protected PathFinder getPathFinder() {
      this.nodeProcessor = new WalkNodeProcessor();
      this.nodeProcessor.setCanEnterDoors(true);
      return new PathFinder(this.nodeProcessor);
   }

   protected boolean canNavigate() {
      return this.entity.onGround || this.isInLiquid() || this.entity.isRiding();
   }

   protected Vec3d getEntityPosition() {
      return new Vec3d(this.entity.posX, (double)this.getPathablePosY(), this.entity.posZ);
   }

   public Path getPathToPos(BlockPos p_179680_1_) {
      if (this.world.getBlockState(p_179680_1_).isAir()) {
         BlockPos blockpos;
         for(blockpos = p_179680_1_.down(); blockpos.getY() > 0 && this.world.getBlockState(blockpos).isAir(); blockpos = blockpos.down()) {
         }

         if (blockpos.getY() > 0) {
            return super.getPathToPos(blockpos.up());
         }

         while(blockpos.getY() < this.world.getHeight() && this.world.getBlockState(blockpos).isAir()) {
            blockpos = blockpos.up();
         }

         p_179680_1_ = blockpos;
      }

      if (!this.world.getBlockState(p_179680_1_).getMaterial().isSolid()) {
         return super.getPathToPos(p_179680_1_);
      } else {
         BlockPos blockpos1;
         for(blockpos1 = p_179680_1_.up(); blockpos1.getY() < this.world.getHeight() && this.world.getBlockState(blockpos1).getMaterial().isSolid(); blockpos1 = blockpos1.up()) {
         }

         return super.getPathToPos(blockpos1);
      }
   }

   public Path getPathToEntityLiving(Entity p_75494_1_) {
      return this.getPathToPos(new BlockPos(p_75494_1_));
   }

   private int getPathablePosY() {
      if (this.entity.isInWater() && this.getCanSwim()) {
         int i = (int)this.entity.getEntityBoundingBox().minY;
         Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))).getBlock();
         int j = 0;

         while(block == Blocks.WATER) {
            ++i;
            block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))).getBlock();
            ++j;
            if (j > 16) {
               return (int)this.entity.getEntityBoundingBox().minY;
            }
         }

         return i;
      } else {
         return (int)(this.entity.getEntityBoundingBox().minY + 0.5D);
      }
   }

   protected void trimPath() {
      super.trimPath();
      if (this.shouldAvoidSun) {
         if (this.world.canSeeSky(new BlockPos(MathHelper.floor(this.entity.posX), (int)(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.posZ)))) {
            return;
         }

         for(int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            if (this.world.canSeeSky(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z))) {
               this.currentPath.setCurrentPathLength(i - 1);
               return;
            }
         }
      }

   }

   protected boolean isDirectPathBetweenPoints(Vec3d p_75493_1_, Vec3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_) {
      int i = MathHelper.floor(p_75493_1_.x);
      int j = MathHelper.floor(p_75493_1_.z);
      double d0 = p_75493_2_.x - p_75493_1_.x;
      double d1 = p_75493_2_.z - p_75493_1_.z;
      double d2 = d0 * d0 + d1 * d1;
      if (d2 < 1.0E-8D) {
         return false;
      } else {
         double d3 = 1.0D / Math.sqrt(d2);
         d0 = d0 * d3;
         d1 = d1 * d3;
         p_75493_3_ = p_75493_3_ + 2;
         p_75493_5_ = p_75493_5_ + 2;
         if (!this.isSafeToStandAt(i, (int)p_75493_1_.y, j, p_75493_3_, p_75493_4_, p_75493_5_, p_75493_1_, d0, d1)) {
            return false;
         } else {
            p_75493_3_ = p_75493_3_ - 2;
            p_75493_5_ = p_75493_5_ - 2;
            double d4 = 1.0D / Math.abs(d0);
            double d5 = 1.0D / Math.abs(d1);
            double d6 = (double)i - p_75493_1_.x;
            double d7 = (double)j - p_75493_1_.z;
            if (d0 >= 0.0D) {
               ++d6;
            }

            if (d1 >= 0.0D) {
               ++d7;
            }

            d6 = d6 / d0;
            d7 = d7 / d1;
            int k = d0 < 0.0D ? -1 : 1;
            int l = d1 < 0.0D ? -1 : 1;
            int i1 = MathHelper.floor(p_75493_2_.x);
            int j1 = MathHelper.floor(p_75493_2_.z);
            int k1 = i1 - i;
            int l1 = j1 - j;

            while(k1 * k > 0 || l1 * l > 0) {
               if (d6 < d7) {
                  d6 += d4;
                  i += k;
                  k1 = i1 - i;
               } else {
                  d7 += d5;
                  j += l;
                  l1 = j1 - j;
               }

               if (!this.isSafeToStandAt(i, (int)p_75493_1_.y, j, p_75493_3_, p_75493_4_, p_75493_5_, p_75493_1_, d0, d1)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean isSafeToStandAt(int p_179683_1_, int p_179683_2_, int p_179683_3_, int p_179683_4_, int p_179683_5_, int p_179683_6_, Vec3d p_179683_7_, double p_179683_8_, double p_179683_10_) {
      int i = p_179683_1_ - p_179683_4_ / 2;
      int j = p_179683_3_ - p_179683_6_ / 2;
      if (!this.isPositionClear(i, p_179683_2_, j, p_179683_4_, p_179683_5_, p_179683_6_, p_179683_7_, p_179683_8_, p_179683_10_)) {
         return false;
      } else {
         for(int k = i; k < i + p_179683_4_; ++k) {
            for(int l = j; l < j + p_179683_6_; ++l) {
               double d0 = (double)k + 0.5D - p_179683_7_.x;
               double d1 = (double)l + 0.5D - p_179683_7_.z;
               if (!(d0 * p_179683_8_ + d1 * p_179683_10_ < 0.0D)) {
                  PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, p_179683_2_ - 1, l, this.entity, p_179683_4_, p_179683_5_, p_179683_6_, true, true);
                  if (pathnodetype == PathNodeType.WATER) {
                     return false;
                  }

                  if (pathnodetype == PathNodeType.LAVA) {
                     return false;
                  }

                  if (pathnodetype == PathNodeType.OPEN) {
                     return false;
                  }

                  pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, p_179683_2_, l, this.entity, p_179683_4_, p_179683_5_, p_179683_6_, true, true);
                  float f = this.entity.getPathPriority(pathnodetype);
                  if (f < 0.0F || f >= 8.0F) {
                     return false;
                  }

                  if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean isPositionClear(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
      for(BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))) {
         double d0 = (double)blockpos.getX() + 0.5D - p_179692_7_.x;
         double d1 = (double)blockpos.getZ() + 0.5D - p_179692_7_.z;
         if (!(d0 * p_179692_8_ + d1 * p_179692_10_ < 0.0D) && !this.world.getBlockState(blockpos).allowsMovement(this.world, blockpos, PathType.LAND)) {
            return false;
         }
      }

      return true;
   }

   public void setBreakDoors(boolean p_179688_1_) {
      this.nodeProcessor.setCanOpenDoors(p_179688_1_);
   }

   public void setEnterDoors(boolean p_179691_1_) {
      this.nodeProcessor.setCanEnterDoors(p_179691_1_);
   }

   public boolean getEnterDoors() {
      return this.nodeProcessor.getCanEnterDoors();
   }

   public void setAvoidSun(boolean p_179685_1_) {
      this.shouldAvoidSun = p_179685_1_;
   }
}
