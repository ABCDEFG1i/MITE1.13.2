package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamage extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RandomValueRange damageRange;

   public SetDamage(LootCondition[] p_i46622_1_, RandomValueRange p_i46622_2_) {
      super(p_i46622_1_);
      this.damageRange = p_i46622_2_;
   }

   public ItemStack apply(ItemStack p_186553_1_, Random p_186553_2_, LootContext p_186553_3_) {
      if (p_186553_1_.isDamageable()) {
         float f = 1.0F - this.damageRange.generateFloat(p_186553_2_);
         p_186553_1_.setDamage(MathHelper.floor(f * (float)p_186553_1_.getMaxDamage()));
      } else {
         LOGGER.warn("Couldn't set damage of loot item {}", (Object)p_186553_1_);
      }

      return p_186553_1_;
   }

   public static class Serializer extends LootFunction.Serializer<SetDamage> {
      protected Serializer() {
         super(new ResourceLocation("set_damage"), SetDamage.class);
      }

      public void serialize(JsonObject p_186532_1_, SetDamage p_186532_2_, JsonSerializationContext p_186532_3_) {
         p_186532_1_.add("damage", p_186532_3_.serialize(p_186532_2_.damageRange));
      }

      public SetDamage deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, LootCondition[] p_186530_3_) {
         return new SetDamage(p_186530_3_, JsonUtils.deserializeClass(p_186530_1_, "damage", p_186530_2_, RandomValueRange.class));
      }
   }
}
