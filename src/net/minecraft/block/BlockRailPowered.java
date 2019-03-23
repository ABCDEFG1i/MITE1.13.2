package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected BlockRailPowered(Block.Properties p_i48349_1_) {
      super(true, p_i48349_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, Boolean.valueOf(false)));
   }

   protected boolean findPoweredRailSignal(World p_176566_1_, BlockPos p_176566_2_, IBlockState p_176566_3_, boolean p_176566_4_, int p_176566_5_) {
      if (p_176566_5_ >= 8) {
         return false;
      } else {
         int i = p_176566_2_.getX();
         int j = p_176566_2_.getY();
         int k = p_176566_2_.getZ();
         boolean flag = true;
         RailShape railshape = p_176566_3_.get(SHAPE);
         switch(railshape) {
         case NORTH_SOUTH:
            if (p_176566_4_) {
               ++k;
            } else {
               --k;
            }
            break;
         case EAST_WEST:
            if (p_176566_4_) {
               --i;
            } else {
               ++i;
            }
            break;
         case ASCENDING_EAST:
            if (p_176566_4_) {
               --i;
            } else {
               ++i;
               ++j;
               flag = false;
            }

            railshape = RailShape.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (p_176566_4_) {
               --i;
               ++j;
               flag = false;
            } else {
               ++i;
            }

            railshape = RailShape.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (p_176566_4_) {
               ++k;
            } else {
               --k;
               ++j;
               flag = false;
            }

            railshape = RailShape.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (p_176566_4_) {
               ++k;
               ++j;
               flag = false;
            } else {
               --k;
            }

            railshape = RailShape.NORTH_SOUTH;
         }

         if (this.func_208071_a(p_176566_1_, new BlockPos(i, j, k), p_176566_4_, p_176566_5_, railshape)) {
            return true;
         } else {
            return flag && this.func_208071_a(p_176566_1_, new BlockPos(i, j - 1, k), p_176566_4_, p_176566_5_, railshape);
         }
      }
   }

   protected boolean func_208071_a(World p_208071_1_, BlockPos p_208071_2_, boolean p_208071_3_, int p_208071_4_, RailShape p_208071_5_) {
      IBlockState iblockstate = p_208071_1_.getBlockState(p_208071_2_);
      if (iblockstate.getBlock() != this) {
         return false;
      } else {
         RailShape railshape = iblockstate.get(SHAPE);
         if (p_208071_5_ != RailShape.EAST_WEST || railshape != RailShape.NORTH_SOUTH && railshape != RailShape.ASCENDING_NORTH && railshape != RailShape.ASCENDING_SOUTH) {
            if (p_208071_5_ != RailShape.NORTH_SOUTH || railshape != RailShape.EAST_WEST && railshape != RailShape.ASCENDING_EAST && railshape != RailShape.ASCENDING_WEST) {
               if (iblockstate.get(POWERED)) {
                  return p_208071_1_.isBlockPowered(p_208071_2_) ? true : this.findPoweredRailSignal(p_208071_1_, p_208071_2_, iblockstate, p_208071_3_, p_208071_4_ + 1);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected void updateState(IBlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
      boolean flag = p_189541_1_.get(POWERED);
      boolean flag1 = p_189541_2_.isBlockPowered(p_189541_3_) || this.findPoweredRailSignal(p_189541_2_, p_189541_3_, p_189541_1_, true, 0) || this.findPoweredRailSignal(p_189541_2_, p_189541_3_, p_189541_1_, false, 0);
      if (flag1 != flag) {
         p_189541_2_.setBlockState(p_189541_3_, p_189541_1_.with(POWERED, Boolean.valueOf(flag1)), 3);
         p_189541_2_.notifyNeighborsOfStateChange(p_189541_3_.down(), this);
         if (p_189541_1_.get(SHAPE).isAscending()) {
            p_189541_2_.notifyNeighborsOfStateChange(p_189541_3_.up(), this);
         }
      }

   }

   public IProperty<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
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
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case NORTH_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
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
         }
      case CLOCKWISE_90:
         switch((RailShape)p_185499_1_.get(SHAPE)) {
         case NORTH_SOUTH:
            return p_185499_1_.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_185499_1_.with(SHAPE, RailShape.NORTH_SOUTH);
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
      p_206840_1_.add(SHAPE, POWERED);
   }
}
