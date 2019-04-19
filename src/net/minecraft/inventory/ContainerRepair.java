package net.minecraft.inventory;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTiered;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ContainerRepair extends Container {
    private final IInventory outputSlot = new InventoryCraftResult();
    private final EntityPlayer player;
    private final BlockPos pos;
    private final World world;
    private final int repairLevel;
    private int durabilityCost;
    private int materialCost;
    private String repairedItemName;
    private final IInventory inputSlots = new InventoryBasic(new TextComponentString("Repair"), 2) {
        public void markDirty() {
            super.markDirty();
            ContainerRepair.this.onCraftMatrixChanged(this);
        }
    };

    @OnlyIn(Dist.CLIENT)
    public ContainerRepair(InventoryPlayer p_i45806_1_, World p_i45806_2_, EntityPlayer p_i45806_3_,int repairLevel) {
        this(p_i45806_1_, p_i45806_2_, BlockPos.ORIGIN, p_i45806_3_,repairLevel);
    }

    public ContainerRepair(InventoryPlayer p_i45807_1_, final World p_i45807_2_, final BlockPos p_i45807_3_, EntityPlayer p_i45807_4_,int repairLevel) {
        this.pos = p_i45807_3_;
        this.world = p_i45807_2_;
        this.player = p_i45807_4_;
        this.repairLevel = repairLevel;
        this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
            public ItemStack onTake(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
                //MITEMODDED
                // Removed the xp cost of repairing items , Original
                //             if (!p_190901_1_.capabilities.isCreativeMode) {
                //                p_190901_1_.addExperienceLevel(-ContainerRepair.this.maximumCost);
                //             }

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


                //MITEMODDED make anvil has damage
                IBlockState iblockstate1 = p_i45807_2_.getBlockState(p_i45807_3_);
                if (!p_i45807_2_.isRemote) {
                    if (!p_190901_1_.capabilities.isCreativeMode && iblockstate1.isIn(BlockTags.ANVIL)) {
                        IBlockState iblockstate = ((BlockAnvil)iblockstate1.getBlock()).damage(durabilityCost);
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

            public boolean isItemValid(ItemStack other) {
                return false;
            }

            public boolean canTakeStack(EntityPlayer p_82869_1_) {
                //MITEMODDED always can repair without exp
                return true;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(p_i45807_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(p_i45807_1_, k, 8 + k * 18, 142));
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
                if (p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
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

    public void onContainerClosed(EntityPlayer p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        if (!this.world.isRemote) {
            this.clearContainer(p_75134_1_, this.world, this.inputSlots);
        }
    }

    public void onCraftMatrixChanged(IInventory p_75130_1_) {
        super.onCraftMatrixChanged(p_75130_1_);
        this.updateRepairOutput();

    }

    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        if (!this.world.getBlockState(this.pos).isIn(BlockTags.ANVIL)) {
            return false;
        } else {
            return p_75145_1_.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
                    (double) this.pos.getZ() + 0.5D) <= 64.0D && this.world.isAirBlock(this.pos.add(0,1,0));
        }
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

    private void updateRepairOutput() {
        ItemStack tool = this.inputSlots.getStackInSlot(0);
        int i = 0;
        int k;
        if (tool.isEmpty()) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        } else {
            ItemStack outputToolStack = tool.copy();
            ItemStack bookOrMaterial = this.inputSlots.getStackInSlot(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(outputToolStack);
            this.materialCost = 0;
            if (!bookOrMaterial.isEmpty()) {
                boolean flag = bookOrMaterial.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(
                        bookOrMaterial).isEmpty();
                if (outputToolStack.isDamageable() && outputToolStack.getItem().getIsRepairable(tool, bookOrMaterial)) {
                    int materialCost;
                    Item resultItem = outputToolStack.getItem();
                    if (resultItem instanceof ItemTiered) {
                        int repairDurability = ((ItemTiered) resultItem).getTier().getRepairDurability();
                        int repairLevel = ((ItemTiered) resultItem).getTier().getHarvestLevel();
                        if (repairLevel>this.repairLevel){
                            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                            return;
                        }
                        if (repairDurability <= 0) {
                            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                            return;
                        }

                        if (tool.getDamage() <= repairDurability) {
                            outputToolStack.setDamage(0);
                            this.materialCost = 1;
                            this.durabilityCost = repairDurability;
                            this.outputSlot.setInventorySlotContents(1,outputToolStack);
                            return;
                        }
                        //MITEMODDED To make anvil can fix item without xp requirement and not waste material
                        materialCost = Math.min(
                                ((tool.getDamage() - (tool.getDamage() % repairDurability)) / repairDurability), bookOrMaterial.getCount());
                        outputToolStack.setDamage(outputToolStack.getDamage() - materialCost*repairDurability);
                        this.outputSlot.setInventorySlotContents(1,outputToolStack);
                        this.durabilityCost = materialCost*repairDurability;
                        this.materialCost = materialCost;
                        return;
                    }



                } else {
                    if (!flag && (outputToolStack.getItem() != bookOrMaterial.getItem() || !outputToolStack.isDamageable())) {
                        this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                        return;
                    }

                    if (outputToolStack.isDamageable() && !flag) {
                        int l = tool.getMaxDamage() - tool.getDamage();
                        int i1 = bookOrMaterial.getMaxDamage() - bookOrMaterial.getDamage();
                        int j1 = i1 + outputToolStack.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = outputToolStack.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < outputToolStack.getDamage()) {
                            outputToolStack.setDamage(l1);
                            i += 2;
                        }
                    }

                    if (!tool.isEnchanted()) {
                        Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(bookOrMaterial);
                        boolean flag2 = false;
                        boolean flag3 = false;

                        for (Enchantment enchantment1 : map1.keySet()) {
                            if (enchantment1 != null) {
                                int i2 = map.getOrDefault(enchantment1, 0);
                                int j2 = map1.get(enchantment1);
                                j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                                boolean flag1 = enchantment1.canApply(tool);
                                //MITEMODDED Make the enchantedBook cannot merge together
                                if (this.player.capabilities.isCreativeMode ) {
                                    flag1 = true;
                                }

                                for (Enchantment enchantment : map.keySet()) {
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
                                    switch (enchantment1.getRarity()) {
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
                                    if (tool.getCount() > 1) {
                                        i = 40;
                                    }
                                }
                            }
                        }
                        if (flag3 && !flag2) {
                            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                            return;
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.repairedItemName)) {
                if (tool.hasDisplayName()) {
                    k = 1;
                    i += k;
                    outputToolStack.clearCustomName();
                }
            } else if (!this.repairedItemName.equals(tool.getDisplayName().getString())) {
                k = 1;
                i += k;
                outputToolStack.setDisplayName(new TextComponentString(this.repairedItemName));
            }

            if (i <= 0) {
                outputToolStack = ItemStack.EMPTY;
            }


            if (!outputToolStack.isEmpty()) {

                EnchantmentHelper.setEnchantments(map, outputToolStack);
            }

            this.outputSlot.setInventorySlotContents(0, outputToolStack);
            this.detectAndSendChanges();
        }
    }
}
