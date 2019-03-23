package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockShearableDoublePlant extends BlockDoublePlant {
   public static final EnumProperty<DoubleBlockHalf> field_208063_b = BlockDoublePlant.HALF;
   private final Block field_196392_b;

   public BlockShearableDoublePlant(Block p_i48335_1_, Block.Properties p_i48335_2_) {
      super(p_i48335_2_);
      this.field_196392_b = p_i48335_1_;
   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      boolean flag = super.isReplaceable(p_196253_1_, p_196253_2_);
      return flag && p_196253_2_.getItem().getItem() == this.asItem() ? false : flag;
   }

   protected void harvest(IBlockState p_196391_1_, World p_196391_2_, BlockPos p_196391_3_, ItemStack p_196391_4_) {
      if (p_196391_4_.getItem() == Items.SHEARS) {
         spawnAsEntity(p_196391_2_, p_196391_3_, new ItemStack(this.field_196392_b, 2));
      } else {
         super.harvest(p_196391_1_, p_196391_2_, p_196391_3_, p_196391_4_);
      }

   }

   public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
      return p_199769_1_.get(field_208063_b) == DoubleBlockHalf.LOWER && this == Blocks.TALL_GRASS && p_199769_2_.rand.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
   }
}
