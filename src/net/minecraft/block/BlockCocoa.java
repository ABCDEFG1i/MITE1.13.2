package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCocoa extends BlockHorizontal implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_2;
   protected static final VoxelShape[] COCOA_EAST_AABB = new VoxelShape[]{Block.makeCuboidShape(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.makeCuboidShape(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.makeCuboidShape(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] COCOA_WEST_AABB = new VoxelShape[]{Block.makeCuboidShape(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.makeCuboidShape(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.makeCuboidShape(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] COCOA_NORTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.makeCuboidShape(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.makeCuboidShape(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
   protected static final VoxelShape[] COCOA_SOUTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.makeCuboidShape(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.makeCuboidShape(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

   public BlockCocoa(Block.Properties p_i48426_1_) {
      super(p_i48426_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_2_.rand.nextInt(5) == 0) {
         int i = p_196267_1_.get(AGE);
         if (i < 2) {
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(AGE, Integer.valueOf(i + 1)), 2);
         }
      }

   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      Block block = p_196260_2_.getBlockState(p_196260_3_.offset(p_196260_1_.get(HORIZONTAL_FACING))).getBlock();
      return block.isIn(BlockTags.JUNGLE_LOGS);
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      int i = p_196244_1_.get(AGE);
      switch(p_196244_1_.get(HORIZONTAL_FACING)) {
      case SOUTH:
         return COCOA_SOUTH_AABB[i];
      case NORTH:
      default:
         return COCOA_NORTH_AABB[i];
      case WEST:
         return COCOA_WEST_AABB[i];
      case EAST:
         return COCOA_EAST_AABB[i];
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = this.getDefaultState();
      IWorldReaderBase iworldreaderbase = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();

      for(EnumFacing enumfacing : p_196258_1_.func_196009_e()) {
         if (enumfacing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.with(HORIZONTAL_FACING, enumfacing);
            if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate;
            }
         }
      }

      return null;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == p_196271_1_.get(HORIZONTAL_FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        int i = blockCurrentState.get(AGE);
      int j = 1;
      if (i >= 2) {
         j = 3;
      }

      for(int k = 0; k < j; ++k) {
          spawnAsEntity(worldIn, blockAt, new ItemStack(Items.COCOA_BEANS));
      }

   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(Items.COCOA_BEANS);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_3_.get(AGE) < 2;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      p_176474_1_.setBlockState(p_176474_3_, p_176474_4_.with(AGE, Integer.valueOf(p_176474_4_.get(AGE) + 1)), 2);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, AGE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
