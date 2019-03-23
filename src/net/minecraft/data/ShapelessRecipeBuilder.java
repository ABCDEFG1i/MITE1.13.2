package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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

public class ShapelessRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;

   public ShapelessRecipeBuilder(IItemProvider p_i48260_1_, int p_i48260_2_) {
      this.result = p_i48260_1_.asItem();
      this.count = p_i48260_2_;
   }

   public static ShapelessRecipeBuilder shapelessRecipe(IItemProvider p_200486_0_) {
      return new ShapelessRecipeBuilder(p_200486_0_, 1);
   }

   public static ShapelessRecipeBuilder shapelessRecipe(IItemProvider p_200488_0_, int p_200488_1_) {
      return new ShapelessRecipeBuilder(p_200488_0_, p_200488_1_);
   }

   public ShapelessRecipeBuilder addIngredient(Tag<Item> p_203221_1_) {
      return this.addIngredient(Ingredient.fromTag(p_203221_1_));
   }

   public ShapelessRecipeBuilder addIngredient(IItemProvider p_200487_1_) {
      return this.addIngredient(p_200487_1_, 1);
   }

   public ShapelessRecipeBuilder addIngredient(IItemProvider p_200491_1_, int p_200491_2_) {
      for(int i = 0; i < p_200491_2_; ++i) {
         this.addIngredient(Ingredient.fromItems(p_200491_1_));
      }

      return this;
   }

   public ShapelessRecipeBuilder addIngredient(Ingredient p_200489_1_) {
      return this.addIngredient(p_200489_1_, 1);
   }

   public ShapelessRecipeBuilder addIngredient(Ingredient p_200492_1_, int p_200492_2_) {
      for(int i = 0; i < p_200492_2_; ++i) {
         this.ingredients.add(p_200492_1_);
      }

      return this;
   }

   public ShapelessRecipeBuilder addCriterion(String p_200483_1_, ICriterionInstance p_200483_2_) {
      this.advancementBuilder.withCriterion(p_200483_1_, p_200483_2_);
      return this;
   }

   public ShapelessRecipeBuilder setGroup(String p_200490_1_) {
      this.group = p_200490_1_;
      return this;
   }

   public void build(Consumer<IFinishedRecipe> p_200482_1_) {
      this.build(p_200482_1_, IRegistry.field_212630_s.func_177774_c(this.result));
   }

   public void build(Consumer<IFinishedRecipe> p_200484_1_, String p_200484_2_) {
      ResourceLocation resourcelocation = IRegistry.field_212630_s.func_177774_c(this.result);
      if ((new ResourceLocation(p_200484_2_)).equals(resourcelocation)) {
         throw new IllegalStateException("Shapeless Recipe " + p_200484_2_ + " should remove its 'save' argument");
      } else {
         this.build(p_200484_1_, new ResourceLocation(p_200484_2_));
      }
   }

   public void build(Consumer<IFinishedRecipe> p_200485_1_, ResourceLocation p_200485_2_) {
      this.validate(p_200485_2_);
      this.advancementBuilder.withParentId(new ResourceLocation("minecraft:recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(p_200485_2_)).withRewards(AdvancementRewards.Builder.recipe(p_200485_2_)).withRequirementsStrategy(RequirementsStrategy.OR);
      p_200485_1_.accept(new ShapelessRecipeBuilder.Result(p_200485_2_, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(p_200485_2_.getNamespace(), "recipes/" + this.result.getGroup().func_200300_c() + "/" + p_200485_2_.getPath())));
   }

   private void validate(ResourceLocation p_200481_1_) {
      if (this.advancementBuilder.func_200277_c().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_200481_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_i48268_1_, Item p_i48268_2_, int p_i48268_3_, String p_i48268_4_, List<Ingredient> p_i48268_5_, Advancement.Builder p_i48268_6_, ResourceLocation p_i48268_7_) {
         this.id = p_i48268_1_;
         this.result = p_i48268_2_;
         this.count = p_i48268_3_;
         this.group = p_i48268_4_;
         this.ingredients = p_i48268_5_;
         this.advancementBuilder = p_i48268_6_;
         this.advancementId = p_i48268_7_;
      }

      public JsonObject getRecipeJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("type", "crafting_shapeless");
         if (!this.group.isEmpty()) {
            jsonobject.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();

         for(Ingredient ingredient : this.ingredients) {
            jsonarray.add(ingredient.toJson());
         }

         jsonobject.add("ingredients", jsonarray);
         JsonObject jsonobject1 = new JsonObject();
         jsonobject1.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.result).toString());
         if (this.count > 1) {
            jsonobject1.addProperty("count", this.count);
         }

         jsonobject.add("result", jsonobject1);
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
