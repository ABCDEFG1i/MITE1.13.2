package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   private final Enchantment enchantment;
   private final MinMaxBounds.IntBound levels;

   public EnchantmentPredicate() {
      this.enchantment = null;
      this.levels = MinMaxBounds.IntBound.UNBOUNDED;
   }

   public EnchantmentPredicate(@Nullable Enchantment p_i49723_1_, MinMaxBounds.IntBound p_i49723_2_) {
      this.enchantment = p_i49723_1_;
      this.levels = p_i49723_2_;
   }

   public boolean test(Map<Enchantment, Integer> p_192463_1_) {
      if (this.enchantment != null) {
         if (!p_192463_1_.containsKey(this.enchantment)) {
            return false;
         }

         int i = p_192463_1_.get(this.enchantment);
         if (this.levels != null && !this.levels.test(i)) {
            return false;
         }
      } else if (this.levels != null) {
         for(Integer integer : p_192463_1_.values()) {
            if (this.levels.test(integer)) {
               return true;
            }
         }

         return false;
      }

      return true;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.enchantment != null) {
            jsonobject.addProperty("enchantment", IRegistry.field_212628_q.func_177774_c(this.enchantment).toString());
         }

         jsonobject.add("levels", this.levels.serialize());
         return jsonobject;
      }
   }

   public static EnchantmentPredicate deserialize(@Nullable JsonElement p_192464_0_) {
      if (p_192464_0_ != null && !p_192464_0_.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_192464_0_, "enchantment");
         Enchantment enchantment = null;
         if (jsonobject.has("enchantment")) {
            ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(jsonobject, "enchantment"));
            enchantment = IRegistry.field_212628_q.func_212608_b(resourcelocation);
            if (enchantment == null) {
               throw new JsonSyntaxException("Unknown enchantment '" + resourcelocation + "'");
            }
         }

         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(jsonobject.get("levels"));
         return new EnchantmentPredicate(enchantment, minmaxbounds$intbound);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] deserializeArray(@Nullable JsonElement p_192465_0_) {
      if (p_192465_0_ != null && !p_192465_0_.isJsonNull()) {
         JsonArray jsonarray = JsonUtils.getJsonArray(p_192465_0_, "enchantments");
         EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.size()];

         for(int i = 0; i < aenchantmentpredicate.length; ++i) {
            aenchantmentpredicate[i] = deserialize(jsonarray.get(i));
         }

         return aenchantmentpredicate;
      } else {
         return new EnchantmentPredicate[0];
      }
   }
}
