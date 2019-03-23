package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class DefaultSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      this.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_14_.getTop(), p_205610_14_.getMiddle(), p_205610_14_.getBottom(), p_205610_11_);
   }

   protected void buildSurface(Random p_206967_1_, IChunk p_206967_2_, Biome p_206967_3_, int p_206967_4_, int p_206967_5_, int p_206967_6_, double p_206967_7_, IBlockState p_206967_9_, IBlockState p_206967_10_, IBlockState p_206967_11_, IBlockState p_206967_12_, IBlockState p_206967_13_, int p_206967_14_) {
      IBlockState iblockstate = p_206967_11_;
      IBlockState iblockstate1 = p_206967_12_;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = -1;
      int j = (int)(p_206967_7_ / 3.0D + 3.0D + p_206967_1_.nextDouble() * 0.25D);
      int k = p_206967_4_ & 15;
      int l = p_206967_5_ & 15;

      for(int i1 = p_206967_6_; i1 >= 0; --i1) {
         blockpos$mutableblockpos.setPos(k, i1, l);
         IBlockState iblockstate2 = p_206967_2_.getBlockState(blockpos$mutableblockpos);
         if (iblockstate2.isAir()) {
            i = -1;
         } else if (iblockstate2.getBlock() == p_206967_9_.getBlock()) {
            if (i == -1) {
               if (j <= 0) {
                  iblockstate = Blocks.AIR.getDefaultState();
                  iblockstate1 = p_206967_9_;
               } else if (i1 >= p_206967_14_ - 4 && i1 <= p_206967_14_ + 1) {
                  iblockstate = p_206967_11_;
                  iblockstate1 = p_206967_12_;
               }

               if (i1 < p_206967_14_ && (iblockstate == null || iblockstate.isAir())) {
                  if (p_206967_3_.getTemperature(blockpos$mutableblockpos.setPos(p_206967_4_, i1, p_206967_5_)) < 0.15F) {
                     iblockstate = Blocks.ICE.getDefaultState();
                  } else {
                     iblockstate = p_206967_10_;
                  }

                  blockpos$mutableblockpos.setPos(k, i1, l);
               }

               i = j;
               if (i1 >= p_206967_14_ - 1) {
                  p_206967_2_.setBlockState(blockpos$mutableblockpos, iblockstate, false);
               } else if (i1 < p_206967_14_ - 7 - j) {
                  iblockstate = Blocks.AIR.getDefaultState();
                  iblockstate1 = p_206967_9_;
                  p_206967_2_.setBlockState(blockpos$mutableblockpos, p_206967_13_, false);
               } else {
                  p_206967_2_.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
               }
            } else if (i > 0) {
               --i;
               p_206967_2_.setBlockState(blockpos$mutableblockpos, iblockstate1, false);
               if (i == 0 && iblockstate1.getBlock() == Blocks.SAND && j > 1) {
                  i = p_206967_1_.nextInt(4) + Math.max(0, i1 - 63);
                  iblockstate1 = iblockstate1.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
               }
            }
         }
      }

   }
}
