package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockClay extends Block {
   public BlockClay(Block.Properties p_i48427_1_) {
      super(p_i48427_1_);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.CLAY_BALL;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 4;
   }
}
