package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class NetherSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private static final IBlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   private static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
   private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   private static final IBlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
   protected long field_205552_a;
   protected NoiseGeneratorOctaves field_205553_b;

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int i = p_205610_11_ + 1;
      int j = p_205610_4_ & 15;
      int k = p_205610_5_ & 15;
      double d0 = 0.03125D;
      boolean flag = this.field_205553_b.func_205563_a((double)p_205610_4_ * 0.03125D, (double)p_205610_5_ * 0.03125D, 0.0D) + p_205610_1_.nextDouble() * 0.2D > 0.0D;
      boolean flag1 = this.field_205553_b.func_205563_a((double)p_205610_4_ * 0.03125D, 109.0D, (double)p_205610_5_ * 0.03125D) + p_205610_1_.nextDouble() * 0.2D > 0.0D;
      int l = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i1 = -1;
      IBlockState iblockstate = NETHERRACK;
      IBlockState iblockstate1 = NETHERRACK;

      for(int j1 = 127; j1 >= 0; --j1) {
         blockpos$mutableblockpos.setPos(j, j1, k);
         IBlockState iblockstate2 = p_205610_2_.getBlockState(blockpos$mutableblockpos);
         if (iblockstate2.getBlock() != null && !iblockstate2.isAir()) {
            if (iblockstate2.getBlock() == p_205610_9_.getBlock()) {
               if (i1 == -1) {
                  if (l <= 0) {
                     iblockstate = CAVE_AIR;
                     iblockstate1 = NETHERRACK;
                  } else if (j1 >= i - 4 && j1 <= i + 1) {
                     iblockstate = NETHERRACK;
                     iblockstate1 = NETHERRACK;
                     if (flag1) {
                        iblockstate = GRAVEL;
                        iblockstate1 = NETHERRACK;
                     }

                     if (flag) {
                        iblockstate = SOUL_SAND;
                        iblockstate1 = SOUL_SAND;
                     }
                  }

                  if (j1 < i && (iblockstate == null || iblockstate.isAir())) {
                     iblockstate = p_205610_10_;
                  }

                  i1 = l;
                  if (j1 >= i - 1) {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate, false);
                  } else {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
                  }
               } else if (i1 > 0) {
                  --i1;
                  p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
               }
            }
         } else {
            i1 = -1;
         }
      }

   }

   public void setSeed(long p_205548_1_) {
      if (this.field_205552_a != p_205548_1_ || this.field_205553_b == null) {
         this.field_205553_b = new NoiseGeneratorOctaves(new SharedSeedRandom(p_205548_1_), 4);
      }

      this.field_205552_a = p_205548_1_;
   }
}
