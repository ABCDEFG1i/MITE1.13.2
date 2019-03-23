package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class FrozenOceanSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   protected static final IBlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
   protected static final IBlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
   private static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   private static final IBlockState ICE = Blocks.ICE.getDefaultState();
   private NoiseGeneratorPerlin field_205199_h;
   private NoiseGeneratorPerlin field_205200_i;
   private long field_205201_j;

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      float f = p_205610_3_.getTemperature(blockpos$mutableblockpos.setPos(p_205610_4_, 63, p_205610_5_));
      double d2 = Math.min(Math.abs(p_205610_7_), this.field_205199_h.getValue((double)p_205610_4_ * 0.1D, (double)p_205610_5_ * 0.1D));
      if (d2 > 1.8D) {
         double d3 = 0.09765625D;
         double d4 = Math.abs(this.field_205200_i.getValue((double)p_205610_4_ * 0.09765625D, (double)p_205610_5_ * 0.09765625D));
         d0 = d2 * d2 * 1.2D;
         double d5 = Math.ceil(d4 * 40.0D) + 14.0D;
         if (d0 > d5) {
            d0 = d5;
         }

         if (f > 0.1F) {
            d0 -= 2.0D;
         }

         if (d0 > 2.0D) {
            d1 = (double)p_205610_11_ - d0 - 7.0D;
            d0 = d0 + (double)p_205610_11_;
         } else {
            d0 = 0.0D;
         }
      }

      int k1 = p_205610_4_ & 15;
      int i = p_205610_5_ & 15;
      IBlockState iblockstate2 = p_205610_3_.getSurfaceBuilderConfig().getMiddle();
      IBlockState iblockstate = p_205610_3_.getSurfaceBuilderConfig().getTop();
      int l1 = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      int j = -1;
      int k = 0;
      int l = 2 + p_205610_1_.nextInt(4);
      int i1 = p_205610_11_ + 18 + p_205610_1_.nextInt(10);

      for(int j1 = Math.max(p_205610_6_, (int)d0 + 1); j1 >= 0; --j1) {
         blockpos$mutableblockpos.setPos(k1, j1, i);
         if (p_205610_2_.getBlockState(blockpos$mutableblockpos).isAir() && j1 < (int)d0 && p_205610_1_.nextDouble() > 0.01D) {
            p_205610_2_.setBlockState(blockpos$mutableblockpos, PACKED_ICE, false);
         } else if (p_205610_2_.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.WATER && j1 > (int)d1 && j1 < p_205610_11_ && d1 != 0.0D && p_205610_1_.nextDouble() > 0.15D) {
            p_205610_2_.setBlockState(blockpos$mutableblockpos, PACKED_ICE, false);
         }

         IBlockState iblockstate1 = p_205610_2_.getBlockState(blockpos$mutableblockpos);
         if (iblockstate1.isAir()) {
            j = -1;
         } else if (iblockstate1.getBlock() != p_205610_9_.getBlock()) {
            if (iblockstate1.getBlock() == Blocks.PACKED_ICE && k <= l && j1 > i1) {
               p_205610_2_.setBlockState(blockpos$mutableblockpos, SNOW_BLOCK, false);
               ++k;
            }
         } else if (j == -1) {
            if (l1 <= 0) {
               iblockstate = AIR;
               iblockstate2 = p_205610_9_;
            } else if (j1 >= p_205610_11_ - 4 && j1 <= p_205610_11_ + 1) {
               iblockstate = p_205610_3_.getSurfaceBuilderConfig().getTop();
               iblockstate2 = p_205610_3_.getSurfaceBuilderConfig().getMiddle();
            }

            if (j1 < p_205610_11_ && (iblockstate == null || iblockstate.isAir())) {
               if (p_205610_3_.getTemperature(blockpos$mutableblockpos.setPos(p_205610_4_, j1, p_205610_5_)) < 0.15F) {
                  iblockstate = ICE;
               } else {
                  iblockstate = p_205610_10_;
               }
            }

            j = l1;
            if (j1 >= p_205610_11_ - 1) {
               p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate, false);
            } else if (j1 < p_205610_11_ - 7 - l1) {
               iblockstate = AIR;
               iblockstate2 = p_205610_9_;
               p_205610_2_.setBlockState(blockpos$mutableblockpos, GRAVEL, false);
            } else {
               p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate2, false);
            }
         } else if (j > 0) {
            --j;
            p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate2, false);
            if (j == 0 && iblockstate2.getBlock() == Blocks.SAND && l1 > 1) {
               j = p_205610_1_.nextInt(4) + Math.max(0, j1 - 63);
               iblockstate2 = iblockstate2.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
            }
         }
      }

   }

   public void setSeed(long p_205548_1_) {
      if (this.field_205201_j != p_205548_1_ || this.field_205199_h == null || this.field_205200_i == null) {
         Random random = new SharedSeedRandom(p_205548_1_);
         this.field_205199_h = new NoiseGeneratorPerlin(random, 4);
         this.field_205200_i = new NoiseGeneratorPerlin(random, 1);
      }

      this.field_205201_j = p_205548_1_;
   }
}
