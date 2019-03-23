package net.minecraft.util;

import java.util.function.Supplier;

public class LazyLoadBase<T> {
   private Supplier<T> supplier;
   private T value;

   public LazyLoadBase(Supplier<T> p_i48587_1_) {
      this.supplier = p_i48587_1_;
   }

   public T getValue() {
      Supplier<T> supplier = this.supplier;
      if (supplier != null) {
         this.value = supplier.get();
         this.supplier = null;
      }

      return this.value;
   }
}
