package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;

public class ExpiringMap<T> extends Long2ObjectOpenHashMap<T> {
   private final int lifespan;
   private final Long2LongMap times = new Long2LongLinkedOpenHashMap();

   public ExpiringMap(int p_i48712_1_, int p_i48712_2_) {
      super(p_i48712_1_);
      this.lifespan = p_i48712_2_;
   }

   private void refreshTimes(long p_201842_1_) {
      long i = Util.milliTime();
      this.times.put(p_201842_1_, i);
      ObjectIterator<Long2LongMap.Entry> objectiterator = this.times.long2LongEntrySet().iterator();

      while(objectiterator.hasNext()) {
         Long2LongMap.Entry entry = objectiterator.next();
         T t = super.get(entry.getLongKey());
         if (i - entry.getLongValue() <= (long)this.lifespan) {
            break;
         }

         if (t != null && this.shouldExpire(t)) {
            super.remove(entry.getLongKey());
            objectiterator.remove();
         }
      }

   }

   protected boolean shouldExpire(T p_205609_1_) {
      return true;
   }

   public T put(long p_put_1_, T p_put_3_) {
      this.refreshTimes(p_put_1_);
      return super.put(p_put_1_, p_put_3_);
   }

   public T put(Long p_put_1_, T p_put_2_) {
      this.refreshTimes(p_put_1_);
      return super.put(p_put_1_, p_put_2_);
   }

   public T get(long p_get_1_) {
      this.refreshTimes(p_get_1_);
      return super.get(p_get_1_);
   }

   public void putAll(Map<? extends Long, ? extends T> p_putAll_1_) {
      throw new RuntimeException("Not implemented");
   }

   public T remove(long p_remove_1_) {
      throw new RuntimeException("Not implemented");
   }

   public T remove(Object p_remove_1_) {
      throw new RuntimeException("Not implemented");
   }
}
