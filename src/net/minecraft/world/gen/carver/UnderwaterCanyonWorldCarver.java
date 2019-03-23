package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class UnderwaterCanyonWorldCarver extends CanyonWorldCarver {
   private final float[] field_203628_i = new float[1024];

   public UnderwaterCanyonWorldCarver() {
      this.terrainBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR);
   }

   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, ProbabilityConfig p_212246_5_) {
      return p_212246_2_.nextFloat() <= p_212246_5_.probability;
   }

   protected boolean carveAtTarget(IWorld p_202516_1_, long p_202516_2_, int p_202516_4_, int p_202516_5_, double p_202516_6_, double p_202516_8_, double p_202516_10_, double p_202516_12_, double p_202516_14_, BitSet p_202516_16_) {
      Random random = new Random(p_202516_2_ + (long)p_202516_4_ + (long)p_202516_5_);
      double d0 = (double)(p_202516_4_ * 16 + 8);
      double d1 = (double)(p_202516_5_ * 16 + 8);
      if (!(p_202516_6_ < d0 - 16.0D - p_202516_12_ * 2.0D) && !(p_202516_10_ < d1 - 16.0D - p_202516_12_ * 2.0D) && !(p_202516_6_ > d0 + 16.0D + p_202516_12_ * 2.0D) && !(p_202516_10_ > d1 + 16.0D + p_202516_12_ * 2.0D)) {
         int i = Math.max(MathHelper.floor(p_202516_6_ - p_202516_12_) - p_202516_4_ * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(p_202516_6_ + p_202516_12_) - p_202516_4_ * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(p_202516_8_ - p_202516_14_) - 1, 1);
         int l = Math.min(MathHelper.floor(p_202516_8_ + p_202516_14_) + 1, 248);
         int i1 = Math.max(MathHelper.floor(p_202516_10_ - p_202516_12_) - p_202516_5_ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(p_202516_10_ + p_202516_12_) - p_202516_5_ * 16 + 1, 16);
         if (i <= j && k <= l && i1 <= j1) {
            boolean flag = false;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + p_202516_4_ * 16;
               double d2 = ((double)l1 + 0.5D - p_202516_6_) / p_202516_12_;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + p_202516_5_ * 16;
                  double d3 = ((double)j2 + 0.5D - p_202516_10_) / p_202516_12_;
                  if (d2 * d2 + d3 * d3 < 1.0D) {
                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)(k2 - 1) + 0.5D - p_202516_8_) / p_202516_14_;
                        if ((d2 * d2 + d3 * d3) * (double)this.field_203628_i[k2 - 1] + d4 * d4 / 6.0D < 1.0D && k2 < p_202516_1_.getSeaLevel()) {
                           int l2 = k1 | i2 << 4 | k2 << 8;
                           if (!p_202516_16_.get(l2)) {
                              p_202516_16_.set(l2);
                              blockpos$mutableblockpos.setPos(l1, k2, j2);
                              IBlockState iblockstate = p_202516_1_.getBlockState(blockpos$mutableblockpos);
                              if (this.isTargetAllowed(iblockstate)) {
                                 if (k2 == 10) {
                                    float f = random.nextFloat();
                                    if ((double)f < 0.25D) {
                                       p_202516_1_.setBlockState(blockpos$mutableblockpos, Blocks.MAGMA_BLOCK.getDefaultState(), 2);
                                       p_202516_1_.getPendingBlockTicks().scheduleTick(blockpos$mutableblockpos, Blocks.MAGMA_BLOCK, 0);
                                       flag = true;
                                    } else {
                                       p_202516_1_.setBlockState(blockpos$mutableblockpos, Blocks.OBSIDIAN.getDefaultState(), 2);
                                       flag = true;
                                    }
                                 } else if (k2 < 10) {
                                    p_202516_1_.setBlockState(blockpos$mutableblockpos, Blocks.LAVA.getDefaultState(), 2);
                                 } else {
                                    boolean flag1 = false;

                                    for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                                       IBlockState iblockstate1 = p_202516_1_.getBlockState(blockpos$mutableblockpos.setPos(l1 + enumfacing.getXOffset(), k2, j2 + enumfacing.getZOffset()));
                                       if (iblockstate1.isAir()) {
                                          p_202516_1_.setBlockState(blockpos$mutableblockpos, WATER_FLUID.getBlockState(), 2);
                                          p_202516_1_.getPendingFluidTicks().scheduleTick(blockpos$mutableblockpos, WATER_FLUID.getFluid(), 0);
                                          flag = true;
                                          flag1 = true;
                                          break;
                                       }
                                    }

                                    blockpos$mutableblockpos.setPos(l1, k2, j2);
                                    if (!flag1) {
                                       p_202516_1_.setBlockState(blockpos$mutableblockpos, WATER_FLUID.getBlockState(), 2);
                                       flag = true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return flag;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
