package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryCraftResult implements IInventory, IRecipeHolder {
   private final NonNullList<ItemStack> stackResult = NonNullList.withSize(1, ItemStack.EMPTY);
   private IRecipe recipeUsed;

   public int getSizeInventory() {
      return 1;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stackResult) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return this.stackResult.get(0);
   }

   public ITextComponent getName() {
      return new TextComponentString("Result");
   }

   public boolean hasCustomName() {
      return false;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.stackResult.set(0, p_70299_2_);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      return true;
   }

   public void openInventory(EntityPlayer p_174889_1_) {
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   public int getField(int p_174887_1_) {
      return 0;
   }

   public void setField(int p_174885_1_, int p_174885_2_) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.stackResult.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe p_193056_1_) {
      this.recipeUsed = p_193056_1_;
   }

   @Nullable
   public IRecipe getRecipeUsed() {
      return this.recipeUsed;
   }
}
