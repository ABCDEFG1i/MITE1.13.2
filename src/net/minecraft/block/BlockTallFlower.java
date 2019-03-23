package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTallFlower extends BlockDoublePlant implements IGrowable {
   public BlockTallFlower(Block.Properties p_i48311_1_) {
      super(p_i48311_1_);
   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return false;
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      spawnAsEntity(p_176474_1_, p_176474_3_, new ItemStack(this));
   }
}
