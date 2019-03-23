package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Container {
   public NonNullList<ItemStack> inventoryItemStacks = NonNullList.create();
   public List<Slot> inventorySlots = Lists.newArrayList();
   public int windowId;
   @OnlyIn(Dist.CLIENT)
   private short transactionID;
   private int dragMode = -1;
   private int dragEvent;
   private final Set<Slot> dragSlots = Sets.newHashSet();
   protected List<IContainerListener> listeners = Lists.newArrayList();
   private final Set<EntityPlayer> playerList = Sets.newHashSet();

   protected Slot addSlot(Slot p_75146_1_) {
      p_75146_1_.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(p_75146_1_);
      this.inventoryItemStacks.add(ItemStack.EMPTY);
      return p_75146_1_;
   }

   public void addListener(IContainerListener p_75132_1_) {
      if (this.listeners.contains(p_75132_1_)) {
         throw new IllegalArgumentException("Listener already listening");
      } else {
         this.listeners.add(p_75132_1_);
         p_75132_1_.sendAllContents(this, this.getInventory());
         this.detectAndSendChanges();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void removeListener(IContainerListener p_82847_1_) {
      this.listeners.remove(p_82847_1_);
   }

   public NonNullList<ItemStack> getInventory() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         nonnulllist.add(this.inventorySlots.get(i).getStack());
      }

      return nonnulllist;
   }

   public void detectAndSendChanges() {
      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         ItemStack itemstack = this.inventorySlots.get(i).getStack();
         ItemStack itemstack1 = this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
            itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);

            for(int j = 0; j < this.listeners.size(); ++j) {
               this.listeners.get(j).sendSlotContents(this, i, itemstack1);
            }
         }
      }

   }

   public boolean enchantItem(EntityPlayer p_75140_1_, int p_75140_2_) {
      return false;
   }

   @Nullable
   public Slot getSlotFromInventory(IInventory p_75147_1_, int p_75147_2_) {
      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         Slot slot = this.inventorySlots.get(i);
         if (slot.isHere(p_75147_1_, p_75147_2_)) {
            return slot;
         }
      }

      return null;
   }

   public Slot getSlot(int p_75139_1_) {
      return this.inventorySlots.get(p_75139_1_);
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      Slot slot = this.inventorySlots.get(p_82846_2_);
      return slot != null ? slot.getStack() : ItemStack.EMPTY;
   }

   public ItemStack slotClick(int slotIndex, int dragEvent, ClickType clickType, EntityPlayer player) {
      //MITEMODDED Add to make the crafting has progress
      if (this instanceof ContainerWorkbench && slotIndex==0){
         if (clickType==ClickType.QUICK_MOVE) {
            ((ContainerWorkbench) this).startCraftingProgress(true);
         }else {
            ((ContainerWorkbench) this).startCraftingProgress(false);
         }
         return ItemStack.EMPTY;
      }
      if (this instanceof ContainerPlayer && slotIndex==0){
         if (clickType==ClickType.QUICK_MOVE) {
            ((ContainerPlayer) this).startCraftingProgress(true);
         }else {
            ((ContainerPlayer) this).startCraftingProgress(false);
         }
         return ItemStack.EMPTY;
      }
      ItemStack itemstack = ItemStack.EMPTY;
      InventoryPlayer inventoryplayer = player.inventory;
      if (clickType == ClickType.QUICK_CRAFT) {
         int j1 = this.dragEvent;
         this.dragEvent = getDragEvent(dragEvent);
         if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
            this.resetDrag();
         } else if (inventoryplayer.getItemStack().isEmpty()) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = extractDragMode(dragEvent);
            if (isValidDragMode(this.dragMode, player)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot slot7 = this.inventorySlots.get(slotIndex);
            ItemStack itemstack12 = inventoryplayer.getItemStack();
            if (slot7 != null && canAddItemToSlot(slot7, itemstack12, true) && slot7.isItemValid(itemstack12) && (this.dragMode == 2 || itemstack12.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot7)) {
               this.dragSlots.add(slot7);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               ItemStack itemstack9 = inventoryplayer.getItemStack().copy();
               int k1 = inventoryplayer.getItemStack().getCount();

               for(Slot slot8 : this.dragSlots) {
                  ItemStack itemstack13 = inventoryplayer.getItemStack();
                  if (slot8 != null && canAddItemToSlot(slot8, itemstack13, true) && slot8.isItemValid(itemstack13) && (this.dragMode == 2 || itemstack13.getCount() >= this.dragSlots.size()) && this.canDragIntoSlot(slot8)) {
                     ItemStack itemstack14 = itemstack9.copy();
                     int j3 = slot8.getHasStack() ? slot8.getStack().getCount() : 0;
                     computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                     int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getItemStackLimit(itemstack14));
                     if (itemstack14.getCount() > k3) {
                        itemstack14.setCount(k3);
                     }

                     k1 -= itemstack14.getCount() - j3;
                     slot8.putStack(itemstack14);
                  }
               }

               itemstack9.setCount(k1);
               inventoryplayer.setItemStack(itemstack9);
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.dragEvent != 0) {
         this.resetDrag();
      } else if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (dragEvent == 0 || dragEvent == 1)) {
         if (slotIndex == -999) {
            if (!inventoryplayer.getItemStack().isEmpty()) {
               if (dragEvent == 0) {
                  player.dropItem(inventoryplayer.getItemStack(), true);
                  inventoryplayer.setItemStack(ItemStack.EMPTY);
               }

               if (dragEvent == 1) {
                  player.dropItem(inventoryplayer.getItemStack().split(1), true);
               }
            }
         } else if (clickType == ClickType.QUICK_MOVE) {
            if (slotIndex < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot5 = this.inventorySlots.get(slotIndex);
            if (slot5 == null || !slot5.canTakeStack(player)) {
               return ItemStack.EMPTY;
            }

            for(ItemStack itemstack7 = this.transferStackInSlot(player, slotIndex); !itemstack7.isEmpty() && ItemStack.areItemsEqual(slot5.getStack(), itemstack7); itemstack7 = this.transferStackInSlot(player, slotIndex)) {
               itemstack = itemstack7.copy();
            }
         } else {
            if (slotIndex < 0) {
               return ItemStack.EMPTY;
            }

            Slot slot6 = this.inventorySlots.get(slotIndex);
            if (slot6 != null) {
               ItemStack itemstack8 = slot6.getStack();
               ItemStack itemstack11 = inventoryplayer.getItemStack();
               if (!itemstack8.isEmpty()) {
                  itemstack = itemstack8.copy();
               }

               if (itemstack8.isEmpty()) {
                  if (!itemstack11.isEmpty() && slot6.isItemValid(itemstack11)) {
                     int j2 = dragEvent == 0 ? itemstack11.getCount() : 1;
                     if (j2 > slot6.getItemStackLimit(itemstack11)) {
                        j2 = slot6.getItemStackLimit(itemstack11);
                     }

                     slot6.putStack(itemstack11.split(j2));
                  }
               } else if (slot6.canTakeStack(player)) {
                  if (itemstack11.isEmpty()) {
                     if (itemstack8.isEmpty()) {
                        slot6.putStack(ItemStack.EMPTY);
                        inventoryplayer.setItemStack(ItemStack.EMPTY);
                     } else {
                        int k2 = dragEvent == 0 ? itemstack8.getCount() : (itemstack8.getCount() + 1) / 2;
                        inventoryplayer.setItemStack(slot6.decrStackSize(k2));
                        if (itemstack8.isEmpty()) {
                           slot6.putStack(ItemStack.EMPTY);
                        }

                        slot6.onTake(player, inventoryplayer.getItemStack());
                     }
                  } else if (slot6.isItemValid(itemstack11)) {
                     if (areItemsAndTagsEqual(itemstack8, itemstack11)) {
                        int l2 = dragEvent == 0 ? itemstack11.getCount() : 1;
                        if (l2 > slot6.getItemStackLimit(itemstack11) - itemstack8.getCount()) {
                           l2 = slot6.getItemStackLimit(itemstack11) - itemstack8.getCount();
                        }

                        if (l2 > itemstack11.getMaxStackSize() - itemstack8.getCount()) {
                           l2 = itemstack11.getMaxStackSize() - itemstack8.getCount();
                        }

                        itemstack11.shrink(l2);
                        itemstack8.grow(l2);
                     } else if (itemstack11.getCount() <= slot6.getItemStackLimit(itemstack11)) {
                        slot6.putStack(itemstack11);
                        inventoryplayer.setItemStack(itemstack8);
                     }
                  } else if (itemstack11.getMaxStackSize() > 1 && areItemsAndTagsEqual(itemstack8, itemstack11) && !itemstack8.isEmpty()) {
                     int i3 = itemstack8.getCount();
                     if (i3 + itemstack11.getCount() <= itemstack11.getMaxStackSize()) {
                        itemstack11.grow(i3);
                        itemstack8 = slot6.decrStackSize(i3);
                        if (itemstack8.isEmpty()) {
                           slot6.putStack(ItemStack.EMPTY);
                        }

                        slot6.onTake(player, inventoryplayer.getItemStack());
                     }
                  }
               }

               slot6.onSlotChanged();
            }
         }
      } else if (clickType == ClickType.SWAP && dragEvent >= 0 && dragEvent < 9) {
         Slot slot4 = this.inventorySlots.get(slotIndex);
         ItemStack itemstack6 = inventoryplayer.getStackInSlot(dragEvent);
         ItemStack itemstack10 = slot4.getStack();
         if (!itemstack6.isEmpty() || !itemstack10.isEmpty()) {
            if (itemstack6.isEmpty()) {
               if (slot4.canTakeStack(player)) {
                  inventoryplayer.setInventorySlotContents(dragEvent, itemstack10);
                  slot4.onSwapCraft(itemstack10.getCount());
                  slot4.putStack(ItemStack.EMPTY);
                  slot4.onTake(player, itemstack10);
               }
            } else if (itemstack10.isEmpty()) {
               if (slot4.isItemValid(itemstack6)) {
                  int l1 = slot4.getItemStackLimit(itemstack6);
                  if (itemstack6.getCount() > l1) {
                     slot4.putStack(itemstack6.split(l1));
                  } else {
                     slot4.putStack(itemstack6);
                     inventoryplayer.setInventorySlotContents(dragEvent, ItemStack.EMPTY);
                  }
               }
            } else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6)) {
               int i2 = slot4.getItemStackLimit(itemstack6);
               if (itemstack6.getCount() > i2) {
                  slot4.putStack(itemstack6.split(i2));
                  slot4.onTake(player, itemstack10);
                  if (!inventoryplayer.addItemStackToInventory(itemstack10)) {
                     player.dropItem(itemstack10, true);
                  }
               } else {
                  slot4.putStack(itemstack6);
                  inventoryplayer.setInventorySlotContents(dragEvent, itemstack10);
                  slot4.onTake(player, itemstack10);
               }
            }
         }
      } else if (clickType == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack().isEmpty() && slotIndex >= 0) {
         Slot slot3 = this.inventorySlots.get(slotIndex);
         if (slot3 != null && slot3.getHasStack()) {
            ItemStack itemstack5 = slot3.getStack().copy();
            itemstack5.setCount(itemstack5.getMaxStackSize());
            inventoryplayer.setItemStack(itemstack5);
         }
      } else if (clickType == ClickType.THROW && inventoryplayer.getItemStack().isEmpty() && slotIndex >= 0) {
         Slot slot2 = this.inventorySlots.get(slotIndex);
         if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player)) {
            ItemStack itemstack4 = slot2.decrStackSize(dragEvent == 0 ? 1 : slot2.getStack().getCount());
            slot2.onTake(player, itemstack4);
            player.dropItem(itemstack4, true);
         }
      } else if (clickType == ClickType.PICKUP_ALL && slotIndex >= 0) {
         Slot slot = this.inventorySlots.get(slotIndex);
         ItemStack itemstack1 = inventoryplayer.getItemStack();
         if (!itemstack1.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
            int i = dragEvent == 0 ? 0 : this.inventorySlots.size() - 1;
            int j = dragEvent == 0 ? 1 : -1;

            for(int k = 0; k < 2; ++k) {
               for(int l = i; l >= 0 && l < this.inventorySlots.size() && itemstack1.getCount() < itemstack1.getMaxStackSize(); l += j) {
                  Slot slot1 = this.inventorySlots.get(l);
                  if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack1, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack1, slot1)) {
                     ItemStack itemstack2 = slot1.getStack();
                     if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize()) {
                        int i1 = Math.min(itemstack1.getMaxStackSize() - itemstack1.getCount(), itemstack2.getCount());
                        ItemStack itemstack3 = slot1.decrStackSize(i1);
                        itemstack1.grow(i1);
                        if (itemstack3.isEmpty()) {
                           slot1.putStack(ItemStack.EMPTY);
                        }

                        slot1.onTake(player, itemstack3);
                     }
                  }
               }
            }
         }

         this.detectAndSendChanges();
      }

      return itemstack;
   }

   public static boolean areItemsAndTagsEqual(ItemStack p_195929_0_, ItemStack p_195929_1_) {
      return p_195929_0_.getItem() == p_195929_1_.getItem() && ItemStack.areItemStackTagsEqual(p_195929_0_, p_195929_1_);
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return true;
   }

   public void onContainerClosed(EntityPlayer p_75134_1_) {
      InventoryPlayer inventoryplayer = p_75134_1_.inventory;
      if (!inventoryplayer.getItemStack().isEmpty()) {
         p_75134_1_.dropItem(inventoryplayer.getItemStack(), false);
         inventoryplayer.setItemStack(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(EntityPlayer p_193327_1_, World p_193327_2_, IInventory p_193327_3_) {
      if (!p_193327_1_.isEntityAlive() || p_193327_1_ instanceof EntityPlayerMP && ((EntityPlayerMP)p_193327_1_).hasDisconnected()) {
         for(int j = 0; j < p_193327_3_.getSizeInventory(); ++j) {
            p_193327_1_.dropItem(p_193327_3_.removeStackFromSlot(j), false);
         }

      } else {
         for(int i = 0; i < p_193327_3_.getSizeInventory(); ++i) {
            p_193327_1_.inventory.placeItemBackInInventory(p_193327_2_, p_193327_3_.removeStackFromSlot(i));
         }

      }
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      this.detectAndSendChanges();
   }

   public void putStackInSlot(int p_75141_1_, ItemStack p_75141_2_) {
      this.getSlot(p_75141_1_).putStack(p_75141_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setAll(List<ItemStack> p_190896_1_) {
      for(int i = 0; i < p_190896_1_.size(); ++i) {
         this.getSlot(i).putStack(p_190896_1_.get(i));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
   }

   @OnlyIn(Dist.CLIENT)
   public short getNextTransactionID(InventoryPlayer p_75136_1_) {
      ++this.transactionID;
      return this.transactionID;
   }

   public boolean getCanCraft(EntityPlayer p_75129_1_) {
      return !this.playerList.contains(p_75129_1_);
   }

   public void setCanCraft(EntityPlayer p_75128_1_, boolean p_75128_2_) {
      if (p_75128_2_) {
         this.playerList.remove(p_75128_1_);
      } else {
         this.playerList.add(p_75128_1_);
      }

   }

   public abstract boolean canInteractWith(EntityPlayer p_75145_1_);

   protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
      boolean flag = false;
      int i = p_75135_2_;
      if (p_75135_4_) {
         i = p_75135_3_ - 1;
      }

      if (p_75135_1_.isStackable()) {
         while(!p_75135_1_.isEmpty()) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            Slot slot = this.inventorySlots.get(i);
            ItemStack itemstack = slot.getStack();
            if (!itemstack.isEmpty() && areItemsAndTagsEqual(p_75135_1_, itemstack)) {
               int j = itemstack.getCount() + p_75135_1_.getCount();
               if (j <= p_75135_1_.getMaxStackSize()) {
                  p_75135_1_.setCount(0);
                  itemstack.setCount(j);
                  slot.onSlotChanged();
                  flag = true;
               } else if (itemstack.getCount() < p_75135_1_.getMaxStackSize()) {
                  p_75135_1_.shrink(p_75135_1_.getMaxStackSize() - itemstack.getCount());
                  itemstack.setCount(p_75135_1_.getMaxStackSize());
                  slot.onSlotChanged();
                  flag = true;
               }
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      if (!p_75135_1_.isEmpty()) {
         if (p_75135_4_) {
            i = p_75135_3_ - 1;
         } else {
            i = p_75135_2_;
         }

         while(true) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            Slot slot1 = this.inventorySlots.get(i);
            ItemStack itemstack1 = slot1.getStack();
            if (itemstack1.isEmpty() && slot1.isItemValid(p_75135_1_)) {
               if (p_75135_1_.getCount() > slot1.getSlotStackLimit()) {
                  slot1.putStack(p_75135_1_.split(slot1.getSlotStackLimit()));
               } else {
                  slot1.putStack(p_75135_1_.split(p_75135_1_.getCount()));
               }

               slot1.onSlotChanged();
               flag = true;
               break;
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      return flag;
   }

   public static int extractDragMode(int p_94529_0_) {
      return p_94529_0_ >> 2 & 3;
   }

   public static int getDragEvent(int p_94532_0_) {
      return p_94532_0_ & 3;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_) {
      return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
   }

   public static boolean isValidDragMode(int p_180610_0_, EntityPlayer p_180610_1_) {
      if (p_180610_0_ == 0) {
         return true;
      } else if (p_180610_0_ == 1) {
         return true;
      } else {
         return p_180610_0_ == 2 && p_180610_1_.capabilities.isCreativeMode;
      }
   }

   protected void resetDrag() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public static boolean canAddItemToSlot(@Nullable Slot p_94527_0_, ItemStack p_94527_1_, boolean p_94527_2_) {
      boolean flag = p_94527_0_ == null || !p_94527_0_.getHasStack();
      if (!flag && p_94527_1_.isItemEqual(p_94527_0_.getStack()) && ItemStack.areItemStackTagsEqual(p_94527_0_.getStack(), p_94527_1_)) {
         return p_94527_0_.getStack().getCount() + (p_94527_2_ ? 0 : p_94527_1_.getCount()) <= p_94527_1_.getMaxStackSize();
      } else {
         return flag;
      }
   }

   public static void computeStackSize(Set<Slot> p_94525_0_, int p_94525_1_, ItemStack p_94525_2_, int p_94525_3_) {
      switch(p_94525_1_) {
      case 0:
         p_94525_2_.setCount(MathHelper.floor((float)p_94525_2_.getCount() / (float)p_94525_0_.size()));
         break;
      case 1:
         p_94525_2_.setCount(1);
         break;
      case 2:
         p_94525_2_.setCount(p_94525_2_.getItem().getMaxStackSize());
      }

      p_94525_2_.grow(p_94525_3_);
   }

   public boolean canDragIntoSlot(Slot p_94531_1_) {
      return true;
   }

   public static int calcRedstone(@Nullable TileEntity p_178144_0_) {
      return p_178144_0_ instanceof IInventory ? calcRedstoneFromInventory((IInventory)p_178144_0_) : 0;
   }

   public static int calcRedstoneFromInventory(@Nullable IInventory p_94526_0_) {
      if (p_94526_0_ == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for(int j = 0; j < p_94526_0_.getSizeInventory(); ++j) {
            ItemStack itemstack = p_94526_0_.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               f += (float)itemstack.getCount() / (float)Math.min(p_94526_0_.getInventoryStackLimit(), itemstack.getMaxStackSize());
               ++i;
            }
         }

         f = f / (float)p_94526_0_.getSizeInventory();
         return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }

   protected void slotChangedCraftingGrid(World p_192389_1_, EntityPlayer p_192389_2_, IInventory p_192389_3_, InventoryCraftResult p_192389_4_) {
      if (!p_192389_1_.isRemote) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)p_192389_2_;
         ItemStack itemstack = ItemStack.EMPTY;
         IRecipe irecipe = p_192389_1_.getServer().getRecipeManager().getRecipe(p_192389_3_, p_192389_1_);
         if (p_192389_4_.canUseRecipe(p_192389_1_, entityplayermp, irecipe) && irecipe != null) {
            itemstack = irecipe.getCraftingResult(p_192389_3_);
         }

         p_192389_4_.setInventorySlotContents(0, itemstack);
         entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
      }
   }
}
