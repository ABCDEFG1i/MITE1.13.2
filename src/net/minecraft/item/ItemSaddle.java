package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

public class ItemSaddle extends Item {
   public ItemSaddle(Item.Properties p_i48474_1_) {
      super(p_i48474_1_);
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, EntityPlayer p_111207_2_, EntityLivingBase p_111207_3_, EnumHand p_111207_4_) {
      if (p_111207_3_ instanceof EntityPig) {
         EntityPig entitypig = (EntityPig)p_111207_3_;
         if (!entitypig.getSaddled() && !entitypig.isChild()) {
            entitypig.setSaddled(true);
            entitypig.world.playSound(p_111207_2_, entitypig.posX, entitypig.posY, entitypig.posZ, SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
            p_111207_1_.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }
}
