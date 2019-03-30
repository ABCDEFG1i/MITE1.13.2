package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSeaGrassTall extends BlockShearableDoublePlant implements ILiquidContainer {
   public static final EnumProperty<DoubleBlockHalf> field_208065_c = BlockShearableDoublePlant.field_208063_b;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public BlockSeaGrassTall(Block p_i48779_1_, Block.Properties p_i48779_2_) {
      super(p_i48779_1_, p_i48779_2_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return Block.doesSideFillSquare(p_200014_1_.getCollisionShape(p_200014_2_, p_200014_3_), EnumFacing.UP) && p_200014_1_.getBlock() != Blocks.MAGMA_BLOCK;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(Blocks.SEAGRASS);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = super.getStateForPlacement(p_196258_1_);
      if (iblockstate != null) {
         IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos().up());
         if (ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8) {
            return iblockstate;
         }
      }

      return null;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      if (p_196260_1_.get(field_208065_c) == DoubleBlockHalf.UPPER) {
         IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
         return iblockstate.getBlock() == this && iblockstate.get(field_208065_c) == DoubleBlockHalf.LOWER;
      } else {
         IFluidState ifluidstate = p_196260_2_.getFluidState(p_196260_3_);
         return super.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_) && ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
      }
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return false;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      return false;
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return Blocks.WATER.getDefaultState().getOpacity(p_200011_2_, p_200011_3_);
   }
}
