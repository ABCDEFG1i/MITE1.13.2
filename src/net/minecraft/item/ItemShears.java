package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemShears extends Item {
   public ItemShears(Item.Properties p_i48471_1_) {
      super(p_i48471_1_);
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, IBlockState p_179218_3_, BlockPos p_179218_4_, EntityLivingBase p_179218_5_) {
      if (!p_179218_2_.isRemote) {
         p_179218_1_.damageItem(1, p_179218_5_);
      }

      Block block = p_179218_3_.getBlock();
      return !p_179218_3_.isIn(BlockTags.LEAVES) && block != Blocks.COBWEB && block != Blocks.GRASS && block != Blocks.FERN && block != Blocks.DEAD_BUSH && block != Blocks.VINE && block != Blocks.TRIPWIRE && !block.isIn(BlockTags.WOOL) ? super.onBlockDestroyed(p_179218_1_, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_) : true;
   }

   public boolean canHarvestBlock(IBlockState p_150897_1_) {
      Block block = p_150897_1_.getBlock();
      return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, IBlockState p_150893_2_) {
      Block block = p_150893_2_.getBlock();
      if (block != Blocks.COBWEB && !p_150893_2_.isIn(BlockTags.LEAVES)) {
         return block.isIn(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(p_150893_1_, p_150893_2_);
      } else {
         return 15.0F;
      }
   }
}
