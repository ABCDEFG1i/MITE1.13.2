package net.minecraft.inventory;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerBrewingStand extends Container {
   private final IInventory tileBrewingStand;
   private final Slot slot;
   private int prevBrewTime;
   private int prevFuel;

   public ContainerBrewingStand(InventoryPlayer p_i45802_1_, IInventory p_i45802_2_) {
      this.tileBrewingStand = p_i45802_2_;
      this.addSlot(new ContainerBrewingStand.Potion(p_i45802_2_, 0, 56, 51));
      this.addSlot(new ContainerBrewingStand.Potion(p_i45802_2_, 1, 79, 58));
      this.addSlot(new ContainerBrewingStand.Potion(p_i45802_2_, 2, 102, 51));
      this.slot = this.addSlot(new ContainerBrewingStand.Ingredient(p_i45802_2_, 3, 79, 17));
      this.addSlot(new ContainerBrewingStand.Fuel(p_i45802_2_, 4, 17, 17));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i45802_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i45802_1_, k, 8 + k * 18, 142));
      }

   }

   public void addListener(IContainerListener p_75132_1_) {
      super.addListener(p_75132_1_);
      p_75132_1_.sendAllWindowProperties(this, this.tileBrewingStand);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < this.listeners.size(); ++i) {
         IContainerListener icontainerlistener = this.listeners.get(i);
         if (this.prevBrewTime != this.tileBrewingStand.getField(0)) {
            icontainerlistener.sendWindowProperty(this, 0, this.tileBrewingStand.getField(0));
         }

         if (this.prevFuel != this.tileBrewingStand.getField(1)) {
            icontainerlistener.sendWindowProperty(this, 1, this.tileBrewingStand.getField(1));
         }
      }

      this.prevBrewTime = this.tileBrewingStand.getField(0);
      this.prevFuel = this.tileBrewingStand.getField(1);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      this.tileBrewingStand.setField(id, data);
   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return this.tileBrewingStand.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if ((p_82846_2_ < 0 || p_82846_2_ > 2) && p_82846_2_ != 3 && p_82846_2_ != 4) {
            if (this.slot.isItemValid(itemstack1)) {
               if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (ContainerBrewingStand.Potion.canHoldPotion(itemstack) && itemstack.getCount() == 1) {
               if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (ContainerBrewingStand.Fuel.isValidBrewingFuel(itemstack)) {
               if (!this.mergeItemStack(itemstack1, 4, 5, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 5 && p_82846_2_ < 32) {
               if (!this.mergeItemStack(itemstack1, 32, 41, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 32 && p_82846_2_ < 41) {
               if (!this.mergeItemStack(itemstack1, 5, 32, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
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

   static class Fuel extends Slot {
      public Fuel(IInventory p_i47070_1_, int p_i47070_2_, int p_i47070_3_, int p_i47070_4_) {
         super(p_i47070_1_, p_i47070_2_, p_i47070_3_, p_i47070_4_);
      }

      public boolean isItemValid(ItemStack other) {
         return isValidBrewingFuel(other);
      }

      public static boolean isValidBrewingFuel(ItemStack p_185004_0_) {
         return p_185004_0_.getItem() == Items.BLAZE_POWDER;
      }

      public int getSlotStackLimit() {
         return 64;
      }
   }

   static class Ingredient extends Slot {
      public Ingredient(IInventory p_i47069_1_, int p_i47069_2_, int p_i47069_3_, int p_i47069_4_) {
         super(p_i47069_1_, p_i47069_2_, p_i47069_3_, p_i47069_4_);
      }

      public boolean isItemValid(ItemStack other) {
         return PotionBrewing.isReagent(other);
      }

      public int getSlotStackLimit() {
         return 64;
      }
   }

   static class Potion extends Slot {
      public Potion(IInventory p_i47598_1_, int p_i47598_2_, int p_i47598_3_, int p_i47598_4_) {
         super(p_i47598_1_, p_i47598_2_, p_i47598_3_, p_i47598_4_);
      }

      public boolean isItemValid(ItemStack other) {
         return canHoldPotion(other);
      }

      public int getSlotStackLimit() {
         return 1;
      }

      public ItemStack onTake(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
         PotionType potiontype = PotionUtils.getPotionFromItem(p_190901_2_);
         if (p_190901_1_ instanceof EntityPlayerMP) {
            CriteriaTriggers.BREWED_POTION.trigger((EntityPlayerMP)p_190901_1_, potiontype);
         }

         super.onTake(p_190901_1_, p_190901_2_);
         return p_190901_2_;
      }

      public static boolean canHoldPotion(ItemStack p_75243_0_) {
         Item item = p_75243_0_.getItem();
         return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
      }
   }
}
