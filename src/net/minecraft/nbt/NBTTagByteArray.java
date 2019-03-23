package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagByteArray extends NBTTagCollection<NBTTagByte> {
   private byte[] data;

   NBTTagByteArray() {
   }

   public NBTTagByteArray(byte[] p_i45128_1_) {
      this.data = p_i45128_1_;
   }

   public NBTTagByteArray(List<Byte> p_i47529_1_) {
      this(toArray(p_i47529_1_));
   }

   private static byte[] toArray(List<Byte> p_193589_0_) {
      byte[] abyte = new byte[p_193589_0_.size()];

      for(int i = 0; i < p_193589_0_.size(); ++i) {
         Byte obyte = p_193589_0_.get(i);
         abyte[i] = obyte == null ? 0 : obyte;
      }

      return abyte;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data.length);
      p_74734_1_.write(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(192L);
      int i = p_152446_1_.readInt();
      p_152446_3_.read((long)(8 * i));
      this.data = new byte[i];
      p_152446_1_.readFully(this.data);
   }

   public byte getId() {
      return 7;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[B;");

      for(int i = 0; i < this.data.length; ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append((int)this.data[i]).append('B');
      }

      return stringbuilder.append(']').toString();
   }

   public INBTBase copy() {
      byte[] abyte = new byte[this.data.length];
      System.arraycopy(this.data, 0, abyte, 0, this.data.length);
      return new NBTTagByteArray(abyte);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagByteArray && Arrays.equals(this.data, ((NBTTagByteArray)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("B")).applyTextStyle(field_197641_e);
      ITextComponent itextcomponent1 = (new TextComponentString("[")).appendSibling(itextcomponent).appendText(";");

      for(int i = 0; i < this.data.length; ++i) {
         ITextComponent itextcomponent2 = (new TextComponentString(String.valueOf((int)this.data[i]))).applyTextStyle(field_197640_d);
         itextcomponent1.appendText(" ").appendSibling(itextcomponent2).appendSibling(itextcomponent);
         if (i != this.data.length - 1) {
            itextcomponent1.appendText(",");
         }
      }

      itextcomponent1.appendText("]");
      return itextcomponent1;
   }

   public byte[] getByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public NBTTagByte func_197647_c(int p_197647_1_) {
      return new NBTTagByte(this.data[p_197647_1_]);
   }

   public void func_197648_a(int p_197648_1_, INBTBase p_197648_2_) {
      this.data[p_197648_1_] = ((NBTPrimitive)p_197648_2_).getByte();
   }

   public void func_197649_b(int p_197649_1_) {
      this.data = ArrayUtils.remove(this.data, p_197649_1_);
   }
}
