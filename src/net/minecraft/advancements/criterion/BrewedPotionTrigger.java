package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BrewedPotionTrigger implements ICriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");
   private final Map<PlayerAdvancements, BrewedPotionTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> p_192165_2_) {
      BrewedPotionTrigger.Listeners brewedpotiontrigger$listeners = this.listeners.get(p_192165_1_);
      if (brewedpotiontrigger$listeners == null) {
         brewedpotiontrigger$listeners = new BrewedPotionTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, brewedpotiontrigger$listeners);
      }

      brewedpotiontrigger$listeners.addListener(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> p_192164_2_) {
      BrewedPotionTrigger.Listeners brewedpotiontrigger$listeners = this.listeners.get(p_192164_1_);
      if (brewedpotiontrigger$listeners != null) {
         brewedpotiontrigger$listeners.removeListener(p_192164_2_);
         if (brewedpotiontrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public BrewedPotionTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      PotionType potiontype = null;
      if (p_192166_1_.has("potion")) {
         ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(p_192166_1_, "potion"));
         if (!IRegistry.field_212621_j.func_212607_c(resourcelocation)) {
            throw new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
         }

         potiontype = IRegistry.field_212621_j.func_82594_a(resourcelocation);
      }

      return new BrewedPotionTrigger.Instance(potiontype);
   }

   public void trigger(EntityPlayerMP p_192173_1_, PotionType p_192173_2_) {
      BrewedPotionTrigger.Listeners brewedpotiontrigger$listeners = this.listeners.get(p_192173_1_.getAdvancements());
      if (brewedpotiontrigger$listeners != null) {
         brewedpotiontrigger$listeners.trigger(p_192173_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final PotionType potion;

      public Instance(@Nullable PotionType p_i47398_1_) {
         super(BrewedPotionTrigger.ID);
         this.potion = p_i47398_1_;
      }

      public static BrewedPotionTrigger.Instance func_203910_c() {
         return new BrewedPotionTrigger.Instance(null);
      }

      public boolean test(PotionType p_192250_1_) {
         return this.potion == null || this.potion == p_192250_1_;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.potion != null) {
            jsonobject.addProperty("potion", IRegistry.field_212621_j.func_177774_c(this.potion).toString());
         }

         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<BrewedPotionTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47399_1_) {
         this.playerAdvancements = p_i47399_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> p_192349_1_) {
         this.listeners.add(p_192349_1_);
      }

      public void removeListener(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> p_192346_1_) {
         this.listeners.remove(p_192346_1_);
      }

      public void trigger(PotionType p_192348_1_) {
         List<ICriterionTrigger.Listener<BrewedPotionTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_192348_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
