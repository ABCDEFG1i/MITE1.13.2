package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagFloat extends NBTPrimitive {
   private float data;

   NBTTagFloat() {
   }

   public NBTTagFloat(float p_i45131_1_) {
      this.data = p_i45131_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeFloat(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(96L);
      this.data = p_152446_1_.readFloat();
   }

   public byte getId() {
      return 5;
   }

   public String toString() {
      return this.data + "f";
   }

   public NBTTagFloat copy() {
      return new NBTTagFloat(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagFloat && this.data == ((NBTTagFloat)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString("f")).applyTextStyle(field_197641_e);
      return (new TextComponentString(String.valueOf(this.data))).appendSibling(itextcomponent).applyTextStyle(field_197640_d);
   }

   public long getLong() {
      return (long)this.data;
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
      return (double)this.data;
   }

   public float getFloat() {
      return this.data;
   }

   public Number func_209908_j() {
      return this.data;
   }
}
