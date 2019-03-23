package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   private final IInventory lowerChestInventory;
   private final int numRows;

   public ContainerChest(IInventory p_i45801_1_, IInventory p_i45801_2_, EntityPlayer p_i45801_3_) {
      this.lowerChestInventory = p_i45801_2_;
      this.numRows = p_i45801_2_.getSizeInventory() / 9;
      p_i45801_2_.openInventory(p_i45801_3_);
      int i = (this.numRows - 4) * 18;

      for(int j = 0; j < this.numRows; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(p_i45801_2_, k + j * 9, 8 + k * 18, 18 + j * 18));
         }
      }

      for(int l = 0; l < 3; ++l) {
         for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(p_i45801_1_, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_i45801_1_, i1, 8 + i1 * 18, 161 + i));
      }

   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return this.lowerChestInventory.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ < this.numRows * 9) {
            if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public void onContainerClosed(EntityPlayer p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.lowerChestInventory.closeInventory(p_75134_1_);
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }
}
