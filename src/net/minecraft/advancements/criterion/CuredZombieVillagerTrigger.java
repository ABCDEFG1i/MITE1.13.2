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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class CuredZombieVillagerTrigger implements ICriterionTrigger<CuredZombieVillagerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");
   private final Map<PlayerAdvancements, CuredZombieVillagerTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> p_192165_2_) {
      CuredZombieVillagerTrigger.Listeners curedzombievillagertrigger$listeners = this.listeners.get(p_192165_1_);
      if (curedzombievillagertrigger$listeners == null) {
         curedzombievillagertrigger$listeners = new CuredZombieVillagerTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, curedzombievillagertrigger$listeners);
      }

      curedzombievillagertrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> p_192164_2_) {
      CuredZombieVillagerTrigger.Listeners curedzombievillagertrigger$listeners = this.listeners.get(p_192164_1_);
      if (curedzombievillagertrigger$listeners != null) {
         curedzombievillagertrigger$listeners.remove(p_192164_2_);
         if (curedzombievillagertrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public CuredZombieVillagerTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("zombie"));
      EntityPredicate entitypredicate1 = EntityPredicate.deserialize(p_192166_1_.get("villager"));
      return new CuredZombieVillagerTrigger.Instance(entitypredicate, entitypredicate1);
   }

   public void trigger(EntityPlayerMP p_192183_1_, EntityZombie p_192183_2_, EntityVillager p_192183_3_) {
      CuredZombieVillagerTrigger.Listeners curedzombievillagertrigger$listeners = this.listeners.get(p_192183_1_.getAdvancements());
      if (curedzombievillagertrigger$listeners != null) {
         curedzombievillagertrigger$listeners.trigger(p_192183_1_, p_192183_2_, p_192183_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate zombie;
      private final EntityPredicate villager;

      public Instance(EntityPredicate p_i47459_1_, EntityPredicate p_i47459_2_) {
         super(CuredZombieVillagerTrigger.ID);
         this.zombie = p_i47459_1_;
         this.villager = p_i47459_2_;
      }

      public static CuredZombieVillagerTrigger.Instance func_203916_c() {
         return new CuredZombieVillagerTrigger.Instance(EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean test(EntityPlayerMP p_192254_1_, EntityZombie p_192254_2_, EntityVillager p_192254_3_) {
         if (!this.zombie.test(p_192254_1_, p_192254_2_)) {
            return false;
         } else {
            return this.villager.test(p_192254_1_, p_192254_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("zombie", this.zombie.serialize());
         jsonobject.add("villager", this.villager.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47460_1_) {
         this.playerAdvancements = p_i47460_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> p_192360_1_) {
         this.listeners.add(p_192360_1_);
      }

      public void remove(ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> p_192358_1_) {
         this.listeners.remove(p_192358_1_);
      }

      public void trigger(EntityPlayerMP p_192361_1_, EntityZombie p_192361_2_, EntityVillager p_192361_3_) {
         List<ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192361_1_, p_192361_2_, p_192361_3_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
