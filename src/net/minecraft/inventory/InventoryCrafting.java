package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCrafting implements IInventory, IRecipeHelperPopulator {
   private final NonNullList<ItemStack> stackList;
   private final int width;
   private final int height;
   private final Container eventHandler;

   public InventoryCrafting(Container p_i1807_1_, int p_i1807_2_, int p_i1807_3_) {
      this.stackList = NonNullList.withSize(p_i1807_2_ * p_i1807_3_, ItemStack.EMPTY);
      this.eventHandler = p_i1807_1_;
      this.width = p_i1807_2_;
      this.height = p_i1807_3_;
   }

   public int getSizeInventory() {
      return this.stackList.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stackList) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(p_70301_1_);
   }

   public ITextComponent getName() {
      return new TextComponentTranslation("container.crafting");
   }

   public boolean hasCustomName() {
      return false;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.stackList, p_70304_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, p_70298_1_, p_70298_2_);
      if (!itemstack.isEmpty()) {
         this.eventHandler.onCraftMatrixChanged(this);
      }

      return itemstack;
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.stackList.set(p_70299_1_, p_70299_2_);
      this.eventHandler.onCraftMatrixChanged(this);
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
      this.stackList.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      for(ItemStack itemstack : this.stackList) {
         p_194018_1_.accountPlainStack(itemstack);
      }

   }
}
