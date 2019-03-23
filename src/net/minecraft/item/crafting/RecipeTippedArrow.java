package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeTippedArrow extends IRecipeHidden {
   public RecipeTippedArrow(ResourceLocation p_i48184_1_) {
      super(p_i48184_1_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (p_77569_1_.getWidth() == 3 && p_77569_1_.getHeight() == 3) {
         for(int i = 0; i < p_77569_1_.getWidth(); ++i) {
            for(int j = 0; j < p_77569_1_.getHeight(); ++j) {
               ItemStack itemstack = p_77569_1_.getStackInSlot(i + j * p_77569_1_.getWidth());
               if (itemstack.isEmpty()) {
                  return false;
               }

               Item item = itemstack.getItem();
               if (i == 1 && j == 1) {
                  if (item != Items.LINGERING_POTION) {
                     return false;
                  }
               } else if (item != Items.ARROW) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = p_77572_1_.getStackInSlot(1 + p_77572_1_.getWidth());
      if (itemstack.getItem() != Items.LINGERING_POTION) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);
         PotionUtils.addPotionToItemStack(itemstack1, PotionUtils.getPotionFromItem(itemstack));
         PotionUtils.appendEffects(itemstack1, PotionUtils.getFullEffectsFromItem(itemstack));
         return itemstack1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= 2 && p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_TIPPEDARROW;
   }
}
