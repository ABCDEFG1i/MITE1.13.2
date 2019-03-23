package net.minecraft.inventory;

import com.sun.istack.internal.NotNull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ITimedRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ContainerPlayer extends ContainerRecipeBook {
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS =
            new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS,
                    EntityEquipmentSlot.FEET};
    private static final String[] field_200829_h =
            new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings",
                    "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
    private final EntityPlayer player;
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    private InventoryCraftResult craftResult = new InventoryCraftResult();
    private SlotCrafting craftingSlot;
    private Thread craftingThread = new Thread();
    private int craftingTime;
    private boolean isCrafting;
    private int totalCraftingTime;

    public ContainerPlayer(InventoryPlayer p_i1819_1_, EntityPlayer p_i1819_3_) {
        this.player = p_i1819_3_;
        this.craftingSlot = new SlotCrafting(p_i1819_1_.player, this.craftMatrix, this.craftResult, 0, 154, 28);
        this.addSlot(new SlotCrafting(p_i1819_1_.player, this.craftMatrix, this.craftResult, 0, 154, 28));

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        for (int k = 0; k < 4; ++k) {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(p_i1819_1_, 39 - k, 8, 8 + k * 18) {
                public boolean isItemValid(ItemStack other) {
                    return entityequipmentslot == EntityLiving.getSlotForItemStack(other);
                }

                public int getSlotStackLimit() {
                    return 1;
                }

                @NotNull
                @OnlyIn(Dist.CLIENT)
                public String getSlotTexture() {
                    return ContainerPlayer.field_200829_h[entityequipmentslot.getIndex()];
                }

                public boolean canTakeStack(EntityPlayer p_82869_1_) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || p_82869_1_.isCreative() || !EnchantmentHelper.hasBindingCurse(
                            itemstack)) && super.canTakeStack(p_82869_1_);
                }
            });
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(p_i1819_1_, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(p_i1819_1_, i1, 8 + i1 * 18, 142));
        }

        this.addSlot(new Slot(p_i1819_1_, 40, 77, 62) {
            @Nonnull
            @OnlyIn(Dist.CLIENT)
            public String getSlotTexture() {
                return "item/empty_armor_slot_shield";
            }
        });
    }

    //Only invoke in the crafting thread
    //MITEMODDED Add
    private void craftOne() throws InterruptedException {
        this.isCrafting = true;
        this.totalCraftingTime = ((ITimedRecipe) Objects.requireNonNull(this.craftResult.getRecipeUsed()))
                .getCraftingTime();
        ItemStack targetResult = craftingSlot.getStack();
        for (int i = 0; i < totalCraftingTime; i++) {
            synchronized (this) {
                if (!craftingSlot.getStack().isItemEqual(targetResult)) {
                    this.craftingTime = 0;
                    this.isCrafting = false;
                    //To break up the loop in the method startCraftingProgress()
                    throw new RuntimeException();
                }
                this.craftingTime = i;
            }
            Thread.sleep(1);
        }
        synchronized (this) {
            this.craftingTime = 0;
        }

        if (!this.player.addItemStackToInventory(this.craftingSlot.getStack())) {
            this.player.dropItem(this.craftingSlot.getStack(), true);
        }
        this.craftingSlot.onTake(this.player, this.craftingSlot.getStack());
        this.isCrafting = false;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            listener.sendWindowProperty(this, 0, this.craftingTime);
            listener.sendWindowProperty(this, 1, this.totalCraftingTime);
        }
    }

    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(p_82846_2_);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            if (p_82846_2_ == 0) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (p_82846_2_ >= 1 && p_82846_2_ < 5) {
                if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 5 && p_82846_2_ < 9) {
                if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get(
                    8 - entityequipmentslot.getIndex()).getHasStack()) {
                int i = 8 - entityequipmentslot.getIndex();
                if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND &&
                       !this.inventorySlots.get(45).getHasStack()) {
                if (!this.mergeItemStack(itemstack1, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 9 && p_82846_2_ < 36) {
                if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 36 && p_82846_2_ < 45) {
                if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
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

            ItemStack itemstack2 = slot.onTake(p_82846_1_, itemstack1);
            if (p_82846_2_ == 0) {
                p_82846_1_.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
    }

    public void onContainerClosed(EntityPlayer p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        this.craftResult.clear();
        this.craftingThread.interrupt();
        this.craftingTime=0;
        this.totalCraftingTime=0;
        this.isCrafting = false;
        if (!p_75134_1_.world.isRemote) {
            this.clearContainer(p_75134_1_, p_75134_1_.world, this.craftMatrix);
        }
    }

    public void onCraftMatrixChanged(IInventory p_75130_1_) {
        this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        switch (id) {
            case 0:
                this.craftingTime = data;
                break;
            case 1:
                this.totalCraftingTime = data;
                break;
            default:
                break;
        }
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    public void func_201771_a(RecipeItemHelper p_201771_1_) {
        this.craftMatrix.fillStackedContents(p_201771_1_);
    }

    public void clear() {
        this.craftResult.clear();
        this.craftMatrix.clear();
    }

    public boolean matches(IRecipe p_201769_1_) {
        return p_201769_1_.matches(this.craftMatrix, this.player.world);
    }

    public int getOutputSlot() {
        return 0;
    }

    public int getWidth() {
        return this.craftMatrix.getWidth();
    }

    public int getHeight() {
        return this.craftMatrix.getHeight();
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return 5;
    }

    public synchronized int getCraftingTime() {
        return this.craftingTime;
    }

    public synchronized int getTotalCraftingTime() {
        return this.totalCraftingTime == 0 ? 1 : this.totalCraftingTime;
    }

    public void resetCraftingProgress() {
        this.craftingThread.interrupt();
        synchronized (this) {
            this.isCrafting = false;
            this.craftingTime = 0;
        }
    }

    //MITEMODDED
    @OnlyIn(Dist.CLIENT)
    void startCraftingProgress(boolean craftAll) {
        IRecipe recipeUsed = this.craftResult.getRecipeUsed();
        if (isCrafting | !(recipeUsed instanceof ITimedRecipe)) {
            return;
        }
        this.craftingThread = new Thread(() -> {
            try {
                if (craftAll) {
                    while (true) {
                        craftOne();
                    }
                } else {
                    craftOne();
                }
            } catch (Exception ignored) {
            }
        });
        this.craftingThread.start();
    }
}
