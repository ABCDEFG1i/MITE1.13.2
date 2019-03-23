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
import net.minecraft.world.WorldServer;

public class NetherTravelTrigger implements ICriterionTrigger<NetherTravelTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");
   private final Map<PlayerAdvancements, NetherTravelTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<NetherTravelTrigger.Instance> p_192165_2_) {
      NetherTravelTrigger.Listeners nethertraveltrigger$listeners = this.listeners.get(p_192165_1_);
      if (nethertraveltrigger$listeners == null) {
         nethertraveltrigger$listeners = new NetherTravelTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, nethertraveltrigger$listeners);
      }

      nethertraveltrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<NetherTravelTrigger.Instance> p_192164_2_) {
      NetherTravelTrigger.Listeners nethertraveltrigger$listeners = this.listeners.get(p_192164_1_);
      if (nethertraveltrigger$listeners != null) {
         nethertraveltrigger$listeners.remove(p_192164_2_);
         if (nethertraveltrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public NetherTravelTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      LocationPredicate locationpredicate = LocationPredicate.deserialize(p_192166_1_.get("entered"));
      LocationPredicate locationpredicate1 = LocationPredicate.deserialize(p_192166_1_.get("exited"));
      DistancePredicate distancepredicate = DistancePredicate.deserialize(p_192166_1_.get("distance"));
      return new NetherTravelTrigger.Instance(locationpredicate, locationpredicate1, distancepredicate);
   }

   public void trigger(EntityPlayerMP p_193168_1_, Vec3d p_193168_2_) {
      NetherTravelTrigger.Listeners nethertraveltrigger$listeners = this.listeners.get(p_193168_1_.getAdvancements());
      if (nethertraveltrigger$listeners != null) {
         nethertraveltrigger$listeners.trigger(p_193168_1_.getServerWorld(), p_193168_2_, p_193168_1_.posX, p_193168_1_.posY, p_193168_1_.posZ);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public Instance(LocationPredicate p_i47574_1_, LocationPredicate p_i47574_2_, DistancePredicate p_i47574_3_) {
         super(NetherTravelTrigger.ID);
         this.entered = p_i47574_1_;
         this.exited = p_i47574_2_;
         this.distance = p_i47574_3_;
      }

      public static NetherTravelTrigger.Instance func_203933_a(DistancePredicate p_203933_0_) {
         return new NetherTravelTrigger.Instance(LocationPredicate.ANY, LocationPredicate.ANY, p_203933_0_);
      }

      public boolean test(WorldServer p_193206_1_, Vec3d p_193206_2_, double p_193206_3_, double p_193206_5_, double p_193206_7_) {
         if (!this.entered.test(p_193206_1_, p_193206_2_.x, p_193206_2_.y, p_193206_2_.z)) {
            return false;
         } else if (!this.exited.test(p_193206_1_, p_193206_3_, p_193206_5_, p_193206_7_)) {
            return false;
         } else {
            return this.distance.test(p_193206_2_.x, p_193206_2_.y, p_193206_2_.z, p_193206_3_, p_193206_5_, p_193206_7_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entered", this.entered.serialize());
         jsonobject.add("exited", this.exited.serialize());
         jsonobject.add("distance", this.distance.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<NetherTravelTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47575_1_) {
         this.playerAdvancements = p_i47575_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<NetherTravelTrigger.Instance> p_193484_1_) {
         this.listeners.add(p_193484_1_);
      }

      public void remove(ICriterionTrigger.Listener<NetherTravelTrigger.Instance> p_193481_1_) {
         this.listeners.remove(p_193481_1_);
      }

      public void trigger(WorldServer p_193483_1_, Vec3d p_193483_2_, double p_193483_3_, double p_193483_5_, double p_193483_7_) {
         List<ICriterionTrigger.Listener<NetherTravelTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<NetherTravelTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193483_1_, p_193483_2_, p_193483_3_, p_193483_5_, p_193483_7_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<NetherTravelTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
