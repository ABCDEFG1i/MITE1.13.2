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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger implements ICriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");
   private final Map<PlayerAdvancements, RecipeUnlockedTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> p_192165_2_) {
      RecipeUnlockedTrigger.Listeners recipeunlockedtrigger$listeners = this.listeners.get(p_192165_1_);
      if (recipeunlockedtrigger$listeners == null) {
         recipeunlockedtrigger$listeners = new RecipeUnlockedTrigger.Listeners(p_192165_1_);
         this.listeners.put(p_192165_1_, recipeunlockedtrigger$listeners);
      }

      recipeunlockedtrigger$listeners.add(p_192165_2_);
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> p_192164_2_) {
      RecipeUnlockedTrigger.Listeners recipeunlockedtrigger$listeners = this.listeners.get(p_192164_1_);
      if (recipeunlockedtrigger$listeners != null) {
         recipeunlockedtrigger$listeners.remove(p_192164_2_);
         if (recipeunlockedtrigger$listeners.isEmpty()) {
            this.listeners.remove(p_192164_1_);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.listeners.remove(p_192167_1_);
   }

   public RecipeUnlockedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(p_192166_1_, "recipe"));
      return new RecipeUnlockedTrigger.Instance(resourcelocation);
   }

   public void trigger(EntityPlayerMP p_192225_1_, IRecipe p_192225_2_) {
      RecipeUnlockedTrigger.Listeners recipeunlockedtrigger$listeners = this.listeners.get(p_192225_1_.getAdvancements());
      if (recipeunlockedtrigger$listeners != null) {
         recipeunlockedtrigger$listeners.trigger(p_192225_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ResourceLocation field_212243_a;

      public Instance(ResourceLocation p_i48179_1_) {
         super(RecipeUnlockedTrigger.ID);
         this.field_212243_a = p_i48179_1_;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("recipe", this.field_212243_a.toString());
         return jsonobject;
      }

      public boolean test(IRecipe p_193215_1_) {
         return this.field_212243_a.equals(p_193215_1_.getId());
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i47397_1_) {
         this.playerAdvancements = p_i47397_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> p_192528_1_) {
         this.listeners.add(p_192528_1_);
      }

      public void remove(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> p_192525_1_) {
         this.listeners.remove(p_192525_1_);
      }

      public void trigger(IRecipe p_193493_1_) {
         List<ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_193493_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<RecipeUnlockedTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}
