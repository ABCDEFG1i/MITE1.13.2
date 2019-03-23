package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerBeacon extends Container {
   private final IInventory tileBeacon;
   private final ContainerBeacon.BeaconSlot beaconSlot;

   public ContainerBeacon(IInventory p_i45804_1_, IInventory p_i45804_2_) {
      this.tileBeacon = p_i45804_2_;
      this.beaconSlot = new ContainerBeacon.BeaconSlot(p_i45804_2_, 0, 136, 110);
      this.addSlot(this.beaconSlot);
      int i = 36;
      int j = 137;

      for(int k = 0; k < 3; ++k) {
         for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(p_i45804_1_, l + k * 9 + 9, 36 + l * 18, 137 + k * 18));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_i45804_1_, i1, 36 + i1 * 18, 195));
      }

   }

   public void addListener(IContainerListener p_75132_1_) {
      super.addListener(p_75132_1_);
      p_75132_1_.sendAllWindowProperties(this, this.tileBeacon);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      this.tileBeacon.setField(id, data);
   }

   public IInventory getTileEntity() {
      return this.tileBeacon;
   }

   public void onContainerClosed(EntityPlayer p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      if (!p_75134_1_.world.isRemote) {
         ItemStack itemstack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         if (!itemstack.isEmpty()) {
            p_75134_1_.dropItem(itemstack, false);
         }

      }
   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return this.tileBeacon.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 0) {
            if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(itemstack1) && itemstack1.getCount() == 1) {
            if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 1 && p_82846_2_ < 28) {
            if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 28 && p_82846_2_ < 37) {
            if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
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

   class BeaconSlot extends Slot {
      public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {
         super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
      }

      public boolean isItemValid(ItemStack other) {
         Item item = other.getItem();
         return item == Items.EMERALD || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.IRON_INGOT;
      }

      public int getSlotStackLimit() {
         return 1;
      }
   }
}
