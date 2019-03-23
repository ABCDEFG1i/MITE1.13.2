package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagIntArray extends NBTTagCollection<NBTTagInt> {
   private int[] intArray;

   NBTTagIntArray() {
   }

   public NBTTagIntArray(int[] p_i45132_1_) {
      this.intArray = p_i45132_1_;
   }

   public NBTTagIntArray(List<Integer> p_i47528_1_) {
      this(toArray(p_i47528_1_));
   }

   private static int[] toArray(List<Integer> p_193584_0_) {
      int[] aint = new int[p_193584_0_.size()];

      for(int i = 0; i < p_193584_0_.size(); ++i) {
         Integer integer = p_193584_0_.get(i);
         aint[i] = integer == null ? 0 : integer;
      }

      return aint;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.intArray.length);

      for(int i : this.intArray) {
         p_74734_1_.writeInt(i);
      }

   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(192L);
      int i = p_152446_1_.readInt();
      p_152446_3_.read((long)(32 * i));
      this.intArray = new int[i];

      for(int j = 0; j < i; ++j) {
         this.intArray[j] = p_152446_1_.readInt();
      }

   }

   public byte getId() {
      return 11;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[I;");

      for(int i = 0; i < this.intArray.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.intArray[i]);
      }

      return stringbuilder.append(']').toString();
   }

   public NBTTagIntArray copy() {
      int[] aint = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
      return new NBTTagIntArray(aint);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagIntArray && Arrays.equals(this.intArray, ((NBTTagIntArray)p_equals_1_).intArray);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("I")).applyTextStyle(field_197641_e);
      ITextComponent itextcomponent1 = (new TextComponentString("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.intArray.length; ++i) {
         itextcomponent1.appendText(" ").appendSibling((new TextComponentString(String.valueOf(this.intArray[i]))).applyTextStyle(field_197640_d));
         if (i != this.intArray.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public int size() {
      return this.intArray.length;
   }

   public NBTTagInt func_197647_c(int p_197647_1_) {
      return new NBTTagInt(this.intArray[p_197647_1_]);
   }

   public void func_197648_a(int p_197648_1_, INBTBase p_197648_2_) {
      this.intArray[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getInt();
   }

   public void func_197649_b(int p_197649_1_) {
      this.intArray = ArrayUtils.remove(this.intArray, p_197649_1_);
   }
}
