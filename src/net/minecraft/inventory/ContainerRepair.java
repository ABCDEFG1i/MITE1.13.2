package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IInventory outputSlot = new InventoryCraftResult();
   private final IInventory inputSlots = new InventoryBasic(new TextComponentString("Repair"), 2) {
      public void markDirty() {
         super.markDirty();
         ContainerRepair.this.onCraftMatrixChanged(this);
      }
   };
   private final World world;
   private final BlockPos pos;
   public int maximumCost;
   public int materialCost;
   private String repairedItemName;
   private final EntityPlayer player;

   @OnlyIn(Dist.CLIENT)
   public ContainerRepair(InventoryPlayer p_i45806_1_, World p_i45806_2_, EntityPlayer p_i45806_3_) {
      this(p_i45806_1_, p_i45806_2_, BlockPos.ORIGIN, p_i45806_3_);
   }

   public ContainerRepair(InventoryPlayer p_i45807_1_, final World p_i45807_2_, final BlockPos p_i45807_3_, EntityPlayer p_i45807_4_) {
      this.pos = p_i45807_3_;
      this.world = p_i45807_2_;
      this.player = p_i45807_4_;
      this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
         public boolean isItemValid(ItemStack other) {
            return false;
         }

         public boolean canTakeStack(EntityPlayer p_82869_1_) {
            return (p_82869_1_.capabilities.isCreativeMode || p_82869_1_.experienceLevel >= ContainerRepair.this.maximumCost) && ContainerRepair.this.maximumCost > 0 && this.getHasStack();
         }

         public ItemStack onTake(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
            if (!p_190901_1_.capabilities.isCreativeMode) {
               p_190901_1_.addExperienceLevel(-ContainerRepair.this.maximumCost);
            }

            ContainerRepair.this.inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);
            if (ContainerRepair.this.materialCost > 0) {
               ItemStack itemstack = ContainerRepair.this.inputSlots.getStackInSlot(1);
               if (!itemstack.isEmpty() && itemstack.getCount() > ContainerRepair.this.materialCost) {
                  itemstack.shrink(ContainerRepair.this.materialCost);
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, itemstack);
               } else {
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
               }
            } else {
               ContainerRepair.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
            }

            ContainerRepair.this.maximumCost = 0;
            IBlockState iblockstate1 = p_i45807_2_.getBlockState(p_i45807_3_);
            if (!p_i45807_2_.isRemote) {
               if (!p_190901_1_.capabilities.isCreativeMode && iblockstate1.isIn(BlockTags.ANVIL) && p_190901_1_.getRNG().nextFloat() < 0.12F) {
                  IBlockState iblockstate = BlockAnvil.damage(iblockstate1);
                  if (iblockstate == null) {
                     p_i45807_2_.removeBlock(p_i45807_3_);
                     p_i45807_2_.playEvent(1029, p_i45807_3_, 0);
                  } else {
                     p_i45807_2_.setBlockState(p_i45807_3_, iblockstate, 2);
                     p_i45807_2_.playEvent(1030, p_i45807_3_, 0);
                  }
               } else {
                  p_i45807_2_.playEvent(1030, p_i45807_3_, 0);
               }
            }

            return p_190901_2_;
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i45807_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i45807_1_, k, 8 + k * 18, 142));
      }

   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      super.onCraftMatrixChanged(p_75130_1_);
      if (p_75130_1_ == this.inputSlots) {
         this.updateRepairOutput();
      }

   }

   public void updateRepairOutput() {
      ItemStack itemstack = this.inputSlots.getStackInSlot(0);
      this.maximumCost = 1;
      int i = 0;
      int j = 0;
      int k = 0;
      if (itemstack.isEmpty()) {
         this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
         this.maximumCost = 0;
      } else {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
         Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
         j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
         this.materialCost = 0;
         if (!itemstack2.isEmpty()) {
            boolean flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).isEmpty();
            if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
               int l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
               if (l2 <= 0) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost = 0;
                  return;
               }

               int i3;
               for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                  int j3 = itemstack1.getDamage() - l2;
                  itemstack1.setDamage(j3);
                  ++i;
                  l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
               }

               this.materialCost = i3;
            } else {
               if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost = 0;
                  return;
               }

               if (itemstack1.isDamageable() && !flag) {
                  int l = itemstack.getMaxDamage() - itemstack.getDamage();
                  int i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
                  int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                  int k1 = l + j1;
                  int l1 = itemstack1.getMaxDamage() - k1;
                  if (l1 < 0) {
                     l1 = 0;
                  }

                  if (l1 < itemstack1.getDamage()) {
                     itemstack1.setDamage(l1);
                     i += 2;
                  }
               }

               Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
               boolean flag2 = false;
               boolean flag3 = false;

               for(Enchantment enchantment1 : map1.keySet()) {
                  if (enchantment1 != null) {
                     int i2 = map.containsKey(enchantment1) ? map.get(enchantment1) : 0;
                     int j2 = map1.get(enchantment1);
                     j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                     boolean flag1 = enchantment1.canApply(itemstack);
                     if (this.player.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                        flag1 = true;
                     }

                     for(Enchantment enchantment : map.keySet()) {
                        if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                           flag1 = false;
                           ++i;
                        }
                     }

                     if (!flag1) {
                        flag3 = true;
                     } else {
                        flag2 = true;
                        if (j2 > enchantment1.getMaxLevel()) {
                           j2 = enchantment1.getMaxLevel();
                        }

                        map.put(enchantment1, j2);
                        int k3 = 0;
                        switch(enchantment1.getRarity()) {
                        case COMMON:
                           k3 = 1;
                           break;
                        case UNCOMMON:
                           k3 = 2;
                           break;
                        case RARE:
                           k3 = 4;
                           break;
                        case VERY_RARE:
                           k3 = 8;
                        }

                        if (flag) {
                           k3 = Math.max(1, k3 / 2);
                        }

                        i += k3 * j2;
                        if (itemstack.getCount() > 1) {
                           i = 40;
                        }
                     }
                  }
               }

               if (flag3 && !flag2) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost = 0;
                  return;
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (itemstack.hasDisplayName()) {
               k = 1;
               i += k;
               itemstack1.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(itemstack.getDisplayName().getString())) {
            k = 1;
            i += k;
            itemstack1.setDisplayName(new TextComponentString(this.repairedItemName));
         }

         this.maximumCost = j + i;
         if (i <= 0) {
            itemstack1 = ItemStack.EMPTY;
         }

         if (k == i && k > 0 && this.maximumCost >= 40) {
            this.maximumCost = 39;
         }

         if (this.maximumCost >= 40 && !this.player.capabilities.isCreativeMode) {
            itemstack1 = ItemStack.EMPTY;
         }

         if (!itemstack1.isEmpty()) {
            int k2 = itemstack1.getRepairCost();
            if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
               k2 = itemstack2.getRepairCost();
            }

            if (k != i || k == 0) {
               k2 = k2 * 2 + 1;
            }

            itemstack1.setRepairCost(k2);
            EnchantmentHelper.setEnchantments(map, itemstack1);
         }

         this.outputSlot.setInventorySlotContents(0, itemstack1);
         this.detectAndSendChanges();
      }
   }

   public void addListener(IContainerListener p_75132_1_) {
      super.addListener(p_75132_1_);
      p_75132_1_.sendWindowProperty(this, 0, this.maximumCost);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      if (id == 0) {
         this.maximumCost = data;
      }

   }

   public void onContainerClosed(EntityPlayer p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      if (!this.world.isRemote) {
         this.clearContainer(p_75134_1_, this.world, this.inputSlots);
      }
   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      if (!this.world.getBlockState(this.pos).isIn(BlockTags.ANVIL)) {
         return false;
      } else {
         return p_75145_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
            if (p_82846_2_ >= 3 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
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

   public void updateItemName(String p_82850_1_) {
      this.repairedItemName = p_82850_1_;
      if (this.getSlot(2).getHasStack()) {
         ItemStack itemstack = this.getSlot(2).getStack();
         if (StringUtils.isBlank(p_82850_1_)) {
            itemstack.clearCustomName();
         } else {
            itemstack.setDisplayName(new TextComponentString(this.repairedItemName));
         }
      }

      this.updateRepairOutput();
   }
}
