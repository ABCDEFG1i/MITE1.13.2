package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentLoyalty extends Enchantment {
   public EnchantmentLoyalty(Enchantment.Rarity p_i48785_1_, EntityEquipmentSlot... p_i48785_2_) {
      super(p_i48785_1_, EnumEnchantmentType.TRIDENT, p_i48785_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 5 + p_77321_1_ * 7;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_);
   }
}
