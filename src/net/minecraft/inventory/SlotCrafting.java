package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SlotCrafting extends Slot {
   private final InventoryCrafting craftMatrix;
   private final EntityPlayer player;
   private int amountCrafted;

   public SlotCrafting(EntityPlayer player, InventoryCrafting craftMatrix, IInventory inventory, int slotIndex, int xPos, int yPos) {
      super(inventory, slotIndex, xPos, yPos);
      this.player = player;
      this.craftMatrix = craftMatrix;
   }

   public boolean isItemValid(ItemStack other) {
      return false;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      if (this.getHasStack()) {
         this.amountCrafted += Math.min(p_75209_1_, this.getStack().getCount());
      }

      return super.decrStackSize(p_75209_1_);
   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
      this.amountCrafted += p_75210_2_;
      this.onCrafting(p_75210_1_);
   }

   protected void onSwapCraft(int p_190900_1_) {
      this.amountCrafted += p_190900_1_;
   }

   protected void onCrafting(ItemStack p_75208_1_) {
      if (this.amountCrafted > 0) {
         p_75208_1_.onCrafting(this.player.world, this.player, this.amountCrafted);
      }

      ((IRecipeHolder)this.inventory).onCrafting(this.player);
      this.amountCrafted = 0;
   }

   @Override
   public void onSlotChanged() {
      super.onSlotChanged();

   }

   public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
      this.onCrafting(itemStack);
      NonNullList<ItemStack> nonnulllist = player.world.getRecipeManager().getRemainingItems(this.craftMatrix, player.world);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
         ItemStack itemstack1 = nonnulllist.get(i);
         if (!itemstack.isEmpty()) {
            this.craftMatrix.decrStackSize(i, 1);
            itemstack = this.craftMatrix.getStackInSlot(i);
         }

         if (!itemstack1.isEmpty()) {
            if (itemstack.isEmpty()) {
               this.craftMatrix.setInventorySlotContents(i, itemstack1);
            } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
               itemstack1.grow(itemstack.getCount());
               this.craftMatrix.setInventorySlotContents(i, itemstack1);
            } else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
               this.player.dropItem(itemstack1, false);
            }
         }
      }

      return itemStack;
   }
}
