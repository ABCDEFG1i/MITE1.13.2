package net.minecraft.state;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;

public class EnumProperty<T extends Enum<T> & IStringSerializable> extends AbstractProperty<T> {
   private final ImmutableSet<T> allowedValues;
   private final Map<String, T> nameToValue = Maps.newHashMap();

   protected EnumProperty(String p_i45649_1_, Class<T> p_i45649_2_, Collection<T> p_i45649_3_) {
      super(p_i45649_1_, p_i45649_2_);
      this.allowedValues = ImmutableSet.copyOf(p_i45649_3_);

      for(T t : p_i45649_3_) {
         String s = ((IStringSerializable)t).getName();
         if (this.nameToValue.containsKey(s)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
         }

         this.nameToValue.put(s, t);
      }

   }

   public Collection<T> getAllowedValues() {
      return this.allowedValues;
   }

   public Optional<T> parseValue(String p_185929_1_) {
      return Optional.ofNullable(this.nameToValue.get(p_185929_1_));
   }

   public String getName(T p_177702_1_) {
      return ((IStringSerializable)p_177702_1_).getName();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof EnumProperty && super.equals(p_equals_1_)) {
         EnumProperty<?> enumproperty = (EnumProperty)p_equals_1_;
         return this.allowedValues.equals(enumproperty.allowedValues) && this.nameToValue.equals(enumproperty.nameToValue);
      } else {
         return false;
      }
   }

   public int computeHashCode() {
      int i = super.computeHashCode();
      i = 31 * i + this.allowedValues.hashCode();
      i = 31 * i + this.nameToValue.hashCode();
      return i;
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177709_0_, Class<T> p_177709_1_) {
      return create(p_177709_0_, p_177709_1_, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177708_0_, Class<T> p_177708_1_, Predicate<T> p_177708_2_) {
      return create(p_177708_0_, p_177708_1_, Arrays.<T>stream(p_177708_1_.getEnumConstants()).filter(p_177708_2_).collect(Collectors.toList()));
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177706_0_, Class<T> p_177706_1_, T... p_177706_2_) {
      return create(p_177706_0_, p_177706_1_, Lists.newArrayList(p_177706_2_));
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177707_0_, Class<T> p_177707_1_, Collection<T> p_177707_2_) {
      return new EnumProperty<>(p_177707_0_, p_177707_1_, p_177707_2_);
   }
}
