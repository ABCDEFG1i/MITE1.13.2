package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   boolean setBlockState(BlockPos p_180501_1_, IBlockState p_180501_2_, int p_180501_3_);

   boolean spawnEntity(Entity p_72838_1_);

   boolean removeBlock(BlockPos p_175698_1_);

   void setLightFor(EnumLightType p_175653_1_, BlockPos p_175653_2_, int p_175653_3_);

   boolean destroyBlock(BlockPos p_175655_1_, boolean p_175655_2_);
}
