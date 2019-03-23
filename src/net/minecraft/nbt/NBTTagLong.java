package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagLong extends NBTPrimitive {
   private long data;

   NBTTagLong() {
   }

   public NBTTagLong(long p_i45134_1_) {
      this.data = p_i45134_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeLong(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(128L);
      this.data = p_152446_1_.readLong();
   }

   public byte getId() {
      return 4;
   }

   public String toString() {
      return this.data + "L";
   }

   public NBTTagLong copy() {
      return new NBTTagLong(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagLong && this.data == ((NBTTagLong)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("L")).applyTextStyle(field_197641_e);
      return (new TextComponentString(String.valueOf(this.data))).appendSibling(itextcomponent).applyTextStyle(field_197640_d);
   }

   public long getLong() {
      return this.data;
   }

   public int getInt() {
      return (int)(this.data & -1L);
   }

   public short getShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getByte() {
      return (byte)((int)(this.data & 255L));
   }

   public double getDouble() {
      return (double)this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public Number func_209908_j() {
      return this.data;
   }
}
