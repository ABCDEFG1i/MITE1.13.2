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
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class BredAnimalsTrigger implements ICriterionTrigger<BredAnimalsTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");
   private final Map<PlayerAdvancements, BredAnimalsTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> p_192165_2_) {
      BredAnimalsTrigger.Listeners bredanimalstrigger$listeners = this.listeners.get(p_192165_1_);
      if (bredanimalstrigger$listeners == null) {
         bredanimalstrigger$listeners = new BredAnimalsTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, bredanimalstrigger$listeners);
      }

      bredanimalstrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> p_192164_2_) {
      BredAnimalsTrigger.Listeners bredanimalstrigger$listeners = this.listeners.get(p_192164_1_);
      if (bredanimalstrigger$listeners != null) {
         bredanimalstrigger$listeners.remove(p_192164_2_);
         if (bredanimalstrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public BredAnimalsTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("parent"));
      EntityPredicate entitypredicate1 = EntityPredicate.deserialize(p_192166_1_.get("partner"));
      EntityPredicate entitypredicate2 = EntityPredicate.deserialize(p_192166_1_.get("child"));
      return new BredAnimalsTrigger.Instance(entitypredicate, entitypredicate1, entitypredicate2);
   }

   public void trigger(EntityPlayerMP p_192168_1_, EntityAnimal p_192168_2_, EntityAnimal p_192168_3_, @Nullable EntityAgeable p_192168_4_) {
      BredAnimalsTrigger.Listeners bredanimalstrigger$listeners = this.listeners.get(p_192168_1_.getAdvancements());
      if (bredanimalstrigger$listeners != null) {
         bredanimalstrigger$listeners.trigger(p_192168_1_, p_192168_2_, p_192168_3_, p_192168_4_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate parent;
      private final EntityPredicate partner;
      private final EntityPredicate child;

      public Instance(EntityPredicate p_i47408_1_, EntityPredicate p_i47408_2_, EntityPredicate p_i47408_3_) {
         super(BredAnimalsTrigger.ID);
         this.parent = p_i47408_1_;
         this.partner = p_i47408_2_;
         this.child = p_i47408_3_;
      }

      public static BredAnimalsTrigger.Instance func_203908_c() {
         return new BredAnimalsTrigger.Instance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public static BredAnimalsTrigger.Instance func_203909_a(EntityPredicate.Builder p_203909_0_) {
         return new BredAnimalsTrigger.Instance(p_203909_0_.func_204000_b(), EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean test(EntityPlayerMP p_192246_1_, EntityAnimal p_192246_2_, EntityAnimal p_192246_3_, @Nullable EntityAgeable p_192246_4_) {
         if (!this.child.test(p_192246_1_, p_192246_4_)) {
            return false;
         } else {
            return this.parent.test(p_192246_1_, p_192246_2_) && this.partner.test(p_192246_1_, p_192246_3_) || this.parent.test(p_192246_1_, p_192246_3_) && this.partner.test(p_192246_1_, p_192246_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("parent", this.parent.serialize());
         jsonobject.add("partner", this.partner.serialize());
         jsonobject.add("child", this.child.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<BredAnimalsTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47409_1_) {
         this.playerAdvancements = p_i47409_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> p_192343_1_) {
         this.listeners.add(p_192343_1_);
      }

      public void remove(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> p_192340_1_) {
         this.listeners.remove(p_192340_1_);
      }

      public void trigger(EntityPlayerMP p_192342_1_, EntityAnimal p_192342_2_, EntityAnimal p_192342_3_, @Nullable EntityAgeable p_192342_4_) {
         List<ICriterionTrigger.Listener<BredAnimalsTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192342_1_, p_192342_2_, p_192342_3_, p_192342_4_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
