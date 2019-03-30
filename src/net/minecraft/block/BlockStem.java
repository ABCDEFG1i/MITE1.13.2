package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockStem extends BlockBush implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   private final BlockStemGrown crop;

   protected BlockStem(BlockStemGrown p_i48318_1_, Block.Properties p_i48318_2_) {
      super(p_i48318_2_);
      this.crop = p_i48318_1_;
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPES[p_196244_1_.get(AGE)];
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.FARMLAND;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      super.tick(p_196267_1_, p_196267_2_, p_196267_3_, p_196267_4_);
      if (p_196267_2_.getLightSubtracted(p_196267_3_.up(), 0) >= 9) {
         float f = BlockCrops.getGrowthChance(this, p_196267_2_, p_196267_3_);
         if (p_196267_4_.nextInt((int)(25.0F / f) + 1) == 0) {
            int i = p_196267_1_.get(AGE);
            if (i < 7) {
               p_196267_1_ = p_196267_1_.with(AGE, Integer.valueOf(i + 1));
               p_196267_2_.setBlockState(p_196267_3_, p_196267_1_, 2);
            } else {
               EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_196267_4_);
               BlockPos blockpos = p_196267_3_.offset(enumfacing);
               Block block = p_196267_2_.getBlockState(blockpos.down()).getBlock();
               if (p_196267_2_.getBlockState(blockpos).isAir() && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                  p_196267_2_.setBlockState(blockpos, this.crop.getDefaultState());
                  p_196267_2_.setBlockState(p_196267_3_, this.crop.getAttachedStem().getDefaultState().with(BlockHorizontal.HORIZONTAL_FACING, enumfacing));
               }
            }
         }

      }
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, fortuneLevel);
        if (!worldIn.isRemote) {
         Item item = this.getSeedItem();
         if (item != null) {
             int i = blockCurrentState.get(AGE);

            for(int j = 0; j < 3; ++j) {
                if (worldIn.rand.nextInt(15) <= i) {
                    spawnAsEntity(worldIn, blockAt, new ItemStack(item));
               }
            }

         }
      }
   }

   @Nullable
   protected Item getSeedItem() {
      if (this.crop == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.crop == Blocks.MELON ? Items.MELON_SEEDS : null;
      }
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      Item item = this.getSeedItem();
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_3_.get(AGE) != 7;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      int i = Math.min(7, p_176474_4_.get(AGE) + MathHelper.nextInt(p_176474_1_.rand, 2, 5));
      IBlockState iblockstate = p_176474_4_.with(AGE, Integer.valueOf(i));
      p_176474_1_.setBlockState(p_176474_3_, iblockstate, 2);
      if (i == 7) {
         iblockstate.tick(p_176474_1_, p_176474_3_, p_176474_1_.rand);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public BlockStemGrown func_208486_d() {
      return this.crop;
   }
}
