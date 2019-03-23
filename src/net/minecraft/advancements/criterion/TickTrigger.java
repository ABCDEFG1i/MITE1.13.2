package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class TickTrigger implements ICriterionTrigger<TickTrigger.Instance> {
   public static final ResourceLocation ID = new ResourceLocation("tick");
   private final Map<PlayerAdvancements, TickTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<TickTrigger.Instance> p_192165_2_) {
      TickTrigger.Listeners ticktrigger$listeners = this.listeners.get(p_192165_1_);
      if (ticktrigger$listeners == null) {
         ticktrigger$listeners = new TickTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, ticktrigger$listeners);
      }

      ticktrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<TickTrigger.Instance> p_192164_2_) {
      TickTrigger.Listeners ticktrigger$listeners = this.listeners.get(p_192164_1_);
      if (ticktrigger$listeners != null) {
         ticktrigger$listeners.remove(p_192164_2_);
         if (ticktrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public TickTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new TickTrigger.Instance();
   }

   public void trigger(EntityPlayerMP p_193182_1_) {
      TickTrigger.Listeners ticktrigger$listeners = this.listeners.get(p_193182_1_.getAdvancements());
      if (ticktrigger$listeners != null) {
         ticktrigger$listeners.trigger();
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      public Instance() {
         super(TickTrigger.ID);
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<TickTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47496_1_) {
         this.playerAdvancements = p_i47496_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<TickTrigger.Instance> p_193502_1_) {
         this.listeners.add(p_193502_1_);
      }

      public void remove(ICriterionTrigger.Listener<TickTrigger.Instance> p_193500_1_) {
         this.listeners.remove(p_193500_1_);
      }

      public void trigger() {
         for(ICriterionTrigger.Listener<TickTrigger.Instance> listener : Lists.newArrayList(this.listeners)) {
            listener.grantCriterion(this.playerAdvancements);
         }

      }
   }
}
