package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockSlab extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   public BlockSlab(Block.Properties p_i48331_1_) {
      super(p_i48331_1_);
      this.setDefaultState(this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return p_200011_2_.getMaxLightLevel();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(TYPE, WATERLOGGED);
   }

   protected boolean canSilkHarvest() {
      return false;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      SlabType slabtype = p_196244_1_.get(TYPE);
      switch(slabtype) {
      case DOUBLE:
         return VoxelShapes.func_197868_b();
      case TOP:
         return TOP_SHAPE;
      default:
         return BOTTOM_SHAPE;
      }
   }

   public boolean isTopSolid(IBlockState p_185481_1_) {
      return p_185481_1_.get(TYPE) == SlabType.DOUBLE || p_185481_1_.get(TYPE) == SlabType.TOP;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      SlabType slabtype = p_193383_2_.get(TYPE);
      if (slabtype == SlabType.DOUBLE) {
         return BlockFaceShape.SOLID;
      } else if (p_193383_4_ == EnumFacing.UP && slabtype == SlabType.TOP) {
         return BlockFaceShape.SOLID;
      } else {
         return p_193383_4_ == EnumFacing.DOWN && slabtype == SlabType.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      if (iblockstate.getBlock() == this) {
         return iblockstate.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, Boolean.valueOf(false));
      } else {
         IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
         IBlockState iblockstate1 = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
         EnumFacing enumfacing = p_196258_1_.getFace();
         return enumfacing != EnumFacing.DOWN && (enumfacing == EnumFacing.UP || !((double)p_196258_1_.getHitY() > 0.5D)) ? iblockstate1 : iblockstate1.with(TYPE, SlabType.TOP);
      }
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return p_196264_1_.get(TYPE) == SlabType.DOUBLE ? 2 : 1;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return p_149686_1_.get(TYPE) == SlabType.DOUBLE;
   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      ItemStack itemstack = p_196253_2_.getItem();
      SlabType slabtype = p_196253_1_.get(TYPE);
      if (slabtype != SlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
         if (p_196253_2_.func_196012_c()) {
            boolean flag = (double)p_196253_2_.getHitY() > 0.5D;
            EnumFacing enumfacing = p_196253_2_.getFace();
            if (slabtype == SlabType.BOTTOM) {
               return enumfacing == EnumFacing.UP || flag && enumfacing.getAxis().isHorizontal();
            } else {
               return enumfacing == EnumFacing.DOWN || !flag && enumfacing.getAxis().isHorizontal();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
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
      return p_204510_3_.get(TYPE) != SlabType.DOUBLE && !p_204510_3_.get(WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      if (p_204509_3_.get(TYPE) != SlabType.DOUBLE && !p_204509_3_.get(WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
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

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return p_196266_1_.get(TYPE) == SlabType.BOTTOM;
      case WATER:
         return p_196266_2_.getFluidState(p_196266_3_).isTagged(FluidTags.WATER);
      case AIR:
         return false;
      default:
         return false;
      }
   }
}
