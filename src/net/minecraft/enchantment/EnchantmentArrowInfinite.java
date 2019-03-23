package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArrowInfinite extends Enchantment {
   public EnchantmentArrowInfinite(Enchantment.Rarity p_i46736_1_, EntityEquipmentSlot... p_i46736_2_) {
      super(p_i46736_1_, EnumEnchantmentType.BOW, p_i46736_2_);
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

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return p_77326_1_ instanceof EnchantmentMending ? false : super.canApplyTogether(p_77326_1_);
   }
}
