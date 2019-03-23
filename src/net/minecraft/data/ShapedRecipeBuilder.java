package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<String> pattern = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;

   public ShapedRecipeBuilder(IItemProvider p_i48261_1_, int p_i48261_2_) {
      this.result = p_i48261_1_.asItem();
      this.count = p_i48261_2_;
   }

   public static ShapedRecipeBuilder shapedRecipe(IItemProvider p_200470_0_) {
      return shapedRecipe(p_200470_0_, 1);
   }

   public static ShapedRecipeBuilder shapedRecipe(IItemProvider p_200468_0_, int p_200468_1_) {
      return new ShapedRecipeBuilder(p_200468_0_, p_200468_1_);
   }

   public ShapedRecipeBuilder key(Character p_200469_1_, Tag<Item> p_200469_2_) {
      return this.key(p_200469_1_, Ingredient.fromTag(p_200469_2_));
   }

   public ShapedRecipeBuilder key(Character p_200462_1_, IItemProvider p_200462_2_) {
      return this.key(p_200462_1_, Ingredient.fromItems(p_200462_2_));
   }

   public ShapedRecipeBuilder key(Character p_200471_1_, Ingredient p_200471_2_) {
      if (this.key.containsKey(p_200471_1_)) {
         throw new IllegalArgumentException("Symbol '" + p_200471_1_ + "' is already defined!");
      } else if (p_200471_1_ == ' ') {
         throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
      } else {
         this.key.put(p_200471_1_, p_200471_2_);
         return this;
      }
   }

   public ShapedRecipeBuilder patternLine(String p_200472_1_) {
      if (!this.pattern.isEmpty() && p_200472_1_.length() != this.pattern.get(0).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.pattern.add(p_200472_1_);
         return this;
      }
   }

   public ShapedRecipeBuilder addCriterion(String p_200465_1_, ICriterionInstance p_200465_2_) {
      this.advancementBuilder.withCriterion(p_200465_1_, p_200465_2_);
      return this;
   }

   public ShapedRecipeBuilder setGroup(String p_200473_1_) {
      this.group = p_200473_1_;
      return this;
   }

   public void build(Consumer<IFinishedRecipe> p_200464_1_) {
      this.build(p_200464_1_, IRegistry.field_212630_s.func_177774_c(this.result));
   }

   public void build(Consumer<IFinishedRecipe> p_200466_1_, String p_200466_2_) {
      ResourceLocation resourcelocation = IRegistry.field_212630_s.func_177774_c(this.result);
      if ((new ResourceLocation(p_200466_2_)).equals(resourcelocation)) {
         throw new IllegalStateException("Shaped Recipe " + p_200466_2_ + " should remove its 'save' argument");
      } else {
         this.build(p_200466_1_, new ResourceLocation(p_200466_2_));
      }
   }

   public void build(Consumer<IFinishedRecipe> p_200467_1_, ResourceLocation p_200467_2_) {
      this.validate(p_200467_2_);
      this.advancementBuilder.withParentId(new ResourceLocation("minecraft:recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(p_200467_2_)).withRewards(AdvancementRewards.Builder.recipe(p_200467_2_)).withRequirementsStrategy(RequirementsStrategy.OR);
      p_200467_1_.accept(new ShapedRecipeBuilder.Result(p_200467_2_, this.result, this.count, this.group == null ? "" : this.group, this.pattern, this.key, this.advancementBuilder, new ResourceLocation(p_200467_2_.getNamespace(), "recipes/" + this.result.getGroup().func_200300_c() + "/" + p_200467_2_.getPath())));
   }

   private void validate(ResourceLocation p_200463_1_) {
      if (this.pattern.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + p_200463_1_ + "!");
      } else {
         Set<Character> set = Sets.newHashSet(this.key.keySet());
         set.remove(' ');

         for(String s : this.pattern) {
            for(int i = 0; i < s.length(); ++i) {
               char c0 = s.charAt(i);
               if (!this.key.containsKey(c0) && c0 != ' ') {
                  throw new IllegalStateException("Pattern in recipe " + p_200463_1_ + " uses undefined symbol '" + c0 + "'");
               }

               set.remove(c0);
            }
         }

         if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + p_200463_1_);
         } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + p_200463_1_ + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if (this.advancementBuilder.func_200277_c().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_200463_1_);
         }
      }
   }

   class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_i48271_2_, Item p_i48271_3_, int p_i48271_4_, String p_i48271_5_, List<String> p_i48271_6_, Map<Character, Ingredient> p_i48271_7_, Advancement.Builder p_i48271_8_, ResourceLocation p_i48271_9_) {
         this.id = p_i48271_2_;
         this.result = p_i48271_3_;
         this.count = p_i48271_4_;
         this.group = p_i48271_5_;
         this.pattern = p_i48271_6_;
         this.key = p_i48271_7_;
         this.advancementBuilder = p_i48271_8_;
         this.advancementId = p_i48271_9_;
      }

      public JsonObject getRecipeJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("type", "crafting_shaped");
         if (!this.group.isEmpty()) {
            jsonobject.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(String s : this.pattern) {
            jsonarray.add(s);
         }

         jsonobject.add("pattern", jsonarray);
         JsonObject jsonobject1 = new JsonObject();

         for(Entry<Character, Ingredient> entry : this.key.entrySet()) {
            jsonobject1.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
         }

         jsonobject.add("key", jsonobject1);
         JsonObject jsonobject2 = new JsonObject();
         jsonobject2.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.result).toString());
         if (this.count > 1) {
            jsonobject2.addProperty("count", this.count);
         }

         jsonobject.add("result", jsonobject2);
         return jsonobject;
      }

      public ResourceLocation getID() {
         return this.id;
      }

      @Nullable
      public JsonObject getAdvancementJson() {
         return this.advancementBuilder.serialize();
      }

      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.advancementId;
      }
   }
}
