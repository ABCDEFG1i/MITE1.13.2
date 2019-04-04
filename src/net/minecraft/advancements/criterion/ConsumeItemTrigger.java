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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ConsumeItemTrigger implements ICriterionTrigger<ConsumeItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");
   private final Map<PlayerAdvancements, ConsumeItemTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> p_192165_2_) {
      ConsumeItemTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(p_192165_1_);
      if (consumeitemtrigger$listeners == null) {
         consumeitemtrigger$listeners = new ConsumeItemTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, consumeitemtrigger$listeners);
      }

      consumeitemtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> p_192164_2_) {
      ConsumeItemTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(p_192164_1_);
      if (consumeitemtrigger$listeners != null) {
         consumeitemtrigger$listeners.remove(p_192164_2_);
         if (consumeitemtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public ConsumeItemTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new ConsumeItemTrigger.Instance(ItemPredicate.deserialize(p_192166_1_.get("item")));
   }

   public void trigger(EntityPlayerMP p_193148_1_, ItemStack p_193148_2_) {
      ConsumeItemTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(p_193148_1_.getAdvancements());
      if (consumeitemtrigger$listeners != null) {
         consumeitemtrigger$listeners.trigger(p_193148_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i47562_1_) {
         super(ConsumeItemTrigger.ID);
         this.item = p_i47562_1_;
      }

      public static ConsumeItemTrigger.Instance func_203914_c() {
         return new ConsumeItemTrigger.Instance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.Instance func_203913_a(IItemProvider p_203913_0_) {
         return new ConsumeItemTrigger.Instance(new ItemPredicate(null, p_203913_0_.asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, new EnchantmentPredicate[0],
                 null, NBTPredicate.ANY));
      }

      public boolean test(ItemStack p_193193_1_) {
         return this.item.test(p_193193_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<ConsumeItemTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47563_1_) {
         this.playerAdvancements = p_i47563_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> p_193239_1_) {
         this.listeners.add(p_193239_1_);
      }

      public void remove(ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> p_193237_1_) {
         this.listeners.remove(p_193237_1_);
      }

      public void trigger(ItemStack p_193240_1_) {
         List<ICriterionTrigger.Listener<ConsumeItemTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193240_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ConsumeItemTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
