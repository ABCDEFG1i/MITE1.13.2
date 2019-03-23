package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;

public interface IStateHolder<C> {
   Collection<IProperty<?>> getProperties();

   <T extends Comparable<T>> boolean has(IProperty<T> p_196959_1_);

   <T extends Comparable<T>> T get(IProperty<T> p_177229_1_);

   <T extends Comparable<T>, V extends T> C with(IProperty<T> p_206870_1_, V p_206870_2_);

   <T extends Comparable<T>> C cycle(IProperty<T> p_177231_1_);

   ImmutableMap<IProperty<?>, Comparable<?>> getValues();
}
