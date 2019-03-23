package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class MesaBryceSurfaceBuilder extends MesaSurfaceBuilder {
   private static final IBlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   private static final IBlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
   private static final IBlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = 0.0D;
      double d1 = Math.min(Math.abs(p_205610_7_), this.field_202617_c.getValue((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D));
      if (d1 > 0.0D) {
         double d2 = 0.001953125D;
         double d3 = Math.abs(this.field_202618_d.getValue((double)p_205610_4_ * 0.001953125D, (double)p_205610_5_ * 0.001953125D));
         d0 = d1 * d1 * 2.5D;
         double d4 = Math.ceil(d3 * 50.0D) + 14.0D;
         if (d0 > d4) {
            d0 = d4;
         }

         d0 = d0 + 64.0D;
      }

      int l = p_205610_4_ & 15;
      int i = p_205610_5_ & 15;
      IBlockState iblockstate2 = WHITE_TERRACOTTA;
      IBlockState iblockstate = p_205610_3_.getSurfaceBuilderConfig().getMiddle();
      int i1 = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean flag = Math.cos(p_205610_7_ / 3.0D * Math.PI) > 0.0D;
      int j = -1;
      boolean flag1 = false;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k = Math.max(p_205610_6_, (int)d0 + 1); k >= 0; --k) {
         blockpos$mutableblockpos.setPos(l, k, i);
         if (p_205610_2_.getBlockState(blockpos$mutableblockpos).isAir() && k < (int)d0) {
            p_205610_2_.setBlockState(blockpos$mutableblockpos, p_205610_9_, false);
         }

         IBlockState iblockstate1 = p_205610_2_.getBlockState(blockpos$mutableblockpos);
         if (iblockstate1.isAir()) {
            j = -1;
         } else if (iblockstate1.getBlock() == p_205610_9_.getBlock()) {
            if (j == -1) {
               flag1 = false;
               if (i1 <= 0) {
                  iblockstate2 = Blocks.AIR.getDefaultState();
                  iblockstate = p_205610_9_;
               } else if (k >= p_205610_11_ - 4 && k <= p_205610_11_ + 1) {
                  iblockstate2 = WHITE_TERRACOTTA;
                  iblockstate = p_205610_3_.getSurfaceBuilderConfig().getMiddle();
               }

               if (k < p_205610_11_ && (iblockstate2 == null || iblockstate2.isAir())) {
                  iblockstate2 = p_205610_10_;
               }

               j = i1 + Math.max(0, k - p_205610_11_);
               if (k >= p_205610_11_ - 1) {
                  if (k <= p_205610_11_ + 3 + i1) {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, p_205610_3_.getSurfaceBuilderConfig().getTop(), false);
                     flag1 = true;
                  } else {
                     IBlockState iblockstate3;
                     if (k >= 64 && k <= 127) {
                        if (flag) {
                           iblockstate3 = TERRACOTTA;
                        } else {
                           iblockstate3 = this.func_202614_a(p_205610_4_, k, p_205610_5_);
                        }
                     } else {
                        iblockstate3 = ORANGE_TERRACOTTA;
                     }

                     p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate3, false);
                  }
               } else {
                  p_205610_2_.setBlockState(blockpos$mutableblockpos, iblockstate, false);
                  Block block = iblockstate.getBlock();
                  if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (j > 0) {
               --j;
               if (flag1) {
                  p_205610_2_.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
               } else {
                  p_205610_2_.setBlockState(blockpos$mutableblockpos, this.func_202614_a(p_205610_4_, k, p_205610_5_), false);
               }
            }
         }
      }

   }
}
