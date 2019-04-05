package net.minecraft.item;

import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadBase;

import java.util.function.Supplier;

public enum ItemTier implements IItemTier {
    WOOD(0, 2.0F, 0.0F, 15, () -> {
        return Ingredient.fromTag(ItemTags.PLANKS);
    }), FLINT(1, 3.0F, 0.5F, 5, () -> {
        return Ingredient.fromItems(Items.FLINT);
    }), COPPER(2, 3.5F, 2.0F, 8, () -> {
        return Ingredient.fromItems(Items.COPPER_NUGGET);
    }), SILVER(2, 3.5F, 2.0F, 10, () -> {
        return Ingredient.fromItems(Items.SILVER_NUGGET);
    }), GOLD(2, 12.0F, 0.0F, 22, () -> {
        return Ingredient.fromItems(Items.GOLD_NUGGET);
    }), IRON(3, 6.0F, 3.0F, 14, () -> {
        return Ingredient.fromItems(Items.IRON_NUGGET);
    }), ANCIENT_METAL(3, 6.0F, 3.0F, 16, () -> {
        return Ingredient.fromItems(Items.ANCIENT_METAL_NUGGET);
    }), MITHRIL(4, 8.0F, 4.0F, 22, () -> {
        return Ingredient.fromItems(Items.MITHRIL_NUGGET);
    }), TUNGSTEN(5, 9.0F, 5.0F, 20, () -> {
        return Ingredient.fromItems(Items.TUNGSTEN_NUGGET);
    }), ADAMANTIUM(6, 10.0F, 5.0F, 18, () -> {
        return Ingredient.fromItems(Items.ADAMANTIUM_NUGGET);
    });


    private final float attackDamage;
    private final float efficiency;
    private final int enchantability;
    private final int harvestLevel;
    private final LazyLoadBase<Ingredient> repairMaterial;

    ItemTier(int harvestLevel, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> p_i48458_8_) {
        this.harvestLevel = harvestLevel;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
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

    public Ingredient getRepairMaterial() {
        return this.repairMaterial.getValue();
    }
}
