package net.minecraft.item;

import net.minecraft.item.crafting.Ingredient;

public interface IItemTier {

   float getEfficiency();

   float getAttackDamage();

   int getHarvestLevel();

   int getEnchantability();

   Ingredient getRepairMaterial();
}
