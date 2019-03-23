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
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PlayerHurtEntityTrigger implements ICriterionTrigger<PlayerHurtEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");
   private final Map<PlayerAdvancements, PlayerHurtEntityTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> p_192165_2_) {
      PlayerHurtEntityTrigger.Listeners playerhurtentitytrigger$listeners = this.listeners.get(p_192165_1_);
      if (playerhurtentitytrigger$listeners == null) {
         playerhurtentitytrigger$listeners = new PlayerHurtEntityTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, playerhurtentitytrigger$listeners);
      }

      playerhurtentitytrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> p_192164_2_) {
      PlayerHurtEntityTrigger.Listeners playerhurtentitytrigger$listeners = this.listeners.get(p_192164_1_);
      if (playerhurtentitytrigger$listeners != null) {
         playerhurtentitytrigger$listeners.remove(p_192164_2_);
         if (playerhurtentitytrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public PlayerHurtEntityTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DamagePredicate damagepredicate = DamagePredicate.deserialize(p_192166_1_.get("damage"));
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      return new PlayerHurtEntityTrigger.Instance(damagepredicate, entitypredicate);
   }

   public void trigger(EntityPlayerMP p_192220_1_, Entity p_192220_2_, DamageSource p_192220_3_, float p_192220_4_, float p_192220_5_, boolean p_192220_6_) {
      PlayerHurtEntityTrigger.Listeners playerhurtentitytrigger$listeners = this.listeners.get(p_192220_1_.getAdvancements());
      if (playerhurtentitytrigger$listeners != null) {
         playerhurtentitytrigger$listeners.trigger(p_192220_1_, p_192220_2_, p_192220_3_, p_192220_4_, p_192220_5_, p_192220_6_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final DamagePredicate damage;
      private final EntityPredicate entity;

      public Instance(DamagePredicate p_i47406_1_, EntityPredicate p_i47406_2_) {
         super(PlayerHurtEntityTrigger.ID);
         this.damage = p_i47406_1_;
         this.entity = p_i47406_2_;
      }

      public static PlayerHurtEntityTrigger.Instance func_203936_a(DamagePredicate.Builder p_203936_0_) {
         return new PlayerHurtEntityTrigger.Instance(p_203936_0_.build(), EntityPredicate.ANY);
      }

      public boolean test(EntityPlayerMP p_192278_1_, Entity p_192278_2_, DamageSource p_192278_3_, float p_192278_4_, float p_192278_5_, boolean p_192278_6_) {
         if (!this.damage.test(p_192278_1_, p_192278_3_, p_192278_4_, p_192278_5_, p_192278_6_)) {
            return false;
         } else {
            return this.entity.test(p_192278_1_, p_192278_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("damage", this.damage.serialize());
         jsonobject.add("entity", this.entity.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47407_1_) {
         this.playerAdvancements = p_i47407_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> p_192522_1_) {
         this.listeners.add(p_192522_1_);
      }

      public void remove(ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> p_192519_1_) {
         this.listeners.remove(p_192519_1_);
      }

      public void trigger(EntityPlayerMP p_192521_1_, Entity p_192521_2_, DamageSource p_192521_3_, float p_192521_4_, float p_192521_5_, boolean p_192521_6_) {
         List<ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192521_1_, p_192521_2_, p_192521_3_, p_192521_4_, p_192521_5_, p_192521_6_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<PlayerHurtEntityTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
