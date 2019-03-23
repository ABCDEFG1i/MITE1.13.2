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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger implements ICriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");
   private final Map<PlayerAdvancements, UsedTotemTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<UsedTotemTrigger.Instance> p_192165_2_) {
      UsedTotemTrigger.Listeners usedtotemtrigger$listeners = this.listeners.get(p_192165_1_);
      if (usedtotemtrigger$listeners == null) {
         usedtotemtrigger$listeners = new UsedTotemTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, usedtotemtrigger$listeners);
      }

      usedtotemtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<UsedTotemTrigger.Instance> p_192164_2_) {
      UsedTotemTrigger.Listeners usedtotemtrigger$listeners = this.listeners.get(p_192164_1_);
      if (usedtotemtrigger$listeners != null) {
         usedtotemtrigger$listeners.remove(p_192164_2_);
         if (usedtotemtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public UsedTotemTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new UsedTotemTrigger.Instance(itempredicate);
   }

   public void trigger(EntityPlayerMP p_193187_1_, ItemStack p_193187_2_) {
      UsedTotemTrigger.Listeners usedtotemtrigger$listeners = this.listeners.get(p_193187_1_.getAdvancements());
      if (usedtotemtrigger$listeners != null) {
         usedtotemtrigger$listeners.trigger(p_193187_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i47564_1_) {
         super(UsedTotemTrigger.ID);
         this.item = p_i47564_1_;
      }

      public static UsedTotemTrigger.Instance func_203941_a(IItemProvider p_203941_0_) {
         return new UsedTotemTrigger.Instance(ItemPredicate.Builder.create().func_200308_a(p_203941_0_).build());
      }

      public boolean test(ItemStack p_193218_1_) {
         return this.item.test(p_193218_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<UsedTotemTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47565_1_) {
         this.playerAdvancements = p_i47565_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> p_193508_1_) {
         this.listeners.add(p_193508_1_);
      }

      public void remove(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> p_193506_1_) {
         this.listeners.remove(p_193506_1_);
      }

      public void trigger(ItemStack p_193509_1_) {
         List<ICriterionTrigger.Listener<UsedTotemTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193509_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<UsedTotemTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
