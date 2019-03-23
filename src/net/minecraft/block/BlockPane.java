package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPane extends BlockFourWay {
   protected BlockPane(Block.Properties p_i48373_1_) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, p_i48373_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.south();
      BlockPos blockpos3 = blockpos.west();
      BlockPos blockpos4 = blockpos.east();
      IBlockState iblockstate = iblockreader.getBlockState(blockpos1);
      IBlockState iblockstate1 = iblockreader.getBlockState(blockpos2);
      IBlockState iblockstate2 = iblockreader.getBlockState(blockpos3);
      IBlockState iblockstate3 = iblockreader.getBlockState(blockpos4);
      return this.getDefaultState().with(NORTH, Boolean.valueOf(this.func_196417_a(iblockstate, iblockstate.getBlockFaceShape(iblockreader, blockpos1, EnumFacing.SOUTH)))).with(SOUTH, Boolean.valueOf(this.func_196417_a(iblockstate1, iblockstate1.getBlockFaceShape(iblockreader, blockpos2, EnumFacing.NORTH)))).with(WEST, Boolean.valueOf(this.func_196417_a(iblockstate2, iblockstate2.getBlockFaceShape(iblockreader, blockpos3, EnumFacing.EAST)))).with(EAST, Boolean.valueOf(this.func_196417_a(iblockstate3, iblockstate3.getBlockFaceShape(iblockreader, blockpos4, EnumFacing.WEST)))).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_.getAxis().isHorizontal() ? p_196271_1_.with(FACING_TO_PROPERTY_MAP.get(p_196271_2_), Boolean.valueOf(this.func_196417_a(p_196271_3_, p_196271_3_.getBlockFaceShape(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite())))) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState p_200122_1_, IBlockState p_200122_2_, EnumFacing p_200122_3_) {
      if (p_200122_2_.getBlock() == this) {
         if (!p_200122_3_.getAxis().isHorizontal()) {
            return true;
         }

         if (p_200122_1_.get(FACING_TO_PROPERTY_MAP.get(p_200122_3_)) && p_200122_2_.get(FACING_TO_PROPERTY_MAP.get(p_200122_3_.getOpposite()))) {
            return true;
         }
      }

      return super.isSideInvisible(p_200122_1_, p_200122_2_, p_200122_3_);
   }

   public final boolean func_196417_a(IBlockState p_196417_1_, BlockFaceShape p_196417_2_) {
      Block block = p_196417_1_.getBlock();
      return !func_196418_h(block) && p_196417_2_ == BlockFaceShape.SOLID || p_196417_2_ == BlockFaceShape.MIDDLE_POLE_THIN;
   }

   public static boolean func_196418_h(Block p_196418_0_) {
      return p_196418_0_ instanceof BlockShulkerBox || p_196418_0_ instanceof BlockLeaves || p_196418_0_ == Blocks.BEACON || p_196418_0_ == Blocks.CAULDRON || p_196418_0_ == Blocks.GLOWSTONE || p_196418_0_ == Blocks.ICE || p_196418_0_ == Blocks.SEA_LANTERN || p_196418_0_ == Blocks.PISTON || p_196418_0_ == Blocks.STICKY_PISTON || p_196418_0_ == Blocks.PISTON_HEAD || p_196418_0_ == Blocks.MELON || p_196418_0_ == Blocks.PUMPKIN || p_196418_0_ == Blocks.CARVED_PUMPKIN || p_196418_0_ == Blocks.JACK_O_LANTERN || p_196418_0_ == Blocks.BARRIER;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
   }
}
