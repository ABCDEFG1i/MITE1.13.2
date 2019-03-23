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
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger implements ICriterionTrigger<TameAnimalTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");
   private final Map<PlayerAdvancements, TameAnimalTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<TameAnimalTrigger.Instance> p_192165_2_) {
      TameAnimalTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(p_192165_1_);
      if (tameanimaltrigger$listeners == null) {
         tameanimaltrigger$listeners = new TameAnimalTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, tameanimaltrigger$listeners);
      }

      tameanimaltrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<TameAnimalTrigger.Instance> p_192164_2_) {
      TameAnimalTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(p_192164_1_);
      if (tameanimaltrigger$listeners != null) {
         tameanimaltrigger$listeners.remove(p_192164_2_);
         if (tameanimaltrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public TameAnimalTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      return new TameAnimalTrigger.Instance(entitypredicate);
   }

   public void trigger(EntityPlayerMP p_193178_1_, EntityAnimal p_193178_2_) {
      TameAnimalTrigger.Listeners tameanimaltrigger$listeners = this.listeners.get(p_193178_1_.getAdvancements());
      if (tameanimaltrigger$listeners != null) {
         tameanimaltrigger$listeners.trigger(p_193178_1_, p_193178_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate p_i47513_1_) {
         super(TameAnimalTrigger.ID);
         this.entity = p_i47513_1_;
      }

      public static TameAnimalTrigger.Instance func_203938_c() {
         return new TameAnimalTrigger.Instance(EntityPredicate.ANY);
      }

      public boolean test(EntityPlayerMP p_193216_1_, EntityAnimal p_193216_2_) {
         return this.entity.test(p_193216_1_, p_193216_2_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("entity", this.entity.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<TameAnimalTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47514_1_) {
         this.playerAdvancements = p_i47514_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<TameAnimalTrigger.Instance> p_193496_1_) {
         this.listeners.add(p_193496_1_);
      }

      public void remove(ICriterionTrigger.Listener<TameAnimalTrigger.Instance> p_193494_1_) {
         this.listeners.remove(p_193494_1_);
      }

      public void trigger(EntityPlayerMP p_193497_1_, EntityAnimal p_193497_2_) {
         List<ICriterionTrigger.Listener<TameAnimalTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<TameAnimalTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193497_1_, p_193497_2_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<TameAnimalTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
