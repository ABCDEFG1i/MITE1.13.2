package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockGlowstone extends Block {
   public BlockGlowstone(Block.Properties p_i48389_1_) {
      super(p_i48389_1_);
   }

   public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
      return MathHelper.clamp(this.quantityDropped(p_196251_1_, p_196251_5_) + p_196251_5_.nextInt(p_196251_2_ + 1), 1, 4);
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 2 + p_196264_2_.nextInt(3);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.GLOWSTONE_DUST;
   }
}
