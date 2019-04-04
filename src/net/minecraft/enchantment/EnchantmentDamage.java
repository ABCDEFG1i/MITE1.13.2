package net.minecraft.enchantment;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EnchantmentDamage extends Enchantment {
   private static final String[] DAMAGE_NAMES = new String[]{"all", "undead", "arthropods"};
   private static final int[] MIN_COST = new int[]{1, 5, 5};
   private static final int[] LEVEL_COST = new int[]{11, 8, 8};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
   public final int damageType;

   public EnchantmentDamage(Enchantment.Rarity p_i46734_1_, int p_i46734_2_, EntityEquipmentSlot... p_i46734_3_) {
      super(p_i46734_1_, EnumEnchantmentType.WEAPON, p_i46734_3_);
      this.damageType = p_i46734_2_;
   }

   public int getMinEnchantability(int p_77321_1_) {
      return MIN_COST[this.damageType] + (p_77321_1_ - 1) * LEVEL_COST[this.damageType];
   }

   public int getMaxEnchantability(int p_77317_1_) {
      return this.getMinEnchantability(p_77317_1_) + LEVEL_COST_SPAN[this.damageType];
   }

   public int getMaxLevel() {
      return 5;
   }

   public float calcDamageByCreature(int p_152376_1_, CreatureAttribute p_152376_2_) {
      if (this.damageType == 0) {
         return 1.0F + (float)Math.max(0, p_152376_1_ - 1) * 0.5F;
      } else if (this.damageType == 1 && p_152376_2_ == CreatureAttribute.UNDEAD) {
         return (float)p_152376_1_ * 2.5F;
      } else {
         return this.damageType == 2 && p_152376_2_ == CreatureAttribute.ARTHROPOD ? (float)p_152376_1_ * 2.5F : 0.0F;
      }
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return !(p_77326_1_ instanceof EnchantmentDamage);
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() instanceof ItemAxe || super.canApply(p_92089_1_);
   }

   public void onEntityDamaged(EntityLivingBase p_151368_1_, Entity p_151368_2_, int p_151368_3_) {
      if (p_151368_2_ instanceof EntityLivingBase) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)p_151368_2_;
         if (this.damageType == 2 && entitylivingbase.getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
            int i = 20 + p_151368_1_.getRNG().nextInt(10 * p_151368_3_);
            entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, i, 3));
         }
      }

   }
}
