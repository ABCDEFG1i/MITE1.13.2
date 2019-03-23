package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentChanneling extends Enchantment {
   public EnchantmentChanneling(Enchantment.Rarity p_i48787_1_, EntityEquipmentSlot... p_i48787_2_) {
      super(p_i48787_1_, EnumEnchantmentType.TRIDENT, p_i48787_2_);
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

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_);
   }
}
