package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEmptyDrops extends Block {
   public BlockEmptyDrops(Block.Properties p_i48360_1_) {
      super(p_i48360_1_);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }
}
