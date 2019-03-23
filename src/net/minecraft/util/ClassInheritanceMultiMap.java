package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   private static final Set<Class<?>> ALL_KNOWN = Sets.newHashSet();
   private final Map<Class<?>, List<T>> map = Maps.newHashMap();
   private final Set<Class<?>> knownKeys = Sets.newIdentityHashSet();
   private final Class<T> baseClass;
   private final List<T> values = Lists.newArrayList();

   public ClassInheritanceMultiMap(Class<T> p_i45909_1_) {
      this.baseClass = p_i45909_1_;
      this.knownKeys.add(p_i45909_1_);
      this.map.put(p_i45909_1_, this.values);

      for(Class<?> oclass : Lists.newArrayList(ALL_KNOWN)) {
         this.createLookup(oclass);
      }

   }

   protected void createLookup(Class<?> p_180213_1_) {
      ALL_KNOWN.add(p_180213_1_);

      for(T t : this.values) {
         if (p_180213_1_.isAssignableFrom(t.getClass())) {
            this.addForClass(t, p_180213_1_);
         }
      }

      this.knownKeys.add(p_180213_1_);
   }

   protected Class<?> initializeClassLookup(Class<?> p_181157_1_) {
      if (this.baseClass.isAssignableFrom(p_181157_1_)) {
         if (!this.knownKeys.contains(p_181157_1_)) {
            this.createLookup(p_181157_1_);
         }

         return p_181157_1_;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + p_181157_1_);
      }
   }

   public boolean add(T p_add_1_) {
      for(Class<?> oclass : this.knownKeys) {
         if (oclass.isAssignableFrom(p_add_1_.getClass())) {
            this.addForClass(p_add_1_, oclass);
         }
      }

      return true;
   }

   private void addForClass(T p_181743_1_, Class<?> p_181743_2_) {
      List<T> list = this.map.get(p_181743_2_);
      if (list == null) {
         this.map.put(p_181743_2_, Lists.newArrayList(p_181743_1_));
      } else {
         list.add(p_181743_1_);
      }

   }

   public boolean remove(Object p_remove_1_) {
      T t = (T)p_remove_1_;
      boolean flag = false;

      for(Class<?> oclass : this.knownKeys) {
         if (oclass.isAssignableFrom(t.getClass())) {
            List<T> list = this.map.get(oclass);
            if (list != null && list.remove(t)) {
               flag = true;
            }
         }
      }

      return flag;
   }

   public boolean contains(Object p_contains_1_) {
      return Iterators.contains(this.getByClass(p_contains_1_.getClass()).iterator(), p_contains_1_);
   }

   public <S> Iterable<S> getByClass(Class<S> p_180215_1_) {
      return () -> {
         List<T> list = this.map.get(this.initializeClassLookup(p_180215_1_));
         if (list == null) {
            return Collections.emptyIterator();
         } else {
            Iterator<T> iterator = list.iterator();
            return Iterators.filter(iterator, p_180215_1_);
         }
      };
   }

   public Iterator<T> iterator() {
      return (Iterator<T>)(this.values.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.values.iterator()));
   }

   public int size() {
      return this.values.size();
   }
}
