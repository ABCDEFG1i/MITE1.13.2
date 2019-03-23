package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class KilledByPlayer implements LootCondition {
   private final boolean inverse;

   public KilledByPlayer(boolean p_i46616_1_) {
      this.inverse = p_i46616_1_;
   }

   public boolean testCondition(Random p_186618_1_, LootContext p_186618_2_) {
      boolean flag = p_186618_2_.getKillerPlayer() != null;
      return flag == !this.inverse;
   }

   public static class Serializer extends LootCondition.Serializer<KilledByPlayer> {
      protected Serializer() {
         super(new ResourceLocation("killed_by_player"), KilledByPlayer.class);
      }

      public void serialize(JsonObject p_186605_1_, KilledByPlayer p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("inverse", p_186605_2_.inverse);
      }

      public KilledByPlayer deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return new KilledByPlayer(JsonUtils.getBoolean(p_186603_1_, "inverse", false));
      }
   }
}
