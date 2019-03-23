package net.minecraft.item;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {
   public ItemCarrotOnAStick(Item.Properties p_i48519_1_) {
      super(p_i48519_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, EntityPlayer p_77659_2_, EnumHand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      if (p_77659_1_.isRemote) {
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      } else {
         if (p_77659_2_.isRiding() && p_77659_2_.getRidingEntity() instanceof EntityPig) {
            EntityPig entitypig = (EntityPig)p_77659_2_.getRidingEntity();
            if (itemstack.getMaxDamage() - itemstack.getDamage() >= 7 && entitypig.boost()) {
               itemstack.damageItem(7, p_77659_2_);
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                  itemstack1.setTag(itemstack.getTag());
                  return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
               }

               return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
         }

         p_77659_2_.func_71029_a(StatList.ITEM_USED.func_199076_b(this));
         return new ActionResult<>(EnumActionResult.PASS, itemstack);
      }
   }
}
