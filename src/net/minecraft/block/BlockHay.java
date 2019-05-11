package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay(Block.Properties p_i48380_1_) {
      super(p_i48380_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, EnumFacing.Axis.Y));
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      p_180658_3_.fall(p_180658_4_, 0.2F, false);
   }
}
