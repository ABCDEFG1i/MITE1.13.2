package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagEnd implements INBTBase {
   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(64L);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
   }

   public byte getId() {
      return 0;
   }

   public String toString() {
      return "END";
   }

   public NBTTagEnd copy() {
      return new NBTTagEnd();
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      return new TextComponentString("");
   }

   public boolean equals(Object p_equals_1_) {
      return p_equals_1_ instanceof NBTTagEnd;
   }

   public int hashCode() {
      return this.getId();
   }
}
