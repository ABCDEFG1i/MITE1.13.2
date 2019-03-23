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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public class PositionTrigger implements ICriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation id;
   private final Map<PlayerAdvancements, PositionTrigger.Listeners> listeners = Maps.newHashMap();

   public PositionTrigger(ResourceLocation p_i47432_1_) {
      this.id = p_i47432_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<PositionTrigger.Instance> p_192165_2_) {
      PositionTrigger.Listeners positiontrigger$listeners = this.listeners.get(p_192165_1_);
      if (positiontrigger$listeners == null) {
         positiontrigger$listeners = new PositionTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, positiontrigger$listeners);
      }

      positiontrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<PositionTrigger.Instance> p_192164_2_) {
      PositionTrigger.Listeners positiontrigger$listeners = this.listeners.get(p_192164_1_);
      if (positiontrigger$listeners != null) {
         positiontrigger$listeners.remove(p_192164_2_);
         if (positiontrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public PositionTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      LocationPredicate locationpredicate = LocationPredicate.deserialize(p_192166_1_);
      return new PositionTrigger.Instance(this.id, locationpredicate);
   }

   public void trigger(EntityPlayerMP p_192215_1_) {
      PositionTrigger.Listeners positiontrigger$listeners = this.listeners.get(p_192215_1_.getAdvancements());
      if (positiontrigger$listeners != null) {
         positiontrigger$listeners.trigger(p_192215_1_.getServerWorld(), p_192215_1_.posX, p_192215_1_.posY, p_192215_1_.posZ);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final LocationPredicate location;

      public Instance(ResourceLocation p_i47544_1_, LocationPredicate p_i47544_2_) {
         super(p_i47544_1_);
         this.location = p_i47544_2_;
      }

      public static PositionTrigger.Instance func_203932_a(LocationPredicate p_203932_0_) {
         return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, p_203932_0_);
      }

      public static PositionTrigger.Instance func_203931_c() {
         return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public boolean test(WorldServer p_193204_1_, double p_193204_2_, double p_193204_4_, double p_193204_6_) {
         return this.location.test(p_193204_1_, p_193204_2_, p_193204_4_, p_193204_6_);
      }

      public JsonElement serialize() {
         return this.location.serialize();
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<PositionTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47442_1_) {
         this.playerAdvancements = p_i47442_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<PositionTrigger.Instance> p_192510_1_) {
         this.listeners.add(p_192510_1_);
      }

      public void remove(ICriterionTrigger.Listener<PositionTrigger.Instance> p_192507_1_) {
         this.listeners.remove(p_192507_1_);
      }

      public void trigger(WorldServer p_193462_1_, double p_193462_2_, double p_193462_4_, double p_193462_6_) {
         List<ICriterionTrigger.Listener<PositionTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<PositionTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193462_1_, p_193462_2_, p_193462_4_, p_193462_6_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<PositionTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
