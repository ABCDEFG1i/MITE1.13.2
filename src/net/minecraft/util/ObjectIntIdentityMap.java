package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T> {
   protected int nextId;
   protected final IdentityHashMap<T, Integer> identityMap;
   protected final List<T> objectList;

   public ObjectIntIdentityMap() {
      this(512);
   }

   public ObjectIntIdentityMap(int p_i46984_1_) {
      this.objectList = Lists.newArrayListWithExpectedSize(p_i46984_1_);
      this.identityMap = new IdentityHashMap<>(p_i46984_1_);
   }

   public void put(T p_148746_1_, int p_148746_2_) {
      this.identityMap.put(p_148746_1_, p_148746_2_);

      while(this.objectList.size() <= p_148746_2_) {
         this.objectList.add((T)null);
      }

      this.objectList.set(p_148746_2_, p_148746_1_);
      if (this.nextId <= p_148746_2_) {
         this.nextId = p_148746_2_ + 1;
      }

   }

   public void add(T p_195867_1_) {
      this.put(p_195867_1_, this.nextId);
   }

   public int get(T p_148747_1_) {
      Integer integer = this.identityMap.get(p_148747_1_);
      return integer == null ? -1 : integer;
   }

   @Nullable
   public final T getByValue(int p_148745_1_) {
      return (T)(p_148745_1_ >= 0 && p_148745_1_ < this.objectList.size() ? this.objectList.get(p_148745_1_) : null);
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
   }

   public int size() {
      return this.identityMap.size();
   }
}
