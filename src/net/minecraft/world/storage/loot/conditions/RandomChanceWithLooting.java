package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChanceWithLooting implements LootCondition {
   private final float chance;
   private final float lootingMultiplier;

   public RandomChanceWithLooting(float p_i46614_1_, float p_i46614_2_) {
      this.chance = p_i46614_1_;
      this.lootingMultiplier = p_i46614_2_;
   }

   public boolean testCondition(Random p_186618_1_, LootContext p_186618_2_) {
      int i = 0;
      if (p_186618_2_.getKiller() instanceof EntityLivingBase) {
         i = EnchantmentHelper.getLootingModifier((EntityLivingBase)p_186618_2_.getKiller());
      }

      return p_186618_1_.nextFloat() < this.chance + (float)i * this.lootingMultiplier;
   }

   public static class Serializer extends LootCondition.Serializer<RandomChanceWithLooting> {
      protected Serializer() {
         super(new ResourceLocation("random_chance_with_looting"), RandomChanceWithLooting.class);
      }

      public void serialize(JsonObject p_186605_1_, RandomChanceWithLooting p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("chance", p_186605_2_.chance);
         p_186605_1_.addProperty("looting_multiplier", p_186605_2_.lootingMultiplier);
      }

      public RandomChanceWithLooting deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return new RandomChanceWithLooting(JsonUtils.getFloat(p_186603_1_, "chance"), JsonUtils.getFloat(p_186603_1_, "looting_multiplier"));
      }
   }
}
