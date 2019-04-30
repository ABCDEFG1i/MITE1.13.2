package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;

public interface ITimedRecipe extends IRecipe {
    int getCraftingTime(IInventory inventory);
}
