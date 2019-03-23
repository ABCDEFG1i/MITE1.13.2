package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class AbstractStateHolder<O, S> implements IStateHolder<S> {
   private static final Function<Entry<IProperty<?>, Comparable<?>>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<IProperty<?>, Comparable<?>> p_apply_1_) {
         if (p_apply_1_ == null) {
            return "<NULL>";
         } else {
            IProperty<?> iproperty = p_apply_1_.getKey();
            return iproperty.getName() + "=" + this.getPropertyName(iproperty, p_apply_1_.getValue());
         }
      }

      private <T extends Comparable<T>> String getPropertyName(IProperty<T> p_185886_1_, Comparable<?> p_185886_2_) {
         return p_185886_1_.getName((T)p_185886_2_);
      }
   };
   protected final O object;
   private final ImmutableMap<IProperty<?>, Comparable<?>> properties;
   private final int hashCode;
   private Table<IProperty<?>, Comparable<?>, S> propertyToStateMap;

   protected AbstractStateHolder(O p_i49008_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i49008_2_) {
      this.object = p_i49008_1_;
      this.properties = p_i49008_2_;
      this.hashCode = p_i49008_2_.hashCode();
   }

   public <T extends Comparable<T>> S cycle(IProperty<T> p_177231_1_) {
      return (S)this.with(p_177231_1_, (T)(cyclePropertyValue(p_177231_1_.getAllowedValues(), this.get(p_177231_1_))));
   }

   protected static <T> T cyclePropertyValue(Collection<T> p_177232_0_, T p_177232_1_) {
      Iterator<T> iterator = p_177232_0_.iterator();

      while(iterator.hasNext()) {
         if (iterator.next().equals(p_177232_1_)) {
            if (iterator.hasNext()) {
               return iterator.next();
            }

            return p_177232_0_.iterator().next();
         }
      }

      return iterator.next();
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(this.object);
      if (!this.getValues().isEmpty()) {
         stringbuilder.append('[');
         stringbuilder.append(this.getValues().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.joining(",")));
         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   public Collection<IProperty<?>> getProperties() {
      return Collections.unmodifiableCollection(this.properties.keySet());
   }

   public <T extends Comparable<T>> boolean has(IProperty<T> p_196959_1_) {
      return this.properties.containsKey(p_196959_1_);
   }

   public <T extends Comparable<T>> T get(IProperty<T> p_177229_1_) {
      Comparable<?> comparable = this.properties.get(p_177229_1_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot get property " + p_177229_1_ + " as it does not exist in " + this.object);
      } else {
         return (T)(p_177229_1_.getValueClass().cast(comparable));
      }
   }

   public <T extends Comparable<T>, V extends T> S with(IProperty<T> p_206870_1_, V p_206870_2_) {
      Comparable<?> comparable = this.properties.get(p_206870_1_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " as it does not exist in " + this.object);
      } else if (comparable == p_206870_2_) {
         return (S)this;
      } else {
         S s = this.propertyToStateMap.get(p_206870_1_, p_206870_2_);
         if (s == null) {
            throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " to " + p_206870_2_ + " on " + this.object + ", it is not an allowed value");
         } else {
            return s;
         }
      }
   }

   public void func_206874_a(Map<Map<IProperty<?>, Comparable<?>>, S> p_206874_1_) {
      if (this.propertyToStateMap != null) {
         throw new IllegalStateException();
      } else {
         Table<IProperty<?>, Comparable<?>, S> table = HashBasedTable.create();

         for(Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            IProperty<?> iproperty = entry.getKey();

            for(Comparable<?> comparable : iproperty.getAllowedValues()) {
               if (comparable != entry.getValue()) {
                  table.put(iproperty, comparable, p_206874_1_.get(this.func_206875_b(iproperty, comparable)));
               }
            }
         }

         this.propertyToStateMap = (Table<IProperty<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
      }
   }

   private Map<IProperty<?>, Comparable<?>> func_206875_b(IProperty<?> p_206875_1_, Comparable<?> p_206875_2_) {
      Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap(this.properties);
      map.put(p_206875_1_, p_206875_2_);
      return map;
   }

   public ImmutableMap<IProperty<?>, Comparable<?>> getValues() {
      return this.properties;
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_;
   }

   public int hashCode() {
      return this.hashCode;
   }
}
