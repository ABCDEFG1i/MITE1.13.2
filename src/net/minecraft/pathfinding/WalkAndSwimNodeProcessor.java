package net.minecraft.pathfinding;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class WalkAndSwimNodeProcessor extends WalkNodeProcessor {
   private float field_203247_k;
   private float field_203248_l;

   public void init(IBlockReader p_186315_1_, EntityLiving p_186315_2_) {
      super.init(p_186315_1_, p_186315_2_);
      p_186315_2_.setPathPriority(PathNodeType.WATER, 0.0F);
      this.field_203247_k = p_186315_2_.getPathPriority(PathNodeType.WALKABLE);
      p_186315_2_.setPathPriority(PathNodeType.WALKABLE, 6.0F);
      this.field_203248_l = p_186315_2_.getPathPriority(PathNodeType.WATER_BORDER);
      p_186315_2_.setPathPriority(PathNodeType.WATER_BORDER, 4.0F);
   }

   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WALKABLE, this.field_203247_k);
      this.entity.setPathPriority(PathNodeType.WATER_BORDER, this.field_203248_l);
      super.postProcess();
   }

   public PathPoint getStart() {
      return this.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX), MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
   }

   public PathPoint getPathPointToCoords(double p_186325_1_, double p_186325_3_, double p_186325_5_) {
      return this.openPoint(MathHelper.floor(p_186325_1_), MathHelper.floor(p_186325_3_ + 0.5D), MathHelper.floor(p_186325_5_));
   }

   public int findPathOptions(PathPoint[] p_186320_1_, PathPoint p_186320_2_, PathPoint p_186320_3_, float p_186320_4_) {
      int i = 0;
      int j = 1;
      BlockPos blockpos = new BlockPos(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z);
      double d0 = this.func_203246_a(blockpos);
      PathPoint pathpoint = this.func_203245_a(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z + 1, 1, d0);
      PathPoint pathpoint1 = this.func_203245_a(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z, 1, d0);
      PathPoint pathpoint2 = this.func_203245_a(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z, 1, d0);
      PathPoint pathpoint3 = this.func_203245_a(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z - 1, 1, d0);
      PathPoint pathpoint4 = this.func_203245_a(p_186320_2_.x, p_186320_2_.y + 1, p_186320_2_.z, 0, d0);
      PathPoint pathpoint5 = this.func_203245_a(p_186320_2_.x, p_186320_2_.y - 1, p_186320_2_.z, 1, d0);
      if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(p_186320_3_) < p_186320_4_) {
         p_186320_1_[i++] = pathpoint;
      }

      if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(p_186320_3_) < p_186320_4_) {
         p_186320_1_[i++] = pathpoint1;
      }

      if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(p_186320_3_) < p_186320_4_) {
         p_186320_1_[i++] = pathpoint2;
      }

      if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(p_186320_3_) < p_186320_4_) {
         p_186320_1_[i++] = pathpoint3;
      }

      if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(p_186320_3_) < p_186320_4_) {
         p_186320_1_[i++] = pathpoint4;
      }

      if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(p_186320_3_) < p_186320_4_) {
         p_186320_1_[i++] = pathpoint5;
      }

      boolean flag = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint6 = this.func_203245_a(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z - 1, 1, d0);
         if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint6;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint7 = this.func_203245_a(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z - 1, 1, d0);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint7;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint8 = this.func_203245_a(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z + 1, 1, d0);
         if (pathpoint8 != null && !pathpoint8.visited && pathpoint8.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint8;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint9 = this.func_203245_a(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z + 1, 1, d0);
         if (pathpoint9 != null && !pathpoint9.visited && pathpoint9.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint9;
         }
      }

      return i;
   }

   private double func_203246_a(BlockPos p_203246_1_) {
      if (!this.entity.isInWater()) {
         BlockPos blockpos = p_203246_1_.down();
         VoxelShape voxelshape = this.blockaccess.getBlockState(blockpos).getCollisionShape(this.blockaccess, blockpos);
         return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(EnumFacing.Axis.Y));
      } else {
         return (double)p_203246_1_.getY() + 0.5D;
      }
   }

   @Nullable
   private PathPoint func_203245_a(int p_203245_1_, int p_203245_2_, int p_203245_3_, int p_203245_4_, double p_203245_5_) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(p_203245_1_, p_203245_2_, p_203245_3_);
      double d0 = this.func_203246_a(blockpos);
      if (d0 - p_203245_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType pathnodetype = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
         float f = this.entity.getPathPriority(pathnodetype);
         double d1 = (double)this.entity.width / 2.0D;
         if (f >= 0.0F) {
            pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         }

         if (pathnodetype != PathNodeType.WATER && pathnodetype != PathNodeType.WALKABLE) {
            if (pathpoint == null && p_203245_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
               pathpoint = this.func_203245_a(p_203245_1_, p_203245_2_ + 1, p_203245_3_, p_203245_4_ - 1, p_203245_5_);
            }

            if (pathnodetype == PathNodeType.OPEN) {
               AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)p_203245_1_ - d1 + 0.5D, (double)p_203245_2_ + 0.001D, (double)p_203245_3_ - d1 + 0.5D, (double)p_203245_1_ + d1 + 0.5D, (double)((float)p_203245_2_ + this.entity.height), (double)p_203245_3_ + d1 + 0.5D);
               if (!this.entity.world.isCollisionBoxesEmpty(null, axisalignedbb)) {
                  return null;
               }

               PathNodeType pathnodetype1 = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_ - 1, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
               if (pathnodetype1 == PathNodeType.BLOCKED) {
                  pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                  pathpoint.nodeType = PathNodeType.WALKABLE;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                  return pathpoint;
               }

               if (pathnodetype1 == PathNodeType.WATER) {
                  pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                  pathpoint.nodeType = PathNodeType.WATER;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                  return pathpoint;
               }

               int i = 0;

               while(p_203245_2_ > 0 && pathnodetype == PathNodeType.OPEN) {
                  --p_203245_2_;
                  if (i++ >= this.entity.getMaxFallHeight()) {
                     return null;
                  }

                  pathnodetype = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
                  f = this.entity.getPathPriority(pathnodetype);
                  if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                     pathpoint = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                     pathpoint.nodeType = pathnodetype;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     break;
                  }

                  if (f < 0.0F) {
                     return null;
                  }
               }
            }

            return pathpoint;
         } else {
            if (p_203245_2_ < this.entity.world.getSeaLevel() - 10 && pathpoint != null) {
               ++pathpoint.costMalus;
            }

            return pathpoint;
         }
      }
   }

   public PathNodeType getPathNodeType(IBlockReader p_193577_1_, int p_193577_2_, int p_193577_3_, int p_193577_4_, int p_193577_5_, int p_193577_6_, int p_193577_7_, boolean p_193577_8_, boolean p_193577_9_, EnumSet<PathNodeType> p_193577_10_, PathNodeType p_193577_11_, BlockPos p_193577_12_) {
      for(int i = 0; i < p_193577_5_; ++i) {
         for(int j = 0; j < p_193577_6_; ++j) {
            for(int k = 0; k < p_193577_7_; ++k) {
               int l = i + p_193577_2_;
               int i1 = j + p_193577_3_;
               int j1 = k + p_193577_4_;
               PathNodeType pathnodetype = this.getPathNodeType(p_193577_1_, l, i1, j1);
               if (pathnodetype == PathNodeType.RAIL && !(p_193577_1_.getBlockState(p_193577_12_).getBlock() instanceof BlockRailBase) && !(p_193577_1_.getBlockState(p_193577_12_.down()).getBlock() instanceof BlockRailBase)) {
                  pathnodetype = PathNodeType.FENCE;
               }

               if (pathnodetype == PathNodeType.DOOR_OPEN || pathnodetype == PathNodeType.DOOR_WOOD_CLOSED || pathnodetype == PathNodeType.DOOR_IRON_CLOSED) {
                  pathnodetype = PathNodeType.BLOCKED;
               }

               if (i == 0 && j == 0 && k == 0) {
                  p_193577_11_ = pathnodetype;
               }

               p_193577_10_.add(pathnodetype);
            }
         }
      }

      return p_193577_11_;
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      PathNodeType pathnodetype = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_);
      if (pathnodetype == PathNodeType.WATER) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            PathNodeType pathnodetype2 = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_ + enumfacing.getXOffset(), p_186330_3_ + enumfacing.getYOffset(), p_186330_4_ + enumfacing.getZOffset());
            if (pathnodetype2 == PathNodeType.BLOCKED) {
               return PathNodeType.WATER_BORDER;
            }
         }

         return PathNodeType.WATER;
      } else {
         if (pathnodetype == PathNodeType.OPEN && p_186330_3_ >= 1) {
            Block block = p_186330_1_.getBlockState(new BlockPos(p_186330_2_, p_186330_3_ - 1, p_186330_4_)).getBlock();
            PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_ - 1, p_186330_4_);
            if (pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.LAVA) {
               pathnodetype = PathNodeType.WALKABLE;
            } else {
               pathnodetype = PathNodeType.OPEN;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK) {
               pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }
         }

         pathnodetype = this.checkNeighborBlocks(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_, pathnodetype);
         return pathnodetype;
      }
   }
}
