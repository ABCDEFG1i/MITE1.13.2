package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JsonUtils {
   private static final Gson field_212747_a = (new GsonBuilder()).create();

   public static boolean isString(JsonObject p_151205_0_, String p_151205_1_) {
      return !isJsonPrimitive(p_151205_0_, p_151205_1_) ? false : p_151205_0_.getAsJsonPrimitive(p_151205_1_).isString();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isString(JsonElement p_151211_0_) {
      return !p_151211_0_.isJsonPrimitive() ? false : p_151211_0_.getAsJsonPrimitive().isString();
   }

   public static boolean isNumber(JsonElement p_188175_0_) {
      return !p_188175_0_.isJsonPrimitive() ? false : p_188175_0_.getAsJsonPrimitive().isNumber();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isBoolean(JsonObject p_180199_0_, String p_180199_1_) {
      return !isJsonPrimitive(p_180199_0_, p_180199_1_) ? false : p_180199_0_.getAsJsonPrimitive(p_180199_1_).isBoolean();
   }

   public static boolean isJsonArray(JsonObject p_151202_0_, String p_151202_1_) {
      return !hasField(p_151202_0_, p_151202_1_) ? false : p_151202_0_.get(p_151202_1_).isJsonArray();
   }

   public static boolean isJsonPrimitive(JsonObject p_151201_0_, String p_151201_1_) {
      return !hasField(p_151201_0_, p_151201_1_) ? false : p_151201_0_.get(p_151201_1_).isJsonPrimitive();
   }

   public static boolean hasField(JsonObject p_151204_0_, String p_151204_1_) {
      if (p_151204_0_ == null) {
         return false;
      } else {
         return p_151204_0_.get(p_151204_1_) != null;
      }
   }

   public static String getString(JsonElement p_151206_0_, String p_151206_1_) {
      if (p_151206_0_.isJsonPrimitive()) {
         return p_151206_0_.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + p_151206_1_ + " to be a string, was " + toString(p_151206_0_));
      }
   }

   public static String getString(JsonObject p_151200_0_, String p_151200_1_) {
      if (p_151200_0_.has(p_151200_1_)) {
         return getString(p_151200_0_.get(p_151200_1_), p_151200_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151200_1_ + ", expected to find a string");
      }
   }

   public static String getString(JsonObject p_151219_0_, String p_151219_1_, String p_151219_2_) {
      return p_151219_0_.has(p_151219_1_) ? getString(p_151219_0_.get(p_151219_1_), p_151219_1_) : p_151219_2_;
   }

   public static Item getItem(JsonElement p_188172_0_, String p_188172_1_) {
      if (p_188172_0_.isJsonPrimitive()) {
         String s = p_188172_0_.getAsString();
         Item item = IRegistry.field_212630_s.func_212608_b(new ResourceLocation(s));
         if (item == null) {
            throw new JsonSyntaxException("Expected " + p_188172_1_ + " to be an item, was unknown string '" + s + "'");
         } else {
            return item;
         }
      } else {
         throw new JsonSyntaxException("Expected " + p_188172_1_ + " to be an item, was " + toString(p_188172_0_));
      }
   }

   public static Item getItem(JsonObject p_188180_0_, String p_188180_1_) {
      if (p_188180_0_.has(p_188180_1_)) {
         return getItem(p_188180_0_.get(p_188180_1_), p_188180_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_188180_1_ + ", expected to find an item");
      }
   }

   public static boolean getBoolean(JsonElement p_151216_0_, String p_151216_1_) {
      if (p_151216_0_.isJsonPrimitive()) {
         return p_151216_0_.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + p_151216_1_ + " to be a Boolean, was " + toString(p_151216_0_));
      }
   }

   public static boolean getBoolean(JsonObject p_151212_0_, String p_151212_1_) {
      if (p_151212_0_.has(p_151212_1_)) {
         return getBoolean(p_151212_0_.get(p_151212_1_), p_151212_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151212_1_ + ", expected to find a Boolean");
      }
   }

   public static boolean getBoolean(JsonObject p_151209_0_, String p_151209_1_, boolean p_151209_2_) {
      return p_151209_0_.has(p_151209_1_) ? getBoolean(p_151209_0_.get(p_151209_1_), p_151209_1_) : p_151209_2_;
   }

   public static float getFloat(JsonElement p_151220_0_, String p_151220_1_) {
      if (p_151220_0_.isJsonPrimitive() && p_151220_0_.getAsJsonPrimitive().isNumber()) {
         return p_151220_0_.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + p_151220_1_ + " to be a Float, was " + toString(p_151220_0_));
      }
   }

   public static float getFloat(JsonObject p_151217_0_, String p_151217_1_) {
      if (p_151217_0_.has(p_151217_1_)) {
         return getFloat(p_151217_0_.get(p_151217_1_), p_151217_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151217_1_ + ", expected to find a Float");
      }
   }

   public static float getFloat(JsonObject p_151221_0_, String p_151221_1_, float p_151221_2_) {
      return p_151221_0_.has(p_151221_1_) ? getFloat(p_151221_0_.get(p_151221_1_), p_151221_1_) : p_151221_2_;
   }

   public static int getInt(JsonElement p_151215_0_, String p_151215_1_) {
      if (p_151215_0_.isJsonPrimitive() && p_151215_0_.getAsJsonPrimitive().isNumber()) {
         return p_151215_0_.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + p_151215_1_ + " to be a Int, was " + toString(p_151215_0_));
      }
   }

   public static int getInt(JsonObject p_151203_0_, String p_151203_1_) {
      if (p_151203_0_.has(p_151203_1_)) {
         return getInt(p_151203_0_.get(p_151203_1_), p_151203_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151203_1_ + ", expected to find a Int");
      }
   }

   public static int getInt(JsonObject p_151208_0_, String p_151208_1_, int p_151208_2_) {
      return p_151208_0_.has(p_151208_1_) ? getInt(p_151208_0_.get(p_151208_1_), p_151208_1_) : p_151208_2_;
   }

   public static byte getByte(JsonElement p_204332_0_, String p_204332_1_) {
      if (p_204332_0_.isJsonPrimitive() && p_204332_0_.getAsJsonPrimitive().isNumber()) {
         return p_204332_0_.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + p_204332_1_ + " to be a Byte, was " + toString(p_204332_0_));
      }
   }

   public static byte getByte(JsonObject p_204331_0_, String p_204331_1_) {
      if (p_204331_0_.has(p_204331_1_)) {
         return getByte(p_204331_0_.get(p_204331_1_), p_204331_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_204331_1_ + ", expected to find a Byte");
      }
   }

   public static JsonObject getJsonObject(JsonElement p_151210_0_, String p_151210_1_) {
      if (p_151210_0_.isJsonObject()) {
         return p_151210_0_.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + p_151210_1_ + " to be a JsonObject, was " + toString(p_151210_0_));
      }
   }

   public static JsonObject getJsonObject(JsonObject p_152754_0_, String p_152754_1_) {
      if (p_152754_0_.has(p_152754_1_)) {
         return getJsonObject(p_152754_0_.get(p_152754_1_), p_152754_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_152754_1_ + ", expected to find a JsonObject");
      }
   }

   public static JsonObject getJsonObject(JsonObject p_151218_0_, String p_151218_1_, JsonObject p_151218_2_) {
      return p_151218_0_.has(p_151218_1_) ? getJsonObject(p_151218_0_.get(p_151218_1_), p_151218_1_) : p_151218_2_;
   }

   public static JsonArray getJsonArray(JsonElement p_151207_0_, String p_151207_1_) {
      if (p_151207_0_.isJsonArray()) {
         return p_151207_0_.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + p_151207_1_ + " to be a JsonArray, was " + toString(p_151207_0_));
      }
   }

   public static JsonArray getJsonArray(JsonObject p_151214_0_, String p_151214_1_) {
      if (p_151214_0_.has(p_151214_1_)) {
         return getJsonArray(p_151214_0_.get(p_151214_1_), p_151214_1_);
      } else {
         throw new JsonSyntaxException("Missing " + p_151214_1_ + ", expected to find a JsonArray");
      }
   }

   public static JsonArray getJsonArray(JsonObject p_151213_0_, String p_151213_1_, @Nullable JsonArray p_151213_2_) {
      return p_151213_0_.has(p_151213_1_) ? getJsonArray(p_151213_0_.get(p_151213_1_), p_151213_1_) : p_151213_2_;
   }

   public static <T> T deserializeClass(@Nullable JsonElement p_188179_0_, String p_188179_1_, JsonDeserializationContext p_188179_2_, Class<? extends T> p_188179_3_) {
      if (p_188179_0_ != null) {
         return p_188179_2_.deserialize(p_188179_0_, p_188179_3_);
      } else {
         throw new JsonSyntaxException("Missing " + p_188179_1_);
      }
   }

   public static <T> T deserializeClass(JsonObject p_188174_0_, String p_188174_1_, JsonDeserializationContext p_188174_2_, Class<? extends T> p_188174_3_) {
      if (p_188174_0_.has(p_188174_1_)) {
         return deserializeClass(p_188174_0_.get(p_188174_1_), p_188174_1_, p_188174_2_, p_188174_3_);
      } else {
         throw new JsonSyntaxException("Missing " + p_188174_1_);
      }
   }

   public static <T> T deserializeClass(JsonObject p_188177_0_, String p_188177_1_, T p_188177_2_, JsonDeserializationContext p_188177_3_, Class<? extends T> p_188177_4_) {
      return (T)(p_188177_0_.has(p_188177_1_) ? deserializeClass(p_188177_0_.get(p_188177_1_), p_188177_1_, p_188177_3_, p_188177_4_) : p_188177_2_);
   }

   public static String toString(JsonElement p_151222_0_) {
      String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf((Object)p_151222_0_), "...", 10);
      if (p_151222_0_ == null) {
         return "null (missing)";
      } else if (p_151222_0_.isJsonNull()) {
         return "null (json)";
      } else if (p_151222_0_.isJsonArray()) {
         return "an array (" + s + ")";
      } else if (p_151222_0_.isJsonObject()) {
         return "an object (" + s + ")";
      } else {
         if (p_151222_0_.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = p_151222_0_.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               return "a number (" + s + ")";
            }

            if (jsonprimitive.isBoolean()) {
               return "a boolean (" + s + ")";
            }
         }

         return s;
      }
   }

   @Nullable
   public static <T> T fromJson(Gson p_188173_0_, Reader p_188173_1_, Class<T> p_188173_2_, boolean p_188173_3_) {
      try {
         JsonReader jsonreader = new JsonReader(p_188173_1_);
         jsonreader.setLenient(p_188173_3_);
         return p_188173_0_.getAdapter(p_188173_2_).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson p_193838_0_, Reader p_193838_1_, Type p_193838_2_, boolean p_193838_3_) {
      try {
         JsonReader jsonreader = new JsonReader(p_193838_1_);
         jsonreader.setLenient(p_193838_3_);
         return (T)p_193838_0_.getAdapter(TypeToken.get(p_193838_2_)).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_193837_0_, String p_193837_1_, Type p_193837_2_, boolean p_193837_3_) {
      return fromJson(p_193837_0_, new StringReader(p_193837_1_), p_193837_2_, p_193837_3_);
   }

   @Nullable
   public static <T> T fromJson(Gson p_188176_0_, String p_188176_1_, Class<T> p_188176_2_, boolean p_188176_3_) {
      return fromJson(p_188176_0_, new StringReader(p_188176_1_), p_188176_2_, p_188176_3_);
   }

   @Nullable
   public static <T> T fromJson(Gson p_193841_0_, Reader p_193841_1_, Type p_193841_2_) {
      return fromJson(p_193841_0_, p_193841_1_, p_193841_2_, false);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_193840_0_, String p_193840_1_, Type p_193840_2_) {
      return fromJson(p_193840_0_, p_193840_1_, p_193840_2_, false);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson p_193839_0_, Reader p_193839_1_, Class<T> p_193839_2_) {
      return fromJson(p_193839_0_, p_193839_1_, p_193839_2_, false);
   }

   @Nullable
   public static <T> T fromJson(Gson p_188178_0_, String p_188178_1_, Class<T> p_188178_2_) {
      return fromJson(p_188178_0_, p_188178_1_, p_188178_2_, false);
   }

   public static JsonObject func_212746_a(String p_212746_0_, boolean p_212746_1_) {
      return func_212744_a(new StringReader(p_212746_0_), p_212746_1_);
   }

   public static JsonObject func_212744_a(Reader p_212744_0_, boolean p_212744_1_) {
      return fromJson(field_212747_a, p_212744_0_, JsonObject.class, p_212744_1_);
   }

   public static JsonObject func_212745_a(String p_212745_0_) {
      return func_212746_a(p_212745_0_, false);
   }

   public static JsonObject func_212743_a(Reader p_212743_0_) {
      return func_212744_a(p_212743_0_, false);
   }
}
