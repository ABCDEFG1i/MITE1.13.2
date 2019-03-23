package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public interface LootCondition {
   boolean testCondition(Random p_186618_1_, LootContext p_186618_2_);

   public abstract static class Serializer<T extends LootCondition> {
      private final ResourceLocation lootTableLocation;
      private final Class<T> conditionClass;

      protected Serializer(ResourceLocation p_i47021_1_, Class<T> p_i47021_2_) {
         this.lootTableLocation = p_i47021_1_;
         this.conditionClass = p_i47021_2_;
      }

      public ResourceLocation getLootTableLocation() {
         return this.lootTableLocation;
      }

      public Class<T> getConditionClass() {
         return this.conditionClass;
      }

      public abstract void serialize(JsonObject p_186605_1_, T p_186605_2_, JsonSerializationContext p_186605_3_);

      public abstract T deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_);
   }
}
