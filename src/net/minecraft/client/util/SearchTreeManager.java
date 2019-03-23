package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTreeManager implements IResourceManagerReloadListener {
   public static final SearchTreeManager.Key<ItemStack> ITEMS = new SearchTreeManager.Key<>();
   public static final SearchTreeManager.Key<RecipeList> RECIPES = new SearchTreeManager.Key<>();
   private final Map<SearchTreeManager.Key<?>, SearchTree<?>> trees = Maps.newHashMap();

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      for(SearchTree<?> searchtree : this.trees.values()) {
         searchtree.recalculate();
      }

   }

   public <T> void register(SearchTreeManager.Key<T> p_194009_1_, SearchTree<T> p_194009_2_) {
      this.trees.put(p_194009_1_, p_194009_2_);
   }

   public <T> ISearchTree<T> get(SearchTreeManager.Key<T> p_194010_1_) {
      return (ISearchTree<T>) this.trees.get(p_194010_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Key<T> {
   }
}
