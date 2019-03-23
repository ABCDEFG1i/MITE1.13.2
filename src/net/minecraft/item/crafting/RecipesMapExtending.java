package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class RecipesMapExtending extends ShapedRecipe {
   public RecipesMapExtending(ResourceLocation p_i48164_1_) {
      super(p_i48164_1_, "", 3, 3, NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.FILLED_MAP), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER)), new ItemStack(Items.MAP),2000);
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!super.matches(p_77569_1_, p_77569_2_)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;

         for(int i = 0; i < p_77569_1_.getSizeInventory() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = p_77569_1_.getStackInSlot(i);
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               itemstack = itemstack1;
            }
         }

         if (itemstack.isEmpty()) {
            return false;
         } else {
            MapData mapdata = ItemMap.getMapData(itemstack, p_77569_2_);
            if (mapdata == null) {
               return false;
            } else if (this.isExplorationMap(mapdata)) {
               return false;
            } else {
               return mapdata.scale < 4;
            }
         }
      }
   }

   private boolean isExplorationMap(MapData p_190934_1_) {
      if (p_190934_1_.mapDecorations != null) {
         for(MapDecoration mapdecoration : p_190934_1_.mapDecorations.values()) {
            if (mapdecoration.getType() == MapDecoration.Type.MANSION || mapdecoration.getType() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < p_77572_1_.getSizeInventory() && itemstack.isEmpty(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         if (itemstack1.getItem() == Items.FILLED_MAP) {
            itemstack = itemstack1;
         }
      }

      itemstack = itemstack.copy();
      itemstack.setCount(1);
      itemstack.getOrCreateTag().setInteger("map_scale_direction", 1);
      return itemstack;
   }

   public boolean isDynamic() {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_MAPEXTENDING;
   }
}
