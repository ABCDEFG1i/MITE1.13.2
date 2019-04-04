package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface ICriterionTrigger<T extends ICriterionInstance> {
   ResourceLocation getId();

   void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<T> p_192165_2_);

   void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<T> p_192164_2_);

   void removeAllListeners(PlayerAdvancements p_192167_1_);

   T deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_);

   class Listener<T extends ICriterionInstance> {
      private final T criterionInstance;
      private final Advancement advancement;
      private final String criterionName;

      public Listener(T p_i47405_1_, Advancement p_i47405_2_, String p_i47405_3_) {
         this.criterionInstance = p_i47405_1_;
         this.advancement = p_i47405_2_;
         this.criterionName = p_i47405_3_;
      }

      public T getCriterionInstance() {
         return this.criterionInstance;
      }

      public void grantCriterion(PlayerAdvancements p_192159_1_) {
         p_192159_1_.grantCriterion(this.advancement, this.criterionName);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            ICriterionTrigger.Listener<?> listener = (ICriterionTrigger.Listener)p_equals_1_;
            if (!this.criterionInstance.equals(listener.criterionInstance)) {
               return false;
            } else {
               return this.advancement.equals(listener.advancement) && this.criterionName.equals(
                       listener.criterionName);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.criterionInstance.hashCode();
         i = 31 * i + this.advancement.hashCode();
         i = 31 * i + this.criterionName.hashCode();
         return i;
      }
   }
}
