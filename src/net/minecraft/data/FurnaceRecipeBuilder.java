package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;

   public FurnaceRecipeBuilder(Ingredient p_i48737_1_, IItemProvider p_i48737_2_, float p_i48737_3_, int p_i48737_4_) {
      this.result = p_i48737_2_.asItem();
      this.ingredient = p_i48737_1_;
      this.experience = p_i48737_3_;
      this.cookingTime = p_i48737_4_;
   }

   public static FurnaceRecipeBuilder furnaceRecipe(Ingredient p_202138_0_, IItemProvider p_202138_1_, float p_202138_2_, int p_202138_3_) {
      return new FurnaceRecipeBuilder(p_202138_0_, p_202138_1_, p_202138_2_, p_202138_3_);
   }

   public FurnaceRecipeBuilder addCriterion(String p_202139_1_, ICriterionInstance p_202139_2_) {
      this.advancementBuilder.withCriterion(p_202139_1_, p_202139_2_);
      return this;
   }

   public void build(Consumer<IFinishedRecipe> p_202140_1_) {
      this.build(p_202140_1_, IRegistry.field_212630_s.func_177774_c(this.result));
   }

   public void build(Consumer<IFinishedRecipe> p_202141_1_, String p_202141_2_) {
      ResourceLocation resourcelocation = IRegistry.field_212630_s.func_177774_c(this.result);
      if ((new ResourceLocation(p_202141_2_)).equals(resourcelocation)) {
         throw new IllegalStateException("Smelting Recipe " + p_202141_2_ + " should remove its 'save' argument");
      } else {
         this.build(p_202141_1_, new ResourceLocation(p_202141_2_));
      }
   }

   public void build(Consumer<IFinishedRecipe> p_202143_1_, ResourceLocation p_202143_2_) {
      this.validate(p_202143_2_);
      this.advancementBuilder.withParentId(new ResourceLocation("minecraft:recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(p_202143_2_)).withRewards(AdvancementRewards.Builder.recipe(p_202143_2_)).withRequirementsStrategy(RequirementsStrategy.OR);
      p_202143_1_.accept(new FurnaceRecipeBuilder.Result(p_202143_2_, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancementBuilder, new ResourceLocation(p_202143_2_.getNamespace(), "recipes/" + this.result.getGroup().func_200300_c() + "/" + p_202143_2_.getPath())));
   }

   private void validate(ResourceLocation p_202142_1_) {
      if (this.advancementBuilder.func_200277_c().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_202142_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_i48774_1_, String p_i48774_2_, Ingredient p_i48774_3_, Item p_i48774_4_, float p_i48774_5_, int p_i48774_6_, Advancement.Builder p_i48774_7_, ResourceLocation p_i48774_8_) {
         this.id = p_i48774_1_;
         this.group = p_i48774_2_;
         this.ingredient = p_i48774_3_;
         this.result = p_i48774_4_;
         this.experience = p_i48774_5_;
         this.cookingTime = p_i48774_6_;
         this.advancementBuilder = p_i48774_7_;
         this.advancementId = p_i48774_8_;
      }

      public JsonObject getRecipeJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("type", "smelting");
         if (!this.group.isEmpty()) {
            jsonobject.addProperty("group", this.group);
         }

         jsonobject.add("ingredient", this.ingredient.toJson());
         jsonobject.addProperty("result", IRegistry.field_212630_s.func_177774_c(this.result).toString());
         jsonobject.addProperty("experience", this.experience);
         jsonobject.addProperty("cookingtime", this.cookingTime);
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
