package net.minecraft.enchantment;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentImpaling extends Enchantment {
   public EnchantmentImpaling(Enchantment.Rarity p_i48786_1_, EntityEquipmentSlot... p_i48786_2_) {
      super(p_i48786_1_, EnumEnchantmentType.TRIDENT, p_i48786_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + (p_77321_1_ - 1) * 8;
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + 20;
   }

   public int getMaxLevel() {
      return 5;
   }

   public float calcDamageByCreature(int p_152376_1_, CreatureAttribute p_152376_2_) {
      return p_152376_2_ == CreatureAttribute.WATER ? (float)p_152376_1_ * 2.5F : 0.0F;
   }
}
