package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IBlockReader {
   @Nullable
   TileEntity getTileEntity(BlockPos p_175625_1_);

   IBlockState getBlockState(BlockPos p_180495_1_);

   IFluidState getFluidState(BlockPos p_204610_1_);

   default int getMaxLightLevel() {
      return 15;
   }
}
