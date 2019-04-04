package net.minecraft.enchantment;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class EnchantmentProtection extends Enchantment {
   public final EnchantmentProtection.Type protectionType;

   public EnchantmentProtection(Enchantment.Rarity p_i46723_1_, EnchantmentProtection.Type p_i46723_2_, EntityEquipmentSlot... p_i46723_3_) {
      super(p_i46723_1_, EnumEnchantmentType.ARMOR, p_i46723_3_);
      this.protectionType = p_i46723_2_;
      if (p_i46723_2_ == EnchantmentProtection.Type.FALL) {
         this.type = EnumEnchantmentType.ARMOR_FEET;
      }

   }

   public int getMinEnchantability(int p_77321_1_) {
      return this.protectionType.getMinimalEnchantability() + (p_77321_1_ - 1) * this.protectionType.getEnchantIncreasePerLevel();
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + this.protectionType.getEnchantIncreasePerLevel();
   }

   public int getMaxLevel() {
      return 4;
   }

   public int calcModifierDamage(int p_77318_1_, DamageSource p_77318_2_) {
      if (p_77318_2_.canHarmInCreative()) {
         return 0;
      } else if (this.protectionType == EnchantmentProtection.Type.ALL) {
         return p_77318_1_;
      } else if (this.protectionType == EnchantmentProtection.Type.FIRE && p_77318_2_.isFireDamage()) {
         return p_77318_1_ * 2;
      } else if (this.protectionType == EnchantmentProtection.Type.FALL && p_77318_2_ == DamageSource.FALL) {
         return p_77318_1_ * 3;
      } else if (this.protectionType == EnchantmentProtection.Type.EXPLOSION && p_77318_2_.isExplosion()) {
         return p_77318_1_ * 2;
      } else {
         return this.protectionType == EnchantmentProtection.Type.PROJECTILE && p_77318_2_.isProjectile() ? p_77318_1_ * 2 : 0;
      }
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      if (p_77326_1_ instanceof EnchantmentProtection) {
         EnchantmentProtection enchantmentprotection = (EnchantmentProtection)p_77326_1_;
         if (this.protectionType == enchantmentprotection.protectionType) {
            return false;
         } else {
            return this.protectionType == EnchantmentProtection.Type.FALL || enchantmentprotection.protectionType == EnchantmentProtection.Type.FALL;
         }
      } else {
         return super.canApplyTogether(p_77326_1_);
      }
   }

   public static int getFireTimeForEntity(EntityLivingBase p_92093_0_, int p_92093_1_) {
      int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FIRE_PROTECTION, p_92093_0_);
      if (i > 0) {
         p_92093_1_ -= MathHelper.floor((float)p_92093_1_ * (float)i * 0.15F);
      }

      return p_92093_1_;
   }

   public static double getBlastDamageReduction(EntityLivingBase p_92092_0_, double p_92092_1_) {
      int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.BLAST_PROTECTION, p_92092_0_);
      if (i > 0) {
         p_92092_1_ -= (double)MathHelper.floor(p_92092_1_ * (double)((float)i * 0.15F));
      }

      return p_92092_1_;
   }

   public enum Type {
      ALL("all", 1, 11),
      FIRE("fire", 10, 8),
      FALL("fall", 5, 6),
      EXPLOSION("explosion", 5, 8),
      PROJECTILE("projectile", 3, 6);

      private final String typeName;
      private final int minEnchantability;
      private final int levelCost;

      Type(String p_i48839_3_, int p_i48839_4_, int p_i48839_5_) {
         this.typeName = p_i48839_3_;
         this.minEnchantability = p_i48839_4_;
         this.levelCost = p_i48839_5_;
      }

      public int getMinimalEnchantability() {
         return this.minEnchantability;
      }

      public int getEnchantIncreasePerLevel() {
         return this.levelCost;
      }
   }
}
