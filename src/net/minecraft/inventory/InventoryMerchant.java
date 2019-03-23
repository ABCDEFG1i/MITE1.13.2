package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
   private final IMerchant merchant;
   private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
   private final EntityPlayer player;
   private MerchantRecipe currentRecipe;
   private int currentRecipeIndex;

   public InventoryMerchant(EntityPlayer p_i1820_1_, IMerchant p_i1820_2_) {
      this.player = p_i1820_1_;
      this.merchant = p_i1820_2_;
   }

   public int getSizeInventory() {
      return this.slots.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.slots) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return this.slots.get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      ItemStack itemstack = this.slots.get(p_70298_1_);
      if (p_70298_1_ == 2 && !itemstack.isEmpty()) {
         return ItemStackHelper.getAndSplit(this.slots, p_70298_1_, itemstack.getCount());
      } else {
         ItemStack itemstack1 = ItemStackHelper.getAndSplit(this.slots, p_70298_1_, p_70298_2_);
         if (!itemstack1.isEmpty() && this.inventoryResetNeededOnSlotChange(p_70298_1_)) {
            this.resetRecipeAndSlots();
         }

         return itemstack1;
      }
   }

   private boolean inventoryResetNeededOnSlotChange(int p_70469_1_) {
      return p_70469_1_ == 0 || p_70469_1_ == 1;
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.slots, p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.slots.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      if (this.inventoryResetNeededOnSlotChange(p_70299_1_)) {
         this.resetRecipeAndSlots();
      }

   }

   public ITextComponent getName() {
      return new TextComponentTranslation("mob.villager");
   }

   public boolean hasCustomName() {
      return false;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      return this.merchant.getCustomer() == p_70300_1_;
   }

   public void openInventory(EntityPlayer p_174889_1_) {
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   public void markDirty() {
      this.resetRecipeAndSlots();
   }

   public void resetRecipeAndSlots() {
      this.currentRecipe = null;
      ItemStack itemstack = this.slots.get(0);
      ItemStack itemstack1 = this.slots.get(1);
      if (itemstack.isEmpty()) {
         itemstack = itemstack1;
         itemstack1 = ItemStack.EMPTY;
      }

      if (itemstack.isEmpty()) {
         this.setInventorySlotContents(2, ItemStack.EMPTY);
      } else {
         MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.player);
         if (merchantrecipelist != null) {
            MerchantRecipe merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack, itemstack1, this.currentRecipeIndex);
            if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
               this.currentRecipe = merchantrecipe;
               this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
            } else if (!itemstack1.isEmpty()) {
               merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack1, itemstack, this.currentRecipeIndex);
               if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
                  this.currentRecipe = merchantrecipe;
                  this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
               } else {
                  this.setInventorySlotContents(2, ItemStack.EMPTY);
               }
            } else {
               this.setInventorySlotContents(2, ItemStack.EMPTY);
            }
         }

         this.merchant.verifySellingItem(this.getStackInSlot(2));
      }

   }

   public MerchantRecipe getCurrentRecipe() {
      return this.currentRecipe;
   }

   public void setCurrentRecipeIndex(int p_70471_1_) {
      this.currentRecipeIndex = p_70471_1_;
      this.resetRecipeAndSlots();
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
      this.slots.clear();
   }
}
