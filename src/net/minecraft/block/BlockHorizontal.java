package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class BlockHorizontal extends Block {
   public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

   protected BlockHorizontal(Block.Properties p_i48377_1_) {
      super(p_i48377_1_);
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(HORIZONTAL_FACING, p_185499_2_.rotate(p_185499_1_.get(HORIZONTAL_FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(HORIZONTAL_FACING)));
   }
}
