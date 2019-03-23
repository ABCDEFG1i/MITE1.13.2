package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentSweepingEdge extends Enchantment {
   public EnchantmentSweepingEdge(Enchantment.Rarity p_i47366_1_, EntityEquipmentSlot... p_i47366_2_) {
      super(p_i47366_1_, EnumEnchantmentType.WEAPON, p_i47366_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 5 + (p_77321_1_ - 1) * 9;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public static float getSweepingDamageRatio(int p_191526_0_) {
      return 1.0F - 1.0F / (float)(p_191526_0_ + 1);
   }
}
