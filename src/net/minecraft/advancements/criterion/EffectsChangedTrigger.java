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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger implements ICriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");
   private final Map<PlayerAdvancements, EffectsChangedTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> p_192165_2_) {
      EffectsChangedTrigger.Listeners effectschangedtrigger$listeners = this.listeners.get(p_192165_1_);
      if (effectschangedtrigger$listeners == null) {
         effectschangedtrigger$listeners = new EffectsChangedTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, effectschangedtrigger$listeners);
      }

      effectschangedtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> p_192164_2_) {
      EffectsChangedTrigger.Listeners effectschangedtrigger$listeners = this.listeners.get(p_192164_1_);
      if (effectschangedtrigger$listeners != null) {
         effectschangedtrigger$listeners.remove(p_192164_2_);
         if (effectschangedtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public EffectsChangedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(p_192166_1_.get("effects"));
      return new EffectsChangedTrigger.Instance(mobeffectspredicate);
   }

   public void trigger(EntityPlayerMP p_193153_1_) {
      EffectsChangedTrigger.Listeners effectschangedtrigger$listeners = this.listeners.get(p_193153_1_.getAdvancements());
      if (effectschangedtrigger$listeners != null) {
         effectschangedtrigger$listeners.trigger(p_193153_1_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(MobEffectsPredicate p_i47545_1_) {
         super(EffectsChangedTrigger.ID);
         this.effects = p_i47545_1_;
      }

      public static EffectsChangedTrigger.Instance func_203917_a(MobEffectsPredicate p_203917_0_) {
         return new EffectsChangedTrigger.Instance(p_203917_0_);
      }

      public boolean test(EntityPlayerMP p_193195_1_) {
         return this.effects.test(p_193195_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("effects", this.effects.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EffectsChangedTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47546_1_) {
         this.playerAdvancements = p_i47546_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> p_193431_1_) {
         this.listeners.add(p_193431_1_);
      }

      public void remove(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> p_193429_1_) {
         this.listeners.remove(p_193429_1_);
      }

      public void trigger(EntityPlayerMP p_193432_1_) {
         List<ICriterionTrigger.Listener<EffectsChangedTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193432_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EffectsChangedTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
