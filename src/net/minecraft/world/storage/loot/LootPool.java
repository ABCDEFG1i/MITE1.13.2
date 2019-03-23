package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import org.apache.commons.lang3.ArrayUtils;

public class LootPool {
   private final LootEntry[] lootEntries;
   private final LootCondition[] poolConditions;
   private RandomValueRange rolls;
   private RandomValueRange bonusRolls;

   public LootPool(LootEntry[] p_i46643_1_, LootCondition[] p_i46643_2_, RandomValueRange p_i46643_3_, RandomValueRange p_i46643_4_) {
      this.lootEntries = p_i46643_1_;
      this.poolConditions = p_i46643_2_;
      this.rolls = p_i46643_3_;
      this.bonusRolls = p_i46643_4_;
   }

   protected void createLootRoll(Collection<ItemStack> p_186452_1_, Random p_186452_2_, LootContext p_186452_3_) {
      List<LootEntry> list = Lists.newArrayList();
      int i = 0;

      for(LootEntry lootentry : this.lootEntries) {
         if (LootConditionManager.testAllConditions(lootentry.conditions, p_186452_2_, p_186452_3_)) {
            int j = lootentry.getEffectiveWeight(p_186452_3_.getLuck());
            if (j > 0) {
               list.add(lootentry);
               i += j;
            }
         }
      }

      if (i != 0 && !list.isEmpty()) {
         int k = p_186452_2_.nextInt(i);

         for(LootEntry lootentry1 : list) {
            k -= lootentry1.getEffectiveWeight(p_186452_3_.getLuck());
            if (k < 0) {
               lootentry1.addLoot(p_186452_1_, p_186452_2_, p_186452_3_);
               return;
            }
         }

      }
   }

   public void generateLoot(Collection<ItemStack> p_186449_1_, Random p_186449_2_, LootContext p_186449_3_) {
      if (LootConditionManager.testAllConditions(this.poolConditions, p_186449_2_, p_186449_3_)) {
         int i = this.rolls.generateInt(p_186449_2_) + MathHelper.floor(this.bonusRolls.generateFloat(p_186449_2_) * p_186449_3_.getLuck());

         for(int j = 0; j < i; ++j) {
            this.createLootRoll(p_186449_1_, p_186449_2_, p_186449_3_);
         }

      }
   }

   public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
      public LootPool deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "loot pool");
         LootEntry[] alootentry = JsonUtils.deserializeClass(jsonobject, "entries", p_deserialize_3_, LootEntry[].class);
         LootCondition[] alootcondition = JsonUtils.deserializeClass(jsonobject, "conditions", new LootCondition[0], p_deserialize_3_, LootCondition[].class);
         RandomValueRange randomvaluerange = JsonUtils.deserializeClass(jsonobject, "rolls", p_deserialize_3_, RandomValueRange.class);
         RandomValueRange randomvaluerange1 = JsonUtils.deserializeClass(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), p_deserialize_3_, RandomValueRange.class);
         return new LootPool(alootentry, alootcondition, randomvaluerange, randomvaluerange1);
      }

      public JsonElement serialize(LootPool p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entries", p_serialize_3_.serialize(p_serialize_1_.lootEntries));
         jsonobject.add("rolls", p_serialize_3_.serialize(p_serialize_1_.rolls));
         if (p_serialize_1_.bonusRolls.getMin() != 0.0F && p_serialize_1_.bonusRolls.getMax() != 0.0F) {
            jsonobject.add("bonus_rolls", p_serialize_3_.serialize(p_serialize_1_.bonusRolls));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.poolConditions)) {
            jsonobject.add("conditions", p_serialize_3_.serialize(p_serialize_1_.poolConditions));
         }

         return jsonobject;
      }
   }
}
