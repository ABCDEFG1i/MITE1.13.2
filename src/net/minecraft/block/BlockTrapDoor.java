package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockTrapDoor extends BlockHorizontal implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   protected BlockTrapDoor(Block.Properties p_i48307_1_) {
      super(p_i48307_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(OPEN, Boolean.valueOf(false)).with(HALF, Half.BOTTOM).with(POWERED, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      if (!p_196244_1_.get(OPEN)) {
         return p_196244_1_.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch(p_196244_1_.get(HORIZONTAL_FACING)) {
         case NORTH:
         default:
            return NORTH_OPEN_AABB;
         case SOUTH:
            return SOUTH_OPEN_AABB;
         case WEST:
            return WEST_OPEN_AABB;
         case EAST:
            return EAST_OPEN_AABB;
         }
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return p_196266_1_.get(OPEN);
      case WATER:
         return p_196266_1_.get(WATERLOGGED);
      case AIR:
         return p_196266_1_.get(OPEN);
      default:
         return false;
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (this.material == Material.IRON) {
         return false;
      } else {
         p_196250_1_ = p_196250_1_.cycle(OPEN);
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 2);
         if (p_196250_1_.get(WATERLOGGED)) {
            p_196250_2_.getPendingFluidTicks().scheduleTick(p_196250_3_, Fluids.WATER, Fluids.WATER.getTickRate(p_196250_2_));
         }

         this.playSound(p_196250_4_, p_196250_2_, p_196250_3_, p_196250_1_.get(OPEN));
         return true;
      }
   }

   protected void playSound(@Nullable EntityPlayer p_185731_1_, World p_185731_2_, BlockPos p_185731_3_, boolean p_185731_4_) {
      if (p_185731_4_) {
         int i = this.material == Material.IRON ? 1037 : 1007;
         p_185731_2_.playEvent(p_185731_1_, i, p_185731_3_, 0);
      } else {
         int j = this.material == Material.IRON ? 1036 : 1013;
         p_185731_2_.playEvent(p_185731_1_, j, p_185731_3_, 0);
      }

   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         boolean flag = p_189540_2_.isBlockPowered(p_189540_3_);
         if (flag != p_189540_1_.get(POWERED)) {
            if (p_189540_1_.get(OPEN) != flag) {
               p_189540_1_ = p_189540_1_.with(OPEN, Boolean.valueOf(flag));
               this.playSound(null, p_189540_2_, p_189540_3_, flag);
            }

            p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.with(POWERED, Boolean.valueOf(flag)), 2);
            if (p_189540_1_.get(WATERLOGGED)) {
               p_189540_2_.getPendingFluidTicks().scheduleTick(p_189540_3_, Fluids.WATER, Fluids.WATER.getTickRate(p_189540_2_));
            }
         }

      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = this.getDefaultState();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      EnumFacing enumfacing = p_196258_1_.getFace();
      if (!p_196258_1_.func_196012_c() && enumfacing.getAxis().isHorizontal()) {
         iblockstate = iblockstate.with(HORIZONTAL_FACING, enumfacing).with(HALF, p_196258_1_.getHitY() > 0.5F ? Half.TOP : Half.BOTTOM);
      } else {
         iblockstate = iblockstate.with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite()).with(HALF, enumfacing == EnumFacing.UP ? Half.BOTTOM : Half.TOP);
      }

      if (p_196258_1_.getWorld().isBlockPowered(p_196258_1_.getPos())) {
         iblockstate = iblockstate.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true));
      }

      return iblockstate.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return (p_193383_4_ == EnumFacing.UP && p_193383_2_.get(HALF) == Half.TOP || p_193383_4_ == EnumFacing.DOWN && p_193383_2_.get(HALF) == Half.BOTTOM) && !p_193383_2_.get(OPEN) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
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

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }
}
