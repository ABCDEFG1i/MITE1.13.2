package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockDirtSnowySpreadable extends BlockDirtSnowy {
   protected BlockDirtSnowySpreadable(Block.Properties p_i48324_1_) {
      super(p_i48324_1_);
   }

   private static boolean func_196383_a(IWorldReaderBase p_196383_0_, BlockPos p_196383_1_) {
      BlockPos blockpos = p_196383_1_.up();
      return p_196383_0_.getLight(blockpos) >= 4 || p_196383_0_.getBlockState(blockpos).getOpacity(p_196383_0_, blockpos) < p_196383_0_.getMaxLightLevel();
   }

   private static boolean func_196384_b(IWorldReaderBase p_196384_0_, BlockPos p_196384_1_) {
      BlockPos blockpos = p_196384_1_.up();
      return p_196384_0_.getLight(blockpos) >= 4 && p_196384_0_.getBlockState(blockpos).getOpacity(p_196384_0_, blockpos) < p_196384_0_.getMaxLightLevel() && !p_196384_0_.getFluidState(blockpos).isTagged(FluidTags.WATER);
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         if (!func_196383_a(p_196267_2_, p_196267_3_)) {
            p_196267_2_.setBlockState(p_196267_3_, Blocks.DIRT.getDefaultState());
         } else {
            if (p_196267_2_.getLight(p_196267_3_.up()) >= 9) {
               for(int i = 0; i < 4; ++i) {
                  BlockPos blockpos = p_196267_3_.add(p_196267_4_.nextInt(3) - 1, p_196267_4_.nextInt(5) - 3, p_196267_4_.nextInt(3) - 1);
                  if (!p_196267_2_.isBlockPresent(blockpos)) {
                     return;
                  }

                  if (p_196267_2_.getBlockState(blockpos).getBlock() == Blocks.DIRT && func_196384_b(p_196267_2_, blockpos)) {
                     p_196267_2_.setBlockState(blockpos, this.getDefaultState());
                  }
               }
            }

         }
      }
   }
}
