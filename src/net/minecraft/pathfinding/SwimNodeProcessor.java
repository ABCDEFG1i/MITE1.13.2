package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class SwimNodeProcessor extends NodeProcessor {
   private final boolean field_205202_j;

   public SwimNodeProcessor(boolean p_i48927_1_) {
      this.field_205202_j = p_i48927_1_;
   }

   public PathPoint getStart() {
      return super.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX), MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
   }

   public PathPoint getPathPointToCoords(double p_186325_1_, double p_186325_3_, double p_186325_5_) {
      return super.openPoint(MathHelper.floor(p_186325_1_ - (double)(this.entity.width / 2.0F)), MathHelper.floor(p_186325_3_ + 0.5D), MathHelper.floor(p_186325_5_ - (double)(this.entity.width / 2.0F)));
   }

   public int findPathOptions(PathPoint[] p_186320_1_, PathPoint p_186320_2_, PathPoint p_186320_3_, float p_186320_4_) {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         PathPoint pathpoint = this.getWaterNode(p_186320_2_.x + enumfacing.getXOffset(), p_186320_2_.y + enumfacing.getYOffset(), p_186320_2_.z + enumfacing.getZOffset());
         if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint;
         }
      }

      return i;
   }

   public PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, EntityLiving p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      return this.getPathNodeType(p_186319_1_, p_186319_2_, p_186319_3_, p_186319_4_);
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      BlockPos blockpos = new BlockPos(p_186330_2_, p_186330_3_, p_186330_4_);
      IFluidState ifluidstate = p_186330_1_.getFluidState(blockpos);
      IBlockState iblockstate = p_186330_1_.getBlockState(blockpos);
      if (ifluidstate.isEmpty() && iblockstate.allowsMovement(p_186330_1_, blockpos.down(), PathType.WATER) && iblockstate.isAir()) {
         return PathNodeType.BREACH;
      } else {
         return ifluidstate.isTagged(FluidTags.WATER) && iblockstate.allowsMovement(p_186330_1_, blockpos, PathType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
      }
   }

   @Nullable
   private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
      PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
      return (!this.field_205202_j || pathnodetype != PathNodeType.BREACH) && pathnodetype != PathNodeType.WATER ? null : this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_);
   }

   @Nullable
   protected PathPoint openPoint(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity.world, p_176159_1_, p_176159_2_, p_176159_3_);
      float f = this.entity.getPathPriority(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.openPoint(p_176159_1_, p_176159_2_, p_176159_3_);
         pathpoint.nodeType = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (this.blockaccess.getFluidState(new BlockPos(p_176159_1_, p_176159_2_, p_176159_3_)).isEmpty()) {
            pathpoint.costMalus += 8.0F;
         }
      }

      return pathnodetype == PathNodeType.OPEN ? pathpoint : pathpoint;
   }

   private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = p_186327_1_; i < p_186327_1_ + this.entitySizeX; ++i) {
         for(int j = p_186327_2_; j < p_186327_2_ + this.entitySizeY; ++j) {
            for(int k = p_186327_3_; k < p_186327_3_ + this.entitySizeZ; ++k) {
               IFluidState ifluidstate = this.blockaccess.getFluidState(blockpos$mutableblockpos.setPos(i, j, k));
               IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));
               if (ifluidstate.isEmpty() && iblockstate.allowsMovement(this.blockaccess, blockpos$mutableblockpos.down(), PathType.WATER) && iblockstate.isAir()) {
                  return PathNodeType.BREACH;
               }

               if (!ifluidstate.isTagged(FluidTags.WATER)) {
                  return PathNodeType.BLOCKED;
               }
            }
         }
      }

      IBlockState iblockstate1 = this.blockaccess.getBlockState(blockpos$mutableblockpos);
      if (iblockstate1.allowsMovement(this.blockaccess, blockpos$mutableblockpos, PathType.WATER)) {
         return PathNodeType.WATER;
      } else {
         return PathNodeType.BLOCKED;
      }
   }
}
