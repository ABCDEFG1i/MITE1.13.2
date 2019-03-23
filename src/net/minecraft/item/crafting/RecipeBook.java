package net.minecraft.item.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeBook {
   protected final Set<ResourceLocation> recipes = Sets.newHashSet();
   protected final Set<ResourceLocation> newRecipes = Sets.newHashSet();
   protected boolean isGuiOpen;
   protected boolean isFilteringCraftable;
   protected boolean isFurnaceGuiOpen;
   protected boolean isFurnaceFilteringCraftable;

   public void copyFrom(RecipeBook p_193824_1_) {
      this.recipes.clear();
      this.newRecipes.clear();
      this.recipes.addAll(p_193824_1_.recipes);
      this.newRecipes.addAll(p_193824_1_.newRecipes);
   }

   public void unlock(IRecipe p_194073_1_) {
      if (!p_194073_1_.isDynamic()) {
         this.unlock(p_194073_1_.getId());
      }

   }

   protected void unlock(ResourceLocation p_209118_1_) {
      this.recipes.add(p_209118_1_);
   }

   public boolean isUnlocked(@Nullable IRecipe p_193830_1_) {
      return p_193830_1_ == null ? false : this.recipes.contains(p_193830_1_.getId());
   }

   @OnlyIn(Dist.CLIENT)
   public void lock(IRecipe p_193831_1_) {
      this.lock(p_193831_1_.getId());
   }

   protected void lock(ResourceLocation p_209119_1_) {
      this.recipes.remove(p_209119_1_);
      this.newRecipes.remove(p_209119_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNew(IRecipe p_194076_1_) {
      return this.newRecipes.contains(p_194076_1_.getId());
   }

   public void markSeen(IRecipe p_194074_1_) {
      this.newRecipes.remove(p_194074_1_.getId());
   }

   public void markNew(IRecipe p_193825_1_) {
      this.markNew(p_193825_1_.getId());
   }

   protected void markNew(ResourceLocation p_209120_1_) {
      this.newRecipes.add(p_209120_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public void setGuiOpen(boolean p_192813_1_) {
      this.isGuiOpen = p_192813_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_203432_a(ContainerRecipeBook p_203432_1_) {
      return p_203432_1_ instanceof ContainerFurnace ? this.isFurnaceFilteringCraftable : this.isFilteringCraftable;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFilteringCraftable() {
      return this.isFilteringCraftable;
   }

   public void setFilteringCraftable(boolean p_192810_1_) {
      this.isFilteringCraftable = p_192810_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202883_c() {
      return this.isFurnaceGuiOpen;
   }

   public void setFurnaceGuiOpen(boolean p_202881_1_) {
      this.isFurnaceGuiOpen = p_202881_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202884_d() {
      return this.isFurnaceFilteringCraftable;
   }

   public void setFurnaceFilteringCraftable(boolean p_202882_1_) {
      this.isFurnaceFilteringCraftable = p_202882_1_;
   }
}
