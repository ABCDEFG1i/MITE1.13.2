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
import net.minecraft.util.ResourceLocation;

public class SummonedEntityTrigger implements ICriterionTrigger<SummonedEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("summoned_entity");
   private final Map<PlayerAdvancements, SummonedEntityTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> p_192165_2_) {
      SummonedEntityTrigger.Listeners summonedentitytrigger$listeners = this.listeners.get(p_192165_1_);
      if (summonedentitytrigger$listeners == null) {
         summonedentitytrigger$listeners = new SummonedEntityTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, summonedentitytrigger$listeners);
      }

      summonedentitytrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> p_192164_2_) {
      SummonedEntityTrigger.Listeners summonedentitytrigger$listeners = this.listeners.get(p_192164_1_);
      if (summonedentitytrigger$listeners != null) {
         summonedentitytrigger$listeners.remove(p_192164_2_);
         if (summonedentitytrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public SummonedEntityTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      return new SummonedEntityTrigger.Instance(entitypredicate);
   }

   public void trigger(EntityPlayerMP p_192229_1_, Entity p_192229_2_) {
      SummonedEntityTrigger.Listeners summonedentitytrigger$listeners = this.listeners.get(p_192229_1_.getAdvancements());
      if (summonedentitytrigger$listeners != null) {
         summonedentitytrigger$listeners.trigger(p_192229_1_, p_192229_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate p_i47371_1_) {
         super(SummonedEntityTrigger.ID);
         this.entity = p_i47371_1_;
      }

      public static SummonedEntityTrigger.Instance func_203937_a(EntityPredicate.Builder p_203937_0_) {
         return new SummonedEntityTrigger.Instance(p_203937_0_.func_204000_b());
      }

      public boolean test(EntityPlayerMP p_192283_1_, Entity p_192283_2_) {
         return this.entity.test(p_192283_1_, p_192283_2_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<SummonedEntityTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47372_1_) {
         this.playerAdvancements = p_i47372_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> p_192534_1_) {
         this.listeners.add(p_192534_1_);
      }

      public void remove(ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> p_192531_1_) {
         this.listeners.remove(p_192531_1_);
      }

      public void trigger(EntityPlayerMP p_192533_1_, Entity p_192533_2_) {
         List<ICriterionTrigger.Listener<SummonedEntityTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192533_1_, p_192533_2_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<SummonedEntityTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
