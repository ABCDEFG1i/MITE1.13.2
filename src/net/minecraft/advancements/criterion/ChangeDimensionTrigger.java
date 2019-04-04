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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class ChangeDimensionTrigger implements ICriterionTrigger<ChangeDimensionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");
   private final Map<PlayerAdvancements, ChangeDimensionTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> p_192165_2_) {
      ChangeDimensionTrigger.Listeners changedimensiontrigger$listeners = this.listeners.get(p_192165_1_);
      if (changedimensiontrigger$listeners == null) {
         changedimensiontrigger$listeners = new ChangeDimensionTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, changedimensiontrigger$listeners);
      }

      changedimensiontrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> p_192164_2_) {
      ChangeDimensionTrigger.Listeners changedimensiontrigger$listeners = this.listeners.get(p_192164_1_);
      if (changedimensiontrigger$listeners != null) {
         changedimensiontrigger$listeners.remove(p_192164_2_);
         if (changedimensiontrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public ChangeDimensionTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DimensionType dimensiontype = p_192166_1_.has("from") ? DimensionType.func_193417_a(new ResourceLocation(JsonUtils.getString(p_192166_1_, "from"))) : null;
      DimensionType dimensiontype1 = p_192166_1_.has("to") ? DimensionType.func_193417_a(new ResourceLocation(JsonUtils.getString(p_192166_1_, "to"))) : null;
      return new ChangeDimensionTrigger.Instance(dimensiontype, dimensiontype1);
   }

   public void trigger(EntityPlayerMP p_193143_1_, DimensionType p_193143_2_, DimensionType p_193143_3_) {
      ChangeDimensionTrigger.Listeners changedimensiontrigger$listeners = this.listeners.get(p_193143_1_.getAdvancements());
      if (changedimensiontrigger$listeners != null) {
         changedimensiontrigger$listeners.trigger(p_193143_2_, p_193143_3_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public Instance(@Nullable DimensionType p_i47475_1_, @Nullable DimensionType p_i47475_2_) {
         super(ChangeDimensionTrigger.ID);
         this.from = p_i47475_1_;
         this.to = p_i47475_2_;
      }

      public static ChangeDimensionTrigger.Instance func_203911_a(DimensionType p_203911_0_) {
         return new ChangeDimensionTrigger.Instance(null, p_203911_0_);
      }

      public boolean test(DimensionType p_193190_1_, DimensionType p_193190_2_) {
         if (this.from != null && this.from != p_193190_1_) {
            return false;
         } else {
            return this.to == null || this.to == p_193190_2_;
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.from != null) {
            jsonobject.addProperty("from", DimensionType.func_212678_a(this.from).toString());
         }

         if (this.to != null) {
            jsonobject.addProperty("to", DimensionType.func_212678_a(this.to).toString());
         }

         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47476_1_) {
         this.playerAdvancements = p_i47476_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> p_193233_1_) {
         this.listeners.add(p_193233_1_);
      }

      public void remove(ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> p_193231_1_) {
         this.listeners.remove(p_193231_1_);
      }

      public void trigger(DimensionType p_193234_1_, DimensionType p_193234_2_) {
         List<ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193234_1_, p_193234_2_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ChangeDimensionTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
