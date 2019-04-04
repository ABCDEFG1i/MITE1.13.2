package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryEnderChest extends InventoryBasic {
   private TileEntityEnderChest associatedChest;

   public InventoryEnderChest() {
      super(new TextComponentTranslation("container.enderchest"), 27);
   }

   public void setChestTileEntity(TileEntityEnderChest p_146031_1_) {
      this.associatedChest = p_146031_1_;
   }

   public void read(NBTTagList p_70486_1_) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.setInventorySlotContents(i, ItemStack.EMPTY);
      }

      for(int k = 0; k < p_70486_1_.size(); ++k) {
         NBTTagCompound nbttagcompound = p_70486_1_.getCompoundTagAt(k);
         int j = nbttagcompound.getByte("Slot") & 255;
         if (j >= 0 && j < this.getSizeInventory()) {
            this.setInventorySlotContents(j, ItemStack.loadFromNBT(nbttagcompound));
         }
      }

   }

   public NBTTagList write() {
      NBTTagList nbttaglist = new NBTTagList();

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         ItemStack itemstack = this.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            itemstack.write(nbttagcompound);
            nbttaglist.add(nbttagcompound);
         }
      }

      return nbttaglist;
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      return (this.associatedChest == null || this.associatedChest.canBeUsed(p_70300_1_)) && super.isUsableByPlayer(
              p_70300_1_);
   }

   public void openInventory(EntityPlayer p_174889_1_) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(p_174889_1_);
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(p_174886_1_);
      this.associatedChest = null;
   }
}
