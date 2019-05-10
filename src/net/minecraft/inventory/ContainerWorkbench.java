package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ITieredRecipe;
import net.minecraft.item.crafting.ITimedRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public class ContainerWorkbench extends ContainerRecipeBook {
    private final SlotCrafting craftingSlot;
    private final EntityPlayer player;
    private final BlockPos pos;
    private final World world;
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    private InventoryCraftResult craftResult = new InventoryCraftResult();
    private Thread craftingThread = new Thread();
    private int craftingTime;
    private boolean isCrafting;
    private int totalCraftingTime;
    private final int craftingTier;

    public ContainerWorkbench(InventoryPlayer inventoryPlayer, World world, BlockPos pos,int craftingTier) {
        this.world = world;
        this.pos = pos;
        this.player = inventoryPlayer.player;
        this.craftingTier = craftingTier;
        this.craftingSlot = new SlotCrafting(inventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 124, 35);
        this.addSlot(craftingSlot);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(inventoryPlayer, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventoryPlayer, l, 8 + l * 18, 142));
        }

    }

    //Only invoke in crafting thread
    private void craftOne() throws InterruptedException {
        this.isCrafting = true;
        this.totalCraftingTime = ((ITimedRecipe) Objects.requireNonNull(this.craftResult.getRecipeUsed()))
                .getCraftingTime(craftMatrix);
        ItemStack targetResult = craftingSlot.getStack();
        for (int i = 0; i < totalCraftingTime; i++) {
            synchronized (this) {
                ItemStack current = craftingSlot.getStack();
                if (!current.isItemEqual(targetResult)||(current.getTag()!=null&&!current.getTag().equals(targetResult.getTag()))) {
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
            if (p_82846_2_ == 0) {
                itemstack1.getItem().onCreated(itemstack1, this.world, p_82846_1_);
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (p_82846_2_ >= 10 && p_82846_2_ < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 37 && p_82846_2_ < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
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
        if (!this.world.isRemote) {
            this.craftingThread.interrupt();
            this.clearContainer(p_75134_1_, this.world, this.craftMatrix);
        }
    }

    public void onCraftMatrixChanged(IInventory p_75130_1_) {
        this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);
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
        if (!this.world.getBlockState(this.pos.up()).getBlock().isAir(null)) {
            return false;
        }
        if (!this.world.getBlockState(this.pos).getBlock().isIn(BlockTags.CRAFTING_TABLE)) {
            return false;
        } else {
            return p_75145_1_.getDistanceSq((double) this.pos.getX() + 0.5D,
                                            (double) this.pos.getY() + 0.5D,
                                            (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void func_201771_a(RecipeItemHelper p_201771_1_) {
        this.craftMatrix.fillStackedContents(p_201771_1_);
    }

    public void clear() {
        this.craftMatrix.clear();
        this.craftResult.clear();
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
        return 10;
    }

    public synchronized int getCraftingTime() {
        return this.craftingTime;
    }

    public synchronized int getTotalCraftingTime() {
        return this.totalCraftingTime == 0 ? 1 : this.totalCraftingTime;
    }

    //MITEMODDED
    @OnlyIn(Dist.CLIENT)
    void startCraftingProgress(boolean craftAll) {
        IRecipe recipeUsed = this.craftResult.getRecipeUsed();
        if (isCrafting | !(recipeUsed instanceof ITimedRecipe)) {
            return;
        }
        if (recipeUsed instanceof ITieredRecipe){
            if (((ITieredRecipe)recipeUsed).getTier()>craftingTier){
                return;
            }
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
