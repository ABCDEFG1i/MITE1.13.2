package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadBase;

public enum ItemTier implements IItemTier {
   WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
      return Ingredient.fromTag(ItemTags.PLANKS);
   }),
   FLINT(1, 131, 4.0F, 1.0F, 5, () -> {
      return Ingredient.fromItems(Items.FLINT);
   }),
   IRON(2, 250, 6.0F, 2.0F, 14, () -> {
      return Ingredient.fromItems(Items.IRON_INGOT);
   }),
   DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.fromItems(Items.DIAMOND);
   }),
   GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
      return Ingredient.fromItems(Items.GOLD_INGOT);
   });

   private final int harvestLevel;
   private final int maxUses;
   private final float efficiency;
   private final float attackDamage;
   private final int enchantability;
   private final LazyLoadBase<Ingredient> repairMaterial;

   private ItemTier(int p_i48458_3_, int p_i48458_4_, float p_i48458_5_, float p_i48458_6_, int p_i48458_7_, Supplier<Ingredient> p_i48458_8_) {
      this.harvestLevel = p_i48458_3_;
      this.maxUses = p_i48458_4_;
      this.efficiency = p_i48458_5_;
      this.attackDamage = p_i48458_6_;
      this.enchantability = p_i48458_7_;
      this.repairMaterial = new LazyLoadBase<>(p_i48458_8_);
   }

   public int getMaxUses() {
      return this.maxUses;
   }

   public float getEfficiency() {
      return this.efficiency;
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public int getHarvestLevel() {
      return this.harvestLevel;
   }

   public int getEnchantability() {
      return this.enchantability;
   }

   public Ingredient getRepairMaterial() {
      return this.repairMaterial.getValue();
   }
}
