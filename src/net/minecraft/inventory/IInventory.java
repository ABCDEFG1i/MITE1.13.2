package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.INameable;

public interface IInventory extends INameable {
   int getSizeInventory();

   boolean isEmpty();

   ItemStack getStackInSlot(int p_70301_1_);

   ItemStack decrStackSize(int p_70298_1_, int p_70298_2_);

   ItemStack removeStackFromSlot(int p_70304_1_);

   void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_);

   int getInventoryStackLimit();

   void markDirty();

   boolean isUsableByPlayer(EntityPlayer p_70300_1_);

   void openInventory(EntityPlayer p_174889_1_);

   void closeInventory(EntityPlayer p_174886_1_);

   boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_);

   int getField(int p_174887_1_);

   void setField(int p_174885_1_, int p_174885_2_);

   int getFieldCount();

   void clear();

   default int getHeight() {
      return 0;
   }

   default int getWidth() {
      return 0;
   }
}
