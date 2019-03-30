package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSnow extends Block {
   protected BlockSnow(Block.Properties p_i48329_1_) {
      super(p_i48329_1_);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.SNOWBALL;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 4;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_2_.getLightFor(EnumLightType.BLOCK, p_196267_3_) > 11) {
         p_196267_1_.dropBlockAsItem(p_196267_2_, p_196267_3_, 0);
         p_196267_2_.removeBlock(p_196267_3_);
      }

   }
}
