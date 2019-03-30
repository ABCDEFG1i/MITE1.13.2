package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Map;

public class BlockAttachedStem extends BlockBush {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   private final BlockStemGrown grownFruit;
   private static final Map<EnumFacing, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(EnumFacing.SOUTH, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), EnumFacing.WEST, Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), EnumFacing.NORTH, Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), EnumFacing.EAST, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));

   protected BlockAttachedStem(BlockStemGrown p_i48449_1_, Block.Properties p_i48449_2_) {
      super(p_i48449_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
      this.grownFruit = p_i48449_1_;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPES.get(p_196244_1_.get(FACING));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_3_.getBlock() != this.grownFruit && p_196271_2_ == p_196271_1_.get(FACING) ? this.grownFruit.getStem().getDefaultState().with(BlockStem.AGE, Integer.valueOf(7)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.FARMLAND;
   }

   protected Item getSeeds() {
      if (this.grownFruit == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.grownFruit == Blocks.MELON ? Items.MELON_SEEDS : Items.AIR;
      }
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(this.getSeeds());
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }
}
