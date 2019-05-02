package net.minecraft.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShapelessRecipe implements ITimedRecipe,ITieredRecipe {
   private final ResourceLocation id;
   private final String group;
   private final ItemStack recipeOutput;
   private final NonNullList<Ingredient> recipeItems;
   private final int craftingTime;
   private final int craftingTier;

   public ShapelessRecipe(ResourceLocation p_i48161_1_, String p_i48161_2_, ItemStack p_i48161_3_, NonNullList<Ingredient> p_i48161_4_,int craftingTime,int craftingTier) {
      this.id = p_i48161_1_;
      this.group = p_i48161_2_;
      this.recipeOutput = p_i48161_3_;
      this.recipeItems = p_i48161_4_;
      this.craftingTime = craftingTime;
      this.craftingTier = craftingTier;
   }

   @Override
   public int getCraftingTime(IInventory inventory) {
      return craftingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }
   
   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SHAPELESS;
   }

   @OnlyIn(Dist.CLIENT)
   public String getGroup() {
      return this.group;
   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   @Override
   public int getTier() {
      return craftingTier;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
         int i = 0;

         for(int j = 0; j < p_77569_1_.getHeight(); ++j) {
            for(int k = 0; k < p_77569_1_.getWidth(); ++k) {
               ItemStack itemstack = p_77569_1_.getStackInSlot(k + j * p_77569_1_.getWidth());
               if (!itemstack.isEmpty()) {
                  ++i;
                  recipeitemhelper.accountStack(new ItemStack(itemstack.getItem()));
               }
            }
         }

         return i == this.recipeItems.size() && recipeitemhelper.canCraft(this, null);
      }
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      return this.recipeOutput.copy();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= this.recipeItems.size();
   }

   public static class Serializer implements IRecipeSerializer<ShapelessRecipe> {
      public ShapelessRecipe read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         String s = JsonUtils.getString(p_199425_2_, "group", "");
         int craftingTime = JsonUtils.getInt(p_199425_2_,"craftTime");
         int craftingTier = JsonUtils.getInt(p_199425_2_,"craftTier");
         NonNullList<Ingredient> nonnulllist = readIngredients(JsonUtils.getJsonArray(p_199425_2_, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack itemstack = ShapedRecipe.deserializeItem(JsonUtils.getJsonObject(p_199425_2_, "result"));
            return new ShapelessRecipe(p_199425_1_, s, itemstack, nonnulllist,craftingTime,craftingTier);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for(int i = 0; i < p_199568_0_.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(p_199568_0_.get(i));
            if (!ingredient.hasNoMatchingItems()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public String getId() {
         return "crafting_shapeless";
      }

      public ShapelessRecipe read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         String s = p_199426_2_.readString(32767);
         int i = p_199426_2_.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for(int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.fromBuffer(p_199426_2_));
         }

         ItemStack itemstack = p_199426_2_.readItemStack();
         int craftingTime = p_199426_2_.readInt();
         int craftingTier = p_199426_2_.readInt();
         return new ShapelessRecipe(p_199426_1_, s, itemstack, nonnulllist,craftingTime,craftingTier);
      }

      public void write(PacketBuffer p_199427_1_, ShapelessRecipe p_199427_2_) {
         p_199427_1_.writeString(p_199427_2_.group);
         p_199427_1_.writeVarInt(p_199427_2_.recipeItems.size());

         for(Ingredient ingredient : p_199427_2_.recipeItems) {
            ingredient.writeToBuffer(p_199427_1_);
         }

         p_199427_1_.writeItemStack(p_199427_2_.recipeOutput);
         //MITEMODDED Add
         p_199427_1_.writeInt(p_199427_2_.craftingTime);
         p_199427_1_.writeInt(p_199427_2_.craftingTier);
      }
   }
}
