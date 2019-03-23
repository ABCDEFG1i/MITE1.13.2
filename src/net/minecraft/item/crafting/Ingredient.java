package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Ingredient implements Predicate<ItemStack> {
   private static final Predicate<? super Ingredient.IItemList> IS_EMPTY = (p_209361_0_) -> {
      return !p_209361_0_.getStacks().stream().allMatch(ItemStack::isEmpty);
   };
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.IItemList[] acceptedItems;
   private ItemStack[] matchingStacks;
   private IntList matchingStacksPacked;

   protected Ingredient(Stream<? extends Ingredient.IItemList> p_i49381_1_) {
      this.acceptedItems = p_i49381_1_.filter(IS_EMPTY).toArray((p_209360_0_) -> {
         return new Ingredient.IItemList[p_209360_0_];
      });
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack[] getMatchingStacks() {
      this.determineMatchingStacks();
      return this.matchingStacks;
   }

   private void determineMatchingStacks() {
      if (this.matchingStacks == null) {
         this.matchingStacks = Arrays.stream(this.acceptedItems).flatMap((p_209359_0_) -> {
            return p_209359_0_.getStacks().stream();
         }).distinct().toArray((p_209358_0_) -> {
            return new ItemStack[p_209358_0_];
         });
      }

   }

   public boolean test(@Nullable ItemStack p_test_1_) {
      if (p_test_1_ == null) {
         return false;
      } else if (this.acceptedItems.length == 0) {
         return p_test_1_.isEmpty();
      } else {
         this.determineMatchingStacks();

         for(ItemStack itemstack : this.matchingStacks) {
            if (itemstack.getItem() == p_test_1_.getItem()) {
               return true;
            }
         }

         return false;
      }
   }

   public IntList getValidItemStacksPacked() {
      if (this.matchingStacksPacked == null) {
         this.determineMatchingStacks();
         this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);

         for(ItemStack itemstack : this.matchingStacks) {
            this.matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
         }

         this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.matchingStacksPacked;
   }

   public final void writeToBuffer(PacketBuffer p_199564_1_) {
      this.determineMatchingStacks();
      p_199564_1_.writeVarInt(this.matchingStacks.length);

      for(int i = 0; i < this.matchingStacks.length; ++i) {
         p_199564_1_.writeItemStack(this.matchingStacks[i]);
      }

   }

   public JsonElement toJson() {
      if (this.acceptedItems.length == 1) {
         return this.acceptedItems[0].toJson();
      } else {
         JsonArray jsonarray = new JsonArray();

         for(Ingredient.IItemList ingredient$iitemlist : this.acceptedItems) {
            jsonarray.add(ingredient$iitemlist.toJson());
         }

         return jsonarray;
      }
   }

   public boolean hasNoMatchingItems() {
      return this.acceptedItems.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.matchingStacksPacked == null || this.matchingStacksPacked.isEmpty());
   }

   public static Ingredient fromItemListStream(Stream<? extends Ingredient.IItemList> p_209357_0_) {
      Ingredient ingredient = new Ingredient(p_209357_0_);
      return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
   }

   public static Ingredient fromItems(IItemProvider... p_199804_0_) {
      return fromItemListStream(Arrays.stream(p_199804_0_).map((p_209353_0_) -> {
         return new Ingredient.SingleItemList(new ItemStack(p_209353_0_));
      }));
   }

   @OnlyIn(Dist.CLIENT)
   public static Ingredient fromStacks(ItemStack... p_193369_0_) {
      return fromItemListStream(Arrays.stream(p_193369_0_).map((p_209356_0_) -> {
         return new Ingredient.SingleItemList(p_209356_0_);
      }));
   }

   public static Ingredient fromTag(Tag<Item> p_199805_0_) {
      return fromItemListStream(Stream.of(new Ingredient.TagList(p_199805_0_)));
   }

   public static Ingredient fromBuffer(PacketBuffer p_199566_0_) {
      int i = p_199566_0_.readVarInt();
      return fromItemListStream(Stream.generate(() -> {
         return new Ingredient.SingleItemList(p_199566_0_.readItemStack());
      }).limit((long)i));
   }

   public static Ingredient fromJson(@Nullable JsonElement p_199802_0_) {
      if (p_199802_0_ != null && !p_199802_0_.isJsonNull()) {
         if (p_199802_0_.isJsonObject()) {
            return fromItemListStream(Stream.of(deserializeItemList(p_199802_0_.getAsJsonObject())));
         } else if (p_199802_0_.isJsonArray()) {
            JsonArray jsonarray = p_199802_0_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromItemListStream(StreamSupport.stream(jsonarray.spliterator(), false).map((p_209355_0_) -> {
                  return deserializeItemList(JsonUtils.getJsonObject(p_209355_0_, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.IItemList deserializeItemList(JsonObject p_199803_0_) {
      if (p_199803_0_.has("item") && p_199803_0_.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if (p_199803_0_.has("item")) {
         ResourceLocation resourcelocation1 = new ResourceLocation(JsonUtils.getString(p_199803_0_, "item"));
         Item item = IRegistry.field_212630_s.func_212608_b(resourcelocation1);
         if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + resourcelocation1 + "'");
         } else {
            return new Ingredient.SingleItemList(new ItemStack(item));
         }
      } else if (p_199803_0_.has("tag")) {
         ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(p_199803_0_, "tag"));
         Tag<Item> tag = ItemTags.getCollection().get(resourcelocation);
         if (tag == null) {
            throw new JsonSyntaxException("Unknown item tag '" + resourcelocation + "'");
         } else {
            return new Ingredient.TagList(tag);
         }
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   public interface IItemList {
      Collection<ItemStack> getStacks();

      JsonObject toJson();
   }

   public static class SingleItemList implements Ingredient.IItemList {
      private final ItemStack stack;

      public SingleItemList(ItemStack p_i48195_1_) {
         this.stack = p_i48195_1_;
      }

      public Collection<ItemStack> getStacks() {
         return Collections.singleton(this.stack);
      }

      public JsonObject toJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.stack.getItem()).toString());
         return jsonobject;
      }
   }

   public static class TagList implements Ingredient.IItemList {
      private final Tag<Item> tag;

      public TagList(Tag<Item> p_i48193_1_) {
         this.tag = p_i48193_1_;
      }

      public Collection<ItemStack> getStacks() {
         List<ItemStack> list = Lists.newArrayList();

         for(Item item : this.tag.getAllElements()) {
            list.add(new ItemStack(item));
         }

         return list;
      }

      public JsonObject toJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("tag", this.tag.getId().toString());
         return jsonobject;
      }
   }
}
