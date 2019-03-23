package net.minecraft.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ItemNameTag extends Item {
   public ItemNameTag(Item.Properties p_i48479_1_) {
      super(p_i48479_1_);
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, EntityPlayer p_111207_2_, EntityLivingBase p_111207_3_, EnumHand p_111207_4_) {
      if (p_111207_1_.hasDisplayName() && !(p_111207_3_ instanceof EntityPlayer)) {
         p_111207_3_.setCustomName(p_111207_1_.getDisplayName());
         if (p_111207_3_ instanceof EntityLiving) {
            ((EntityLiving)p_111207_3_).enablePersistence();
         }

         p_111207_1_.shrink(1);
         return true;
      } else {
         return false;
      }
   }
}
