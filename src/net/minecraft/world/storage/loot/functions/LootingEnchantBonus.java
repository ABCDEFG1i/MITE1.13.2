package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootingEnchantBonus extends LootFunction {
   private final RandomValueRange count;
   private final int limit;

   public LootingEnchantBonus(LootCondition[] p_i47145_1_, RandomValueRange p_i47145_2_, int p_i47145_3_) {
      super(p_i47145_1_);
      this.count = p_i47145_2_;
      this.limit = p_i47145_3_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      Entity entity = p_186553_3_.getKiller();
      if (entity instanceof EntityLivingBase) {
         int i = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
         if (i == 0) {
            return p_186553_1_;
         }

         float f = (float)i * this.count.generateFloat(p_186553_2_);
         p_186553_1_.grow(Math.round(f));
         if (this.limit != 0 && p_186553_1_.getCount() > this.limit) {
            p_186553_1_.setCount(this.limit);
         }
      }

      return p_186553_1_;
   }

   public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus> {
      protected Serializer() {
         super(new ResourceLocation("looting_enchant"), LootingEnchantBonus.class);
      }

      public void serialize(JsonObject p_186532_1_, LootingEnchantBonus p_186532_2_, JsonSerializationContext p_186532_3_) {
         p_186532_1_.add("count", p_186532_3_.serialize(p_186532_2_.count));
         if (p_186532_2_.limit > 0) {
            p_186532_1_.add("limit", p_186532_3_.serialize(p_186532_2_.limit));
         }

      }

      public LootingEnchantBonus deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         int i = JsonUtils.getInt(p_186530_1_, "limit", 0);
         return new LootingEnchantBonus(p_186530_3_, JsonUtils.deserializeClass(p_186530_1_, "count", p_186530_2_, RandomValueRange.class), i);
      }
   }
}
