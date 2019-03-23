package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.trees.AbstractTree;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockSapling extends BlockBush implements IGrowable {
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE_0_1;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
   private final AbstractTree tree;

   protected BlockSapling(AbstractTree p_i48337_1_, Block.Properties p_i48337_2_) {
      super(p_i48337_2_);
      this.tree = p_i48337_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(STAGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      super.tick(p_196267_1_, p_196267_2_, p_196267_3_, p_196267_4_);
      if (p_196267_2_.getLight(p_196267_3_.up()) >= 9 && p_196267_4_.nextInt(7) == 0) {
         this.grow(p_196267_2_, p_196267_3_, p_196267_1_, p_196267_4_);
      }

   }

   public void grow(IWorld p_176478_1_, BlockPos p_176478_2_, IBlockState p_176478_3_, Random p_176478_4_) {
      if (p_176478_3_.get(STAGE) == 0) {
         p_176478_1_.setBlockState(p_176478_2_, p_176478_3_.cycle(STAGE), 4);
      } else {
         this.tree.spawn(p_176478_1_, p_176478_2_, p_176478_3_, p_176478_4_);
      }

   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return (double)p_180670_1_.rand.nextFloat() < 0.45D;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      this.grow(p_176474_1_, p_176474_3_, p_176474_4_, p_176474_2_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(STAGE);
   }
}
