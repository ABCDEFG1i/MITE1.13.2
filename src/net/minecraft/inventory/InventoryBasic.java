package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InventoryBasic implements IInventory, IRecipeHelperPopulator {
   private final ITextComponent inventoryTitle;
   private final int slotsCount;
   private final NonNullList<ItemStack> inventoryContents;
   private List<IInventoryChangedListener> listeners;
   private ITextComponent customName;

   public InventoryBasic(ITextComponent p_i45902_1_, int p_i45902_2_) {
      this.inventoryTitle = p_i45902_1_;
      this.slotsCount = p_i45902_2_;
      this.inventoryContents = NonNullList.withSize(p_i45902_2_, ItemStack.EMPTY);
   }

   public void addListener(IInventoryChangedListener p_110134_1_) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(p_110134_1_);
   }

   public void removeListener(IInventoryChangedListener p_110132_1_) {
      this.listeners.remove(p_110132_1_);
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= 0 && p_70301_1_ < this.inventoryContents.size() ? this.inventoryContents.get(p_70301_1_) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, p_70298_1_, p_70298_2_);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack addItem(ItemStack p_174894_1_) {
      ItemStack itemstack = p_174894_1_.copy();

      for(int i = 0; i < this.slotsCount; ++i) {
         ItemStack itemstack1 = this.getStackInSlot(i);
         if (itemstack1.isEmpty()) {
            this.setInventorySlotContents(i, itemstack);
            this.markDirty();
            return ItemStack.EMPTY;
         }

         if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
            int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
            int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());
            if (k > 0) {
               itemstack1.grow(k);
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.markDirty();
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (itemstack.getCount() != p_174894_1_.getCount()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      ItemStack itemstack = this.inventoryContents.get(p_70304_1_);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.inventoryContents.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.inventoryContents.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public int getSizeInventory() {
      return this.slotsCount;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.inventoryContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : this.inventoryTitle;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public void setCustomName(@Nullable ITextComponent p_200228_1_) {
      this.customName = p_200228_1_;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
      if (this.listeners != null) {
         for(int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).onInventoryChanged(this);
         }
      }

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
      this.inventoryContents.clear();
   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      for(ItemStack itemstack : this.inventoryContents) {
         p_194018_1_.accountStack(itemstack);
      }

   }
}
