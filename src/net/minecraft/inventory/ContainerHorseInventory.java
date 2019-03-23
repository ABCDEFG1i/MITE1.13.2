package net.minecraft.inventory;

import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerHorseInventory extends Container {
   private final IInventory horseInventory;
   private final AbstractHorse horse;

   public ContainerHorseInventory(IInventory p_i45791_1_, IInventory p_i45791_2_, final AbstractHorse p_i45791_3_, EntityPlayer p_i45791_4_) {
      this.horseInventory = p_i45791_2_;
      this.horse = p_i45791_3_;
      int i = 3;
      p_i45791_2_.openInventory(p_i45791_4_);
      int j = -18;
      this.addSlot(new Slot(p_i45791_2_, 0, 8, 18) {
         public boolean isItemValid(ItemStack other) {
            return other.getItem() == Items.SADDLE && !this.getHasStack() && p_i45791_3_.canBeSaddled();
         }

         @OnlyIn(Dist.CLIENT)
         public boolean isEnabled() {
            return p_i45791_3_.canBeSaddled();
         }
      });
      this.addSlot(new Slot(p_i45791_2_, 1, 8, 36) {
         public boolean isItemValid(ItemStack other) {
            return p_i45791_3_.isArmor(other);
         }

         @OnlyIn(Dist.CLIENT)
         public boolean isEnabled() {
            return p_i45791_3_.wearsArmor();
         }

         public int getSlotStackLimit() {
            return 1;
         }
      });
      if (p_i45791_3_ instanceof AbstractChestHorse && ((AbstractChestHorse)p_i45791_3_).hasChest()) {
         for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < ((AbstractChestHorse)p_i45791_3_).getInventoryColumns(); ++l) {
               this.addSlot(new Slot(p_i45791_2_, 2 + l + k * ((AbstractChestHorse)p_i45791_3_).getInventoryColumns(), 80 + l * 18, 18 + k * 18));
            }
         }
      }

      for(int i1 = 0; i1 < 3; ++i1) {
         for(int k1 = 0; k1 < 9; ++k1) {
            this.addSlot(new Slot(p_i45791_1_, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
         }
      }

      for(int j1 = 0; j1 < 9; ++j1) {
         this.addSlot(new Slot(p_i45791_1_, j1, 8 + j1 * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return this.horseInventory.isUsableByPlayer(p_75145_1_) && this.horse.isEntityAlive() && this.horse.getDistance(p_75145_1_) < 8.0F;
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ < this.horseInventory.getSizeInventory()) {
            if (!this.mergeItemStack(itemstack1, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack()) {
            if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(0).isItemValid(itemstack1)) {
            if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.horseInventory.getSizeInventory(), false)) {
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
      this.horseInventory.closeInventory(p_75134_1_);
   }
}
