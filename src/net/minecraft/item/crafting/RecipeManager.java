package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int PATH_PREFIX_LENGTH = "recipes/".length();
   public static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Map<ResourceLocation, IRecipe> recipes = Maps.newHashMap();
   private boolean someRecipesErrored;

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      this.someRecipesErrored = false;
      this.recipes.clear();

      for(ResourceLocation resourcelocation : p_195410_1_.getAllResourceLocations("recipes", (p_199516_0_) -> p_199516_0_.endsWith(".json"))) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - PATH_SUFFIX_LENGTH));

         try (IResource iresource = p_195410_1_.getResource(resourcelocation)) {
            JsonObject jsonobject = JsonUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
            if (jsonobject == null) {
               LOGGER.error("Couldn't load recipe {} as it's null or empty", resourcelocation1);
            } else {
               this.addRecipe(RecipeSerializers.deserialize(resourcelocation1, jsonobject));
            }
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading recipe {}", resourcelocation1, jsonparseexception);
            this.someRecipesErrored = true;
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read custom advancement {} from {}", resourcelocation1, resourcelocation, ioexception);
            this.someRecipesErrored = true;
         }
      }

      LOGGER.info("Loaded {} recipes", this.recipes.size());
   }

   public void addRecipe(IRecipe p_199509_1_) {
      if (this.recipes.containsKey(p_199509_1_.getId())) {
         throw new IllegalStateException("Duplicate recipe ignored with ID " + p_199509_1_.getId());
      } else {
         this.recipes.put(p_199509_1_.getId(), p_199509_1_);
      }
   }

   public ItemStack getResult(IInventory p_199514_1_, World p_199514_2_) {
      for(IRecipe irecipe : this.recipes.values()) {
         if (irecipe.matches(p_199514_1_, p_199514_2_)) {
            return irecipe.getCraftingResult(p_199514_1_);
         }
      }

      return ItemStack.EMPTY;
   }

   @Nullable
   public IRecipe getRecipe(IInventory p_199515_1_, World p_199515_2_) {
      for(IRecipe irecipe : this.recipes.values()) {
         if (irecipe.matches(p_199515_1_, p_199515_2_)) {
            return irecipe;
         }
      }

      return null;
   }

   public NonNullList<ItemStack> getRemainingItems(IInventory p_199513_1_, World p_199513_2_) {
      for(IRecipe irecipe : this.recipes.values()) {
         if (irecipe.matches(p_199513_1_, p_199513_2_)) {
            return irecipe.getRemainingItems(p_199513_1_);
         }
      }

      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_199513_1_.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         nonnulllist.set(i, p_199513_1_.getStackInSlot(i));
      }

      return nonnulllist;
   }

   @Nullable
   public IRecipe getRecipe(ResourceLocation p_199517_1_) {
      return this.recipes.get(p_199517_1_);
   }

   public Collection<IRecipe> getRecipes() {
      return this.recipes.values();
   }

   public Collection<ResourceLocation> getIds() {
      return this.recipes.keySet();
   }

   @OnlyIn(Dist.CLIENT)
   public void clear() {
      this.recipes.clear();
   }
}
