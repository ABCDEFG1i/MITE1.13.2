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
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class VillagerTradeTrigger implements ICriterionTrigger<VillagerTradeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");
   private final Map<PlayerAdvancements, VillagerTradeTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> p_192165_2_) {
      VillagerTradeTrigger.Listeners villagertradetrigger$listeners = this.listeners.get(p_192165_1_);
      if (villagertradetrigger$listeners == null) {
         villagertradetrigger$listeners = new VillagerTradeTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, villagertradetrigger$listeners);
      }

      villagertradetrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> p_192164_2_) {
      VillagerTradeTrigger.Listeners villagertradetrigger$listeners = this.listeners.get(p_192164_1_);
      if (villagertradetrigger$listeners != null) {
         villagertradetrigger$listeners.remove(p_192164_2_);
         if (villagertradetrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public VillagerTradeTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("villager"));
      ItemPredicate itempredicate = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new VillagerTradeTrigger.Instance(entitypredicate, itempredicate);
   }

   public void trigger(EntityPlayerMP p_192234_1_, EntityVillager p_192234_2_, ItemStack p_192234_3_) {
      VillagerTradeTrigger.Listeners villagertradetrigger$listeners = this.listeners.get(p_192234_1_.getAdvancements());
      if (villagertradetrigger$listeners != null) {
         villagertradetrigger$listeners.trigger(p_192234_1_, p_192234_2_, p_192234_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate villager;
      private final ItemPredicate item;

      public Instance(EntityPredicate p_i47457_1_, ItemPredicate p_i47457_2_) {
         super(VillagerTradeTrigger.ID);
         this.villager = p_i47457_1_;
         this.item = p_i47457_2_;
      }

      public static VillagerTradeTrigger.Instance func_203939_c() {
         return new VillagerTradeTrigger.Instance(EntityPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean test(EntityPlayerMP p_192285_1_, EntityVillager p_192285_2_, ItemStack p_192285_3_) {
         if (!this.villager.test(p_192285_1_, p_192285_2_)) {
            return false;
         } else {
            return this.item.test(p_192285_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("villager", this.villager.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<VillagerTradeTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47458_1_) {
         this.playerAdvancements = p_i47458_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> p_192540_1_) {
         this.listeners.add(p_192540_1_);
      }

      public void remove(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> p_192538_1_) {
         this.listeners.remove(p_192538_1_);
      }

      public void trigger(EntityPlayerMP p_192537_1_, EntityVillager p_192537_2_, ItemStack p_192537_3_) {
         List<ICriterionTrigger.Listener<VillagerTradeTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192537_1_, p_192537_2_, p_192537_3_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
