package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagDouble extends NBTPrimitive {
   private double data;

   NBTTagDouble() {
   }

   public NBTTagDouble(double p_i45130_1_) {
      this.data = p_i45130_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeDouble(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(128L);
      this.data = p_152446_1_.readDouble();
   }

   public byte getId() {
      return 6;
   }

   public String toString() {
      return this.data + "d";
   }

   public NBTTagDouble copy() {
      return new NBTTagDouble(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagDouble && this.data == ((NBTTagDouble)p_equals_1_).data;
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.data);
      return (int)(i ^ i >>> 32);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("d")).applyTextStyle(field_197641_e);
      return (new TextComponentString(String.valueOf(this.data))).appendSibling(itextcomponent).applyTextStyle(field_197640_d);
   }

   public long getLong() {
      return (long)Math.floor(this.data);
   }

   public int getInt() {
      return MathHelper.floor(this.data);
   }

   public short getShort() {
      return (short)(MathHelper.floor(this.data) & '\uffff');
   }

   public byte getByte() {
      return (byte)(MathHelper.floor(this.data) & 255);
   }

   public double getDouble() {
      return this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public Number func_209908_j() {
      return this.data;
   }
}
