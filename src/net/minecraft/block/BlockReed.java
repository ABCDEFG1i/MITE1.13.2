package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockReed extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   protected BlockReed(Block.Properties p_i48312_1_) {
      super(p_i48312_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_1_.isValidPosition(p_196267_2_, p_196267_3_) && p_196267_2_.isAirBlock(p_196267_3_.up())) {
         int i;
         for(i = 1; p_196267_2_.getBlockState(p_196267_3_.down(i)).getBlock() == this; ++i) {
            ;
         }

         if (i < 3) {
            int j = p_196267_1_.get(AGE);
            if (j == 15) {
               p_196267_2_.setBlockState(p_196267_3_.up(), this.getDefaultState());
               p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(AGE, Integer.valueOf(0)), 4);
            } else {
               p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(AGE, Integer.valueOf(j + 1)), 4);
            }
         }
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      Block block = p_196260_2_.getBlockState(p_196260_3_.down()).getBlock();
      if (block == this) {
         return true;
      } else {
         if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.SAND || block == Blocks.RED_SAND) {
            BlockPos blockpos = p_196260_3_.down();

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               IBlockState iblockstate = p_196260_2_.getBlockState(blockpos.offset(enumfacing));
               IFluidState ifluidstate = p_196260_2_.getFluidState(blockpos.offset(enumfacing));
               if (ifluidstate.isTagged(FluidTags.WATER) || iblockstate.getBlock() == Blocks.FROSTED_ICE) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
