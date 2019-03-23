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

public class CaveWorldCarver extends WorldCarver<ProbabilityConfig> {
   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, ProbabilityConfig p_212246_5_) {
      return p_212246_2_.nextFloat() <= p_212246_5_.probability;
   }

   public boolean carve(IWorld p_202522_1_, Random p_202522_2_, int p_202522_3_, int p_202522_4_, int p_202522_5_, int p_202522_6_, BitSet p_202522_7_, ProbabilityConfig p_202522_8_) {
      int i = (this.func_202520_b() * 2 - 1) * 16;
      int j = p_202522_2_.nextInt(p_202522_2_.nextInt(p_202522_2_.nextInt(15) + 1) + 1);

      for(int k = 0; k < j; ++k) {
         double d0 = (double)(p_202522_3_ * 16 + p_202522_2_.nextInt(16));
         double d1 = (double)p_202522_2_.nextInt(p_202522_2_.nextInt(120) + 8);
         double d2 = (double)(p_202522_4_ * 16 + p_202522_2_.nextInt(16));
         int l = 1;
         if (p_202522_2_.nextInt(4) == 0) {
            double d3 = 0.5D;
            float f1 = 1.0F + p_202522_2_.nextFloat() * 6.0F;
            this.addRoom(p_202522_1_, p_202522_2_.nextLong(), p_202522_5_, p_202522_6_, d0, d1, d2, f1, 0.5D, p_202522_7_);
            l += p_202522_2_.nextInt(4);
         }

         for(int k1 = 0; k1 < l; ++k1) {
            float f = p_202522_2_.nextFloat() * ((float)Math.PI * 2F);
            float f3 = (p_202522_2_.nextFloat() - 0.5F) / 4.0F;
            double d4 = 1.0D;
            float f2 = p_202522_2_.nextFloat() * 2.0F + p_202522_2_.nextFloat();
            if (p_202522_2_.nextInt(10) == 0) {
               f2 *= p_202522_2_.nextFloat() * p_202522_2_.nextFloat() * 3.0F + 1.0F;
            }

            int i1 = i - p_202522_2_.nextInt(i / 4);
            int j1 = 0;
            this.addTunnel(p_202522_1_, p_202522_2_.nextLong(), p_202522_5_, p_202522_6_, d0, d1, d2, f2, f, f3, 0, i1, 1.0D, p_202522_7_);
         }
      }

      return true;
   }

   protected void addRoom(IWorld p_203627_1_, long p_203627_2_, int p_203627_4_, int p_203627_5_, double p_203627_6_, double p_203627_8_, double p_203627_10_, float p_203627_12_, double p_203627_13_, BitSet p_203627_15_) {
      double d0 = 1.5D + (double)(MathHelper.sin(((float)Math.PI / 2F)) * p_203627_12_);
      double d1 = d0 * p_203627_13_;
      this.carveAtTarget(p_203627_1_, p_203627_2_, p_203627_4_, p_203627_5_, p_203627_6_ + 1.0D, p_203627_8_, p_203627_10_, d0, d1, p_203627_15_);
   }

   protected void addTunnel(IWorld p_202533_1_, long p_202533_2_, int p_202533_4_, int p_202533_5_, double p_202533_6_, double p_202533_8_, double p_202533_10_, float p_202533_12_, float p_202533_13_, float p_202533_14_, int p_202533_15_, int p_202533_16_, double p_202533_17_, BitSet p_202533_19_) {
      Random random = new Random(p_202533_2_);
      int i = random.nextInt(p_202533_16_ / 2) + p_202533_16_ / 4;
      boolean flag = random.nextInt(6) == 0;
      float f = 0.0F;
      float f1 = 0.0F;

      for(int j = p_202533_15_; j < p_202533_16_; ++j) {
         double d0 = 1.5D + (double)(MathHelper.sin((float)Math.PI * (float)j / (float)p_202533_16_) * p_202533_12_);
         double d1 = d0 * p_202533_17_;
         float f2 = MathHelper.cos(p_202533_14_);
         p_202533_6_ += (double)(MathHelper.cos(p_202533_13_) * f2);
         p_202533_8_ += (double)MathHelper.sin(p_202533_14_);
         p_202533_10_ += (double)(MathHelper.sin(p_202533_13_) * f2);
         p_202533_14_ = p_202533_14_ * (flag ? 0.92F : 0.7F);
         p_202533_14_ = p_202533_14_ + f1 * 0.1F;
         p_202533_13_ += f * 0.1F;
         f1 = f1 * 0.9F;
         f = f * 0.75F;
         f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (j == i && p_202533_12_ > 1.0F) {
            this.addTunnel(p_202533_1_, random.nextLong(), p_202533_4_, p_202533_5_, p_202533_6_, p_202533_8_, p_202533_10_, random.nextFloat() * 0.5F + 0.5F, p_202533_13_ - ((float)Math.PI / 2F), p_202533_14_ / 3.0F, j, p_202533_16_, 1.0D, p_202533_19_);
            this.addTunnel(p_202533_1_, random.nextLong(), p_202533_4_, p_202533_5_, p_202533_6_, p_202533_8_, p_202533_10_, random.nextFloat() * 0.5F + 0.5F, p_202533_13_ + ((float)Math.PI / 2F), p_202533_14_ / 3.0F, j, p_202533_16_, 1.0D, p_202533_19_);
            return;
         }

         if (random.nextInt(4) != 0) {
            if (!this.isWithinGenerationDepth(p_202533_4_, p_202533_5_, p_202533_6_, p_202533_10_, j, p_202533_16_, p_202533_12_)) {
               return;
            }

            this.carveAtTarget(p_202533_1_, p_202533_2_, p_202533_4_, p_202533_5_, p_202533_6_, p_202533_8_, p_202533_10_, d0, d1, p_202533_19_);
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
         } else {
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
                  if (!(d2 * d2 + d3 * d3 >= 1.0D)) {
                     boolean flag1 = false;

                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)k2 - 0.5D - p_202516_8_) / p_202516_14_;
                        if (!(d4 <= -0.7D) && !(d2 * d2 + d4 * d4 + d3 * d3 >= 1.0D)) {
                           int l2 = k1 | i2 << 4 | k2 << 8;
                           if (!p_202516_16_.get(l2)) {
                              p_202516_16_.set(l2);
                              blockpos$mutableblockpos.setPos(l1, k2, j2);
                              IBlockState iblockstate = p_202516_1_.getBlockState(blockpos$mutableblockpos);
                              IBlockState iblockstate1 = p_202516_1_.getBlockState(blockpos$mutableblockpos1.setPos(blockpos$mutableblockpos).move(EnumFacing.UP));
                              if (iblockstate.getBlock() == Blocks.GRASS_BLOCK || iblockstate.getBlock() == Blocks.MYCELIUM) {
                                 flag1 = true;
                              }

                              if (this.isTargetSafeFromFalling(iblockstate, iblockstate1)) {
                                 if (k2 < 11) {
                                    p_202516_1_.setBlockState(blockpos$mutableblockpos, LAVA_FLUID.getBlockState(), 2);
                                 } else {
                                    p_202516_1_.setBlockState(blockpos$mutableblockpos, DEFAULT_CAVE_AIR, 2);
                                    if (flag1) {
                                       blockpos$mutableblockpos2.setPos(blockpos$mutableblockpos).move(EnumFacing.DOWN);
                                       if (p_202516_1_.getBlockState(blockpos$mutableblockpos2).getBlock() == Blocks.DIRT) {
                                          IBlockState iblockstate2 = p_202516_1_.getBiome(blockpos$mutableblockpos).getSurfaceBuilderConfig().getTop();
                                          p_202516_1_.setBlockState(blockpos$mutableblockpos2, iblockstate2, 2);
                                       }
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
         }
      } else {
         return false;
      }
   }
}
