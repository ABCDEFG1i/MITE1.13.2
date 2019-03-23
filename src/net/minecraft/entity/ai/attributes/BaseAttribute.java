package net.minecraft.entity.ai.attributes;

import javax.annotation.Nullable;

public abstract class BaseAttribute implements IAttribute {
   private final IAttribute parent;
   private final String translationKey;
   private final double defaultValue;
   private boolean shouldWatch;

   protected BaseAttribute(@Nullable IAttribute p_i45892_1_, String p_i45892_2_, double p_i45892_3_) {
      this.parent = p_i45892_1_;
      this.translationKey = p_i45892_2_;
      this.defaultValue = p_i45892_3_;
      if (p_i45892_2_ == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   public String getName() {
      return this.translationKey;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean getShouldWatch() {
      return this.shouldWatch;
   }

   public BaseAttribute setShouldWatch(boolean p_111112_1_) {
      this.shouldWatch = p_111112_1_;
      return this;
   }

   @Nullable
   public IAttribute getParent() {
      return this.parent;
   }

   public int hashCode() {
      return this.translationKey.hashCode();
   }

   public boolean equals(Object p_equals_1_) {
      return p_equals_1_ instanceof IAttribute && this.translationKey.equals(((IAttribute)p_equals_1_).getName());
   }
}
