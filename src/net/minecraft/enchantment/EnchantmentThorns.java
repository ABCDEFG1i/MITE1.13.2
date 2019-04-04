package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class EnchantmentThorns extends Enchantment {
   public EnchantmentThorns(Enchantment.Rarity p_i46722_1_, EntityEquipmentSlot... p_i46722_2_) {
      super(p_i46722_1_, EnumEnchantmentType.ARMOR_CHEST, p_i46722_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 10 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return super.getMinEnchantability(p_77317_1_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() instanceof ItemArmor || super.canApply(p_92089_1_);
   }

   public void onUserHurt(EntityLivingBase p_151367_1_, Entity p_151367_2_, int p_151367_3_) {
      Random random = p_151367_1_.getRNG();
      ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.THORNS, p_151367_1_);
      if (shouldHit(p_151367_3_, random)) {
         if (p_151367_2_ != null) {
            p_151367_2_.attackEntityFrom(DamageSource.causeThornsDamage(p_151367_1_), (float)getDamage(p_151367_3_, random));
         }

         if (!itemstack.isEmpty()) {
            itemstack.damageItem(3, p_151367_1_);
         }
      } else if (!itemstack.isEmpty()) {
         itemstack.damageItem(1, p_151367_1_);
      }

   }

   public static boolean shouldHit(int p_92094_0_, Random p_92094_1_) {
      if (p_92094_0_ <= 0) {
         return false;
      } else {
         return p_92094_1_.nextFloat() < 0.15F * (float)p_92094_0_;
      }
   }

   public static int getDamage(int p_92095_0_, Random p_92095_1_) {
      return p_92095_0_ > 10 ? p_92095_0_ - 10 : 1 + p_92095_1_.nextInt(4);
   }
}
