package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockLadder extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape LADDER_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape LADDER_WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape LADDER_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape LADDER_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);

   protected BlockLadder(Block.Properties p_i48371_1_) {
      super(p_i48371_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch(p_196244_1_.get(FACING)) {
      case NORTH:
         return LADDER_NORTH_AABB;
      case SOUTH:
         return LADDER_SOUTH_AABB;
      case WEST:
         return LADDER_WEST_AABB;
      case EAST:
      default:
         return LADDER_EAST_AABB;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   private boolean canAttachTo(IBlockReader p_196471_1_, BlockPos p_196471_2_, EnumFacing p_196471_3_) {
      IBlockState iblockstate = p_196471_1_.getBlockState(p_196471_2_);
      boolean flag = isExceptBlockForAttachWithPiston(iblockstate.getBlock());
      return !flag && iblockstate.getBlockFaceShape(p_196471_1_, p_196471_2_, p_196471_3_) == BlockFaceShape.SOLID && !iblockstate.canProvidePower();
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      EnumFacing enumfacing = p_196260_1_.get(FACING);
      return this.canAttachTo(p_196260_2_, p_196260_3_.offset(enumfacing.getOpposite()), enumfacing);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (p_196271_1_.get(WATERLOGGED)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         }

         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      if (!p_196258_1_.func_196012_c()) {
         IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().offset(p_196258_1_.getFace().getOpposite()));
         if (iblockstate.getBlock() == this && iblockstate.get(FACING) == p_196258_1_.getFace()) {
            return null;
         }
      }

      IBlockState iblockstate1 = this.getDefaultState();
      IWorldReaderBase iworldreaderbase = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());

      for(EnumFacing enumfacing : p_196258_1_.func_196009_e()) {
         if (enumfacing.getAxis().isHorizontal()) {
            iblockstate1 = iblockstate1.with(FACING, enumfacing.getOpposite());
            if (iblockstate1.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate1.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      if (p_204508_3_.get(WATERLOGGED)) {
         p_204508_1_.setBlockState(p_204508_2_, p_204508_3_.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return !p_204510_3_.get(WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!p_204509_3_.get(WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
         if (!p_204509_1_.isRemote()) {
            p_204509_1_.setBlockState(p_204509_2_, p_204509_3_.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, p_204509_4_.getFluid(), p_204509_4_.getFluid().getTickRate(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }
}
