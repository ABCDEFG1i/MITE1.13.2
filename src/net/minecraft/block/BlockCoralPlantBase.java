package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
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

public class BlockCoralPlantBase extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_212560_b = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape field_212559_a = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

   protected BlockCoralPlantBase(Block.Properties p_i49810_1_) {
      super(p_i49810_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(field_212560_b, Boolean.valueOf(true)));
   }

   protected void func_212558_a(IBlockState p_212558_1_, IWorld p_212558_2_, BlockPos p_212558_3_) {
      if (!func_212557_b_(p_212558_1_, p_212558_2_, p_212558_3_)) {
         p_212558_2_.getPendingBlockTicks().scheduleTick(p_212558_3_, this, 60 + p_212558_2_.getRandom().nextInt(40));
      }

   }

   protected static boolean func_212557_b_(IBlockState p_212557_0_, IBlockReader p_212557_1_, BlockPos p_212557_2_) {
      if (p_212557_0_.get(field_212560_b)) {
         return true;
      } else {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            if (p_212557_1_.getFluidState(p_212557_2_.offset(enumfacing)).isTagged(FluidTags.WATER)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return this.getDefaultState().with(field_212560_b, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return field_212559_a;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(field_212560_b)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_ == EnumFacing.DOWN && !this.isValidPosition(p_196271_1_, p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).isTopSolid();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(field_212560_b);
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return p_204507_1_.get(field_212560_b) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      if (p_204508_3_.get(field_212560_b)) {
         p_204508_1_.setBlockState(p_204508_2_, p_204508_3_.with(field_212560_b, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return !p_204510_3_.get(field_212560_b) && p_204510_4_ == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!p_204509_3_.get(field_212560_b) && p_204509_4_.getFluid() == Fluids.WATER) {
         if (!p_204509_1_.isRemote()) {
            p_204509_1_.setBlockState(p_204509_2_, p_204509_3_.with(field_212560_b, Boolean.valueOf(true)), 3);
            p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, p_204509_4_.getFluid(), p_204509_4_.getFluid().getTickRate(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }
}
