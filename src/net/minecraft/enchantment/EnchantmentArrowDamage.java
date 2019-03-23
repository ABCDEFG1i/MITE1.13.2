package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArrowDamage extends Enchantment {
   public EnchantmentArrowDamage(Enchantment.Rarity p_i46738_1_, EntityEquipmentSlot... p_i46738_2_) {
      super(p_i46738_1_, EnumEnchantmentType.BOW, p_i46738_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + (p_77321_1_ - 1) * 10;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}
