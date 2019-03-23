package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Block.Properties p_i48392_1_) {
      super(p_i48392_1_);
   }

   public boolean func_200123_i(IBlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
