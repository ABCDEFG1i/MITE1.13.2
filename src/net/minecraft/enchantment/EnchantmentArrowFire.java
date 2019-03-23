package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArrowFire extends Enchantment {
   public EnchantmentArrowFire(Enchantment.Rarity p_i46737_1_, EntityEquipmentSlot... p_i46737_2_) {
      super(p_i46737_1_, EnumEnchantmentType.BOW, p_i46737_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 20;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
