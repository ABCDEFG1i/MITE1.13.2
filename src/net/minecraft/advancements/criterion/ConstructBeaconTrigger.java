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
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;

public class ConstructBeaconTrigger implements ICriterionTrigger<ConstructBeaconTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");
   private final Map<PlayerAdvancements, ConstructBeaconTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> p_192165_2_) {
      ConstructBeaconTrigger.Listeners constructbeacontrigger$listeners = this.listeners.get(p_192165_1_);
      if (constructbeacontrigger$listeners == null) {
         constructbeacontrigger$listeners = new ConstructBeaconTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, constructbeacontrigger$listeners);
      }

      constructbeacontrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> p_192164_2_) {
      ConstructBeaconTrigger.Listeners constructbeacontrigger$listeners = this.listeners.get(p_192164_1_);
      if (constructbeacontrigger$listeners != null) {
         constructbeacontrigger$listeners.remove(p_192164_2_);
         if (constructbeacontrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public ConstructBeaconTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(p_192166_1_.get("level"));
      return new ConstructBeaconTrigger.Instance(minmaxbounds$intbound);
   }

   public void trigger(EntityPlayerMP p_192180_1_, TileEntityBeacon p_192180_2_) {
      ConstructBeaconTrigger.Listeners constructbeacontrigger$listeners = this.listeners.get(p_192180_1_.getAdvancements());
      if (constructbeacontrigger$listeners != null) {
         constructbeacontrigger$listeners.trigger(p_192180_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.IntBound level;

      public Instance(MinMaxBounds.IntBound p_i49736_1_) {
         super(ConstructBeaconTrigger.ID);
         this.level = p_i49736_1_;
      }

      public static ConstructBeaconTrigger.Instance func_203912_a(MinMaxBounds.IntBound p_203912_0_) {
         return new ConstructBeaconTrigger.Instance(p_203912_0_);
      }

      public boolean test(TileEntityBeacon p_192252_1_) {
         return this.level.test(p_192252_1_.getLevels());
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("level", this.level.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47374_1_) {
         this.playerAdvancements = p_i47374_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> p_192355_1_) {
         this.listeners.add(p_192355_1_);
      }

      public void remove(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> p_192353_1_) {
         this.listeners.remove(p_192353_1_);
      }

      public void trigger(TileEntityBeacon p_192352_1_) {
         List<ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192352_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ConstructBeaconTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
