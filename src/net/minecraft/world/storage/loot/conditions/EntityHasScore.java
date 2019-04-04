package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;

public class EntityHasScore implements LootCondition {
   private final Map<String, RandomValueRange> scores;
   private final LootContext.EntityTarget target;

   public EntityHasScore(Map<String, RandomValueRange> p_i46618_1_, LootContext.EntityTarget p_i46618_2_) {
      this.scores = p_i46618_1_;
      this.target = p_i46618_2_;
   }

   public boolean testCondition(Random p_186618_1_, LootContext p_186618_2_) {
      Entity entity = p_186618_2_.getEntity(this.target);
      if (entity == null) {
         return false;
      } else {
         Scoreboard scoreboard = entity.world.getScoreboard();

         for(Entry<String, RandomValueRange> entry : this.scores.entrySet()) {
            if (!this.entityScoreMatch(entity, scoreboard, entry.getKey(), entry.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean entityScoreMatch(Entity p_186631_1_, Scoreboard p_186631_2_, String p_186631_3_, RandomValueRange p_186631_4_) {
      ScoreObjective scoreobjective = p_186631_2_.getObjective(p_186631_3_);
      if (scoreobjective == null) {
         return false;
      } else {
         String s = p_186631_1_.getScoreboardName();
         return p_186631_2_.entityHasObjective(s, scoreobjective) && p_186631_4_.isInRange(
                 p_186631_2_.getOrCreateScore(s, scoreobjective).getScorePoints());
      }
   }

   public static class Serializer extends LootCondition.Serializer<EntityHasScore> {
      protected Serializer() {
         super(new ResourceLocation("entity_scores"), EntityHasScore.class);
      }

      public void serialize(JsonObject p_186605_1_, EntityHasScore p_186605_2_, JsonSerializationContext p_186605_3_) {
         JsonObject jsonobject = new JsonObject();

         for(Entry<String, RandomValueRange> entry : p_186605_2_.scores.entrySet()) {
            jsonobject.add(entry.getKey(), p_186605_3_.serialize(entry.getValue()));
         }

         p_186605_1_.add("scores", jsonobject);
         p_186605_1_.add("entity", p_186605_3_.serialize(p_186605_2_.target));
      }

      public EntityHasScore deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         Set<Entry<String, JsonElement>> set = JsonUtils.getJsonObject(p_186603_1_, "scores").entrySet();
         Map<String, RandomValueRange> map = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> entry : set) {
            map.put(entry.getKey(), JsonUtils.deserializeClass(entry.getValue(), "score", p_186603_2_, RandomValueRange.class));
         }

         return new EntityHasScore(map, JsonUtils.deserializeClass(p_186603_1_, "entity", p_186603_2_, LootContext.EntityTarget.class));
      }
   }
}
