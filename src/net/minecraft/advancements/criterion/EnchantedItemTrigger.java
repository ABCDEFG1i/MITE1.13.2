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

public class EnchantedItemTrigger implements ICriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");
   private final Map<PlayerAdvancements, EnchantedItemTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> p_192165_2_) {
      EnchantedItemTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(p_192165_1_);
      if (enchanteditemtrigger$listeners == null) {
         enchanteditemtrigger$listeners = new EnchantedItemTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, enchanteditemtrigger$listeners);
      }

      enchanteditemtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> p_192164_2_) {
      EnchantedItemTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(p_192164_1_);
      if (enchanteditemtrigger$listeners != null) {
         enchanteditemtrigger$listeners.remove(p_192164_2_);
         if (enchanteditemtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public EnchantedItemTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(p_192166_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(p_192166_1_.get("levels"));
      return new EnchantedItemTrigger.Instance(itempredicate, minmaxbounds$intbound);
   }

   public void trigger(EntityPlayerMP p_192190_1_, ItemStack p_192190_2_, int p_192190_3_) {
      EnchantedItemTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(p_192190_1_.getAdvancements());
      if (enchanteditemtrigger$listeners != null) {
         enchanteditemtrigger$listeners.trigger(p_192190_2_, p_192190_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound levels;

      public Instance(ItemPredicate p_i49731_1_, MinMaxBounds.IntBound p_i49731_2_) {
         super(EnchantedItemTrigger.ID);
         this.item = p_i49731_1_;
         this.levels = p_i49731_2_;
      }

      public static EnchantedItemTrigger.Instance func_203918_c() {
         return new EnchantedItemTrigger.Instance(ItemPredicate.ANY, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack p_192257_1_, int p_192257_2_) {
         if (!this.item.test(p_192257_1_)) {
            return false;
         } else {
            return this.levels.test(p_192257_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("levels", this.levels.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EnchantedItemTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47377_1_) {
         this.playerAdvancements = p_i47377_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> p_192460_1_) {
         this.listeners.add(p_192460_1_);
      }

      public void remove(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> p_192457_1_) {
         this.listeners.remove(p_192457_1_);
      }

      public void trigger(ItemStack p_192459_1_, int p_192459_2_) {
         List<ICriterionTrigger.Listener<EnchantedItemTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192459_1_, p_192459_2_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EnchantedItemTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
