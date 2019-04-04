package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;

public class BlockRotatedPillar extends Block {
   public static final EnumProperty<EnumFacing.Axis> AXIS = BlockStateProperties.AXIS;

   public BlockRotatedPillar(Block.Properties p_i48339_1_) {
      super(p_i48339_1_);
      this.setDefaultState(this.getDefaultState().with(AXIS, EnumFacing.Axis.Y));
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch(p_185499_1_.get(AXIS)) {
         case X:
            return p_185499_1_.with(AXIS, EnumFacing.Axis.Z);
         case Z:
            return p_185499_1_.with(AXIS, EnumFacing.Axis.X);
         default:
            return p_185499_1_;
         }
      default:
         return p_185499_1_;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AXIS);
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(AXIS, p_196258_1_.getFace().getAxis());
   }
}
