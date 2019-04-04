package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentDigging extends Enchantment {
   protected EnchantmentDigging(Enchantment.Rarity p_i46732_1_, EntityEquipmentSlot... p_i46732_2_) {
      super(p_i46732_1_, EnumEnchantmentType.DIGGER, p_i46732_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + 10 * (p_77321_1_ - 1);
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return super.getMinEnchantability(p_77317_1_) + 50;
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() == Items.SHEARS || super.canApply(p_92089_1_);
   }
}
