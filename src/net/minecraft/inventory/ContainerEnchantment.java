package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerEnchantment extends Container {
   public IInventory tableInventory = new InventoryBasic(new TextComponentString("Enchant"), 2) {
      public int getInventoryStackLimit() {
         return 64;
      }

      public void markDirty() {
         super.markDirty();
         ContainerEnchantment.this.onCraftMatrixChanged(this);
      }
   };
   private final World world;
   private final BlockPos position;
   private final Random rand = new Random();
   public int xpSeed;
   public int[] enchantLevels = new int[3];
   public int[] enchantClue = new int[]{-1, -1, -1};
   public int[] worldClue = new int[]{-1, -1, -1};

   @OnlyIn(Dist.CLIENT)
   public ContainerEnchantment(InventoryPlayer p_i45797_1_, World p_i45797_2_) {
      this(p_i45797_1_, p_i45797_2_, BlockPos.ORIGIN);
   }

   public ContainerEnchantment(InventoryPlayer p_i45798_1_, World p_i45798_2_, BlockPos p_i45798_3_) {
      this.world = p_i45798_2_;
      this.position = p_i45798_3_;
      this.xpSeed = p_i45798_1_.player.getXPSeed();
      this.addSlot(new Slot(this.tableInventory, 0, 15, 47) {
         public boolean isItemValid(ItemStack other) {
            return true;
         }

         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.tableInventory, 1, 35, 47) {
         public boolean isItemValid(ItemStack other) {
            return other.getItem() == Items.LAPIS_LAZULI;
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i45798_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i45798_1_, k, 8 + k * 18, 142));
      }

   }

   protected void broadcastData(IContainerListener p_185000_1_) {
      p_185000_1_.sendWindowProperty(this, 0, this.enchantLevels[0]);
      p_185000_1_.sendWindowProperty(this, 1, this.enchantLevels[1]);
      p_185000_1_.sendWindowProperty(this, 2, this.enchantLevels[2]);
      p_185000_1_.sendWindowProperty(this, 3, this.xpSeed & -16);
      p_185000_1_.sendWindowProperty(this, 4, this.enchantClue[0]);
      p_185000_1_.sendWindowProperty(this, 5, this.enchantClue[1]);
      p_185000_1_.sendWindowProperty(this, 6, this.enchantClue[2]);
      p_185000_1_.sendWindowProperty(this, 7, this.worldClue[0]);
      p_185000_1_.sendWindowProperty(this, 8, this.worldClue[1]);
      p_185000_1_.sendWindowProperty(this, 9, this.worldClue[2]);
   }

   public void addListener(IContainerListener p_75132_1_) {
      super.addListener(p_75132_1_);
      this.broadcastData(p_75132_1_);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < this.listeners.size(); ++i) {
         IContainerListener icontainerlistener = this.listeners.get(i);
         this.broadcastData(icontainerlistener);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      if (id >= 0 && id <= 2) {
         this.enchantLevels[id] = data;
      } else if (id == 3) {
         this.xpSeed = data;
      } else if (id >= 4 && id <= 6) {
         this.enchantClue[id - 4] = data;
      } else if (id >= 7 && id <= 9) {
         this.worldClue[id - 7] = data;
      } else {
         super.updateProgressBar(id, data);
      }

   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      if (p_75130_1_ == this.tableInventory) {
         ItemStack itemstack = p_75130_1_.getStackInSlot(0);
         if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
            if (!this.world.isRemote) {
               int l = 0;

               for(int j = -1; j <= 1; ++j) {
                  for(int k = -1; k <= 1; ++k) {
                     if ((j != 0 || k != 0) && this.world.isAirBlock(this.position.add(k, 0, j)) && this.world.isAirBlock(this.position.add(k, 1, j))) {
                        if (this.world.getBlockState(this.position.add(k * 2, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                           ++l;
                        }

                        if (this.world.getBlockState(this.position.add(k * 2, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                           ++l;
                        }

                        if (k != 0 && j != 0) {
                           if (this.world.getBlockState(this.position.add(k * 2, 0, j)).getBlock() == Blocks.BOOKSHELF) {
                              ++l;
                           }

                           if (this.world.getBlockState(this.position.add(k * 2, 1, j)).getBlock() == Blocks.BOOKSHELF) {
                              ++l;
                           }

                           if (this.world.getBlockState(this.position.add(k, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                              ++l;
                           }

                           if (this.world.getBlockState(this.position.add(k, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                              ++l;
                           }
                        }
                     }
                  }
               }

               this.rand.setSeed((long)this.xpSeed);

               for(int i1 = 0; i1 < 3; ++i1) {
                  this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i1, l, itemstack);
                  this.enchantClue[i1] = -1;
                  this.worldClue[i1] = -1;
                  if (this.enchantLevels[i1] < i1 + 1) {
                     this.enchantLevels[i1] = 0;
                  }
               }

               for(int j1 = 0; j1 < 3; ++j1) {
                  if (this.enchantLevels[j1] > 0) {
                     List<EnchantmentData> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentData enchantmentdata = list.get(this.rand.nextInt(list.size()));
                        this.enchantClue[j1] = IRegistry.field_212628_q.func_148757_b(enchantmentdata.enchantment);
                        this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else {
            for(int i = 0; i < 3; ++i) {
               this.enchantLevels[i] = 0;
               this.enchantClue[i] = -1;
               this.worldClue[i] = -1;
            }
         }
      }

   }

   public boolean enchantItem(EntityPlayer p_75140_1_, int p_75140_2_) {
      ItemStack itemstack = this.tableInventory.getStackInSlot(0);
      ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
      int i = p_75140_2_ + 1;
      if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !p_75140_1_.capabilities.isCreativeMode) {
         return false;
      } else if (this.enchantLevels[p_75140_2_] > 0 && !itemstack.isEmpty() && (p_75140_1_.experienceLevel >= i && p_75140_1_.experienceLevel >= this.enchantLevels[p_75140_2_] || p_75140_1_.capabilities.isCreativeMode)) {
         if (!this.world.isRemote) {
            List<EnchantmentData> list = this.getEnchantmentList(itemstack, p_75140_2_, this.enchantLevels[p_75140_2_]);
            if (!list.isEmpty()) {
               p_75140_1_.onEnchant(itemstack, i);
               boolean flag = itemstack.getItem() == Items.BOOK;
               if (flag) {
                  itemstack = new ItemStack(Items.ENCHANTED_BOOK);
                  this.tableInventory.setInventorySlotContents(0, itemstack);
               }

               for(int j = 0; j < list.size(); ++j) {
                  EnchantmentData enchantmentdata = list.get(j);
                  if (flag) {
                     ItemEnchantedBook.addEnchantment(itemstack, enchantmentdata);
                  } else {
                     itemstack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                  }
               }

               if (!p_75140_1_.capabilities.isCreativeMode) {
                  itemstack1.shrink(i);
                  if (itemstack1.isEmpty()) {
                     this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                  }
               }

               p_75140_1_.addStat(StatList.ENCHANT_ITEM);
               if (p_75140_1_ instanceof EntityPlayerMP) {
                  CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP)p_75140_1_, itemstack, i);
               }

               this.tableInventory.markDirty();
               this.xpSeed = p_75140_1_.getXPSeed();
               this.onCraftMatrixChanged(this.tableInventory);
               this.world.playSound((EntityPlayer)null, this.position, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List<EnchantmentData> getEnchantmentList(ItemStack p_178148_1_, int p_178148_2_, int p_178148_3_) {
      this.rand.setSeed((long)(this.xpSeed + p_178148_2_));
      List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, p_178148_1_, p_178148_3_, false);
      if (p_178148_1_.getItem() == Items.BOOK && list.size() > 1) {
         list.remove(this.rand.nextInt(list.size()));
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLapisAmount() {
      ItemStack itemstack = this.tableInventory.getStackInSlot(1);
      return itemstack.isEmpty() ? 0 : itemstack.getCount();
   }

   public void onContainerClosed(EntityPlayer p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      if (!this.world.isRemote) {
         this.clearContainer(p_75134_1_, p_75134_1_.world, this.tableInventory);
      }
   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      if (this.world.getBlockState(this.position).getBlock() != Blocks.ENCHANTING_TABLE) {
         return false;
      } else {
         return !(p_75145_1_.getDistanceSq((double)this.position.getX() + 0.5D, (double)this.position.getY() + 0.5D, (double)this.position.getZ() + 0.5D) > 64.0D);
      }
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 0) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ == 1) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (itemstack1.getItem() == Items.LAPIS_LAZULI) {
            if (!this.mergeItemStack(itemstack1, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(itemstack1)) {
               return ItemStack.EMPTY;
            }

            if (itemstack1.hasTag() && itemstack1.getCount() == 1) {
               this.inventorySlots.get(0).putStack(itemstack1.copy());
               itemstack1.setCount(0);
            } else if (!itemstack1.isEmpty()) {
               this.inventorySlots.get(0).putStack(new ItemStack(itemstack1.getItem()));
               itemstack1.shrink(1);
            }
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
}
