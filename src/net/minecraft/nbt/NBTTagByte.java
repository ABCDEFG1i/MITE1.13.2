package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagByte extends NBTPrimitive {
   private byte data;

   NBTTagByte() {
   }

   public NBTTagByte(byte p_i45129_1_) {
      this.data = p_i45129_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeByte(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(72L);
      this.data = p_152446_1_.readByte();
   }

   public byte getId() {
      return 1;
   }

   public String toString() {
      return this.data + "b";
   }

   public NBTTagByte copy() {
      return new NBTTagByte(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagByte && this.data == ((NBTTagByte)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("b")).applyTextStyle(field_197641_e);
      return (new TextComponentString(String.valueOf((int)this.data))).appendSibling(itextcomponent).applyTextStyle(field_197640_d);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return (short)this.data;
   }

   public byte getByte() {
      return this.data;
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
