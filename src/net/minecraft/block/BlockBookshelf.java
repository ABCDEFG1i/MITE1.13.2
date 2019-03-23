package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBookshelf extends Block {
   public BlockBookshelf(Block.Properties p_i48439_1_) {
      super(p_i48439_1_);
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 3;
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.BOOK;
   }
}
