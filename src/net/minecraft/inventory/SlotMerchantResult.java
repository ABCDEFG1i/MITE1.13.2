package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {
   private final InventoryMerchant merchantInventory;
   private final EntityPlayer player;
   private int removeCount;
   private final IMerchant merchant;

   public SlotMerchantResult(EntityPlayer p_i1822_1_, IMerchant p_i1822_2_, InventoryMerchant p_i1822_3_, int p_i1822_4_, int p_i1822_5_, int p_i1822_6_) {
      super(p_i1822_3_, p_i1822_4_, p_i1822_5_, p_i1822_6_);
      this.player = p_i1822_1_;
      this.merchant = p_i1822_2_;
      this.merchantInventory = p_i1822_3_;
   }

   public boolean isItemValid(ItemStack other) {
      return false;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      if (this.getHasStack()) {
         this.removeCount += Math.min(p_75209_1_, this.getStack().getCount());
      }

      return super.decrStackSize(p_75209_1_);
   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
      this.removeCount += p_75210_2_;
      this.onCrafting(p_75210_1_);
   }

   protected void onCrafting(ItemStack p_75208_1_) {
      p_75208_1_.onCrafting(this.player.world, this.player, this.removeCount);
      this.removeCount = 0;
   }

   public ItemStack onTake(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
      this.onCrafting(p_190901_2_);
      MerchantRecipe merchantrecipe = this.merchantInventory.getCurrentRecipe();
      if (merchantrecipe != null) {
         ItemStack itemstack = this.merchantInventory.getStackInSlot(0);
         ItemStack itemstack1 = this.merchantInventory.getStackInSlot(1);
         if (this.doTrade(merchantrecipe, itemstack, itemstack1) || this.doTrade(merchantrecipe, itemstack1, itemstack)) {
            this.merchant.useRecipe(merchantrecipe);
            p_190901_1_.addStat(StatList.TRADED_WITH_VILLAGER);
            this.merchantInventory.setInventorySlotContents(0, itemstack);
            this.merchantInventory.setInventorySlotContents(1, itemstack1);
         }
      }

      return p_190901_2_;
   }

   private boolean doTrade(MerchantRecipe p_75230_1_, ItemStack p_75230_2_, ItemStack p_75230_3_) {
      ItemStack itemstack = p_75230_1_.getItemToBuy();
      ItemStack itemstack1 = p_75230_1_.getSecondItemToBuy();
      if (p_75230_2_.getItem() == itemstack.getItem() && p_75230_2_.getCount() >= itemstack.getCount()) {
         if (!itemstack1.isEmpty() && !p_75230_3_.isEmpty() && itemstack1.getItem() == p_75230_3_.getItem() && p_75230_3_.getCount() >= itemstack1.getCount()) {
            p_75230_2_.shrink(itemstack.getCount());
            p_75230_3_.shrink(itemstack1.getCount());
            return true;
         }

         if (itemstack1.isEmpty() && p_75230_3_.isEmpty()) {
            p_75230_2_.shrink(itemstack.getCount());
            return true;
         }
      }

      return false;
   }
}
