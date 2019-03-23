package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.MapPopulator;

public class StateContainer<O, S extends IStateHolder<S>> {
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final O owner;
   private final ImmutableSortedMap<String, IProperty<?>> properties;
   private final ImmutableList<S> validStates;

   protected <A extends AbstractStateHolder<O, S>> StateContainer(O p_i49005_1_, StateContainer.IFactory<O, S, A> p_i49005_2_, Map<String, IProperty<?>> p_i49005_3_) {
      this.owner = p_i49005_1_;
      this.properties = ImmutableSortedMap.copyOf(p_i49005_3_);
      Map<Map<IProperty<?>, Comparable<?>>, A> map = Maps.newLinkedHashMap();
      List<A> list = Lists.newArrayList();
      Stream<List<Comparable<?>>> stream = Stream.of(Collections.emptyList());

      for(IProperty<?> iproperty : this.properties.values()) {
         stream = stream.flatMap((p_200999_1_) -> {
            return iproperty.getAllowedValues().stream().map((p_200998_1_) -> {
               List<Comparable<?>> list1 = Lists.newArrayList(p_200999_1_);
               list1.add(p_200998_1_);
               return list1;
            });
         });
      }

      stream.forEach((p_201000_5_) -> {
         Map<IProperty<?>, Comparable<?>> map1 = MapPopulator.createMap(this.properties.values(), p_201000_5_);
         A a1 = p_i49005_2_.create(p_i49005_1_, ImmutableMap.copyOf(map1));
         map.put(map1, a1);
         list.add(a1);
      });

      for(A a : list) {
         a.func_206874_a((Map<Map<IProperty<?>, Comparable<?>>, S>) map);
      }

      this.validStates = (ImmutableList<S>) ImmutableList.copyOf(list);
   }

   public ImmutableList<S> getValidStates() {
      return this.validStates;
   }

   public S getBaseState() {
      return (S)(this.validStates.get(0));
   }

   public O getOwner() {
      return this.owner;
   }

   public Collection<IProperty<?>> getProperties() {
      return this.properties.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.properties.values().stream().map(IProperty::getName).collect(Collectors.toList())).toString();
   }

   @Nullable
   public IProperty<?> getProperty(String p_185920_1_) {
      return this.properties.get(p_185920_1_);
   }

   public static class Builder<O, S extends IStateHolder<S>> {
      private final O owner;
      private final Map<String, IProperty<?>> properties = Maps.newHashMap();

      public Builder(O p_i49165_1_) {
         this.owner = p_i49165_1_;
      }

      public StateContainer.Builder<O, S> add(IProperty<?>... p_206894_1_) {
         for(IProperty<?> iproperty : p_206894_1_) {
            this.validateProperty(iproperty);
            this.properties.put(iproperty.getName(), iproperty);
         }

         return this;
      }

      private <T extends Comparable<T>> void validateProperty(IProperty<T> p_206892_1_) {
         String s = p_206892_1_.getName();
         if (!StateContainer.NAME_PATTERN.matcher(s).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + s);
         } else {
            Collection<T> collection = p_206892_1_.getAllowedValues();
            if (collection.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + s + " with <= 1 possible values");
            } else {
               for(T t : collection) {
                  String s1 = p_206892_1_.getName(t);
                  if (!StateContainer.NAME_PATTERN.matcher(s1).matches()) {
                     throw new IllegalArgumentException(this.owner + " has property: " + s + " with invalidly named value: " + s1);
                  }
               }

               if (this.properties.containsKey(s)) {
                  throw new IllegalArgumentException(this.owner + " has duplicate property: " + s);
               }
            }
         }
      }

      public <A extends AbstractStateHolder<O, S>> StateContainer<O, S> create(StateContainer.IFactory<O, S, A> p_206893_1_) {
         return new StateContainer<>(this.owner, p_206893_1_, this.properties);
      }
   }

   public interface IFactory<O, S extends IStateHolder<S>, A extends AbstractStateHolder<O, S>> {
      A create(O p_create_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_create_2_);
   }
}
