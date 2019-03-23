package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
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

public class FireworkStarFadeRecipe extends IRecipeHidden {
   private static final Ingredient INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);

   public FireworkStarFadeRecipe(ResourceLocation p_i48167_1_) {
      super(p_i48167_1_);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean flag = false;
         boolean flag1 = false;

         for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
            ItemStack itemstack = p_77569_1_.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               if (itemstack.getItem() instanceof ItemDye) {
                  flag = true;
               } else {
                  if (!INGREDIENT_FIREWORK_STAR.test(itemstack)) {
                     return false;
                  }

                  if (flag1) {
                     return false;
                  }

                  flag1 = true;
               }
            }
         }

         return flag1 && flag;
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      List<Integer> list = Lists.newArrayList();
      ItemStack itemstack = null;

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         Item item = itemstack1.getItem();
         if (item instanceof ItemDye) {
            list.add(((ItemDye)item).getDyeColor().func_196060_f());
         } else if (INGREDIENT_FIREWORK_STAR.test(itemstack1)) {
            itemstack = itemstack1.copy();
            itemstack.setCount(1);
         }
      }

      if (itemstack != null && !list.isEmpty()) {
         itemstack.getOrCreateChildTag("Explosion").setIntArray("FadeColors", list);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_FIREWORK_STAR_FADE;
   }
}
