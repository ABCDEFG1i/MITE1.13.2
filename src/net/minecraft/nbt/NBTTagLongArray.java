package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagLongArray extends NBTTagCollection<NBTTagLong> {
   private long[] data;

   NBTTagLongArray() {
   }

   public NBTTagLongArray(long[] p_i47524_1_) {
      this.data = p_i47524_1_;
   }

   public NBTTagLongArray(LongSet p_i48736_1_) {
      this.data = p_i48736_1_.toLongArray();
   }

   public NBTTagLongArray(List<Long> p_i47525_1_) {
      this(toArray(p_i47525_1_));
   }

   private static long[] toArray(List<Long> p_193586_0_) {
      long[] along = new long[p_193586_0_.size()];

      for(int i = 0; i < p_193586_0_.size(); ++i) {
         Long olong = p_193586_0_.get(i);
         along[i] = olong == null ? 0L : olong;
      }

      return along;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data.length);

      for(long i : this.data) {
         p_74734_1_.writeLong(i);
      }

   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(192L);
      int i = p_152446_1_.readInt();
      p_152446_3_.read((long)(64 * i));
      this.data = new long[i];

      for(int j = 0; j < i; ++j) {
         this.data[j] = p_152446_1_.readLong();
      }

   }

   public byte getId() {
      return 12;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[L;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.data[i]).append('L');
      }

      return stringbuilder.append(']').toString();
   }

   public NBTTagLongArray copy() {
      long[] along = new long[this.data.length];
      System.arraycopy(this.data, 0, along, 0, this.data.length);
      return new NBTTagLongArray(along);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagLongArray && Arrays.equals(this.data, ((NBTTagLongArray)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("L")).applyTextStyle(field_197641_e);
      ITextComponent itextcomponent1 = (new TextComponentString("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.data.length; ++i) {
         ITextComponent itextcomponent2 = (new TextComponentString(String.valueOf(this.data[i]))).applyTextStyle(field_197640_d);
         itextcomponent1.appendText(" ").appendSibling(itextcomponent2).appendSibling(itextcomponent);
         if (i != this.data.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public long[] func_197652_h() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public NBTTagLong func_197647_c(int p_197647_1_) {
      return new NBTTagLong(this.data[p_197647_1_]);
   }

   public void func_197648_a(int p_197648_1_, INBTBase p_197648_2_) {
      this.data[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getLong();
   }

   public void func_197649_b(int p_197649_1_) {
      this.data = ArrayUtils.remove(this.data, p_197649_1_);
   }
}
