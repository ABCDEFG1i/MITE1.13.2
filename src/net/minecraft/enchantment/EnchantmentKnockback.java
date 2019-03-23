package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentKnockback extends Enchantment {
   protected EnchantmentKnockback(Enchantment.Rarity p_i46727_1_, EntityEquipmentSlot... p_i46727_2_) {
      super(p_i46727_1_, EnumEnchantmentType.WEAPON, p_i46727_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 5 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return super.getMinEnchantability(p_77317_1_) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}
