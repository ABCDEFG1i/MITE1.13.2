package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBoxColoringRecipe extends IRecipeHidden {
   public ShulkerBoxColoringRecipe(ResourceLocation p_i48159_1_) {
      super(p_i48159_1_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         int i = 0;
         int j = 0;

         for(int k = 0; k < p_77569_1_.getSizeInventory(); ++k) {
            ItemStack itemstack = p_77569_1_.getStackInSlot(k);
            if (!itemstack.isEmpty()) {
               if (Block.getBlockFromItem(itemstack.getItem()) instanceof BlockShulkerBox) {
                  ++i;
               } else {
                  if (!(itemstack.getItem() instanceof ItemDye)) {
                     return false;
                  }

                  ++j;
               }

               if (j > 1 || i > 1) {
                  return false;
               }
            }
         }

         return i == 1 && j == 1;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = ItemStack.EMPTY;
      ItemDye itemdye = (ItemDye)Items.BONE_MEAL;

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (Block.getBlockFromItem(item) instanceof BlockShulkerBox) {
               itemstack = itemstack1;
            } else if (item instanceof ItemDye) {
               itemdye = (ItemDye)item;
            }
         }
      }

      ItemStack itemstack2 = BlockShulkerBox.getColoredItemStack(itemdye.getDyeColor());
      if (itemstack.hasTag()) {
         itemstack2.setTag(itemstack.getTag().copy());
      }

      return itemstack2;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_SHULKERBOXCOLORING;
   }
}
