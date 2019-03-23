package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BannerDuplicateRecipe extends IRecipeHidden {
   public BannerDuplicateRecipe(ResourceLocation p_i48171_1_) {
      super(p_i48171_1_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         EnumDyeColor enumdyecolor = null;
         ItemStack itemstack = null;
         ItemStack itemstack1 = null;

         for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            ItemStack itemstack2 = p_77569_1_.getStackInSlot(i);
            Item item = itemstack2.getItem();
            if (item instanceof ItemBanner) {
               ItemBanner itembanner = (ItemBanner)item;
               if (enumdyecolor == null) {
                  enumdyecolor = itembanner.getColor();
               } else if (enumdyecolor != itembanner.getColor()) {
                  return false;
               }

               boolean flag = TileEntityBanner.getPatterns(itemstack2) > 0;
               if (flag) {
                  if (itemstack != null) {
                     return false;
                  }

                  itemstack = itemstack2;
               } else {
                  if (itemstack1 != null) {
                     return false;
                  }

                  itemstack1 = itemstack2;
               }
            }
         }

         return itemstack != null && itemstack1 != null;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack = p_77572_1_.getStackInSlot(i);
         if (!itemstack.isEmpty() && TileEntityBanner.getPatterns(itemstack) > 0) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            return itemstack1;
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(IInventory p_179532_1_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_179532_1_.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = p_179532_1_.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem().hasContainerItem()) {
               nonnulllist.set(i, new ItemStack(itemstack.getItem().getContainerItem()));
            } else if (itemstack.hasTag() && TileEntityBanner.getPatterns(itemstack) > 0) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(1);
               nonnulllist.set(i, itemstack1);
            }
         }
      }

      return nonnulllist;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_BANNERDUPLICATE;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }
}
