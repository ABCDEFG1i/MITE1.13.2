package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class IRecipeHidden implements IRecipe {
   private final ResourceLocation id;

   public IRecipeHidden(ResourceLocation p_i48169_1_) {
      this.id = p_i48169_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isDynamic() {
      return true;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }
}
