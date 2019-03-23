package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentLootBonus extends Enchantment {
   protected EnchantmentLootBonus(Enchantment.Rarity p_i46726_1_, EnumEnchantmentType p_i46726_2_, EntityEquipmentSlot... p_i46726_3_) {
      super(p_i46726_1_, p_i46726_2_, p_i46726_3_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 15 + (p_77321_1_ - 1) * 9;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return super.getMinEnchantability(p_77317_1_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.SILK_TOUCH;
   }
}
