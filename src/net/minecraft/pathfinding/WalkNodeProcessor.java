package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class WalkNodeProcessor extends NodeProcessor {
   protected float avoidsWater;

   public void init(IBlockReader p_186315_1_, EntityLiving p_186315_2_) {
      super.init(p_186315_1_, p_186315_2_);
      this.avoidsWater = p_186315_2_.getPathPriority(PathNodeType.WATER);
   }

   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
      super.postProcess();
   }

   public PathPoint getStart() {
      int i;
      if (this.getCanSwim() && this.entity.isInWater()) {
         i = (int)this.entity.getEntityBoundingBox().minY;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ));

         for(Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock(); block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock()) {
            ++i;
            blockpos$mutableblockpos.setPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ));
         }

         --i;
      } else if (this.entity.onGround) {
         i = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D);
      } else {
         BlockPos blockpos;
         for(blockpos = new BlockPos(this.entity); (this.blockaccess.getBlockState(blockpos).isAir() || this.blockaccess.getBlockState(blockpos).allowsMovement(this.blockaccess, blockpos, PathType.LAND)) && blockpos.getY() > 0; blockpos = blockpos.down()) {
            ;
         }

         i = blockpos.up().getY();
      }

      BlockPos blockpos2 = new BlockPos(this.entity);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos2.getX(), i, blockpos2.getZ());
      if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().maxZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().maxZ));

         for(BlockPos blockpos1 : set) {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos1);
            if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
               return this.openPoint(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
         }
      }

      return this.openPoint(blockpos2.getX(), i, blockpos2.getZ());
   }

   public PathPoint getPathPointToCoords(double p_186325_1_, double p_186325_3_, double p_186325_5_) {
      return this.openPoint(MathHelper.floor(p_186325_1_), MathHelper.floor(p_186325_3_), MathHelper.floor(p_186325_5_));
   }

   public int findPathOptions(PathPoint[] p_186320_1_, PathPoint p_186320_2_, PathPoint p_186320_3_, float p_186320_4_) {
      int i = 0;
      int j = 0;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity, p_186320_2_.x, p_186320_2_.y + 1, p_186320_2_.z);
      if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
         j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
      }

      double d0 = func_197682_a(this.blockaccess, new BlockPos(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z));
      PathPoint pathpoint = this.getSafePoint(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z + 1, j, d0, EnumFacing.SOUTH);
      PathPoint pathpoint1 = this.getSafePoint(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z, j, d0, EnumFacing.WEST);
      PathPoint pathpoint2 = this.getSafePoint(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z, j, d0, EnumFacing.EAST);
      PathPoint pathpoint3 = this.getSafePoint(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z - 1, j, d0, EnumFacing.NORTH);
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

      boolean flag = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint4 = this.getSafePoint(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z - 1, j, d0, EnumFacing.NORTH);
         if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint4;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint5 = this.getSafePoint(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z - 1, j, d0, EnumFacing.NORTH);
         if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint5;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint6 = this.getSafePoint(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z + 1, j, d0, EnumFacing.SOUTH);
         if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint6;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint7 = this.getSafePoint(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z + 1, j, d0, EnumFacing.SOUTH);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint7;
         }
      }

      return i;
   }

   @Nullable
   private PathPoint getSafePoint(int p_186332_1_, int p_186332_2_, int p_186332_3_, int p_186332_4_, double p_186332_5_, EnumFacing p_186332_7_) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(p_186332_1_, p_186332_2_, p_186332_3_);
      double d0 = func_197682_a(this.blockaccess, blockpos);
      if (d0 - p_186332_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType pathnodetype = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_, p_186332_3_);
         float f = this.entity.getPathPriority(pathnodetype);
         double d1 = (double)this.entity.width / 2.0D;
         if (f >= 0.0F) {
            pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         }

         if (pathnodetype == PathNodeType.WALKABLE) {
            return pathpoint;
         } else {
            if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
               pathpoint = this.getSafePoint(p_186332_1_, p_186332_2_ + 1, p_186332_3_, p_186332_4_ - 1, p_186332_5_, p_186332_7_);
               if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
                  double d2 = (double)(p_186332_1_ - p_186332_7_.getXOffset()) + 0.5D;
                  double d3 = (double)(p_186332_3_ - p_186332_7_.getZOffset()) + 0.5D;
                  AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, (double)p_186332_2_ + 0.001D, d3 - d1, d2 + d1, (double)this.entity.height + func_197682_a(this.blockaccess, blockpos.up()) - 0.002D, d3 + d1);
                  if (!this.entity.world.isCollisionBoxesEmpty((Entity)null, axisalignedbb)) {
                     pathpoint = null;
                  }
               }
            }

            if (pathnodetype == PathNodeType.WATER && !this.getCanSwim()) {
               if (this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_ - 1, p_186332_3_) != PathNodeType.WATER) {
                  return pathpoint;
               }

               while(p_186332_2_ > 0) {
                  --p_186332_2_;
                  pathnodetype = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_, p_186332_3_);
                  if (pathnodetype != PathNodeType.WATER) {
                     return pathpoint;
                  }

                  pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
                  pathpoint.nodeType = pathnodetype;
                  pathpoint.costMalus = Math.max(pathpoint.costMalus, this.entity.getPathPriority(pathnodetype));
               }
            }

            if (pathnodetype == PathNodeType.OPEN) {
               AxisAlignedBB axisalignedbb1 = new AxisAlignedBB((double)p_186332_1_ - d1 + 0.5D, (double)p_186332_2_ + 0.001D, (double)p_186332_3_ - d1 + 0.5D, (double)p_186332_1_ + d1 + 0.5D, (double)((float)p_186332_2_ + this.entity.height), (double)p_186332_3_ + d1 + 0.5D);
               if (!this.entity.world.isCollisionBoxesEmpty((Entity)null, axisalignedbb1)) {
                  return null;
               }

               if (this.entity.width >= 1.0F) {
                  PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_ - 1, p_186332_3_);
                  if (pathnodetype1 == PathNodeType.BLOCKED) {
                     pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
                     pathpoint.nodeType = PathNodeType.WALKABLE;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     return pathpoint;
                  }
               }

               int i = 0;

               while(p_186332_2_ > 0 && pathnodetype == PathNodeType.OPEN) {
                  --p_186332_2_;
                  if (i++ >= this.entity.getMaxFallHeight()) {
                     return null;
                  }

                  pathnodetype = this.getPathNodeType(this.entity, p_186332_1_, p_186332_2_, p_186332_3_);
                  f = this.entity.getPathPriority(pathnodetype);
                  if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                     pathpoint = this.openPoint(p_186332_1_, p_186332_2_, p_186332_3_);
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
         }
      }
   }

   public static double func_197682_a(IBlockReader p_197682_0_, BlockPos p_197682_1_) {
      BlockPos blockpos = p_197682_1_.down();
      VoxelShape voxelshape = p_197682_0_.getBlockState(blockpos).getCollisionShape(p_197682_0_, blockpos);
      return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(EnumFacing.Axis.Y));
   }

   public PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, EntityLiving p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      double d0 = (double)p_186319_5_.width / 2.0D;
      BlockPos blockpos = new BlockPos(p_186319_5_);
      pathnodetype = this.getPathNodeType(p_186319_1_, p_186319_2_, p_186319_3_, p_186319_4_, p_186319_6_, p_186319_7_, p_186319_8_, p_186319_9_, p_186319_10_, enumset, pathnodetype, blockpos);
      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

         for(PathNodeType pathnodetype2 : enumset) {
            if (p_186319_5_.getPathPriority(pathnodetype2) < 0.0F) {
               return pathnodetype2;
            }

            if (p_186319_5_.getPathPriority(pathnodetype2) >= p_186319_5_.getPathPriority(pathnodetype1)) {
               pathnodetype1 = pathnodetype2;
            }
         }

         if (pathnodetype == PathNodeType.OPEN && p_186319_5_.getPathPriority(pathnodetype1) == 0.0F) {
            return PathNodeType.OPEN;
         } else {
            return pathnodetype1;
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
               if (pathnodetype == PathNodeType.DOOR_WOOD_CLOSED && p_193577_8_ && p_193577_9_) {
                  pathnodetype = PathNodeType.WALKABLE;
               }

               if (pathnodetype == PathNodeType.DOOR_OPEN && !p_193577_9_) {
                  pathnodetype = PathNodeType.BLOCKED;
               }

               if (pathnodetype == PathNodeType.RAIL && !(p_193577_1_.getBlockState(p_193577_12_).getBlock() instanceof BlockRailBase) && !(p_193577_1_.getBlockState(p_193577_12_.down()).getBlock() instanceof BlockRailBase)) {
                  pathnodetype = PathNodeType.FENCE;
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

   private PathNodeType getPathNodeType(EntityLiving p_186329_1_, BlockPos p_186329_2_) {
      return this.getPathNodeType(p_186329_1_, p_186329_2_.getX(), p_186329_2_.getY(), p_186329_2_.getZ());
   }

   private PathNodeType getPathNodeType(EntityLiving p_186331_1_, int p_186331_2_, int p_186331_3_, int p_186331_4_) {
      return this.getPathNodeType(this.blockaccess, p_186331_2_, p_186331_3_, p_186331_4_, p_186331_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      PathNodeType pathnodetype = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_);
      if (pathnodetype == PathNodeType.OPEN && p_186330_3_ >= 1) {
         Block block = p_186330_1_.getBlockState(new BlockPos(p_186330_2_, p_186330_3_ - 1, p_186330_4_)).getBlock();
         PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_ - 1, p_186330_4_);
         pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
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

   public PathNodeType checkNeighborBlocks(IBlockReader p_193578_1_, int p_193578_2_, int p_193578_3_, int p_193578_4_, PathNodeType p_193578_5_) {
      if (p_193578_5_ == PathNodeType.WALKABLE) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int i = -1; i <= 1; ++i) {
               for(int j = -1; j <= 1; ++j) {
                  if (i != 0 || j != 0) {
                     Block block = p_193578_1_.getBlockState(blockpos$pooledmutableblockpos.setPos(i + p_193578_2_, p_193578_3_, j + p_193578_4_)).getBlock();
                     if (block == Blocks.CACTUS) {
                        p_193578_5_ = PathNodeType.DANGER_CACTUS;
                     } else if (block == Blocks.FIRE) {
                        p_193578_5_ = PathNodeType.DANGER_FIRE;
                     }
                  }
               }
            }
         }
      }

      return p_193578_5_;
   }

   protected PathNodeType getPathNodeTypeRaw(IBlockReader p_189553_1_, int p_189553_2_, int p_189553_3_, int p_189553_4_) {
      BlockPos blockpos = new BlockPos(p_189553_2_, p_189553_3_, p_189553_4_);
      IBlockState iblockstate = p_189553_1_.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      Material material = iblockstate.getMaterial();
      if (iblockstate.isAir()) {
         return PathNodeType.OPEN;
      } else if (!block.isIn(BlockTags.TRAPDOORS) && block != Blocks.LILY_PAD) {
         if (block == Blocks.FIRE) {
            return PathNodeType.DAMAGE_FIRE;
         } else if (block == Blocks.CACTUS) {
            return PathNodeType.DAMAGE_CACTUS;
         } else if (block instanceof BlockDoor && material == Material.WOOD && !iblockstate.get(BlockDoor.OPEN)) {
            return PathNodeType.DOOR_WOOD_CLOSED;
         } else if (block instanceof BlockDoor && material == Material.IRON && !iblockstate.get(BlockDoor.OPEN)) {
            return PathNodeType.DOOR_IRON_CLOSED;
         } else if (block instanceof BlockDoor && iblockstate.get(BlockDoor.OPEN)) {
            return PathNodeType.DOOR_OPEN;
         } else if (block instanceof BlockRailBase) {
            return PathNodeType.RAIL;
         } else if (!(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockFenceGate) || iblockstate.get(BlockFenceGate.OPEN))) {
            IFluidState ifluidstate = p_189553_1_.getFluidState(blockpos);
            if (ifluidstate.isTagged(FluidTags.WATER)) {
               return PathNodeType.WATER;
            } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
               return PathNodeType.LAVA;
            } else {
               return iblockstate.allowsMovement(p_189553_1_, blockpos, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
            }
         } else {
            return PathNodeType.FENCE;
         }
      } else {
         return PathNodeType.TRAPDOOR;
      }
   }
}
