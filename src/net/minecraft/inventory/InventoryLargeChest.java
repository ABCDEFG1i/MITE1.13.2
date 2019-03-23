package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeChest implements ILockableContainer {
   private final ITextComponent name;
   private final ILockableContainer upperChest;
   private final ILockableContainer lowerChest;

   public InventoryLargeChest(ITextComponent p_i48247_1_, ILockableContainer p_i48247_2_, ILockableContainer p_i48247_3_) {
      this.name = p_i48247_1_;
      if (p_i48247_2_ == null) {
         p_i48247_2_ = p_i48247_3_;
      }

      if (p_i48247_3_ == null) {
         p_i48247_3_ = p_i48247_2_;
      }

      this.upperChest = p_i48247_2_;
      this.lowerChest = p_i48247_3_;
      if (p_i48247_2_.isLocked()) {
         p_i48247_3_.setLockCode(p_i48247_2_.getLockCode());
      } else if (p_i48247_3_.isLocked()) {
         p_i48247_2_.setLockCode(p_i48247_3_.getLockCode());
      }

   }

   public int getSizeInventory() {
      return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
   }

   public boolean isEmpty() {
      return this.upperChest.isEmpty() && this.lowerChest.isEmpty();
   }

   public boolean isPartOfLargeChest(IInventory p_90010_1_) {
      return this.upperChest == p_90010_1_ || this.lowerChest == p_90010_1_;
   }

   public ITextComponent getName() {
      if (this.upperChest.hasCustomName()) {
         return this.upperChest.getName();
      } else {
         return this.lowerChest.hasCustomName() ? this.lowerChest.getName() : this.name;
      }
   }

   public boolean hasCustomName() {
      return this.upperChest.hasCustomName() || this.lowerChest.hasCustomName();
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.upperChest.hasCustomName() ? this.upperChest.getCustomName() : this.lowerChest.getCustomName();
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(p_70301_1_ - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return p_70298_1_ >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(p_70298_1_ - this.upperChest.getSizeInventory(), p_70298_2_) : this.upperChest.decrStackSize(p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return p_70304_1_ >= this.upperChest.getSizeInventory() ? this.lowerChest.removeStackFromSlot(p_70304_1_ - this.upperChest.getSizeInventory()) : this.upperChest.removeStackFromSlot(p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ >= this.upperChest.getSizeInventory()) {
         this.lowerChest.setInventorySlotContents(p_70299_1_ - this.upperChest.getSizeInventory(), p_70299_2_);
      } else {
         this.upperChest.setInventorySlotContents(p_70299_1_, p_70299_2_);
      }

   }

   public int getInventoryStackLimit() {
      return this.upperChest.getInventoryStackLimit();
   }

   public void markDirty() {
      this.upperChest.markDirty();
      this.lowerChest.markDirty();
   }

   public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
      return this.upperChest.isUsableByPlayer(p_70300_1_) && this.lowerChest.isUsableByPlayer(p_70300_1_);
   }

   public void openInventory(EntityPlayer p_174889_1_) {
      this.upperChest.openInventory(p_174889_1_);
      this.lowerChest.openInventory(p_174889_1_);
   }

   public void closeInventory(EntityPlayer p_174886_1_) {
      this.upperChest.closeInventory(p_174886_1_);
      this.lowerChest.closeInventory(p_174886_1_);
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

   public boolean isLocked() {
      return this.upperChest.isLocked() || this.lowerChest.isLocked();
   }

   public void setLockCode(LockCode p_174892_1_) {
      this.upperChest.setLockCode(p_174892_1_);
      this.lowerChest.setLockCode(p_174892_1_);
   }

   public LockCode getLockCode() {
      return this.upperChest.getLockCode();
   }

   public String getGuiID() {
      return this.upperChest.getGuiID();
   }

   public Container createContainer(InventoryPlayer p_174876_1_, EntityPlayer p_174876_2_) {
      return new ContainerChest(p_174876_1_, this, p_174876_2_);
   }

   public void clear() {
      this.upperChest.clear();
      this.lowerChest.clear();
   }
}
