package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagInt extends NBTPrimitive {
   private int data;

   NBTTagInt() {
   }

   public NBTTagInt(int p_i45133_1_) {
      this.data = p_i45133_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(96L);
      this.data = p_152446_1_.readInt();
   }

   public byte getId() {
      return 3;
   }

   public String toString() {
      return String.valueOf(this.data);
   }

   public NBTTagInt copy() {
      return new NBTTagInt(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagInt && this.data == ((NBTTagInt)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      return (new TextComponentString(String.valueOf(this.data))).applyTextStyle(field_197640_d);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return (short)(this.data & '\uffff');
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
