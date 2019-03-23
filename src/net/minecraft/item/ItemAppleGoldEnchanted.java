package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemAppleGoldEnchanted extends ItemFood {
   public ItemAppleGoldEnchanted(int p_i48504_1_, float p_i48504_2_, boolean p_i48504_3_, Item.Properties p_i48504_4_) {
      super(p_i48504_1_, p_i48504_2_, p_i48504_3_, p_i48504_4_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }

   protected void onFoodEaten(ItemStack p_77849_1_, World p_77849_2_, EntityPlayer p_77849_3_) {
      if (!p_77849_2_.isRemote) {
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
         p_77849_3_.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
      }

   }
}
