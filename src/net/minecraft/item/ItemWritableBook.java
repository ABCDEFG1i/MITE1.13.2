package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   public ItemWritableBook(Item.Properties p_i48455_1_) {
      super(p_i48455_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      p_77659_2_.openBook(itemstack, p_77659_3_);
      p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }

   public static boolean isNBTValid(@Nullable NBTTagCompound p_150930_0_) {
      if (p_150930_0_ == null) {
         return false;
      } else if (!p_150930_0_.hasKey("pages", 9)) {
         return false;
      } else {
         NBTTagList nbttaglist = p_150930_0_.getTagList("pages", 8);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            String s = nbttaglist.getStringTagAt(i);
            if (s.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
