package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class FlyingNodeProcessor extends WalkNodeProcessor {
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
      } else {
         i = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D);
      }

      BlockPos blockpos1 = new BlockPos(this.entity);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos1.getX(), i, blockpos1.getZ());
      if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().maxZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().maxZ));

         for(BlockPos blockpos : set) {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos);
            if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
               return super.openPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
         }
      }

      return super.openPoint(blockpos1.getX(), i, blockpos1.getZ());
   }

   public PathPoint getPathPointToCoords(double p_186325_1_, double p_186325_3_, double p_186325_5_) {
      return super.openPoint(MathHelper.floor(p_186325_1_), MathHelper.floor(p_186325_3_), MathHelper.floor(p_186325_5_));
   }

   public int findPathOptions(PathPoint[] p_186320_1_, PathPoint p_186320_2_, PathPoint p_186320_3_, float p_186320_4_) {
      int i = 0;
      PathPoint pathpoint = this.openPoint(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z + 1);
      PathPoint pathpoint1 = this.openPoint(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z);
      PathPoint pathpoint2 = this.openPoint(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z);
      PathPoint pathpoint3 = this.openPoint(p_186320_2_.x, p_186320_2_.y, p_186320_2_.z - 1);
      PathPoint pathpoint4 = this.openPoint(p_186320_2_.x, p_186320_2_.y + 1, p_186320_2_.z);
      PathPoint pathpoint5 = this.openPoint(p_186320_2_.x, p_186320_2_.y - 1, p_186320_2_.z);
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

      boolean flag = pathpoint3 == null || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.costMalus != 0.0F;
      boolean flag4 = pathpoint4 == null || pathpoint4.costMalus != 0.0F;
      boolean flag5 = pathpoint5 == null || pathpoint5.costMalus != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint6 = this.openPoint(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z - 1);
         if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint6;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint7 = this.openPoint(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z - 1);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint7;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint8 = this.openPoint(p_186320_2_.x - 1, p_186320_2_.y, p_186320_2_.z + 1);
         if (pathpoint8 != null && !pathpoint8.visited && pathpoint8.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint8;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint9 = this.openPoint(p_186320_2_.x + 1, p_186320_2_.y, p_186320_2_.z + 1);
         if (pathpoint9 != null && !pathpoint9.visited && pathpoint9.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint9;
         }
      }

      if (flag && flag4) {
         PathPoint pathpoint10 = this.openPoint(p_186320_2_.x, p_186320_2_.y + 1, p_186320_2_.z - 1);
         if (pathpoint10 != null && !pathpoint10.visited && pathpoint10.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint10;
         }
      }

      if (flag1 && flag4) {
         PathPoint pathpoint11 = this.openPoint(p_186320_2_.x, p_186320_2_.y + 1, p_186320_2_.z + 1);
         if (pathpoint11 != null && !pathpoint11.visited && pathpoint11.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint11;
         }
      }

      if (flag2 && flag4) {
         PathPoint pathpoint12 = this.openPoint(p_186320_2_.x + 1, p_186320_2_.y + 1, p_186320_2_.z);
         if (pathpoint12 != null && !pathpoint12.visited && pathpoint12.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint12;
         }
      }

      if (flag3 && flag4) {
         PathPoint pathpoint13 = this.openPoint(p_186320_2_.x - 1, p_186320_2_.y + 1, p_186320_2_.z);
         if (pathpoint13 != null && !pathpoint13.visited && pathpoint13.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint13;
         }
      }

      if (flag && flag5) {
         PathPoint pathpoint14 = this.openPoint(p_186320_2_.x, p_186320_2_.y - 1, p_186320_2_.z - 1);
         if (pathpoint14 != null && !pathpoint14.visited && pathpoint14.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint14;
         }
      }

      if (flag1 && flag5) {
         PathPoint pathpoint15 = this.openPoint(p_186320_2_.x, p_186320_2_.y - 1, p_186320_2_.z + 1);
         if (pathpoint15 != null && !pathpoint15.visited && pathpoint15.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint15;
         }
      }

      if (flag2 && flag5) {
         PathPoint pathpoint16 = this.openPoint(p_186320_2_.x + 1, p_186320_2_.y - 1, p_186320_2_.z);
         if (pathpoint16 != null && !pathpoint16.visited && pathpoint16.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint16;
         }
      }

      if (flag3 && flag5) {
         PathPoint pathpoint17 = this.openPoint(p_186320_2_.x - 1, p_186320_2_.y - 1, p_186320_2_.z);
         if (pathpoint17 != null && !pathpoint17.visited && pathpoint17.distanceTo(p_186320_3_) < p_186320_4_) {
            p_186320_1_[i++] = pathpoint17;
         }
      }

      return i;
   }

   @Nullable
   protected PathPoint openPoint(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity, p_176159_1_, p_176159_2_, p_176159_3_);
      float f = this.entity.getPathPriority(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.openPoint(p_176159_1_, p_176159_2_, p_176159_3_);
         pathpoint.nodeType = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (pathnodetype == PathNodeType.WALKABLE) {
            ++pathpoint.costMalus;
         }
      }

      return pathnodetype != PathNodeType.OPEN && pathnodetype != PathNodeType.WALKABLE ? pathpoint : pathpoint;
   }

   public PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, EntityLiving p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
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

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      PathNodeType pathnodetype = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_);
      if (pathnodetype == PathNodeType.OPEN && p_186330_3_ >= 1) {
         Block block = p_186330_1_.getBlockState(new BlockPos(p_186330_2_, p_186330_3_ - 1, p_186330_4_)).getBlock();
         PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_ - 1, p_186330_4_);
         if (pathnodetype1 != PathNodeType.DAMAGE_FIRE && block != Blocks.MAGMA_BLOCK && pathnodetype1 != PathNodeType.LAVA) {
            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            } else {
               pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            }
         } else {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }
      }

      pathnodetype = this.checkNeighborBlocks(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_, pathnodetype);
      return pathnodetype;
   }

   private PathNodeType getPathNodeType(EntityLiving p_192559_1_, BlockPos p_192559_2_) {
      return this.getPathNodeType(p_192559_1_, p_192559_2_.getX(), p_192559_2_.getY(), p_192559_2_.getZ());
   }

   private PathNodeType getPathNodeType(EntityLiving p_192558_1_, int p_192558_2_, int p_192558_3_, int p_192558_4_) {
      return this.getPathNodeType(this.blockaccess, p_192558_2_, p_192558_3_, p_192558_4_, p_192558_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
   }
}
