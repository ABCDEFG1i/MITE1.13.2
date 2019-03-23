package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentVanishingCurse extends Enchantment {
   public EnchantmentVanishingCurse(Enchantment.Rarity p_i47252_1_, EntityEquipmentSlot... p_i47252_2_) {
      super(p_i47252_1_, EnumEnchantmentType.ALL, p_i47252_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 25;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }
}
