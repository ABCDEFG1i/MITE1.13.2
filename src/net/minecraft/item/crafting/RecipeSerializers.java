package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeSerializers {
   private static final Map<String, IRecipeSerializer<?>> REGISTRY = Maps.newHashMap();
   public static final IRecipeSerializer<ShapedRecipe> CRAFTING_SHAPED = register(new ShapedRecipe.Serializer());
   public static final IRecipeSerializer<ShapelessRecipe> CRAFTING_SHAPELESS = register(new ShapelessRecipe.Serializer());
   public static final RecipeSerializers.SimpleSerializer<RecipesArmorDyes> CRAFTING_SPECIAL_ARMORDYE = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_armordye", RecipesArmorDyes::new));
   public static final RecipeSerializers.SimpleSerializer<RecipeBookCloning> CRAFTING_SPECIAL_BOOKCLONING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_bookcloning", RecipeBookCloning::new));
   public static final RecipeSerializers.SimpleSerializer<RecipesMapCloning> CRAFTING_SPECIAL_MAPCLONING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_mapcloning", RecipesMapCloning::new));
   public static final RecipeSerializers.SimpleSerializer<RecipesMapExtending> CRAFTING_SPECIAL_MAPEXTENDING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_mapextending", RecipesMapExtending::new));
   public static final RecipeSerializers.SimpleSerializer<FireworkRocketRecipe> CRAFTING_SPECIAL_FIREWORK_ROCKET = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_firework_rocket", FireworkRocketRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<FireworkStarRecipe> CRAFTING_SPECIAL_FIREWORK_STAR = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_firework_star", FireworkStarRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<FireworkStarFadeRecipe> CRAFTING_SPECIAL_FIREWORK_STAR_FADE = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_firework_star_fade", FireworkStarFadeRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<RecipeRepairItem> CRAFTING_SPECIAL_REPAIRITEM = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_repairitem", RecipeRepairItem::new));
   public static final RecipeSerializers.SimpleSerializer<RecipeTippedArrow> CRAFTING_SPECIAL_TIPPEDARROW = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_tippedarrow", RecipeTippedArrow::new));
   public static final RecipeSerializers.SimpleSerializer<BannerDuplicateRecipe> CRAFTING_SPECIAL_BANNERDUPLICATE = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_bannerduplicate", BannerDuplicateRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<BannerAddPatternRecipe> CRAFTING_SPECIAL_BANNERADDPATTERN = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_banneraddpattern", BannerAddPatternRecipe::new));
   public static final RecipeSerializers.SimpleSerializer<ShieldRecipes> CRAFTING_SPECIAL_SHIELDDECORATION = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_shielddecoration", ShieldRecipes::new));
   public static final RecipeSerializers.SimpleSerializer<ShulkerBoxColoringRecipe> CRAFTING_SPECIAL_SHULKERBOXCOLORING = register(new RecipeSerializers.SimpleSerializer<>("crafting_special_shulkerboxcoloring", ShulkerBoxColoringRecipe::new));
   public static final IRecipeSerializer<FurnaceRecipe> SMELTING = register(new FurnaceRecipe.Serializer());

   public static <S extends IRecipeSerializer<T>, T extends IRecipe> S register(S p_199573_0_) {
      if (REGISTRY.containsKey(p_199573_0_.getId())) {
         throw new IllegalArgumentException("Duplicate recipe serializer " + p_199573_0_.getId());
      } else {
         REGISTRY.put(p_199573_0_.getId(), p_199573_0_);
         return p_199573_0_;
      }
   }

   public static IRecipe deserialize(ResourceLocation p_199572_0_, JsonObject p_199572_1_) {
      String s = JsonUtils.getString(p_199572_1_, "type");
      IRecipeSerializer<?> irecipeserializer = REGISTRY.get(s);
      if (irecipeserializer == null) {
         throw new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
      } else {
         return irecipeserializer.read(p_199572_0_, p_199572_1_);
      }
   }

   public static IRecipe read(PacketBuffer p_199571_0_) {
      ResourceLocation resourcelocation = p_199571_0_.readResourceLocation();
      String s = p_199571_0_.readString(32767);
      IRecipeSerializer<?> irecipeserializer = REGISTRY.get(s);
      if (irecipeserializer == null) {
         throw new IllegalArgumentException("Unknown recipe serializer " + s);
      } else {
         return irecipeserializer.read(resourcelocation, p_199571_0_);
      }
   }

   public static <T extends IRecipe> void write(T p_199574_0_, PacketBuffer p_199574_1_) {
      p_199574_1_.writeResourceLocation(p_199574_0_.getId());
      p_199574_1_.writeString(p_199574_0_.getSerializer().getId());
      IRecipeSerializer<T> irecipeserializer = (IRecipeSerializer<T>)p_199574_0_.getSerializer();
      irecipeserializer.write(p_199574_1_, p_199574_0_);
   }

   public static final class SimpleSerializer<T extends IRecipe> implements IRecipeSerializer<T> {
      private final String id;
      private final Function<ResourceLocation, T> function;

      public SimpleSerializer(String p_i48188_1_, Function<ResourceLocation, T> p_i48188_2_) {
         this.id = p_i48188_1_;
         this.function = p_i48188_2_;
      }

      public T read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         return (T)(this.function.apply(p_199425_1_));
      }

      public T read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         return (T)(this.function.apply(p_199426_1_));
      }

      public void write(PacketBuffer p_199427_1_, T p_199427_2_) {
      }

      public String getId() {
         return this.id;
      }
   }
}
