package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemEmptyMap extends ItemMapBase {
   public ItemEmptyMap(Item.Properties p_i48506_1_) {
      super(p_i48506_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = ItemMap.setupNewMap(p_77659_1_, MathHelper.floor(p_77659_2_.posX), MathHelper.floor(p_77659_2_.posZ), (byte)0, true, false);
      ItemStack itemstack1 = p_77659_2_.getHeldItem(p_77659_3_);
      itemstack1.shrink(1);
      if (itemstack1.isEmpty()) {
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         if (!p_77659_2_.inventory.addItemStackToInventory(itemstack.copy())) {
            p_77659_2_.dropItem(itemstack, false);
         }

         p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
      }
   }
}
