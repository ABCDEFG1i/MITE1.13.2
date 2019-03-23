package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGenerator {
   @Nullable
   public static Vec3d findRandomTarget(EntityCreature p_75463_0_, int p_75463_1_, int p_75463_2_) {
      return findRandomTargetBlock(p_75463_0_, p_75463_1_, p_75463_2_, (Vec3d)null);
   }

   @Nullable
   public static Vec3d getLandPos(EntityCreature p_191377_0_, int p_191377_1_, int p_191377_2_) {
      return generateRandomPos(p_191377_0_, p_191377_1_, p_191377_2_, (Vec3d)null, false, 0.0D);
   }

   @Nullable
   public static Vec3d findRandomTargetBlockTowards(EntityCreature p_75464_0_, int p_75464_1_, int p_75464_2_, Vec3d p_75464_3_) {
      Vec3d vec3d = p_75464_3_.subtract(p_75464_0_.posX, p_75464_0_.posY, p_75464_0_.posZ);
      return findRandomTargetBlock(p_75464_0_, p_75464_1_, p_75464_2_, vec3d);
   }

   @Nullable
   public static Vec3d func_203155_a(EntityCreature p_203155_0_, int p_203155_1_, int p_203155_2_, Vec3d p_203155_3_, double p_203155_4_) {
      Vec3d vec3d = p_203155_3_.subtract(p_203155_0_.posX, p_203155_0_.posY, p_203155_0_.posZ);
      return generateRandomPos(p_203155_0_, p_203155_1_, p_203155_2_, vec3d, true, p_203155_4_);
   }

   @Nullable
   public static Vec3d findRandomTargetBlockAwayFrom(EntityCreature p_75461_0_, int p_75461_1_, int p_75461_2_, Vec3d p_75461_3_) {
      Vec3d vec3d = (new Vec3d(p_75461_0_.posX, p_75461_0_.posY, p_75461_0_.posZ)).subtract(p_75461_3_);
      return findRandomTargetBlock(p_75461_0_, p_75461_1_, p_75461_2_, vec3d);
   }

   @Nullable
   private static Vec3d findRandomTargetBlock(EntityCreature p_75462_0_, int p_75462_1_, int p_75462_2_, @Nullable Vec3d p_75462_3_) {
      return generateRandomPos(p_75462_0_, p_75462_1_, p_75462_2_, p_75462_3_, true, (double)((float)Math.PI / 2F));
   }

   @Nullable
   private static Vec3d generateRandomPos(EntityCreature p_191379_0_, int p_191379_1_, int p_191379_2_, @Nullable Vec3d p_191379_3_, boolean p_191379_4_, double p_191379_5_) {
      PathNavigate pathnavigate = p_191379_0_.getNavigator();
      Random random = p_191379_0_.getRNG();
      boolean flag;
      if (p_191379_0_.hasHome()) {
         double d0 = p_191379_0_.getHomePosition().distanceSq((double)MathHelper.floor(p_191379_0_.posX), (double)MathHelper.floor(p_191379_0_.posY), (double)MathHelper.floor(p_191379_0_.posZ)) + 4.0D;
         double d1 = (double)(p_191379_0_.getMaximumHomeDistance() + (float)p_191379_1_);
         flag = d0 < d1 * d1;
      } else {
         flag = false;
      }

      boolean flag1 = false;
      float f = -99999.0F;
      int k1 = 0;
      int i = 0;
      int j = 0;

      for(int k = 0; k < 10; ++k) {
         BlockPos blockpos = func_203156_a(random, p_191379_1_, p_191379_2_, p_191379_3_, p_191379_5_);
         if (blockpos != null) {
            int l = blockpos.getX();
            int i1 = blockpos.getY();
            int j1 = blockpos.getZ();
            if (p_191379_0_.hasHome() && p_191379_1_ > 1) {
               BlockPos blockpos1 = p_191379_0_.getHomePosition();
               if (p_191379_0_.posX > (double)blockpos1.getX()) {
                  l -= random.nextInt(p_191379_1_ / 2);
               } else {
                  l += random.nextInt(p_191379_1_ / 2);
               }

               if (p_191379_0_.posZ > (double)blockpos1.getZ()) {
                  j1 -= random.nextInt(p_191379_1_ / 2);
               } else {
                  j1 += random.nextInt(p_191379_1_ / 2);
               }
            }

            BlockPos blockpos2 = new BlockPos((double)l + p_191379_0_.posX, (double)i1 + p_191379_0_.posY, (double)j1 + p_191379_0_.posZ);
            if ((!flag || p_191379_0_.isWithinHomeDistanceFromPosition(blockpos2)) && pathnavigate.canEntityStandOnPos(blockpos2)) {
               if (!p_191379_4_) {
                  blockpos2 = moveAboveSolid(blockpos2, p_191379_0_);
                  if (isWaterDestination(blockpos2, p_191379_0_)) {
                     continue;
                  }
               }

               float f1 = p_191379_0_.getBlockPathWeight(blockpos2);
               if (f1 > f) {
                  f = f1;
                  k1 = l;
                  i = i1;
                  j = j1;
                  flag1 = true;
               }
            }
         }
      }

      if (flag1) {
         return new Vec3d((double)k1 + p_191379_0_.posX, (double)i + p_191379_0_.posY, (double)j + p_191379_0_.posZ);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos func_203156_a(Random p_203156_0_, int p_203156_1_, int p_203156_2_, @Nullable Vec3d p_203156_3_, double p_203156_4_) {
      if (p_203156_3_ != null && !(p_203156_4_ >= Math.PI)) {
         double d3 = MathHelper.atan2(p_203156_3_.z, p_203156_3_.x) - (double)((float)Math.PI / 2F);
         double d4 = d3 + (double)(2.0F * p_203156_0_.nextFloat() - 1.0F) * p_203156_4_;
         double d0 = Math.sqrt(p_203156_0_.nextDouble()) * (double)MathHelper.SQRT_2 * (double)p_203156_1_;
         double d1 = -d0 * Math.sin(d4);
         double d2 = d0 * Math.cos(d4);
         if (!(Math.abs(d1) > (double)p_203156_1_) && !(Math.abs(d2) > (double)p_203156_1_)) {
            int l = p_203156_0_.nextInt(2 * p_203156_2_ + 1) - p_203156_2_;
            return new BlockPos(d1, (double)l, d2);
         } else {
            return null;
         }
      } else {
         int i = p_203156_0_.nextInt(2 * p_203156_1_ + 1) - p_203156_1_;
         int j = p_203156_0_.nextInt(2 * p_203156_2_ + 1) - p_203156_2_;
         int k = p_203156_0_.nextInt(2 * p_203156_1_ + 1) - p_203156_1_;
         return new BlockPos(i, j, k);
      }
   }

   private static BlockPos moveAboveSolid(BlockPos p_191378_0_, EntityCreature p_191378_1_) {
      if (!p_191378_1_.world.getBlockState(p_191378_0_).getMaterial().isSolid()) {
         return p_191378_0_;
      } else {
         BlockPos blockpos;
         for(blockpos = p_191378_0_.up(); blockpos.getY() < p_191378_1_.world.getHeight() && p_191378_1_.world.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.up()) {
            ;
         }

         return blockpos;
      }
   }

   private static boolean isWaterDestination(BlockPos p_191380_0_, EntityCreature p_191380_1_) {
      return p_191380_1_.world.getFluidState(p_191380_0_).isTagged(FluidTags.WATER);
   }
}
