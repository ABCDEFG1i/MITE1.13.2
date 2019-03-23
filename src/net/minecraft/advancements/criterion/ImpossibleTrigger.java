package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.util.ResourceLocation;

public class ImpossibleTrigger implements ICriterionTrigger<ImpossibleTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("impossible");

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<ImpossibleTrigger.Instance> p_192165_2_) {
   }

   public void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<ImpossibleTrigger.Instance> p_192164_2_) {
   }

   public void removeAllListeners(PlayerAdvancements p_192167_1_) {
   }

   public ImpossibleTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new ImpossibleTrigger.Instance();
   }

   public static class Instance extends AbstractCriterionInstance {
      public Instance() {
         super(ImpossibleTrigger.ID);
      }
   }
}
