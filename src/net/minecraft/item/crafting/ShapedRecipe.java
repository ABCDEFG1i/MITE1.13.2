package net.minecraft.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//MITEMODDED
public class ShapedRecipe implements IRecipe,ITimedRecipe {
   private final int recipeWidth;
   private final int recipeHeight;
   private final NonNullList<Ingredient> recipeItems;
   private final ItemStack recipeOutput;
   private final ResourceLocation id;
   private final String group;
   private final int craftingTime;

   public ShapedRecipe(ResourceLocation p_i48162_1_, String p_i48162_2_, int p_i48162_3_, int p_i48162_4_, NonNullList<Ingredient> p_i48162_5_, ItemStack p_i48162_6_,int craftingTime) {
      this.id = p_i48162_1_;
      this.group = p_i48162_2_;
      this.craftingTime = craftingTime;
      this.recipeWidth = p_i48162_3_;
      this.recipeHeight = p_i48162_4_;
      this.recipeItems = p_i48162_5_;
      this.recipeOutput = p_i48162_6_;
   }

   @Override
   public int getCraftingTime() {
      return craftingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SHAPED;
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

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= this.recipeWidth && p_194133_2_ >= this.recipeHeight;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      if (!(p_77569_1_ instanceof InventoryCrafting)) {
         return false;
      } else {
         for(int i = 0; i <= p_77569_1_.getWidth() - this.recipeWidth; ++i) {
            for(int j = 0; j <= p_77569_1_.getHeight() - this.recipeHeight; ++j) {
               if (this.checkMatch(p_77569_1_, i, j, true)) {
                  return true;
               }

               if (this.checkMatch(p_77569_1_, i, j, false)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean checkMatch(IInventory p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
      for(int i = 0; i < p_77573_1_.getWidth(); ++i) {
         for(int j = 0; j < p_77573_1_.getHeight(); ++j) {
            int k = i - p_77573_2_;
            int l = j - p_77573_3_;
            Ingredient ingredient = Ingredient.EMPTY;
            if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
               if (p_77573_4_) {
                  ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
               } else {
                  ingredient = this.recipeItems.get(k + l * this.recipeWidth);
               }
            }

            if (!ingredient.test(p_77573_1_.getStackInSlot(i + j * p_77573_1_.getWidth()))) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      return this.getRecipeOutput().copy();
   }

   public int getWidth() {
      return this.recipeWidth;
   }

   public int getHeight() {
      return this.recipeHeight;
   }

   private static NonNullList<Ingredient> deserializeIngredients(String[] p_192402_0_, Map<String, Ingredient> p_192402_1_, int p_192402_2_, int p_192402_3_) {
      NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_192402_2_ * p_192402_3_, Ingredient.EMPTY);
      Set<String> set = Sets.newHashSet(p_192402_1_.keySet());
      set.remove(" ");

      for(int i = 0; i < p_192402_0_.length; ++i) {
         for(int j = 0; j < p_192402_0_[i].length(); ++j) {
            String s = p_192402_0_[i].substring(j, j + 1);
            Ingredient ingredient = p_192402_1_.get(s);
            if (ingredient == null) {
               throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
            }

            set.remove(s);
            nonnulllist.set(j + p_192402_2_ * i, ingredient);
         }
      }

      if (!set.isEmpty()) {
         throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
      } else {
         return nonnulllist;
      }
   }

   @VisibleForTesting
   static String[] shrink(String... p_194134_0_) {
      int i = Integer.MAX_VALUE;
      int j = 0;
      int k = 0;
      int l = 0;

      for(int i1 = 0; i1 < p_194134_0_.length; ++i1) {
         String s = p_194134_0_[i1];
         i = Math.min(i, firstNonSpace(s));
         int j1 = lastNonSpace(s);
         j = Math.max(j, j1);
         if (j1 < 0) {
            if (k == i1) {
               ++k;
            }

            ++l;
         } else {
            l = 0;
         }
      }

      if (p_194134_0_.length == l) {
         return new String[0];
      } else {
         String[] astring = new String[p_194134_0_.length - l - k];

         for(int k1 = 0; k1 < astring.length; ++k1) {
            astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
         }

         return astring;
      }
   }

   private static int firstNonSpace(String p_194135_0_) {
      int i;
      for(i = 0; i < p_194135_0_.length() && p_194135_0_.charAt(i) == ' '; ++i) {
         ;
      }

      return i;
   }

   private static int lastNonSpace(String p_194136_0_) {
      int i;
      for(i = p_194136_0_.length() - 1; i >= 0 && p_194136_0_.charAt(i) == ' '; --i) {
         ;
      }

      return i;
   }

   private static String[] patternFromJson(JsonArray p_192407_0_) {
      String[] astring = new String[p_192407_0_.size()];
      if (astring.length > 3) {
         throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
      } else if (astring.length == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         for(int i = 0; i < astring.length; ++i) {
            String s = JsonUtils.getString(p_192407_0_.get(i), "pattern[" + i + "]");
            if (s.length() > 3) {
               throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }

            if (i > 0 && astring[0].length() != s.length()) {
               throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            astring[i] = s;
         }

         return astring;
      }
   }

   private static Map<String, Ingredient> deserializeKey(JsonObject p_192408_0_) {
      Map<String, Ingredient> map = Maps.newHashMap();

      for(Entry<String, JsonElement> entry : p_192408_0_.entrySet()) {
         if (entry.getKey().length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
         }

         if (" ".equals(entry.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
         }

         map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
      }

      map.put(" ", Ingredient.EMPTY);
      return map;
   }

   public static ItemStack deserializeItem(JsonObject p_199798_0_) {
      String s = JsonUtils.getString(p_199798_0_, "item");
      Item item = IRegistry.field_212630_s.func_212608_b(new ResourceLocation(s));
      if (item == null) {
         throw new JsonSyntaxException("Unknown item '" + s + "'");
      } else if (p_199798_0_.has("data")) {
         throw new JsonParseException("Disallowed data tag found");
      } else {
         int i = JsonUtils.getInt(p_199798_0_, "count", 1);
         return new ItemStack(item, i);
      }
   }

   public static class Serializer implements IRecipeSerializer<ShapedRecipe> {
      public ShapedRecipe read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         String s = JsonUtils.getString(p_199425_2_, "group", "");
         Map<String, Ingredient> map = ShapedRecipe.deserializeKey(JsonUtils.getJsonObject(p_199425_2_, "key"));
         String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JsonUtils.getJsonArray(p_199425_2_, "pattern")));
         int craftingTime = JsonUtils.getInt(p_199425_2_,"craftTime");
         int i = astring[0].length();
         int j = astring.length;
         NonNullList<Ingredient> nonnulllist = ShapedRecipe.deserializeIngredients(astring, map, i, j);
         ItemStack itemstack = ShapedRecipe.deserializeItem(JsonUtils.getJsonObject(p_199425_2_, "result"));
         return new ShapedRecipe(p_199425_1_, s, i, j, nonnulllist, itemstack,craftingTime);
      }

      public String getId() {
         return "crafting_shaped";
      }

      public ShapedRecipe read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         int i = p_199426_2_.readVarInt();
         int j = p_199426_2_.readVarInt();
         String s = p_199426_2_.readString(32767);
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

         for(int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.fromBuffer(p_199426_2_));
         }

         int craftingTime = p_199426_2_.readInt();

         ItemStack itemstack = p_199426_2_.readItemStack();
         return new ShapedRecipe(p_199426_1_, s, i, j, nonnulllist, itemstack,craftingTime);
      }

      public void write(PacketBuffer p_199427_1_, ShapedRecipe p_199427_2_) {
         p_199427_1_.writeVarInt(p_199427_2_.recipeWidth);
         p_199427_1_.writeVarInt(p_199427_2_.recipeHeight);
         p_199427_1_.writeString(p_199427_2_.group);

         for(Ingredient ingredient : p_199427_2_.recipeItems) {
            ingredient.writeToBuffer(p_199427_1_);
         }
         //MITEMODDED Add
         p_199427_1_.writeInt(p_199427_2_.craftingTime);

         p_199427_1_.writeItemStack(p_199427_2_.recipeOutput);
      }
   }
}
