package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class NetherCaveWorldCarver extends CaveWorldCarver {
   public NetherCaveWorldCarver() {
      this.terrainBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK);
      this.terrainFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
   }

   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, ProbabilityConfig p_212246_5_) {
      return p_212246_2_.nextFloat() <= p_212246_5_.probability;
   }

   public boolean carve(IWorld p_202522_1_, Random p_202522_2_, int p_202522_3_, int p_202522_4_, int p_202522_5_, int p_202522_6_, BitSet p_202522_7_, ProbabilityConfig p_202522_8_) {
      int i = (this.func_202520_b() * 2 - 1) * 16;
      int j = p_202522_2_.nextInt(p_202522_2_.nextInt(p_202522_2_.nextInt(10) + 1) + 1);

      for(int k = 0; k < j; ++k) {
         double d0 = (double)(p_202522_3_ * 16 + p_202522_2_.nextInt(16));
         double d1 = (double)p_202522_2_.nextInt(128);
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
            float f3 = (p_202522_2_.nextFloat() - 0.5F) * 2.0F / 8.0F;
            double d4 = 5.0D;
            float f2 = (p_202522_2_.nextFloat() * 2.0F + p_202522_2_.nextFloat()) * 2.0F;
            int i1 = i - p_202522_2_.nextInt(i / 4);
            int j1 = 0;
            this.addTunnel(p_202522_1_, p_202522_2_.nextLong(), p_202522_5_, p_202522_6_, d0, d1, d2, f2, f, f3, 0, i1, 5.0D, p_202522_7_);
         }
      }

      return true;
   }

   protected boolean carveAtTarget(IWorld p_202516_1_, long p_202516_2_, int p_202516_4_, int p_202516_5_, double p_202516_6_, double p_202516_8_, double p_202516_10_, double p_202516_12_, double p_202516_14_, BitSet p_202516_16_) {
      double d0 = (double)(p_202516_4_ * 16 + 8);
      double d1 = (double)(p_202516_5_ * 16 + 8);
      if (!(p_202516_6_ < d0 - 16.0D - p_202516_12_ * 2.0D) && !(p_202516_10_ < d1 - 16.0D - p_202516_12_ * 2.0D) && !(p_202516_6_ > d0 + 16.0D + p_202516_12_ * 2.0D) && !(p_202516_10_ > d1 + 16.0D + p_202516_12_ * 2.0D)) {
         int i = Math.max(MathHelper.floor(p_202516_6_ - p_202516_12_) - p_202516_4_ * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(p_202516_6_ + p_202516_12_) - p_202516_4_ * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(p_202516_8_ - p_202516_14_) - 1, 1);
         int l = Math.min(MathHelper.floor(p_202516_8_ + p_202516_14_) + 1, 120);
         int i1 = Math.max(MathHelper.floor(p_202516_10_ - p_202516_12_) - p_202516_5_ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(p_202516_10_ + p_202516_12_) - p_202516_5_ * 16 + 1, 16);
         if (this.doesAreaHaveFluids(p_202516_1_, p_202516_4_, p_202516_5_, i, j, k, l, i1, j1)) {
            return false;
         } else if (i <= j && k <= l && i1 <= j1) {
            boolean flag = false;

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + p_202516_4_ * 16;
               double d2 = ((double)l1 + 0.5D - p_202516_6_) / p_202516_12_;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + p_202516_5_ * 16;
                  double d3 = ((double)j2 + 0.5D - p_202516_10_) / p_202516_12_;

                  for(int k2 = l; k2 > k; --k2) {
                     double d4 = ((double)(k2 - 1) + 0.5D - p_202516_8_) / p_202516_14_;
                     if (d4 > -0.7D && d2 * d2 + d4 * d4 + d3 * d3 < 1.0D) {
                        int l2 = k1 | i2 << 4 | k2 << 8;
                        if (!p_202516_16_.get(l2)) {
                           p_202516_16_.set(l2);
                           if (this.isTargetAllowed(p_202516_1_.getBlockState(new BlockPos(l1, k2, j2)))) {
                              if (k2 <= 31) {
                                 p_202516_1_.setBlockState(new BlockPos(l1, k2, j2), LAVA_FLUID.getBlockState(), 2);
                              } else {
                                 p_202516_1_.setBlockState(new BlockPos(l1, k2, j2), DEFAULT_CAVE_AIR, 2);
                              }

                              flag = true;
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
