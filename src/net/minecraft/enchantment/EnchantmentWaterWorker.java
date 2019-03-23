package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(Enchantment.Rarity p_i46719_1_, EntityEquipmentSlot... p_i46719_2_) {
      super(p_i46719_1_, EnumEnchantmentType.ARMOR_HEAD, p_i46719_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 40;
   }

   public int getMaxLevel() {
      return 1;
   }
}
