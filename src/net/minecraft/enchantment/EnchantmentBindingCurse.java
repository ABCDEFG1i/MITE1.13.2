package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentBindingCurse extends Enchantment {
   public EnchantmentBindingCurse(Enchantment.Rarity p_i47254_1_, EntityEquipmentSlot... p_i47254_2_) {
      super(p_i47254_1_, EnumEnchantmentType.WEARABLE, p_i47254_2_);
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
