package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class RegistryNamespacedDefaultedByKey<V> extends RegistryNamespaced<V> {
   private final ResourceLocation defaultValueKey;
   private V defaultValue;

   public RegistryNamespacedDefaultedByKey(ResourceLocation p_i49828_1_) {
      this.defaultValueKey = p_i49828_1_;
   }

   public void func_177775_a(int p_177775_1_, ResourceLocation p_177775_2_, V p_177775_3_) {
      if (this.defaultValueKey.equals(p_177775_2_)) {
         this.defaultValue = p_177775_3_;
      }

      super.func_177775_a(p_177775_1_, p_177775_2_, p_177775_3_);
   }

   public int func_148757_b(@Nullable V p_148757_1_) {
      int i = super.func_148757_b(p_148757_1_);
      return i == -1 ? super.func_148757_b(this.defaultValue) : i;
   }

   public ResourceLocation func_177774_c(V p_177774_1_) {
      ResourceLocation resourcelocation = super.func_177774_c(p_177774_1_);
      return resourcelocation == null ? this.defaultValueKey : resourcelocation;
   }

   public V func_82594_a(@Nullable ResourceLocation p_82594_1_) {
      V v = this.func_212608_b(p_82594_1_);
      return v == null ? this.defaultValue : v;
   }

   @Nonnull
   public V func_148754_a(int p_148754_1_) {
      V v = super.func_148754_a(p_148754_1_);
      return v == null ? this.defaultValue : v;
   }

   @Nonnull
   public V func_186801_a(Random p_186801_1_) {
      V v = super.func_186801_a(p_186801_1_);
      return v == null ? this.defaultValue : v;
   }

   public ResourceLocation func_212609_b() {
      return this.defaultValueKey;
   }
}
