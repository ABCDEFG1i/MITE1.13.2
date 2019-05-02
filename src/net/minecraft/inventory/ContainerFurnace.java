package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerFurnace extends ContainerRecipeBook {
   private final IInventory tileFurnace;
   private final World world;
   private int cookTime;
   private int totalCookTime;
   private int furnaceBurnTime;
   private int heatLevel;
   private int currentItemBurnTime;

   public ContainerFurnace(InventoryPlayer p_i45794_1_, IInventory p_i45794_2_,int heatLevel) {
      this.tileFurnace = p_i45794_2_;
      this.world = p_i45794_1_.player.world;
      this.heatLevel = heatLevel;
      this.addSlot(new Slot(p_i45794_2_, 0, 56, 17));
      this.addSlot(new SlotFurnaceFuel(p_i45794_2_, 1, 56, 53));
      this.addSlot(new SlotFurnaceOutput(p_i45794_1_.player, p_i45794_2_, 2, 116, 35));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i45794_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i45794_1_, k, 8 + k * 18, 142));
      }

   }

   public void addListener(IContainerListener p_75132_1_) {
      super.addListener(p_75132_1_);
      p_75132_1_.sendAllWindowProperties(this, this.tileFurnace);
   }

   public void func_201771_a(RecipeItemHelper p_201771_1_) {
      if (this.tileFurnace instanceof IRecipeHelperPopulator) {
         ((IRecipeHelperPopulator)this.tileFurnace).fillStackedContents(p_201771_1_);
      }

   }

   public void clear() {
      this.tileFurnace.clear();
   }

   public boolean matches(IRecipe p_201769_1_) {
      return p_201769_1_.matches(this.tileFurnace, this.world);
   }

   public int getOutputSlot() {
      return 2;
   }

   public int getWidth() {
      return 1;
   }

   public int getHeight() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 3;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(IContainerListener icontainerlistener : this.listeners) {
         if (this.cookTime != this.tileFurnace.getField(2)) {
            icontainerlistener.sendWindowProperty(this, 2, this.tileFurnace.getField(2));
         }

         if (this.furnaceBurnTime != this.tileFurnace.getField(0)) {
            icontainerlistener.sendWindowProperty(this, 0, this.tileFurnace.getField(0));
         }

         if (this.currentItemBurnTime != this.tileFurnace.getField(1)) {
            icontainerlistener.sendWindowProperty(this, 1, this.tileFurnace.getField(1));
         }

         if (this.totalCookTime != this.tileFurnace.getField(3)) {
            icontainerlistener.sendWindowProperty(this, 3, this.tileFurnace.getField(3));
         }
         if (this.heatLevel != this.tileFurnace.getField(4)) {
            icontainerlistener.sendWindowProperty(this, 4, this.tileFurnace.getField(4));
         }
      }

      this.cookTime = this.tileFurnace.getField(2);
      this.furnaceBurnTime = this.tileFurnace.getField(0);
      this.currentItemBurnTime = this.tileFurnace.getField(1);
      this.totalCookTime = this.tileFurnace.getField(3);
      this.heatLevel = this.tileFurnace.getField(4);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      this.tileFurnace.setField(id, data);
   }

   public boolean canInteractWith(EntityPlayer p_75145_1_) {
      return this.tileFurnace.isUsableByPlayer(p_75145_1_);
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
         } else if (p_82846_2_ != 1 && p_82846_2_ != 0) {
            if (this.canSmelt(itemstack1)) {
               if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
               if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
               if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
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

   private boolean canSmelt(ItemStack p_206253_1_) {
      for(IRecipe irecipe : this.world.getRecipeManager().getRecipes()) {
         if (irecipe instanceof FurnaceRecipe && irecipe.getIngredients().get(0).test(p_206253_1_)) {
            return true;
         }
      }

      return false;
   }
}
