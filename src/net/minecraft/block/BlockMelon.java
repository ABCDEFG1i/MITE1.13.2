package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockMelon extends BlockStemGrown {
   protected BlockMelon(Block.Properties p_i48365_1_) {
      super(p_i48365_1_);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.MELON_SLICE;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 3 + p_196264_2_.nextInt(5);
   }

   public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
      return Math.min(9, this.quantityDropped(p_196251_1_, p_196251_5_) + p_196251_5_.nextInt(1 + p_196251_2_));
   }

   public BlockStem getStem() {
      return (BlockStem)Blocks.MELON_STEM;
   }

   public BlockAttachedStem getAttachedStem() {
      return (BlockAttachedStem)Blocks.ATTACHED_MELON_STEM;
   }
}
