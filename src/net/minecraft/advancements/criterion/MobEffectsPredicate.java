package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<Potion, MobEffectsPredicate.InstancePredicate> effects;

   public MobEffectsPredicate(Map<Potion, MobEffectsPredicate.InstancePredicate> p_i47538_1_) {
      this.effects = p_i47538_1_;
   }

   public static MobEffectsPredicate func_204014_a() {
      return new MobEffectsPredicate(Maps.newHashMap());
   }

   public MobEffectsPredicate func_204015_a(Potion p_204015_1_) {
      this.effects.put(p_204015_1_, new MobEffectsPredicate.InstancePredicate());
      return this;
   }

   public boolean test(Entity p_193469_1_) {
      if (this == ANY) {
         return true;
      } else {
         return p_193469_1_ instanceof EntityLivingBase && this.test(
                 ((EntityLivingBase) p_193469_1_).getActivePotionMap());
      }
   }

   public boolean test(EntityLivingBase p_193472_1_) {
      return this == ANY || this.test(p_193472_1_.getActivePotionMap());
   }

   public boolean test(Map<Potion, PotionEffect> p_193470_1_) {
      if (this == ANY) {
         return true;
      } else {
         for(Entry<Potion, MobEffectsPredicate.InstancePredicate> entry : this.effects.entrySet()) {
            PotionEffect potioneffect = p_193470_1_.get(entry.getKey());
            if (!entry.getValue().test(potioneffect)) {
               return false;
            }
         }

         return true;
      }
   }

   public static MobEffectsPredicate deserialize(@Nullable JsonElement p_193471_0_) {
      if (p_193471_0_ != null && !p_193471_0_.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_193471_0_, "effects");
         Map<Potion, MobEffectsPredicate.InstancePredicate> map = Maps.newHashMap();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
            Potion potion = IRegistry.field_212631_t.func_212608_b(resourcelocation);
            if (potion == null) {
               throw new JsonSyntaxException("Unknown effect '" + resourcelocation + "'");
            }

            MobEffectsPredicate.InstancePredicate mobeffectspredicate$instancepredicate = MobEffectsPredicate.InstancePredicate.deserialize(JsonUtils.getJsonObject(entry.getValue(), entry.getKey()));
            map.put(potion, mobeffectspredicate$instancepredicate);
         }

         return new MobEffectsPredicate(map);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();

         for(Entry<Potion, MobEffectsPredicate.InstancePredicate> entry : this.effects.entrySet()) {
            jsonobject.add(IRegistry.field_212631_t.func_177774_c(entry.getKey()).toString(), entry.getValue().serialize());
         }

         return jsonobject;
      }
   }

   public static class InstancePredicate {
      private final MinMaxBounds.IntBound amplifier;
      private final MinMaxBounds.IntBound duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public InstancePredicate(MinMaxBounds.IntBound p_i49709_1_, MinMaxBounds.IntBound p_i49709_2_, @Nullable Boolean p_i49709_3_, @Nullable Boolean p_i49709_4_) {
         this.amplifier = p_i49709_1_;
         this.duration = p_i49709_2_;
         this.ambient = p_i49709_3_;
         this.visible = p_i49709_4_;
      }

      public InstancePredicate() {
         this(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, null, null);
      }

      public boolean test(@Nullable PotionEffect p_193463_1_) {
         if (p_193463_1_ == null) {
            return false;
         } else if (!this.amplifier.test(p_193463_1_.getAmplifier())) {
            return false;
         } else if (!this.duration.test(p_193463_1_.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != p_193463_1_.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == p_193463_1_.doesShowParticles();
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("amplifier", this.amplifier.serialize());
         jsonobject.add("duration", this.duration.serialize());
         jsonobject.addProperty("ambient", this.ambient);
         jsonobject.addProperty("visible", this.visible);
         return jsonobject;
      }

      public static MobEffectsPredicate.InstancePredicate deserialize(JsonObject p_193464_0_) {
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(p_193464_0_.get("amplifier"));
         MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.func_211344_a(p_193464_0_.get("duration"));
         Boolean obool = p_193464_0_.has("ambient") ? JsonUtils.getBoolean(p_193464_0_, "ambient") : null;
         Boolean obool1 = p_193464_0_.has("visible") ? JsonUtils.getBoolean(p_193464_0_, "visible") : null;
         return new MobEffectsPredicate.InstancePredicate(minmaxbounds$intbound, minmaxbounds$intbound1, obool, obool1);
      }
   }
}
