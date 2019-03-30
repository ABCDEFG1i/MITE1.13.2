package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCrops extends BlockBush implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

   protected BlockCrops(Block.Properties p_i48421_1_) {
      super(p_i48421_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(this.getAgeProperty(), Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE_BY_AGE[p_196244_1_.get(this.getAgeProperty())];
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.FARMLAND;
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 7;
   }

   protected int getAge(IBlockState p_185527_1_) {
      return p_185527_1_.get(this.getAgeProperty());
   }

   public IBlockState withAge(int p_185528_1_) {
      return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(p_185528_1_));
   }

   public boolean isMaxAge(IBlockState p_185525_1_) {
      return p_185525_1_.get(this.getAgeProperty()) >= this.getMaxAge();
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      super.tick(p_196267_1_, p_196267_2_, p_196267_3_, p_196267_4_);
      if (p_196267_2_.getLightSubtracted(p_196267_3_.up(), 0) >= 9) {
         int i = this.getAge(p_196267_1_);
         if (i < this.getMaxAge()) {
            float f = getGrowthChance(this, p_196267_2_, p_196267_3_);
            if (p_196267_4_.nextInt((int)(25.0F / f) + 1) == 0) {
               p_196267_2_.setBlockState(p_196267_3_, this.withAge(i + 1), 2);
            }
         }
      }

   }

   public void grow(World p_176487_1_, BlockPos p_176487_2_, IBlockState p_176487_3_) {
      int i = this.getAge(p_176487_3_) + this.getBonemealAgeIncrease(p_176487_1_);
      int j = this.getMaxAge();
      if (i > j) {
         i = j;
      }

      p_176487_1_.setBlockState(p_176487_2_, this.withAge(i), 2);
   }

   protected int getBonemealAgeIncrease(World p_185529_1_) {
      return MathHelper.nextInt(p_185529_1_.rand, 2, 5);
   }

   protected static float getGrowthChance(Block p_180672_0_, IBlockReader p_180672_1_, BlockPos p_180672_2_) {
      float f = 1.0F;
      BlockPos blockpos = p_180672_2_.down();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            float f1 = 0.0F;
            IBlockState iblockstate = p_180672_1_.getBlockState(blockpos.add(i, 0, j));
            if (iblockstate.getBlock() == Blocks.FARMLAND) {
               f1 = 1.0F;
               if (iblockstate.get(BlockFarmland.MOISTURE) > 0) {
                  f1 = 3.0F;
               }
            }

            if (i != 0 || j != 0) {
               f1 /= 4.0F;
            }

            f += f1;
         }
      }

      BlockPos blockpos1 = p_180672_2_.north();
      BlockPos blockpos2 = p_180672_2_.south();
      BlockPos blockpos3 = p_180672_2_.west();
      BlockPos blockpos4 = p_180672_2_.east();
      boolean flag = p_180672_0_ == p_180672_1_.getBlockState(blockpos3).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos4).getBlock();
      boolean flag1 = p_180672_0_ == p_180672_1_.getBlockState(blockpos1).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos2).getBlock();
      if (flag && flag1) {
         f /= 2.0F;
      } else {
         boolean flag2 = p_180672_0_ == p_180672_1_.getBlockState(blockpos3.north()).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos4.north()).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos4.south()).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos3.south()).getBlock();
         if (flag2) {
            f /= 2.0F;
         }
      }

      return f;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return (p_196260_2_.getLightSubtracted(p_196260_3_, 0) >= 8 || p_196260_2_.canSeeSky(p_196260_3_)) && super.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
   }

   protected IItemProvider getSeedsItem() {
      return Items.WHEAT_SEEDS;
   }

   protected IItemProvider getCropsItem() {
      return Items.WHEAT;
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, 0);
        if (!worldIn.isRemote) {
            int i = this.getAge(blockCurrentState);
         if (i >= this.getMaxAge()) {
             int j = 3 + fortuneLevel;

            for(int k = 0; k < j; ++k) {
                if (worldIn.rand.nextInt(2 * this.getMaxAge()) <= i) {
                    spawnAsEntity(worldIn, blockAt, new ItemStack(this.getSeedsItem()));
               }
            }
         }

      }
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
        return this.isMaxAge(blockCurrentState) ? this.getCropsItem() : this.getSeedsItem();
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(this.getSeedsItem());
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return !this.isMaxAge(p_176473_3_);
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      this.grow(p_176474_1_, p_176474_3_, p_176474_4_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }
}
