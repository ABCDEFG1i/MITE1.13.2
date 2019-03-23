package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeBookCloning extends IRecipeHidden {
   public RecipeBookCloning(ResourceLocation p_i48170_1_) {
      super(p_i48170_1_);
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
               if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                  if (!itemstack.isEmpty()) {
                     return false;
                  }

                  itemstack = itemstack1;
               } else {
                  if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                     return false;
                  }

                  ++i;
               }
            }
         }

         return !itemstack.isEmpty() && itemstack.hasTag() && i > 0;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      int i = 0;
      ItemStack itemstack = ItemStack.EMPTY;

      for(int j = 0; j < p_77572_1_.getSizeInventory(); ++j) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(j);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1;
            } else {
               if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                  return ItemStack.EMPTY;
               }

               ++i;
            }
         }
      }

      if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && ItemWrittenBook.getGeneration(itemstack) < 2) {
         ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
         NBTTagCompound nbttagcompound = itemstack.getTag().copy();
         nbttagcompound.setInteger("generation", ItemWrittenBook.getGeneration(itemstack) + 1);
         itemstack2.setTag(nbttagcompound);
         return itemstack2;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(IInventory p_179532_1_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_179532_1_.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = p_179532_1_.getStackInSlot(i);
         if (itemstack.getItem().hasContainerItem()) {
            nonnulllist.set(i, new ItemStack(itemstack.getItem().getContainerItem()));
         } else if (itemstack.getItem() instanceof ItemWrittenBook) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            nonnulllist.set(i, itemstack1);
            break;
         }
      }

      return nonnulllist;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_BOOKCLONING;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= 3 && p_194133_2_ >= 3;
   }
}
