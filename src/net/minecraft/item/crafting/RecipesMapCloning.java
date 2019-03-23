package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipesMapCloning extends IRecipeHidden {
   public RecipesMapCloning(ResourceLocation p_i48165_1_) {
      super(p_i48165_1_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         int i = 0;
         ItemStack itemstack = ItemStack.EMPTY;

         for(int j = 0; j < p_77569_1_.getSizeInventory(); ++j) {
            ItemStack itemstack1 = p_77569_1_.getStackInSlot(j);
            if (!itemstack1.isEmpty()) {
               if (itemstack1.getItem() == Items.FILLED_MAP) {
                  if (!itemstack.isEmpty()) {
                     return false;
                  }

                  itemstack = itemstack1;
               } else {
                  if (itemstack1.getItem() != Items.MAP) {
                     return false;
                  }

                  ++i;
               }
            }
         }

         return !itemstack.isEmpty() && i > 0;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_77572_1_.getSizeInventory(); ++j) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.MAP) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && i >= 1) {
         ItemStack itemstack2 = itemstack.copy();
         itemstack2.setCount(i + 1);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= 3 && p_194133_2_ >= 3;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_MAPCLONING;
   }
}
