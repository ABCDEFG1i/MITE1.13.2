package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;
   private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};

   protected BlockNetherWart(Block.Properties p_i48361_1_) {
      super(p_i48361_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPES[p_196244_1_.get(AGE)];
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.SOUL_SAND;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      int i = p_196267_1_.get(AGE);
      if (i < 3 && p_196267_4_.nextInt(10) == 0) {
         p_196267_1_ = p_196267_1_.with(AGE, Integer.valueOf(i + 1));
         p_196267_2_.setBlockState(p_196267_3_, p_196267_1_, 2);
      }

      super.tick(p_196267_1_, p_196267_2_, p_196267_3_, p_196267_4_);
   }

   public void dropBlockAsItemWithChance(IBlockState p_196255_1_, World p_196255_2_, BlockPos p_196255_3_, float p_196255_4_, int p_196255_5_) {
      if (!p_196255_2_.isRemote) {
         int i = 1;
         if (p_196255_1_.get(AGE) >= 3) {
            i = 2 + p_196255_2_.rand.nextInt(3);
            if (p_196255_5_ > 0) {
               i += p_196255_2_.rand.nextInt(p_196255_5_ + 1);
            }
         }

         for(int j = 0; j < i; ++j) {
            spawnAsEntity(p_196255_2_, p_196255_3_, new ItemStack(Items.NETHER_WART));
         }

      }
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.AIR;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }
}
