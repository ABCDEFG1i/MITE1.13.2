package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class SwampSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double d0 = Biome.INFO_NOISE.getValue((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D);
      if (d0 > 0.0D) {
         int i = p_205610_4_ & 15;
         int j = p_205610_5_ & 15;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = p_205610_6_; k >= 0; --k) {
            blockpos$mutableblockpos.setPos(i, k, j);
            if (!p_205610_2_.getBlockState(blockpos$mutableblockpos).isAir()) {
               if (k == 62 && p_205610_2_.getBlockState(blockpos$mutableblockpos).getBlock() != p_205610_10_.getBlock()) {
                  p_205610_2_.setBlockState(blockpos$mutableblockpos, p_205610_10_, false);
                  if (d0 < 0.12D) {
                     p_205610_2_.setBlockState(blockpos$mutableblockpos.move(0, 1, 0), Blocks.LILY_PAD.getDefaultState(), false);
                  }
               }
               break;
            }
         }
      }

      Biome.DEFAULT_SURFACE_BUILDER.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, p_205610_14_);
   }
}
