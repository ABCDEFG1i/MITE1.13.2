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
import net.minecraft.util.math.Vec3d;

public class LevitationTrigger implements ICriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");
   private final Map<PlayerAdvancements, LevitationTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<LevitationTrigger.Instance> p_192165_2_) {
      LevitationTrigger.Listeners levitationtrigger$listeners = this.listeners.get(p_192165_1_);
      if (levitationtrigger$listeners == null) {
         levitationtrigger$listeners = new LevitationTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, levitationtrigger$listeners);
      }

      levitationtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<LevitationTrigger.Instance> p_192164_2_) {
      LevitationTrigger.Listeners levitationtrigger$listeners = this.listeners.get(p_192164_1_);
      if (levitationtrigger$listeners != null) {
         levitationtrigger$listeners.remove(p_192164_2_);
         if (levitationtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public LevitationTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DistancePredicate distancepredicate = DistancePredicate.deserialize(p_192166_1_.get("distance"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(p_192166_1_.get("duration"));
      return new LevitationTrigger.Instance(distancepredicate, minmaxbounds$intbound);
   }

   public void trigger(EntityPlayerMP p_193162_1_, Vec3d p_193162_2_, int p_193162_3_) {
      LevitationTrigger.Listeners levitationtrigger$listeners = this.listeners.get(p_193162_1_.getAdvancements());
      if (levitationtrigger$listeners != null) {
         levitationtrigger$listeners.trigger(p_193162_1_, p_193162_2_, p_193162_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.IntBound duration;

      public Instance(DistancePredicate p_i49729_1_, MinMaxBounds.IntBound p_i49729_2_) {
         super(LevitationTrigger.ID);
         this.distance = p_i49729_1_;
         this.duration = p_i49729_2_;
      }

      public static LevitationTrigger.Instance func_203930_a(DistancePredicate p_203930_0_) {
         return new LevitationTrigger.Instance(p_203930_0_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(EntityPlayerMP p_193201_1_, Vec3d p_193201_2_, int p_193201_3_) {
         if (!this.distance.test(p_193201_2_.x, p_193201_2_.y, p_193201_2_.z, p_193201_1_.posX, p_193201_1_.posY, p_193201_1_.posZ)) {
            return false;
         } else {
            return this.duration.test(p_193201_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("distance", this.distance.serialize());
         jsonobject.add("duration", this.duration.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<LevitationTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47572_1_) {
         this.playerAdvancements = p_i47572_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<LevitationTrigger.Instance> p_193449_1_) {
         this.listeners.add(p_193449_1_);
      }

      public void remove(ICriterionTrigger.Listener<LevitationTrigger.Instance> p_193446_1_) {
         this.listeners.remove(p_193446_1_);
      }

      public void trigger(EntityPlayerMP p_193448_1_, Vec3d p_193448_2_, int p_193448_3_) {
         List<ICriterionTrigger.Listener<LevitationTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<LevitationTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193448_1_, p_193448_2_, p_193448_3_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<LevitationTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
