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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger implements ICriterionTrigger<EntityHurtPlayerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");
   private final Map<PlayerAdvancements, EntityHurtPlayerTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> p_192165_2_) {
      EntityHurtPlayerTrigger.Listeners entityhurtplayertrigger$listeners = this.listeners.get(p_192165_1_);
      if (entityhurtplayertrigger$listeners == null) {
         entityhurtplayertrigger$listeners = new EntityHurtPlayerTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, entityhurtplayertrigger$listeners);
      }

      entityhurtplayertrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> p_192164_2_) {
      EntityHurtPlayerTrigger.Listeners entityhurtplayertrigger$listeners = this.listeners.get(p_192164_1_);
      if (entityhurtplayertrigger$listeners != null) {
         entityhurtplayertrigger$listeners.remove(p_192164_2_);
         if (entityhurtplayertrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public EntityHurtPlayerTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DamagePredicate damagepredicate = DamagePredicate.deserialize(p_192166_1_.get("damage"));
      return new EntityHurtPlayerTrigger.Instance(damagepredicate);
   }

   public void trigger(EntityPlayerMP p_192200_1_, DamageSource p_192200_2_, float p_192200_3_, float p_192200_4_, boolean p_192200_5_) {
      EntityHurtPlayerTrigger.Listeners entityhurtplayertrigger$listeners = this.listeners.get(p_192200_1_.getAdvancements());
      if (entityhurtplayertrigger$listeners != null) {
         entityhurtplayertrigger$listeners.trigger(p_192200_1_, p_192200_2_, p_192200_3_, p_192200_4_, p_192200_5_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final DamagePredicate damage;

      public Instance(DamagePredicate p_i47438_1_) {
         super(EntityHurtPlayerTrigger.ID);
         this.damage = p_i47438_1_;
      }

      public static EntityHurtPlayerTrigger.Instance func_203921_a(DamagePredicate.Builder p_203921_0_) {
         return new EntityHurtPlayerTrigger.Instance(p_203921_0_.build());
      }

      public boolean test(EntityPlayerMP p_192263_1_, DamageSource p_192263_2_, float p_192263_3_, float p_192263_4_, boolean p_192263_5_) {
         return this.damage.test(p_192263_1_, p_192263_2_, p_192263_3_, p_192263_4_, p_192263_5_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("damage", this.damage.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47439_1_) {
         this.playerAdvancements = p_i47439_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> p_192477_1_) {
         this.listeners.add(p_192477_1_);
      }

      public void remove(ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> p_192475_1_) {
         this.listeners.remove(p_192475_1_);
      }

      public void trigger(EntityPlayerMP p_192478_1_, DamageSource p_192478_2_, float p_192478_3_, float p_192478_4_, boolean p_192478_5_) {
         List<ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192478_1_, p_192478_2_, p_192478_3_, p_192478_4_, p_192478_5_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EntityHurtPlayerTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
