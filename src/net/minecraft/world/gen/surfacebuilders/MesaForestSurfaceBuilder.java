package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class MesaForestSurfaceBuilder extends MesaSurfaceBuilder {
   private static final IBlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   private static final IBlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
   private static final IBlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int i = p_205610_4_ & 15;
      int j = p_205610_5_ & 15;
      IBlockState iblockstate = WHITE_TERRACOTTA;
      IBlockState iblockstate1 = p_205610_3_.getSurfaceBuilderConfig().getMiddle();
      int k = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean flag = Math.cos(p_205610_7_ / 3.0D * Math.PI) > 0.0D;
      int l = -1;
      boolean flag1 = false;
      int i1 = 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j1 = p_205610_6_; j1 >= 0; --j1) {
         if (i1 < 15) {
            blockpos$mutableblockpos.setPos(i, j1, j);
            IBlockState iblockstate2 = p_205610_2_.getBlockState(blockpos$mutableblockpos);
            if (iblockstate2.isAir()) {
               l = -1;
            } else if (iblockstate2.getBlock() == p_205610_9_.getBlock()) {
               if (l == -1) {
                  flag1 = false;
                  if (k <= 0) {
                     iblockstate = Blocks.AIR.getDefaultState();
                     iblockstate1 = p_205610_9_;
                  } else if (j1 >= p_205610_11_ - 4 && j1 <= p_205610_11_ + 1) {
                     iblockstate = WHITE_TERRACOTTA;
                     iblockstate1 = p_205610_3_.getSurfaceBuilderConfig().getMiddle();
                  }

                  if (j1 < p_205610_11_ && (iblockstate == null || iblockstate.isAir())) {
                     iblockstate = p_205610_10_;
                  }

                  l = k + Math.max(0, j1 - p_205610_11_);
                  if (j1 >= p_205610_11_ - 1) {
                     if (j1 > 86 + k * 2) {
                        if (flag) {
                           p_205610_2_.setBlockState(blockpos$mutableblockpos, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                           p_205610_2_.setBlockState(blockpos$mutableblockpos, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                     } else if (j1 > p_205610_11_ + 3 + k) {
                        IBlockState iblockstate3;
                        if (j1 >= 64 && j1 <= 127) {
                           if (flag) {
                              iblockstate3 = TERRACOTTA;
                           } else {
                              iblockstate3 = this.func_202614_a(p_205610_4_, j1, p_205610_5_);
                           }
                        } else {
                           iblockstate3 = ORANGE_TERRACOTTA;
                        }

                        p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate3, false);
                     } else {
                        p_205610_2_.setBlockState(blockpos$mutableblockpos, p_205610_3_.getSurfaceBuilderConfig().getTop(), false);
                        flag1 = true;
                     }
                  } else {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
                     if (iblockstate1.getBlock() == WHITE_TERRACOTTA) {
                        p_205610_2_.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (l > 0) {
                  --l;
                  if (flag1) {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                  } else {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, this.func_202614_a(p_205610_4_, j1, p_205610_5_), false);
                  }
               }

               ++i1;
            }
         }
      }

   }
}
