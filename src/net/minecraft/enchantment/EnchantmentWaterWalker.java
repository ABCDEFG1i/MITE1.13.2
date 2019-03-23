package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentWaterWalker extends Enchantment {
   public EnchantmentWaterWalker(Enchantment.Rarity p_i46720_1_, EntityEquipmentSlot... p_i46720_2_) {
      super(p_i46720_1_, EnumEnchantmentType.ARMOR_FEET, p_i46720_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return p_77321_1_ * 10;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.FROST_WALKER;
   }
}
