package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockWall extends BlockFourWay {
   public static final BooleanProperty UP = BlockStateProperties.UP;
   private final VoxelShape[] field_196422_D;
   private final VoxelShape[] field_196423_E;

   public BlockWall(Block.Properties p_i48301_1_) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, p_i48301_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(true)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.field_196422_D = this.func_196408_a(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.field_196423_E = this.func_196408_a(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return p_196244_1_.get(UP) ? this.field_196422_D[this.getIndex(p_196244_1_)] : super.getShape(p_196244_1_, p_196244_2_, p_196244_3_);
   }

   public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
      return p_196268_1_.get(UP) ? this.field_196423_E[this.getIndex(p_196268_1_)] : super.getCollisionShape(p_196268_1_, p_196268_2_, p_196268_3_);
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   private boolean attachesTo(IBlockState p_196421_1_, BlockFaceShape p_196421_2_) {
      Block block = p_196421_1_.getBlock();
      boolean flag = p_196421_2_ == BlockFaceShape.MIDDLE_POLE_THICK || p_196421_2_ == BlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate;
      return !isExcepBlockForAttachWithPiston(block) && p_196421_2_ == BlockFaceShape.SOLID || flag;
   }

   public static boolean isExcepBlockForAttachWithPiston(Block p_194143_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194143_0_) || p_194143_0_ == Blocks.BARRIER || p_194143_0_ == Blocks.MELON || p_194143_0_ == Blocks.PUMPKIN || p_194143_0_ == Blocks.CARVED_PUMPKIN || p_194143_0_ == Blocks.JACK_O_LANTERN || p_194143_0_ == Blocks.FROSTED_ICE || p_194143_0_ == Blocks.TNT;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IWorldReaderBase iworldreaderbase = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.east();
      BlockPos blockpos3 = blockpos.south();
      BlockPos blockpos4 = blockpos.west();
      IBlockState iblockstate = iworldreaderbase.getBlockState(blockpos1);
      IBlockState iblockstate1 = iworldreaderbase.getBlockState(blockpos2);
      IBlockState iblockstate2 = iworldreaderbase.getBlockState(blockpos3);
      IBlockState iblockstate3 = iworldreaderbase.getBlockState(blockpos4);
      boolean flag = this.attachesTo(iblockstate, iblockstate.getBlockFaceShape(iworldreaderbase, blockpos1, EnumFacing.SOUTH));
      boolean flag1 = this.attachesTo(iblockstate1, iblockstate1.getBlockFaceShape(iworldreaderbase, blockpos2, EnumFacing.WEST));
      boolean flag2 = this.attachesTo(iblockstate2, iblockstate2.getBlockFaceShape(iworldreaderbase, blockpos3, EnumFacing.NORTH));
      boolean flag3 = this.attachesTo(iblockstate3, iblockstate3.getBlockFaceShape(iworldreaderbase, blockpos4, EnumFacing.EAST));
      boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
      return this.getDefaultState().with(UP, Boolean.valueOf(flag4 || !iworldreaderbase.isAirBlock(blockpos.up()))).with(NORTH, Boolean.valueOf(flag)).with(EAST, Boolean.valueOf(flag1)).with(SOUTH, Boolean.valueOf(flag2)).with(WEST, Boolean.valueOf(flag3)).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      if (p_196271_2_ == EnumFacing.DOWN) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         boolean flag = p_196271_2_ == EnumFacing.NORTH ? this.attachesTo(p_196271_3_, p_196271_3_.getBlockFaceShape(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite())) : p_196271_1_.get(NORTH);
         boolean flag1 = p_196271_2_ == EnumFacing.EAST ? this.attachesTo(p_196271_3_, p_196271_3_.getBlockFaceShape(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite())) : p_196271_1_.get(EAST);
         boolean flag2 = p_196271_2_ == EnumFacing.SOUTH ? this.attachesTo(p_196271_3_, p_196271_3_.getBlockFaceShape(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite())) : p_196271_1_.get(SOUTH);
         boolean flag3 = p_196271_2_ == EnumFacing.WEST ? this.attachesTo(p_196271_3_, p_196271_3_.getBlockFaceShape(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite())) : p_196271_1_.get(WEST);
         boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
         return p_196271_1_.with(UP, Boolean.valueOf(flag4 || !p_196271_4_.isAirBlock(p_196271_5_.up()))).with(NORTH, Boolean.valueOf(flag)).with(EAST, Boolean.valueOf(flag1)).with(SOUTH, Boolean.valueOf(flag2)).with(WEST, Boolean.valueOf(flag3));
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
   }
}
