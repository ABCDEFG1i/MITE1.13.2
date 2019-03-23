package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRecipe {
   boolean matches(IInventory p_77569_1_, World p_77569_2_);

   ItemStack getCraftingResult(IInventory p_77572_1_);

   @OnlyIn(Dist.CLIENT)
   boolean canFit(int p_194133_1_, int p_194133_2_);

   ItemStack getRecipeOutput();


   default NonNullList<ItemStack> getRemainingItems(IInventory p_179532_1_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_179532_1_.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         Item item = p_179532_1_.getStackInSlot(i).getItem();
         if (item.hasContainerItem()) {
            nonnulllist.set(i, new ItemStack(item.getContainerItem()));
         }
      }

      return nonnulllist;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   default boolean isDynamic() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   default String getGroup() {
      return "";
   }

   ResourceLocation getId();

   IRecipeSerializer<?> getSerializer();
}
