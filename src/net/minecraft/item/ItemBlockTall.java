package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ItemBlockTall extends ItemBlock {
   public ItemBlockTall(Block p_i48511_1_, Item.Properties p_i48511_2_) {
      super(p_i48511_1_, p_i48511_2_);
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, IBlockState p_195941_2_) {
      p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos().up(), Blocks.AIR.getDefaultState(), 27);
      return super.placeBlock(p_195941_1_, p_195941_2_);
   }
}
