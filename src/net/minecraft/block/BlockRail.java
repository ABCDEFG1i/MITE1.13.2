package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

   protected BlockRail(Block.Properties p_i48346_1_) {
      super(false, p_i48346_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(SHAPE, RailShape.NORTH_SOUTH));
   }

   protected void updateState(IBlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
      if (p_189541_4_.getDefaultState().canProvidePower() && (new BlockRailState(p_189541_2_, p_189541_3_, p_189541_1_)).countAdjacentRails() == 3) {
         this.func_208489_a(p_189541_2_, p_189541_3_, p_189541_1_, false);
      }

   }

   public IProperty<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         switch(p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch(p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch(p_185499_1_.get(SHAPE)) {
         case ASCENDING_EAST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_185499_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return p_185499_1_;
      }
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      RailShape railshape = p_185471_1_.get(SHAPE);
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(p_185471_1_, p_185471_2_);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_185471_1_.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_185471_1_.with(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(p_185471_1_, p_185471_2_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(SHAPE);
   }
}
