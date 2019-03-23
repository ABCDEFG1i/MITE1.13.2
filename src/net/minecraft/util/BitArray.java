package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
   private final long[] longArray;
   private final int bitsPerEntry;
   private final long maxEntryValue;
   private final int arraySize;

   public BitArray(int p_i46832_1_, int p_i46832_2_) {
      this(p_i46832_1_, p_i46832_2_, new long[MathHelper.roundUp(p_i46832_2_ * p_i46832_1_, 64) / 64]);
   }

   public BitArray(int p_i47901_1_, int p_i47901_2_, long[] p_i47901_3_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_i47901_1_);
      this.arraySize = p_i47901_2_;
      this.bitsPerEntry = p_i47901_1_;
      this.longArray = p_i47901_3_;
      this.maxEntryValue = (1L << p_i47901_1_) - 1L;
      int i = MathHelper.roundUp(p_i47901_2_ * p_i47901_1_, 64) / 64;
      if (p_i47901_3_.length != i) {
         throw new RuntimeException("Invalid length given for storage, got: " + p_i47901_3_.length + " but expected: " + i);
      }
   }

   public void setAt(int p_188141_1_, int p_188141_2_) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_188141_1_);
      Validate.inclusiveBetween(0L, this.maxEntryValue, (long)p_188141_2_);
      int i = p_188141_1_ * this.bitsPerEntry;
      int j = i / 64;
      int k = ((p_188141_1_ + 1) * this.bitsPerEntry - 1) / 64;
      int l = i % 64;
      this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)p_188141_2_ & this.maxEntryValue) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bitsPerEntry - i1;
         this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)p_188141_2_ & this.maxEntryValue) >> i1;
      }

   }

   public int getAt(int p_188142_1_) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_188142_1_);
      int i = p_188142_1_ * this.bitsPerEntry;
      int j = i / 64;
      int k = ((p_188142_1_ + 1) * this.bitsPerEntry - 1) / 64;
      int l = i % 64;
      if (j == k) {
         return (int)(this.longArray[j] >>> l & this.maxEntryValue);
      } else {
         int i1 = 64 - l;
         return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
      }
   }

   public long[] getBackingLongArray() {
      return this.longArray;
   }

   public int size() {
      return this.arraySize;
   }

   public int bitsPerEntry() {
      return this.bitsPerEntry;
   }
}
