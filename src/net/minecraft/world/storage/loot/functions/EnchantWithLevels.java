package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class EnchantWithLevels extends LootFunction {
   private final RandomValueRange randomLevel;
   private final boolean isTreasure;

   public EnchantWithLevels(LootCondition[] p_i46627_1_, RandomValueRange p_i46627_2_, boolean p_i46627_3_) {
      super(p_i46627_1_);
      this.randomLevel = p_i46627_2_;
      this.isTreasure = p_i46627_3_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      return EnchantmentHelper.addRandomEnchantment(p_186553_2_, p_186553_1_, this.randomLevel.generateInt(p_186553_2_), this.isTreasure);
   }

   public static class Serializer extends LootFunction.Serializer<EnchantWithLevels> {
      public Serializer() {
         super(new ResourceLocation("enchant_with_levels"), EnchantWithLevels.class);
      }

      public void serialize(JsonObject p_186532_1_, EnchantWithLevels p_186532_2_, JsonSerializationContext p_186532_3_) {
         p_186532_1_.add("levels", p_186532_3_.serialize(p_186532_2_.randomLevel));
         p_186532_1_.addProperty("treasure", p_186532_2_.isTreasure);
      }

      public EnchantWithLevels deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         RandomValueRange randomvaluerange = JsonUtils.deserializeClass(p_186530_1_, "levels", p_186530_2_, RandomValueRange.class);
         boolean flag = JsonUtils.getBoolean(p_186530_1_, "treasure", false);
         return new EnchantWithLevels(p_186530_3_, randomvaluerange, flag);
      }
   }
}
