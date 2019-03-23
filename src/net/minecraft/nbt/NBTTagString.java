package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagString implements INBTBase {
   private String data;

   public NBTTagString() {
      this("");
   }

   public NBTTagString(String p_i1389_1_) {
      Objects.requireNonNull(p_i1389_1_, "Null string not allowed");
      this.data = p_i1389_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeUTF(this.data);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(288L);
      this.data = p_152446_1_.readUTF();
      p_152446_3_.read((long)(16 * this.data.length()));
   }

   public byte getId() {
      return 8;
   }

   public String toString() {
      return func_197654_a(this.data, true);
   }

   public NBTTagString copy() {
      return new NBTTagString(this.data);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagString && Objects.equals(this.data, ((NBTTagString)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getString() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent itextcomponent = (new TextComponentString(func_197654_a(this.data, false))).applyTextStyle(field_197639_c);
      return (new TextComponentString("\"")).appendSibling(itextcomponent).appendText("\"");
   }

   public static String func_197654_a(String p_197654_0_, boolean p_197654_1_) {
      StringBuilder stringbuilder = new StringBuilder();
      if (p_197654_1_) {
         stringbuilder.append('"');
      }

      for(int i = 0; i < p_197654_0_.length(); ++i) {
         char c0 = p_197654_0_.charAt(i);
         if (c0 == '\\' || c0 == '"') {
            stringbuilder.append('\\');
         }

         stringbuilder.append(c0);
      }

      if (p_197654_1_) {
         stringbuilder.append('"');
      }

      return stringbuilder.toString();
   }
}
