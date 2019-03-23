package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Slot {
   private final int slotIndex;
   public final IInventory inventory;
   public int slotNumber;
   public int xPos;
   public int yPos;

   public Slot(IInventory inventory, int slotIndex, int xPos, int yPos) {
      this.inventory = inventory;
      this.slotIndex = slotIndex;
      this.xPos = xPos;
      this.yPos = yPos;
   }

   public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      int i = p_75220_2_.getCount() - p_75220_1_.getCount();
      if (i > 0) {
         this.onCrafting(p_75220_2_, i);
      }

   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
   }

   protected void onSwapCraft(int p_190900_1_) {
   }

   protected void onCrafting(ItemStack p_75208_1_) {
   }

   public ItemStack onTake(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
      this.onSlotChanged();
      return p_190901_2_;
   }

   public boolean isItemValid(ItemStack other) {
      return true;
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public boolean getHasStack() {
      return !this.getStack().isEmpty();
   }

   public void putStack(ItemStack p_75215_1_) {
      this.inventory.setInventorySlotContents(this.slotIndex, p_75215_1_);
      this.onSlotChanged();
   }

   public void onSlotChanged() {
      this.inventory.markDirty();
   }

   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public int getItemStackLimit(ItemStack p_178170_1_) {
      return this.getSlotStackLimit();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getSlotTexture() {
      return null;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      return this.inventory.decrStackSize(this.slotIndex, p_75209_1_);
   }

   public boolean isHere(IInventory p_75217_1_, int p_75217_2_) {
      return p_75217_1_ == this.inventory && p_75217_2_ == this.slotIndex;
   }

   public boolean canTakeStack(EntityPlayer p_82869_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEnabled() {
      return true;
   }
}
