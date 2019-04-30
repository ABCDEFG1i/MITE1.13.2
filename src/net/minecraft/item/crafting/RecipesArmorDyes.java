package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorDyeable;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipesArmorDyes extends IRecipeHidden implements ITimedRecipe{
   public RecipesArmorDyes(ResourceLocation p_i48173_1_) {
      super(p_i48173_1_);
   }

   @Override
   public int getCraftingTime(IInventory inventory) {
      return 2000;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;
         List<ItemStack> list = Lists.newArrayList();

         for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            ItemStack itemstack1 = p_77569_1_.getStackInSlot(i);
            if (!itemstack1.isEmpty()) {
               if (itemstack1.getItem() instanceof ItemArmorDyeable) {
                  if (!itemstack.isEmpty()) {
                     return false;
                  }

                  itemstack = itemstack1;
               } else {
                  if (!(itemstack1.getItem() instanceof ItemDye)) {
                     return false;
                  }

                  list.add(itemstack1);
               }
            }
         }

         return !itemstack.isEmpty() && !list.isEmpty();
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = ItemStack.EMPTY;
      int[] aint = new int[3];
      int i = 0;
      int j = 0;
      ItemArmorDyeable itemarmordyeable = null;

      for(int k = 0; k < p_77572_1_.getSizeInventory(); ++k) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(k);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (item instanceof ItemArmorDyeable) {
               itemarmordyeable = (ItemArmorDyeable)item;
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1.copy();
               itemstack.setCount(1);
               if (itemarmordyeable.hasColor(itemstack1)) {
                  int l = itemarmordyeable.getColor(itemstack);
                  float f = (float)(l >> 16 & 255) / 255.0F;
                  float f1 = (float)(l >> 8 & 255) / 255.0F;
                  float f2 = (float)(l & 255) / 255.0F;
                  i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                  aint[0] = (int)((float)aint[0] + f * 255.0F);
                  aint[1] = (int)((float)aint[1] + f1 * 255.0F);
                  aint[2] = (int)((float)aint[2] + f2 * 255.0F);
                  ++j;
               }
            } else {
               if (!(item instanceof ItemDye)) {
                  return ItemStack.EMPTY;
               }

               float[] afloat = ((ItemDye)item).getDyeColor().getColorComponentValues();
               int l1 = (int)(afloat[0] * 255.0F);
               int i2 = (int)(afloat[1] * 255.0F);
               int k2 = (int)(afloat[2] * 255.0F);
               i += Math.max(l1, Math.max(i2, k2));
               aint[0] += l1;
               aint[1] += i2;
               aint[2] += k2;
               ++j;
            }
         }
      }

      if (itemarmordyeable == null) {
         return ItemStack.EMPTY;
      } else {
         int i1 = aint[0] / j;
         int j1 = aint[1] / j;
         int k1 = aint[2] / j;
         float f3 = (float)i / (float)j;
         float f4 = (float)Math.max(i1, Math.max(j1, k1));
         i1 = (int)((float)i1 * f3 / f4);
         j1 = (int)((float)j1 * f3 / f4);
         k1 = (int)((float)k1 * f3 / f4);
         int j2 = (i1 << 8) + j1;
         j2 = (j2 << 8) + k1;
         itemarmordyeable.setColor(itemstack, j2);
         return itemstack;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_ARMORDYE;
   }
}
