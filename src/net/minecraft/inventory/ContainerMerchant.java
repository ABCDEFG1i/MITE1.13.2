package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
   private final IMerchant merchant;
   private final InventoryMerchant merchantInventory;
   private final World world;

   public ContainerMerchant(InventoryPlayer p_i1821_1_, IMerchant p_i1821_2_, World p_i1821_3_) {
      this.merchant = p_i1821_2_;
      this.world = p_i1821_3_;
      this.merchantInventory = new InventoryMerchant(p_i1821_1_.player, p_i1821_2_);
      this.addSlot(new Slot(this.merchantInventory, 0, 36, 53));
      this.addSlot(new Slot(this.merchantInventory, 1, 62, 53));
      this.addSlot(new SlotMerchantResult(p_i1821_1_.player, p_i1821_2_, this.merchantInventory, 2, 120, 53));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i1821_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i1821_1_, k, 8 + k * 18, 142));
      }

   }

   public InventoryMerchant getMerchantInventory() {
      return this.merchantInventory;
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      this.merchantInventory.resetRecipeAndSlots();
      super.onCraftMatrixChanged(p_75130_1_);
   }

   public void setCurrentRecipeIndex(int p_75175_1_) {
      this.merchantInventory.setCurrentRecipeIndex(p_75175_1_);
   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return this.merchant.getCustomer() == p_75145_1_;
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
            if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
               if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
      }

      return itemstack;
   }

   public void onContainerClosed(EntityPlayer p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.merchant.setCustomer(null);
      super.onContainerClosed(p_75134_1_);
      if (!this.world.isRemote) {
         ItemStack itemstack = this.merchantInventory.removeStackFromSlot(0);
         if (!itemstack.isEmpty()) {
            p_75134_1_.dropItem(itemstack, false);
         }

         itemstack = this.merchantInventory.removeStackFromSlot(1);
         if (!itemstack.isEmpty()) {
            p_75134_1_.dropItem(itemstack, false);
         }

      }
   }
}
