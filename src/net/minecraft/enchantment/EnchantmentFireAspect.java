package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentFireAspect extends Enchantment {
   protected EnchantmentFireAspect(Enchantment.Rarity p_i46730_1_, EntityEquipmentSlot... p_i46730_2_) {
      super(p_i46730_1_, EnumEnchantmentType.WEAPON, p_i46730_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 10 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return super.getMinEnchantability(p_77317_1_) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}
