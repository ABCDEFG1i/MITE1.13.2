package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockKelp extends Block implements ILiquidContainer {
   final BlockKelpTop field_209904_a;

   protected BlockKelp(BlockKelpTop p_i49501_1_, Block.Properties p_i49501_2_) {
      super(p_i49501_2_);
      this.field_209904_a = p_i49501_1_;
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

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (p_196271_2_ == EnumFacing.UP) {
            Block block = p_196271_3_.getBlock();
            if (block != this && block != this.field_209904_a) {
               return this.field_209904_a.randomAge(p_196271_4_);
            }
         }

         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      IBlockState iblockstate = p_196260_2_.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      return block != Blocks.MAGMA_BLOCK && (block == this || Block.doesSideFillSquare(iblockstate.getCollisionShape(p_196260_2_, blockpos), EnumFacing.UP));
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Blocks.KELP;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(Blocks.KELP);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return false;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      return false;
   }
}
