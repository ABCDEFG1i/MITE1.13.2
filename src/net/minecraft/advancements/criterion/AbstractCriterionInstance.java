package net.minecraft.advancements.criterion;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.util.ResourceLocation;

public class AbstractCriterionInstance implements ICriterionInstance {
   private final ResourceLocation criterion;

   public AbstractCriterionInstance(ResourceLocation p_i47465_1_) {
      this.criterion = p_i47465_1_;
   }

   public ResourceLocation getId() {
      return this.criterion;
   }

   public String toString() {
      return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
   }
}
