package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger implements ICriterionTrigger<UsedEnderEyeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");
   private final Map<PlayerAdvancements, UsedEnderEyeTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> p_192165_2_) {
      UsedEnderEyeTrigger.Listeners usedendereyetrigger$listeners = this.listeners.get(p_192165_1_);
      if (usedendereyetrigger$listeners == null) {
         usedendereyetrigger$listeners = new UsedEnderEyeTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, usedendereyetrigger$listeners);
      }

      usedendereyetrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> p_192164_2_) {
      UsedEnderEyeTrigger.Listeners usedendereyetrigger$listeners = this.listeners.get(p_192164_1_);
      if (usedendereyetrigger$listeners != null) {
         usedendereyetrigger$listeners.remove(p_192164_2_);
         if (usedendereyetrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public UsedEnderEyeTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.func_211356_a(p_192166_1_.get("distance"));
      return new UsedEnderEyeTrigger.Instance(minmaxbounds$floatbound);
   }

   public void trigger(EntityPlayerMP p_192239_1_, BlockPos p_192239_2_) {
      UsedEnderEyeTrigger.Listeners usedendereyetrigger$listeners = this.listeners.get(p_192239_1_.getAdvancements());
      if (usedendereyetrigger$listeners != null) {
         double d0 = p_192239_1_.posX - (double)p_192239_2_.getX();
         double d1 = p_192239_1_.posZ - (double)p_192239_2_.getZ();
         usedendereyetrigger$listeners.trigger(d0 * d0 + d1 * d1);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final MinMaxBounds.FloatBound distance;

      public Instance(MinMaxBounds.FloatBound p_i49730_1_) {
         super(UsedEnderEyeTrigger.ID);
         this.distance = p_i49730_1_;
      }

      public boolean test(double p_192288_1_) {
         return this.distance.testSquared(p_192288_1_);
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47450_1_) {
         this.playerAdvancements = p_i47450_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> p_192546_1_) {
         this.listeners.add(p_192546_1_);
      }

      public void remove(ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> p_192544_1_) {
         this.listeners.remove(p_192544_1_);
      }

      public void trigger(double p_192543_1_) {
         List<ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192543_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<UsedEnderEyeTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
