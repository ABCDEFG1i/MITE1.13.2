package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class RandomValueRange {
   private final float min;
   private final float max;

   public RandomValueRange(float p_i46629_1_, float p_i46629_2_) {
      this.min = p_i46629_1_;
      this.max = p_i46629_2_;
   }

   public RandomValueRange(float p_i46630_1_) {
      this.min = p_i46630_1_;
      this.max = p_i46630_1_;
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public int generateInt(Random p_186511_1_) {
      return MathHelper.nextInt(p_186511_1_, MathHelper.floor(this.min), MathHelper.floor(this.max));
   }

   public float generateFloat(Random p_186507_1_) {
      return MathHelper.nextFloat(p_186507_1_, this.min, this.max);
   }

   public boolean isInRange(int p_186510_1_) {
      return (float)p_186510_1_ <= this.max && (float)p_186510_1_ >= this.min;
   }

   public static class Serializer implements JsonDeserializer<RandomValueRange>, JsonSerializer<RandomValueRange> {
      public RandomValueRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (JsonUtils.isNumber(p_deserialize_1_)) {
            return new RandomValueRange(JsonUtils.getFloat(p_deserialize_1_, "value"));
         } else {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "value");
            float f = JsonUtils.getFloat(jsonobject, "min");
            float f1 = JsonUtils.getFloat(jsonobject, "max");
            return new RandomValueRange(f, f1);
         }
      }

      public JsonElement serialize(RandomValueRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         if (p_serialize_1_.min == p_serialize_1_.max) {
            return new JsonPrimitive(p_serialize_1_.min);
         } else {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("min", p_serialize_1_.min);
            jsonobject.addProperty("max", p_serialize_1_.max);
            return jsonobject;
         }
      }
   }
}
