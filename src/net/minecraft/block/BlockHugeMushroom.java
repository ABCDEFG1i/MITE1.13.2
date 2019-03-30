package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class BlockHugeMushroom extends Block {
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final BooleanProperty UP = BlockSixWay.UP;
   public static final BooleanProperty DOWN = BlockSixWay.DOWN;
   private static final Map<EnumFacing, BooleanProperty> field_196462_B = BlockSixWay.FACING_TO_PROPERTY_MAP;
   @Nullable
   private final Block smallBlock;

   public BlockHugeMushroom(@Nullable Block p_i48376_1_, Block.Properties p_i48376_2_) {
      super(p_i48376_2_);
      this.smallBlock = p_i48376_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(true)).with(EAST, Boolean.valueOf(true)).with(SOUTH, Boolean.valueOf(true)).with(WEST, Boolean.valueOf(true)).with(UP, Boolean.valueOf(true)).with(DOWN, Boolean.valueOf(true)));
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return Math.max(0, p_196264_2_.nextInt(9) - 6);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return (this.smallBlock == null ? Items.AIR : this.smallBlock);
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      return this.getDefaultState().with(DOWN, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.down()).getBlock())).with(UP, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.up()).getBlock())).with(NORTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.north()).getBlock())).with(EAST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.east()).getBlock())).with(SOUTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.south()).getBlock())).with(WEST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.west()).getBlock()));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_3_.getBlock() == this ? p_196271_1_.with(field_196462_B.get(p_196271_2_), Boolean.valueOf(false)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(field_196462_B.get(p_185499_2_.rotate(EnumFacing.NORTH)), p_185499_1_.get(NORTH)).with(field_196462_B.get(p_185499_2_.rotate(EnumFacing.SOUTH)), p_185499_1_.get(SOUTH)).with(field_196462_B.get(p_185499_2_.rotate(EnumFacing.EAST)), p_185499_1_.get(EAST)).with(field_196462_B.get(p_185499_2_.rotate(EnumFacing.WEST)), p_185499_1_.get(WEST)).with(field_196462_B.get(p_185499_2_.rotate(EnumFacing.UP)), p_185499_1_.get(UP)).with(field_196462_B.get(p_185499_2_.rotate(EnumFacing.DOWN)), p_185499_1_.get(DOWN));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.with(field_196462_B.get(p_185471_2_.mirror(EnumFacing.NORTH)), p_185471_1_.get(NORTH)).with(field_196462_B.get(p_185471_2_.mirror(EnumFacing.SOUTH)), p_185471_1_.get(SOUTH)).with(field_196462_B.get(p_185471_2_.mirror(EnumFacing.EAST)), p_185471_1_.get(EAST)).with(field_196462_B.get(p_185471_2_.mirror(EnumFacing.WEST)), p_185471_1_.get(WEST)).with(field_196462_B.get(p_185471_2_.mirror(EnumFacing.UP)), p_185471_1_.get(UP)).with(field_196462_B.get(p_185471_2_.mirror(EnumFacing.DOWN)), p_185471_1_.get(DOWN));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}
