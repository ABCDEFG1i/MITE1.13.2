package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentUntouching extends Enchantment {
   protected EnchantmentUntouching(Enchantment.Rarity p_i46721_1_, EntityEquipmentSlot... p_i46721_2_) {
      super(p_i46721_1_, EnumEnchantmentType.DIGGER, p_i46721_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 15;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return super.getMinEnchantability(p_77317_1_) + 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.FORTUNE;
   }
}
