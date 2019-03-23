package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentOxygen extends Enchantment {
   public EnchantmentOxygen(Enchantment.Rarity p_i46724_1_, EntityEquipmentSlot... p_i46724_2_) {
      super(p_i46724_1_, EnumEnchantmentType.ARMOR_HEAD, p_i46724_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 10 * p_77321_1_;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}
