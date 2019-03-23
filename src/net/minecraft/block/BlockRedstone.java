package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockRedstone extends Block {
   public BlockRedstone(Block.Properties p_i48350_1_) {
      super(p_i48350_1_);
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return 15;
   }
}
