package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentRiptide extends Enchantment {
   public EnchantmentRiptide(Enchantment.Rarity p_i48784_1_, EntityEquipmentSlot... p_i48784_2_) {
      super(p_i48784_1_, EnumEnchantmentType.TRIDENT, p_i48784_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 10 + p_77321_1_ * 7;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.LOYALTY && p_77326_1_ != Enchantments.CHANNELING;
   }
}
