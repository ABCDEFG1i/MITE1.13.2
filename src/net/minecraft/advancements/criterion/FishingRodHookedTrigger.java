package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FishingRodHookedTrigger implements ICriterionTrigger<FishingRodHookedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");
   private final Map<PlayerAdvancements, FishingRodHookedTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> p_192165_2_) {
      FishingRodHookedTrigger.Listeners fishingrodhookedtrigger$listeners = this.listeners.get(p_192165_1_);
      if (fishingrodhookedtrigger$listeners == null) {
         fishingrodhookedtrigger$listeners = new FishingRodHookedTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, fishingrodhookedtrigger$listeners);
      }

      fishingrodhookedtrigger$listeners.addListener(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> p_192164_2_) {
      FishingRodHookedTrigger.Listeners fishingrodhookedtrigger$listeners = this.listeners.get(p_192164_1_);
      if (fishingrodhookedtrigger$listeners != null) {
         fishingrodhookedtrigger$listeners.removeListener(p_192164_2_);
         if (fishingrodhookedtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public FishingRodHookedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(p_192166_1_.get("rod"));
      EntityPredicate entitypredicate = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      ItemPredicate itempredicate1 = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new FishingRodHookedTrigger.Instance(itempredicate, entitypredicate, itempredicate1);
   }

   public void trigger(EntityPlayerMP p_204820_1_, ItemStack p_204820_2_, EntityFishHook p_204820_3_, Collection<ItemStack> p_204820_4_) {
      FishingRodHookedTrigger.Listeners fishingrodhookedtrigger$listeners = this.listeners.get(p_204820_1_.getAdvancements());
      if (fishingrodhookedtrigger$listeners != null) {
         fishingrodhookedtrigger$listeners.trigger(p_204820_1_, p_204820_2_, p_204820_3_, p_204820_4_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i48916_1_, EntityPredicate p_i48916_2_, ItemPredicate p_i48916_3_) {
         super(FishingRodHookedTrigger.ID);
         this.rod = p_i48916_1_;
         this.entity = p_i48916_2_;
         this.item = p_i48916_3_;
      }

      public static FishingRodHookedTrigger.Instance func_204829_a(ItemPredicate p_204829_0_, EntityPredicate p_204829_1_, ItemPredicate p_204829_2_) {
         return new FishingRodHookedTrigger.Instance(p_204829_0_, p_204829_1_, p_204829_2_);
      }

      public boolean func_204830_a(EntityPlayerMP p_204830_1_, ItemStack p_204830_2_, EntityFishHook p_204830_3_, Collection<ItemStack> p_204830_4_) {
         if (!this.rod.test(p_204830_2_)) {
            return false;
         } else if (!this.entity.test(p_204830_1_, p_204830_3_.caughtEntity)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean flag = false;
               if (p_204830_3_.caughtEntity instanceof EntityItem) {
                  EntityItem entityitem = (EntityItem)p_204830_3_.caughtEntity;
                  if (this.item.test(entityitem.getItem())) {
                     flag = true;
                  }
               }

               for(ItemStack itemstack : p_204830_4_) {
                  if (this.item.test(itemstack)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("rod", this.rod.serialize());
         jsonobject.add("entity", this.entity.serialize());
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements field_204862_a;
      private final Set<ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i48917_1_) {
         this.field_204862_a = p_i48917_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> p_204858_1_) {
         this.listeners.add(p_204858_1_);
      }

      public void removeListener(ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> p_204861_1_) {
         this.listeners.remove(p_204861_1_);
      }

      public void trigger(EntityPlayerMP p_204859_1_, ItemStack p_204859_2_, EntityFishHook p_204859_3_, Collection<ItemStack> p_204859_4_) {
         List<ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().func_204830_a(p_204859_1_, p_204859_2_, p_204859_3_, p_204859_4_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<FishingRodHookedTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.field_204862_a);
            }
         }

      }
   }
}
