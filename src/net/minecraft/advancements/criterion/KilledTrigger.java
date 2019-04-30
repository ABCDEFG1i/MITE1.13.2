package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class KilledTrigger implements ICriterionTrigger<KilledTrigger.Instance> {
   private final Map<PlayerAdvancements, KilledTrigger.Listeners> listeners = Maps.newHashMap();
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation p_i47433_1_) {
      this.id = p_i47433_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<KilledTrigger.Instance> p_192165_2_) {
      KilledTrigger.Listeners killedtrigger$listeners = this.listeners.get(p_192165_1_);
      if (killedtrigger$listeners == null) {
         killedtrigger$listeners = new KilledTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, killedtrigger$listeners);
      }

      killedtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<KilledTrigger.Instance> p_192164_2_) {
      KilledTrigger.Listeners killedtrigger$listeners = this.listeners.get(p_192164_1_);
      if (killedtrigger$listeners != null) {
         killedtrigger$listeners.remove(p_192164_2_);
         if (killedtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public KilledTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new KilledTrigger.Instance(this.id, EntityPredicate.deserialize(p_192166_1_.get("entity")), DamageSourcePredicate.deserialize(p_192166_1_.get("killing_blow")));
   }

   public void trigger(EntityPlayerMP p_192211_1_, Entity p_192211_2_, DamageSource p_192211_3_) {
      KilledTrigger.Listeners killedtrigger$listeners = this.listeners.get(p_192211_1_.getAdvancements());
      if (killedtrigger$listeners != null) {
         killedtrigger$listeners.trigger(p_192211_1_, p_192211_2_, p_192211_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate entity;
      private final DamageSourcePredicate killingBlow;
      private boolean isGlobal;

      public Instance(ResourceLocation p_i47454_1_, EntityPredicate p_i47454_2_, DamageSourcePredicate p_i47454_3_) {
         super(p_i47454_1_);
         this.entity = p_i47454_2_;
         this.killingBlow = p_i47454_3_;
      }

      public static KilledTrigger.Instance func_203928_a(EntityPredicate.Builder p_203928_0_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, p_203928_0_.func_204000_b(), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance func_203927_c() {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance func_203929_a(EntityPredicate.Builder p_203929_0_, DamageSourcePredicate.Builder p_203929_1_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, p_203929_0_.func_204000_b(), p_203929_1_.func_203979_b());
      }

      public static KilledTrigger.Instance func_203926_d() {
         return new KilledTrigger.Instance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public KilledTrigger.Instance setGlobal(boolean global) {
         isGlobal = global;
         return this;
      }

      public boolean test(EntityPlayerMP p_192270_1_, Entity p_192270_2_, DamageSource p_192270_3_) {
         return this.killingBlow.test(p_192270_1_, p_192270_3_) && this.entity.test(p_192270_1_, p_192270_2_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serialize());
         jsonobject.add("killing_blow", this.killingBlow.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<KilledTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47455_1_) {
         this.playerAdvancements = p_i47455_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<KilledTrigger.Instance> p_192504_1_) {
         this.listeners.add(p_192504_1_);
      }

      public void remove(ICriterionTrigger.Listener<KilledTrigger.Instance> p_192501_1_) {
         this.listeners.remove(p_192501_1_);
      }

      public void trigger(EntityPlayerMP p_192503_1_, Entity p_192503_2_, DamageSource p_192503_3_) {
         List<ICriterionTrigger.Listener<KilledTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<KilledTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192503_1_, p_192503_2_, p_192503_3_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<KilledTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
               if (listener1.getCriterionInstance().isGlobal){
                  for (EntityPlayer playerEntity : p_192503_1_.world.playerEntities) {
                     if (playerEntity instanceof EntityPlayerMP){
                      listener1.grantCriterion(((EntityPlayerMP) playerEntity).getAdvancements());
                     }
                  }
               }
            }
         }

      }
   }
}
