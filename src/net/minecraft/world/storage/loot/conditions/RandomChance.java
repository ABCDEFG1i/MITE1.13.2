package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChance implements LootCondition {
   private final float chance;

   public RandomChance(float p_i46615_1_) {
      this.chance = p_i46615_1_;
   }

   public boolean testCondition(Random p_186618_1_, LootContext p_186618_2_) {
      return p_186618_1_.nextFloat() < this.chance;
   }

   public static class Serializer extends LootCondition.Serializer<RandomChance> {
      protected Serializer() {
         super(new ResourceLocation("random_chance"), RandomChance.class);
      }

      public void serialize(JsonObject p_186605_1_, RandomChance p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("chance", p_186605_2_.chance);
      }

      public RandomChance deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return new RandomChance(JsonUtils.getFloat(p_186603_1_, "chance"));
      }
   }
}
