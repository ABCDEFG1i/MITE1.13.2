package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeList {
   private final List<IRecipe> recipes = Lists.newArrayList();
   private final Set<IRecipe> craftable = Sets.newHashSet();
   private final Set<IRecipe> canFit = Sets.newHashSet();
   private final Set<IRecipe> inBook = Sets.newHashSet();
   private boolean singleResultItem = true;

   public boolean isNotEmpty() {
      return !this.inBook.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook p_194214_1_) {
      for(IRecipe irecipe : this.recipes) {
         if (p_194214_1_.isUnlocked(irecipe)) {
            this.inBook.add(irecipe);
         }
      }

   }

   public void canCraft(RecipeItemHelper p_194210_1_, int p_194210_2_, int p_194210_3_, RecipeBook p_194210_4_) {
      for(int i = 0; i < this.recipes.size(); ++i) {
         IRecipe irecipe = this.recipes.get(i);
         boolean flag = irecipe.canFit(p_194210_2_, p_194210_3_) && p_194210_4_.isUnlocked(irecipe);
         if (flag) {
            this.canFit.add(irecipe);
         } else {
            this.canFit.remove(irecipe);
         }

         if (flag && p_194210_1_.canCraft(irecipe, (IntList)null)) {
            this.craftable.add(irecipe);
         } else {
            this.craftable.remove(irecipe);
         }
      }

   }

   public boolean isCraftable(IRecipe p_194213_1_) {
      return this.craftable.contains(p_194213_1_);
   }

   public boolean containsCraftableRecipes() {
      return !this.craftable.isEmpty();
   }

   public boolean containsValidRecipes() {
      return !this.canFit.isEmpty();
   }

   public List<IRecipe> getRecipes() {
      return this.recipes;
   }

   public List<IRecipe> getRecipes(boolean p_194208_1_) {
      List<IRecipe> list = Lists.newArrayList();
      Set<IRecipe> set = p_194208_1_ ? this.craftable : this.canFit;

      for(IRecipe irecipe : this.recipes) {
         if (set.contains(irecipe)) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public List<IRecipe> getDisplayRecipes(boolean p_194207_1_) {
      List<IRecipe> list = Lists.newArrayList();

      for(IRecipe irecipe : this.recipes) {
         if (this.canFit.contains(irecipe) && this.craftable.contains(irecipe) == p_194207_1_) {
            list.add(irecipe);
         }
      }

      return list;
   }

   public void add(IRecipe p_192709_1_) {
      this.recipes.add(p_192709_1_);
      if (this.singleResultItem) {
         ItemStack itemstack = this.recipes.get(0).getRecipeOutput();
         ItemStack itemstack1 = p_192709_1_.getRecipeOutput();
         this.singleResultItem = ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1);
      }

   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}
