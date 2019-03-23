package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood {
   public ItemAppleGold(int p_i48490_1_, float p_i48490_2_, boolean p_i48490_3_, Item.Properties p_i48490_4_) {
      super(p_i48490_1_, p_i48490_2_, p_i48490_3_, p_i48490_4_);
   }

   protected void onFoodEaten(ItemStack p_77849_1_, World p_77849_2_, EntityPlayer p_77849_3_) {
      if (!p_77849_2_.isRemote) {
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
      }

   }
}
