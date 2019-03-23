package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public abstract class LootFunction {
   private final LootCondition[] conditions;

   protected LootFunction(LootCondition[] p_i46626_1_) {
      this.conditions = p_i46626_1_;
   }

   public abstract ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_);

   public LootCondition[] getConditions() {
      return this.conditions;
   }

   public abstract static class Serializer<T extends LootFunction> {
      private final ResourceLocation lootTableLocation;
      private final Class<T> functionClass;

      protected Serializer(ResourceLocation p_i47002_1_, Class<T> p_i47002_2_) {
         this.lootTableLocation = p_i47002_1_;
         this.functionClass = p_i47002_2_;
      }

      public ResourceLocation getFunctionName() {
         return this.lootTableLocation;
      }

      public Class<T> getFunctionClass() {
         return this.functionClass;
      }

      public abstract void serialize(JsonObject p_186532_1_, T p_186532_2_, JsonSerializationContext p_186532_3_);

      public abstract T deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_);
   }
}
