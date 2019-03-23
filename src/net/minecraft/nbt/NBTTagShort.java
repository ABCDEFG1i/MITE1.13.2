package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagShort extends NBTPrimitive {
   private short data;

   public NBTTagShort() {
   }

   public NBTTagShort(short p_i45135_1_) {
      this.data = p_i45135_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeShort(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(80L);
      this.data = p_152446_1_.readShort();
   }

   public byte getId() {
      return 2;
   }

   public String toString() {
      return this.data + "s";
   }

   public NBTTagShort copy() {
      return new NBTTagShort(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagShort && this.data == ((NBTTagShort)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("s")).applyTextStyle(field_197641_e);
      return (new TextComponentString(String.valueOf((int)this.data))).appendSibling(itextcomponent).applyTextStyle(field_197640_d);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return this.data;
   }

   public byte getByte() {
      return (byte)(this.data & 255);
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
