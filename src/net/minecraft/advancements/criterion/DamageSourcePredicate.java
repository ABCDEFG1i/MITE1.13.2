package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JsonUtils;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.func_203981_a().func_203979_b();
   private final Boolean isProjectile;
   private final Boolean isExplosion;
   private final Boolean bypassesArmor;
   private final Boolean bypassesInvulnerability;
   private final Boolean bypassesMagic;
   private final Boolean isFire;
   private final Boolean isMagic;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean p_i47543_1_, @Nullable Boolean p_i47543_2_, @Nullable Boolean p_i47543_3_, @Nullable Boolean p_i47543_4_, @Nullable Boolean p_i47543_5_, @Nullable Boolean p_i47543_6_, @Nullable Boolean p_i47543_7_, EntityPredicate p_i47543_8_, EntityPredicate p_i47543_9_) {
      this.isProjectile = p_i47543_1_;
      this.isExplosion = p_i47543_2_;
      this.bypassesArmor = p_i47543_3_;
      this.bypassesInvulnerability = p_i47543_4_;
      this.bypassesMagic = p_i47543_5_;
      this.isFire = p_i47543_6_;
      this.isMagic = p_i47543_7_;
      this.directEntity = p_i47543_8_;
      this.sourceEntity = p_i47543_9_;
   }

   public boolean test(EntityPlayerMP p_193418_1_, DamageSource p_193418_2_) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != p_193418_2_.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != p_193418_2_.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != p_193418_2_.isUnblockable()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != p_193418_2_.canHarmInCreative()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != p_193418_2_.isDamageAbsolute()) {
         return false;
      } else if (this.isFire != null && this.isFire != p_193418_2_.isFireDamage()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != p_193418_2_.isMagicDamage()) {
         return false;
      } else if (!this.directEntity.test(p_193418_1_, p_193418_2_.getImmediateSource())) {
         return false;
      } else {
         return this.sourceEntity.test(p_193418_1_, p_193418_2_.getTrueSource());
      }
   }

   public static DamageSourcePredicate deserialize(@Nullable JsonElement p_192447_0_) {
      if (p_192447_0_ != null && !p_192447_0_.isJsonNull()) {
         JsonObject jsonobject = JsonUtils.getJsonObject(p_192447_0_, "damage type");
         Boolean obool = optionalBoolean(jsonobject, "is_projectile");
         Boolean obool1 = optionalBoolean(jsonobject, "is_explosion");
         Boolean obool2 = optionalBoolean(jsonobject, "bypasses_armor");
         Boolean obool3 = optionalBoolean(jsonobject, "bypasses_invulnerability");
         Boolean obool4 = optionalBoolean(jsonobject, "bypasses_magic");
         Boolean obool5 = optionalBoolean(jsonobject, "is_fire");
         Boolean obool6 = optionalBoolean(jsonobject, "is_magic");
         EntityPredicate entitypredicate = EntityPredicate.deserialize(jsonobject.get("direct_entity"));
         EntityPredicate entitypredicate1 = EntityPredicate.deserialize(jsonobject.get("source_entity"));
         return new DamageSourcePredicate(obool, obool1, obool2, obool3, obool4, obool5, obool6, entitypredicate, entitypredicate1);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean optionalBoolean(JsonObject p_192448_0_, String p_192448_1_) {
      return p_192448_0_.has(p_192448_1_) ? JsonUtils.getBoolean(p_192448_0_, p_192448_1_) : null;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.addProperty(jsonobject, "is_projectile", this.isProjectile);
         this.addProperty(jsonobject, "is_explosion", this.isExplosion);
         this.addProperty(jsonobject, "bypasses_armor", this.bypassesArmor);
         this.addProperty(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addProperty(jsonobject, "bypasses_magic", this.bypassesMagic);
         this.addProperty(jsonobject, "is_fire", this.isFire);
         this.addProperty(jsonobject, "is_magic", this.isMagic);
         jsonobject.add("direct_entity", this.directEntity.serialize());
         jsonobject.add("source_entity", this.sourceEntity.serialize());
         return jsonobject;
      }
   }

   private void addProperty(JsonObject p_203992_1_, String p_203992_2_, @Nullable Boolean p_203992_3_) {
      if (p_203992_3_ != null) {
         p_203992_1_.addProperty(p_203992_2_, p_203992_3_);
      }

   }

   public static class Builder {
      private Boolean field_203982_a;
      private Boolean field_203983_b;
      private Boolean field_203984_c;
      private Boolean field_203985_d;
      private Boolean field_203986_e;
      private Boolean field_203987_f;
      private Boolean field_203988_g;
      private EntityPredicate field_203989_h = EntityPredicate.ANY;
      private EntityPredicate field_203990_i = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder func_203981_a() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder func_203978_a(Boolean p_203978_1_) {
         this.field_203982_a = p_203978_1_;
         return this;
      }

      public DamageSourcePredicate.Builder func_203980_a(EntityPredicate.Builder p_203980_1_) {
         this.field_203989_h = p_203980_1_.func_204000_b();
         return this;
      }

      public DamageSourcePredicate.Builder func_203980_b(EntityPredicate.Builder p_203980_1_) {
         this.field_203990_i = p_203980_1_.func_204000_b();
         return this;
      }

      public DamageSourcePredicate func_203979_b() {
         return new DamageSourcePredicate(this.field_203982_a, this.field_203983_b, this.field_203984_c, this.field_203985_d, this.field_203986_e, this.field_203987_f, this.field_203988_g, this.field_203989_h, this.field_203990_i);
      }
   }
}
