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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemDurabilityTrigger implements ICriterionTrigger<ItemDurabilityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");
   private final Map<PlayerAdvancements, ItemDurabilityTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> p_192165_2_) {
      ItemDurabilityTrigger.Listeners itemdurabilitytrigger$listeners = this.listeners.get(p_192165_1_);
      if (itemdurabilitytrigger$listeners == null) {
         itemdurabilitytrigger$listeners = new ItemDurabilityTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, itemdurabilitytrigger$listeners);
      }

      itemdurabilitytrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> p_192164_2_) {
      ItemDurabilityTrigger.Listeners itemdurabilitytrigger$listeners = this.listeners.get(p_192164_1_);
      if (itemdurabilitytrigger$listeners != null) {
         itemdurabilitytrigger$listeners.remove(p_192164_2_);
         if (itemdurabilitytrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public ItemDurabilityTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(p_192166_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(p_192166_1_.get("durability"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.func_211344_a(p_192166_1_.get("delta"));
      return new ItemDurabilityTrigger.Instance(itempredicate, minmaxbounds$intbound, minmaxbounds$intbound1);
   }

   public void trigger(EntityPlayerMP p_193158_1_, ItemStack p_193158_2_, int p_193158_3_) {
      ItemDurabilityTrigger.Listeners itemdurabilitytrigger$listeners = this.listeners.get(p_193158_1_.getAdvancements());
      if (itemdurabilitytrigger$listeners != null) {
         itemdurabilitytrigger$listeners.trigger(p_193158_2_, p_193158_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound durability;
      private final MinMaxBounds.IntBound delta;

      public Instance(ItemPredicate p_i49703_1_, MinMaxBounds.IntBound p_i49703_2_, MinMaxBounds.IntBound p_i49703_3_) {
         super(ItemDurabilityTrigger.ID);
         this.item = p_i49703_1_;
         this.durability = p_i49703_2_;
         this.delta = p_i49703_3_;
      }

      public static ItemDurabilityTrigger.Instance func_211182_a(ItemPredicate p_211182_0_, MinMaxBounds.IntBound p_211182_1_) {
         return new ItemDurabilityTrigger.Instance(p_211182_0_, p_211182_1_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack p_193197_1_, int p_193197_2_) {
         if (!this.item.test(p_193197_1_)) {
            return false;
         } else if (!this.durability.test(p_193197_1_.getMaxDamage() - p_193197_2_)) {
            return false;
         } else {
            return this.delta.test(p_193197_1_.getDamage() - p_193197_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("durability", this.durability.serialize());
         jsonobject.add("delta", this.delta.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47512_1_) {
         this.playerAdvancements = p_i47512_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> p_193440_1_) {
         this.listeners.add(p_193440_1_);
      }

      public void remove(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> p_193438_1_) {
         this.listeners.remove(p_193438_1_);
      }

      public void trigger(ItemStack p_193441_1_, int p_193441_2_) {
         List<ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193441_1_, p_193441_2_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ItemDurabilityTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
