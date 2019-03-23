package net.minecraft.world.gen.carver;

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

public class CanyonWorldCarver extends WorldCarver<ProbabilityConfig> {
   private final float[] field_202536_i = new float[1024];

   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, ProbabilityConfig p_212246_5_) {
      return p_212246_2_.nextFloat() <= p_212246_5_.probability;
   }

   public boolean carve(IWorld p_202522_1_, Random p_202522_2_, int p_202522_3_, int p_202522_4_, int p_202522_5_, int p_202522_6_, BitSet p_202522_7_, ProbabilityConfig p_202522_8_) {
      int i = (this.func_202520_b() * 2 - 1) * 16;
      double d0 = (double)(p_202522_3_ * 16 + p_202522_2_.nextInt(16));
      double d1 = (double)(p_202522_2_.nextInt(p_202522_2_.nextInt(40) + 8) + 20);
      double d2 = (double)(p_202522_4_ * 16 + p_202522_2_.nextInt(16));
      float f = p_202522_2_.nextFloat() * ((float)Math.PI * 2F);
      float f1 = (p_202522_2_.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double d3 = 3.0D;
      float f2 = (p_202522_2_.nextFloat() * 2.0F + p_202522_2_.nextFloat()) * 2.0F;
      int j = i - p_202522_2_.nextInt(i / 4);
      int k = 0;
      this.func_202535_a(p_202522_1_, p_202522_2_.nextLong(), p_202522_5_, p_202522_6_, d0, d1, d2, f2, f, f1, 0, j, 3.0D, p_202522_7_);
      return true;
   }

   private void func_202535_a(IWorld p_202535_1_, long p_202535_2_, int p_202535_4_, int p_202535_5_, double p_202535_6_, double p_202535_8_, double p_202535_10_, float p_202535_12_, float p_202535_13_, float p_202535_14_, int p_202535_15_, int p_202535_16_, double p_202535_17_, BitSet p_202535_19_) {
      Random random = new Random(p_202535_2_);
      float f = 1.0F;

      for(int i = 0; i < 256; ++i) {
         if (i == 0 || random.nextInt(3) == 0) {
            f = 1.0F + random.nextFloat() * random.nextFloat();
         }

         this.field_202536_i[i] = f * f;
      }

      float f4 = 0.0F;
      float f1 = 0.0F;

      for(int j = p_202535_15_; j < p_202535_16_; ++j) {
         double d0 = 1.5D + (double)(MathHelper.sin((float)j * (float)Math.PI / (float)p_202535_16_) * p_202535_12_);
         double d1 = d0 * p_202535_17_;
         d0 = d0 * ((double)random.nextFloat() * 0.25D + 0.75D);
         d1 = d1 * ((double)random.nextFloat() * 0.25D + 0.75D);
         float f2 = MathHelper.cos(p_202535_14_);
         float f3 = MathHelper.sin(p_202535_14_);
         p_202535_6_ += (double)(MathHelper.cos(p_202535_13_) * f2);
         p_202535_8_ += (double)f3;
         p_202535_10_ += (double)(MathHelper.sin(p_202535_13_) * f2);
         p_202535_14_ = p_202535_14_ * 0.7F;
         p_202535_14_ = p_202535_14_ + f1 * 0.05F;
         p_202535_13_ += f4 * 0.05F;
         f1 = f1 * 0.8F;
         f4 = f4 * 0.5F;
         f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f4 = f4 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (random.nextInt(4) != 0) {
            if (!this.isWithinGenerationDepth(p_202535_4_, p_202535_5_, p_202535_6_, p_202535_10_, j, p_202535_16_, p_202535_12_)) {
               return;
            }

            this.carveAtTarget(p_202535_1_, p_202535_2_, p_202535_4_, p_202535_5_, p_202535_6_, p_202535_8_, p_202535_10_, d0, d1, p_202535_19_);
         }
      }

   }

   protected boolean carveAtTarget(IWorld p_202516_1_, long p_202516_2_, int p_202516_4_, int p_202516_5_, double p_202516_6_, double p_202516_8_, double p_202516_10_, double p_202516_12_, double p_202516_14_, BitSet p_202516_16_) {
      double d0 = (double)(p_202516_4_ * 16 + 8);
      double d1 = (double)(p_202516_5_ * 16 + 8);
      if (!(p_202516_6_ < d0 - 16.0D - p_202516_12_ * 2.0D) && !(p_202516_10_ < d1 - 16.0D - p_202516_12_ * 2.0D) && !(p_202516_6_ > d0 + 16.0D + p_202516_12_ * 2.0D) && !(p_202516_10_ > d1 + 16.0D + p_202516_12_ * 2.0D)) {
         int i = Math.max(MathHelper.floor(p_202516_6_ - p_202516_12_) - p_202516_4_ * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(p_202516_6_ + p_202516_12_) - p_202516_4_ * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(p_202516_8_ - p_202516_14_) - 1, 1);
         int l = Math.min(MathHelper.floor(p_202516_8_ + p_202516_14_) + 1, 248);
         int i1 = Math.max(MathHelper.floor(p_202516_10_ - p_202516_12_) - p_202516_5_ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(p_202516_10_ + p_202516_12_) - p_202516_5_ * 16 + 1, 16);
         if (this.doesAreaHaveFluids(p_202516_1_, p_202516_4_, p_202516_5_, i, j, k, l, i1, j1)) {
            return false;
         } else if (i <= j && k <= l && i1 <= j1) {
            boolean flag = false;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockpos$mutableblockpos2 = new BlockPos.MutableBlockPos();

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + p_202516_4_ * 16;
               double d2 = ((double)l1 + 0.5D - p_202516_6_) / p_202516_12_;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + p_202516_5_ * 16;
                  double d3 = ((double)j2 + 0.5D - p_202516_10_) / p_202516_12_;
                  if (d2 * d2 + d3 * d3 < 1.0D) {
                     boolean flag1 = false;

                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)(k2 - 1) + 0.5D - p_202516_8_) / p_202516_14_;
                        if ((d2 * d2 + d3 * d3) * (double)this.field_202536_i[k2 - 1] + d4 * d4 / 6.0D < 1.0D) {
                           int l2 = k1 | i2 << 4 | k2 << 8;
                           if (!p_202516_16_.get(l2)) {
                              p_202516_16_.set(l2);
                              blockpos$mutableblockpos.setPos(l1, k2, j2);
                              IBlockState iblockstate = p_202516_1_.getBlockState(blockpos$mutableblockpos);
                              blockpos$mutableblockpos1.setPos(blockpos$mutableblockpos).move(EnumFacing.UP);
                              blockpos$mutableblockpos2.setPos(blockpos$mutableblockpos).move(EnumFacing.DOWN);
                              IBlockState iblockstate1 = p_202516_1_.getBlockState(blockpos$mutableblockpos1);
                              if (iblockstate.getBlock() == Blocks.GRASS_BLOCK || iblockstate.getBlock() == Blocks.MYCELIUM) {
                                 flag1 = true;
                              }

                              if (this.isTargetSafeFromFalling(iblockstate, iblockstate1)) {
                                 if (k2 - 1 < 10) {
                                    p_202516_1_.setBlockState(blockpos$mutableblockpos, LAVA_FLUID.getBlockState(), 2);
                                 } else {
                                    p_202516_1_.setBlockState(blockpos$mutableblockpos, DEFAULT_CAVE_AIR, 2);
                                    if (flag1 && p_202516_1_.getBlockState(blockpos$mutableblockpos2).getBlock() == Blocks.DIRT) {
                                       p_202516_1_.setBlockState(blockpos$mutableblockpos2, p_202516_1_.getBiome(blockpos$mutableblockpos).getSurfaceBuilderConfig().getTop(), 2);
                                    }
                                 }

                                 flag = true;
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
