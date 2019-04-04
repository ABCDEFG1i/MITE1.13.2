package net.minecraft.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList<E> extends AbstractList<E> {
   private final List<E> delegate;
   private final E defaultElement;

   public static <E> NonNullList<E> create() {
      return new NonNullList<>();
   }

   public static <E> NonNullList<E> withSize(int p_191197_0_, E p_191197_1_) {
      Validate.notNull(p_191197_1_);
      Object[] aobject = new Object[p_191197_0_];
      Arrays.fill(aobject, p_191197_1_);
      return new NonNullList<>(Arrays.asList((E[])aobject), p_191197_1_);
   }

   @SafeVarargs
   public static <E> NonNullList<E> from(E p_193580_0_, E... p_193580_1_) {
      return new NonNullList<>(Arrays.asList(p_193580_1_), p_193580_0_);
   }

   public NonNullList() {
      this(new ArrayList<>(), null);
   }

   protected NonNullList(List<E> p_i47327_1_, @Nullable E p_i47327_2_) {
      this.delegate = p_i47327_1_;
      this.defaultElement = p_i47327_2_;
   }

   @Nonnull
   public E get(int p_get_1_) {
      return this.delegate.get(p_get_1_);
   }

   public E set(int p_set_1_, E p_set_2_) {
      Validate.notNull(p_set_2_);
      return this.delegate.set(p_set_1_, p_set_2_);
   }

   public void add(int p_add_1_, E p_add_2_) {
      Validate.notNull(p_add_2_);
      this.delegate.add(p_add_1_, p_add_2_);
   }

   public E remove(int p_remove_1_) {
      return this.delegate.remove(p_remove_1_);
   }

   public int size() {
      return this.delegate.size();
   }

   public void clear() {
      if (this.defaultElement == null) {
         super.clear();
      } else {
         for(int i = 0; i < this.size(); ++i) {
            this.set(i, this.defaultElement);
         }
      }

   }
}
