package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBeetroot extends BlockCrops {
   public static final IntegerProperty BEETROOT_AGE = BlockStateProperties.AGE_0_3;
   private static final VoxelShape[] SHAPE = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

   public BlockBeetroot(Block.Properties p_i48441_1_) {
      super(p_i48441_1_);
   }

   public IntegerProperty getAgeProperty() {
      return BEETROOT_AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected IItemProvider getSeedsItem() {
      return Items.BEETROOT_SEEDS;
   }

   protected IItemProvider getCropsItem() {
      return Items.BEETROOT;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_4_.nextInt(3) != 0) {
         super.tick(p_196267_1_, p_196267_2_, p_196267_3_, p_196267_4_);
      }

   }

   protected int getBonemealAgeIncrease(World p_185529_1_) {
      return super.getBonemealAgeIncrease(p_185529_1_) / 3;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(BEETROOT_AGE);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE[p_196244_1_.get(this.getAgeProperty())];
   }
}
