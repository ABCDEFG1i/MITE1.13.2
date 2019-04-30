package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger implements ICriterionTrigger<InventoryChangeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");
   private final Map<PlayerAdvancements, InventoryChangeTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> p_192165_2_) {
      InventoryChangeTrigger.Listeners inventorychangetrigger$listeners = this.listeners.get(p_192165_1_);
      if (inventorychangetrigger$listeners == null) {
         inventorychangetrigger$listeners = new InventoryChangeTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, inventorychangetrigger$listeners);
      }

      inventorychangetrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> p_192164_2_) {
      InventoryChangeTrigger.Listeners inventorychangetrigger$listeners = this.listeners.get(p_192164_1_);
      if (inventorychangetrigger$listeners != null) {
         inventorychangetrigger$listeners.remove(p_192164_2_);
         if (inventorychangetrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public InventoryChangeTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      JsonObject jsonobject = JsonUtils.getJsonObject(p_192166_1_, "slots", new JsonObject());
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.func_211344_a(jsonobject.get("occupied"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.func_211344_a(jsonobject.get("full"));
      MinMaxBounds.IntBound minmaxbounds$intbound2 = MinMaxBounds.IntBound.func_211344_a(jsonobject.get("empty"));
      ItemPredicate[] aitempredicate = ItemPredicate.deserializeArray(p_192166_1_.get("items"));
      return new InventoryChangeTrigger.Instance(minmaxbounds$intbound, minmaxbounds$intbound1, minmaxbounds$intbound2, aitempredicate);
   }

   public void trigger(EntityPlayerMP p_192208_1_, InventoryPlayer p_192208_2_) {
      InventoryChangeTrigger.Listeners inventorychangetrigger$listeners = this.listeners.get(p_192208_1_.getAdvancements());
      if (inventorychangetrigger$listeners != null) {
         inventorychangetrigger$listeners.trigger(p_192208_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.IntBound occupied;
      private final MinMaxBounds.IntBound full;
      private final MinMaxBounds.IntBound empty;
      private final ItemPredicate[] items;
      private boolean isGlobal;

      public Instance(MinMaxBounds.IntBound p_i49710_1_, MinMaxBounds.IntBound p_i49710_2_, MinMaxBounds.IntBound p_i49710_3_, ItemPredicate[] p_i49710_4_) {
         super(InventoryChangeTrigger.ID);
         this.occupied = p_i49710_1_;
         this.full = p_i49710_2_;
         this.empty = p_i49710_3_;
         this.items = p_i49710_4_;
      }


      public static InventoryChangeTrigger.Instance createFromItems(ItemPredicate... p_203923_0_) {
         return new InventoryChangeTrigger.Instance(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, p_203923_0_);
      }

      public static InventoryChangeTrigger.Instance createFromItemItems(IItemProvider... p_203922_0_) {
         ItemPredicate[] aitempredicate = new ItemPredicate[p_203922_0_.length];

         for(int i = 0; i < p_203922_0_.length; ++i) {
            aitempredicate[i] = new ItemPredicate(null, p_203922_0_[i].asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, new EnchantmentPredicate[0],
                    null, NBTPredicate.ANY);
         }

         return createFromItems(aitempredicate);
      }
      public static InventoryChangeTrigger.Instance createFromItemItems(List<? extends IItemProvider> p_203922_0_) {
         ItemPredicate[] aitempredicate = new ItemPredicate[p_203922_0_.size()];

         for(int i = 0; i < p_203922_0_.size(); ++i) {
            aitempredicate[i] = new ItemPredicate(null, p_203922_0_.get(i).asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, new EnchantmentPredicate[0],
                    null, NBTPredicate.ANY);
         }

         return createFromItems(aitempredicate);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (!this.occupied.isUnbounded() || !this.full.isUnbounded() || !this.empty.isUnbounded()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("occupied", this.occupied.serialize());
            jsonobject1.add("full", this.full.serialize());
            jsonobject1.add("empty", this.empty.serialize());
            jsonobject.add("slots", jsonobject1);
         }

         if (this.items.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ItemPredicate itempredicate : this.items) {
               jsonarray.add(itempredicate.serialize());
            }

            jsonobject.add("items", jsonarray);
         }

         return jsonobject;
      }

      public Instance setGlobal() {
         isGlobal = true;
         return this;
      }

      public boolean test(InventoryPlayer p_192265_1_) {
         int i = 0;
         int j = 0;
         int k = 0;
         List<ItemPredicate> list = Lists.newArrayList(this.items);

         for(int l = 0; l < p_192265_1_.getSizeInventory(); ++l) {
            ItemStack itemstack = p_192265_1_.getStackInSlot(l);
            if (itemstack.isEmpty()) {
               ++j;
            } else {
               ++k;
               if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
                  ++i;
               }

               Iterator<ItemPredicate> iterator = list.iterator();

               while(iterator.hasNext()) {
                  ItemPredicate itempredicate = iterator.next();
                  if (itempredicate.test(itemstack)) {
                     iterator.remove();
                  }
               }
            }
         }

         if (!this.full.test(i)) {
            return false;
         } else if (!this.empty.test(j)) {
            return false;
         } else if (!this.occupied.test(k)) {
            return false;
         } else
             return list.isEmpty();
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<InventoryChangeTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47391_1_) {
         this.playerAdvancements = p_i47391_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> p_192489_1_) {
         this.listeners.add(p_192489_1_);
      }

      public void remove(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> p_192487_1_) {
         this.listeners.remove(p_192487_1_);
      }

      public void trigger(InventoryPlayer p_192486_1_) {
         List<ICriterionTrigger.Listener<InventoryChangeTrigger.Instance>> list = null;
         boolean isGlobal = false;
         for(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192486_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }
               isGlobal = listener.getCriterionInstance().isGlobal;

               list.add(listener);
            }
         }

         if (list != null) {
            if (isGlobal){
               for(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> listener1 : list) {
                  for (EntityPlayer playerEntity : p_192486_1_.player.world.playerEntities) {
                     if (playerEntity instanceof EntityPlayerMP){
                        listener1.grantCriterion(((EntityPlayerMP) playerEntity).getAdvancements());
                     }
                  }
               }
            }
            for(ICriterionTrigger.Listener<InventoryChangeTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
