package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ItemSeedFood extends ItemFood {
   private final IBlockState field_195972_b;

   public ItemSeedFood(int p_i48473_1_, float p_i48473_2_, Block p_i48473_3_, Item.Properties p_i48473_4_) {
      super(p_i48473_1_, p_i48473_2_, false, p_i48473_4_);
      this.field_195972_b = p_i48473_3_.getDefaultState();
   }

   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      IWorld iworld = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos().up();
      if (p_195939_1_.getFace() == EnumFacing.UP && iworld.isAirBlock(blockpos) && this.field_195972_b.isValidPosition(iworld, blockpos)) {
         iworld.setBlockState(blockpos, this.field_195972_b, 11);
         EntityPlayer entityplayer = p_195939_1_.getPlayer();
         ItemStack itemstack = p_195939_1_.getItem();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }
}
