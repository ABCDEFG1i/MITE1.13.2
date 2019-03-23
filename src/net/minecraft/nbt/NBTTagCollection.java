package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class NBTTagCollection<T extends INBTBase> extends AbstractList<T> implements INBTBase {
   public abstract int size();

   public T get(int p_get_1_) {
      return (T)this.func_197647_c(p_get_1_);
   }

   public T set(int p_set_1_, T p_set_2_) {
      T t = this.get(p_set_1_);
      this.func_197648_a(p_set_1_, p_set_2_);
      return t;
   }

   public abstract T func_197647_c(int p_197647_1_);

   public abstract void func_197648_a(int p_197648_1_, INBTBase p_197648_2_);

   public abstract void func_197649_b(int p_197649_1_);
}
