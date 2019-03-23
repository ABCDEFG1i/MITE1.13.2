package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentMending extends Enchantment {
   public EnchantmentMending(Enchantment.Rarity p_i46725_1_, EntityEquipmentSlot... p_i46725_2_) {
      super(p_i46725_1_, EnumEnchantmentType.BREAKABLE, p_i46725_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return p_77321_1_ * 25;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 50;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }
}
