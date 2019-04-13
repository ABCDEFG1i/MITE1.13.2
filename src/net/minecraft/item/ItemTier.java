package net.minecraft.item;

import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadBase;

import java.util.function.Supplier;

public enum ItemTier implements IItemTier {
    WOOD(0, 2.0F, 0.0F, 2,-1, () -> {
        return Ingredient.fromTag(ItemTags.PLANKS);
    }), FLINT(1, 2.0F, 0.5F, 5,-1, () -> {
        return Ingredient.fromItems(Items.FLINT);
    }), COPPER(2, 3.5F, 2.0F, 8,800, () -> {
        return Ingredient.fromItems(Items.COPPER_NUGGET);
    }), SILVER(2, 3.5F, 2.0F, 10,800, () -> {
        return Ingredient.fromItems(Items.SILVER_NUGGET);
    }), GOLD(2, 12.0F, 0.0F, 22, 800,() -> {
        return Ingredient.fromItems(Items.GOLD_NUGGET);
    }), IRON(3, 6.0F, 3.0F, 14, 1600, () -> {
        return Ingredient.fromItems(Items.IRON_NUGGET);
    }), ANCIENT_METAL(3, 6.0F, 3.0F, 16,3200, () -> {
        return Ingredient.fromItems(Items.ANCIENT_METAL_NUGGET);
    }), MITHRIL(4, 7.5F, 4.0F, 22,12800, () -> {
        return Ingredient.fromItems(Items.MITHRIL_NUGGET);
    }), TUNGSTEN(5, 9.0F, 5.0F, 20,25600, () -> {
        return Ingredient.fromItems(Items.TUNGSTEN_NUGGET);
    }), ADAMANTIUM(6, 10.0F, 5.0F, 18,51200 , () -> {
        return Ingredient.fromItems(Items.ADAMANTIUM_NUGGET);
    });


    private final float attackDamage;
    private final float efficiency;
    private final int enchantability;
    private final int harvestLevel;
    private final int repairDurability;
    private final LazyLoadBase<Ingredient> repairMaterial;

    ItemTier(int harvestLevel, float efficiency, float attackDamage, int enchantability,int repairDurability, Supplier<Ingredient> p_i48458_8_) {
        this.harvestLevel = harvestLevel;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairDurability = repairDurability;
        this.repairMaterial = new LazyLoadBase<>(p_i48458_8_);
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

    public int getRepairDurability() {
        return repairDurability;
    }

    public Ingredient getRepairMaterial() {
        return this.repairMaterial.getValue();
    }
}
