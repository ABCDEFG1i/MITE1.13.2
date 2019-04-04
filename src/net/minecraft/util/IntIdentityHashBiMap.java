package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class IntIdentityHashBiMap<K> implements IObjectIntIterable<K> {
   private static final Object EMPTY = null;
   private K[] values;
   private int[] intKeys;
   private K[] byId;
   private int nextFreeIndex;
   private int mapSize;

   public IntIdentityHashBiMap(int p_i46830_1_) {
      p_i46830_1_ = (int)((float)p_i46830_1_ / 0.8F);
      this.values = (K[])(new Object[p_i46830_1_]);
      this.intKeys = new int[p_i46830_1_];
      this.byId = (K[])(new Object[p_i46830_1_]);
   }

   public int getId(@Nullable K p_186815_1_) {
      return this.getValue(this.getIndex(p_186815_1_, this.hashObject(p_186815_1_)));
   }

   @Nullable
   public K get(int p_186813_1_) {
      return p_186813_1_ >= 0 && p_186813_1_ < this.byId.length ? this.byId[p_186813_1_] : null;
   }

   private int getValue(int p_186805_1_) {
      return p_186805_1_ == -1 ? -1 : this.intKeys[p_186805_1_];
   }

   public int add(K p_186808_1_) {
      int i = this.nextId();
      this.put(p_186808_1_, i);
      return i;
   }

   private int nextId() {
      while(this.nextFreeIndex < this.byId.length && this.byId[this.nextFreeIndex] != null) {
         ++this.nextFreeIndex;
      }

      return this.nextFreeIndex;
   }

   private void grow(int p_186807_1_) {
      K[] ak = this.values;
      int[] aint = this.intKeys;
      this.values = (K[])(new Object[p_186807_1_]);
      this.intKeys = new int[p_186807_1_];
      this.byId = (K[])(new Object[p_186807_1_]);
      this.nextFreeIndex = 0;
      this.mapSize = 0;

      for(int i = 0; i < ak.length; ++i) {
         if (ak[i] != null) {
            this.put(ak[i], aint[i]);
         }
      }

   }

   public void put(K p_186814_1_, int p_186814_2_) {
      int i = Math.max(p_186814_2_, this.mapSize + 1);
      if ((float)i >= (float)this.values.length * 0.8F) {
         int j;
         for(j = this.values.length << 1; j < p_186814_2_; j <<= 1) {
         }

         this.grow(j);
      }

      int k = this.findEmpty(this.hashObject(p_186814_1_));
      this.values[k] = p_186814_1_;
      this.intKeys[k] = p_186814_2_;
      this.byId[p_186814_2_] = p_186814_1_;
      ++this.mapSize;
      if (p_186814_2_ == this.nextFreeIndex) {
         ++this.nextFreeIndex;
      }

   }

   private int hashObject(@Nullable K p_186811_1_) {
      return (MathHelper.hash(System.identityHashCode(p_186811_1_)) & Integer.MAX_VALUE) % this.values.length;
   }

   private int getIndex(@Nullable K p_186816_1_, int p_186816_2_) {
      for(int i = p_186816_2_; i < this.values.length; ++i) {
         if (this.values[i] == p_186816_1_) {
            return i;
         }

         if (this.values[i] == EMPTY) {
            return -1;
         }
      }

      for(int j = 0; j < p_186816_2_; ++j) {
         if (this.values[j] == p_186816_1_) {
            return j;
         }

         if (this.values[j] == EMPTY) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(int p_186806_1_) {
      for(int i = p_186806_1_; i < this.values.length; ++i) {
         if (this.values[i] == EMPTY) {
            return i;
         }
      }

      for(int j = 0; j < p_186806_1_; ++j) {
         if (this.values[j] == EMPTY) {
            return j;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator<K> iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.values, null);
      Arrays.fill(this.byId, null);
      this.nextFreeIndex = 0;
      this.mapSize = 0;
   }

   public int size() {
      return this.mapSize;
   }
}
