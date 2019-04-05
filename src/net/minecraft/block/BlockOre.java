package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Random;

public class BlockOre extends Block implements IMineLevel{
    private int mineLevel;
   public BlockOre(Block.Properties p_i48357_1_,int mineLevel) {
      super(p_i48357_1_);
      this.mineLevel = mineLevel;
   }

    public int getMineLevel() {
        return mineLevel;
    }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      if (this == Blocks.COAL_ORE) {
         return Items.COAL;
      } else if (this == Blocks.DIAMOND_ORE) {
         return Items.DIAMOND;
      } else if (this == Blocks.LAPIS_ORE) {
         return Items.LAPIS_LAZULI;
      } else if (this == Blocks.EMERALD_ORE) {
         return Items.EMERALD;
      } else {
         return (this == Blocks.NETHER_QUARTZ_ORE ? Items.QUARTZ : this);
      }
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return this == Blocks.LAPIS_ORE ? 4 + p_196264_2_.nextInt(5) : 1;
   }

   public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
      if (p_196251_2_ > 0 && this != this.getItemDropped(this.getStateContainer().getValidStates().iterator().next(), p_196251_3_, p_196251_4_, p_196251_2_)) {
         int i = p_196251_5_.nextInt(p_196251_2_ + 2) - 1;
         if (i < 0) {
            i = 0;
         }

         return this.quantityDropped(p_196251_1_, p_196251_5_) * (i + 1);
      } else {
         return this.quantityDropped(p_196251_1_, p_196251_5_);
      }
   }

    public void dropBlockAsItemWithChance(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, float chanceToDrop, int fortuneLevel) {
        super.dropBlockAsItemWithChance(blockCurrentState, worldIn, blockAt, chanceToDrop, fortuneLevel);
        if (this.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel) != this) {
         int i = 0;
         if (this == Blocks.COAL_ORE) {
             i = MathHelper.nextInt(worldIn.rand, 0, 2);
         } else if (this == Blocks.DIAMOND_ORE) {
             i = MathHelper.nextInt(worldIn.rand, 3, 7);
         } else if (this == Blocks.EMERALD_ORE) {
             i = MathHelper.nextInt(worldIn.rand, 3, 7);
         } else if (this == Blocks.LAPIS_ORE) {
             i = MathHelper.nextInt(worldIn.rand, 2, 5);
         } else if (this == Blocks.NETHER_QUARTZ_ORE) {
             i = MathHelper.nextInt(worldIn.rand, 2, 5);
         }

            this.dropXpOnBlockBreak(worldIn, blockAt, i);
      }

   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return new ItemStack(this);
   }
}
