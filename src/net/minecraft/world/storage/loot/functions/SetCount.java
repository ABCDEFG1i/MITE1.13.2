package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetCount extends LootFunction {
   private final RandomValueRange countRange;

   public SetCount(LootCondition[] p_i46623_1_, RandomValueRange p_i46623_2_) {
      super(p_i46623_1_);
      this.countRange = p_i46623_2_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      p_186553_1_.setCount(this.countRange.generateInt(p_186553_2_));
      return p_186553_1_;
   }

   public static class Serializer extends LootFunction.Serializer<SetCount> {
      protected Serializer() {
         super(new ResourceLocation("set_count"), SetCount.class);
      }

      public void serialize(JsonObject p_186532_1_, SetCount p_186532_2_, JsonSerializationContext p_186532_3_) {
         p_186532_1_.add("count", p_186532_3_.serialize(p_186532_2_.countRange));
      }

      public SetCount deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         return new SetCount(p_186530_3_, JsonUtils.deserializeClass(p_186530_1_, "count", p_186530_2_, RandomValueRange.class));
      }
   }
}
