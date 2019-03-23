package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockSeaLantern extends Block {
   public BlockSeaLantern(Block.Properties p_i48336_1_) {
      super(p_i48336_1_);
   }

   public int getItemsToDropCount(IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, Random p_196251_5_) {
      return MathHelper.clamp(this.quantityDropped(p_196251_1_, p_196251_5_) + p_196251_5_.nextInt(p_196251_2_ + 1), 1, 5);
   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return Items.PRISMARINE_CRYSTALS;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 2 + p_196264_2_.nextInt(2);
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
