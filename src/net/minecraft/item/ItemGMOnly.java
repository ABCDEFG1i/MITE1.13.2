package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public class ItemGMOnly extends ItemBlock {
   public ItemGMOnly(Block p_i48491_1_, Item.Properties p_i48491_2_) {
      super(p_i48491_1_, p_i48491_2_);
   }

   @Nullable
   protected IBlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
      EntityPlayer entityplayer = p_195945_1_.getPlayer();
      return entityplayer != null && !entityplayer.canUseCommandBlock() ? null : super.getStateForPlacement(p_195945_1_);
   }
}
