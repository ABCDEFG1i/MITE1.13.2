package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTTagCollection<INBTBase> {
   private static final Logger LOGGER = LogManager.getLogger();
   private List<INBTBase> tagList = Lists.newArrayList();
   private byte tagType = 0;

   public void write(DataOutput p_74734_1_) throws IOException {
      if (this.tagList.isEmpty()) {
         this.tagType = 0;
      } else {
         this.tagType = this.tagList.get(0).getId();
      }

      p_74734_1_.writeByte(this.tagType);
      p_74734_1_.writeInt(this.tagList.size());

      for(int i = 0; i < this.tagList.size(); ++i) {
         this.tagList.get(i).write(p_74734_1_);
      }

   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(296L);
      if (p_152446_2_ > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.tagType = p_152446_1_.readByte();
         int i = p_152446_1_.readInt();
         if (this.tagType == 0 && i > 0) {
            throw new RuntimeException("Missing type on ListTag");
         } else {
            p_152446_3_.read(32L * (long)i);
            this.tagList = Lists.newArrayListWithCapacity(i);

            for(int j = 0; j < i; ++j) {
               INBTBase inbtbase = INBTBase.create(this.tagType);
               inbtbase.read(p_152446_1_, p_152446_2_ + 1, p_152446_3_);
               this.tagList.add(inbtbase);
            }

         }
      }
   }

   public byte getId() {
      return 9;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("[");

      for(int i = 0; i < this.tagList.size(); ++i) {
         if (i != 0) {
            stringbuilder.append(',');
         }

         stringbuilder.append(this.tagList.get(i));
      }

      return stringbuilder.append(']').toString();
   }

   public boolean add(INBTBase p_add_1_) {
      if (p_add_1_.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
         return false;
      } else {
         if (this.tagType == 0) {
            this.tagType = p_add_1_.getId();
         } else if (this.tagType != p_add_1_.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return false;
         }

         this.tagList.add(p_add_1_);
         return true;
      }
   }

   public INBTBase set(int p_set_1_, INBTBase p_set_2_) {
      if (p_set_2_.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
         return this.tagList.get(p_set_1_);
      } else if (p_set_1_ >= 0 && p_set_1_ < this.tagList.size()) {
         if (this.tagType == 0) {
            this.tagType = p_set_2_.getId();
         } else if (this.tagType != p_set_2_.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return this.tagList.get(p_set_1_);
         }

         return this.tagList.set(p_set_1_, p_set_2_);
      } else {
         LOGGER.warn("index out of bounds to set tag in tag list");
         return null;
      }
   }

   public INBTBase remove(int p_remove_1_) {
      return this.tagList.remove(p_remove_1_);
   }

   public boolean isEmpty() {
      return this.tagList.isEmpty();
   }

   public NBTTagCompound getCompoundTagAt(int p_150305_1_) {
      if (p_150305_1_ >= 0 && p_150305_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_150305_1_);
         if (inbtbase.getId() == 10) {
            return (NBTTagCompound)inbtbase;
         }
      }

      return new NBTTagCompound();
   }

   public NBTTagList getTagListAt(int p_202169_1_) {
      if (p_202169_1_ >= 0 && p_202169_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_202169_1_);
         if (inbtbase.getId() == 9) {
            return (NBTTagList)inbtbase;
         }
      }

      return new NBTTagList();
   }

   public short getShortAt(int p_202170_1_) {
      if (p_202170_1_ >= 0 && p_202170_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_202170_1_);
         if (inbtbase.getId() == 2) {
            return ((NBTTagShort)inbtbase).getShort();
         }
      }

      return 0;
   }

   public int getIntAt(int p_186858_1_) {
      if (p_186858_1_ >= 0 && p_186858_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_186858_1_);
         if (inbtbase.getId() == 3) {
            return ((NBTTagInt)inbtbase).getInt();
         }
      }

      return 0;
   }

   public int[] getIntArrayAt(int p_150306_1_) {
      if (p_150306_1_ >= 0 && p_150306_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_150306_1_);
         if (inbtbase.getId() == 11) {
            return ((NBTTagIntArray)inbtbase).getIntArray();
         }
      }

      return new int[0];
   }

   public double getDoubleAt(int p_150309_1_) {
      if (p_150309_1_ >= 0 && p_150309_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_150309_1_);
         if (inbtbase.getId() == 6) {
            return ((NBTTagDouble)inbtbase).getDouble();
         }
      }

      return 0.0D;
   }

   public float getFloatAt(int p_150308_1_) {
      if (p_150308_1_ >= 0 && p_150308_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_150308_1_);
         if (inbtbase.getId() == 5) {
            return ((NBTTagFloat)inbtbase).getFloat();
         }
      }

      return 0.0F;
   }

   public String getStringTagAt(int p_150307_1_) {
      if (p_150307_1_ >= 0 && p_150307_1_ < this.tagList.size()) {
         INBTBase inbtbase = this.tagList.get(p_150307_1_);
         return inbtbase.getId() == 8 ? inbtbase.getString() : inbtbase.toString();
      } else {
         return "";
      }
   }

   public INBTBase get(int p_get_1_) {
      return (INBTBase)(p_get_1_ >= 0 && p_get_1_ < this.tagList.size() ? this.tagList.get(p_get_1_) : new NBTTagEnd());
   }

   public int size() {
      return this.tagList.size();
   }

   public INBTBase func_197647_c(int p_197647_1_) {
      return this.tagList.get(p_197647_1_);
   }

   public void func_197648_a(int p_197648_1_, INBTBase p_197648_2_) {
      this.tagList.set(p_197648_1_, p_197648_2_);
   }

   public void func_197649_b(int p_197649_1_) {
      this.tagList.remove(p_197649_1_);
   }

   public NBTTagList copy() {
      NBTTagList nbttaglist = new NBTTagList();
      nbttaglist.tagType = this.tagType;

      for(INBTBase inbtbase : this.tagList) {
         INBTBase inbtbase1 = inbtbase.copy();
         nbttaglist.tagList.add(inbtbase1);
      }

      return nbttaglist;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagList && Objects.equals(this.tagList, ((NBTTagList)p_equals_1_).tagList);
      }
   }

   public int hashCode() {
      return this.tagList.hashCode();
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      if (this.isEmpty()) {
         return new TextComponentString("[]");
      } else {
         ITextComponent itextcomponent = new TextComponentString("[");
         if (!p_199850_1_.isEmpty()) {
            itextcomponent.appendText("\n");
         }

         for(int i = 0; i < this.tagList.size(); ++i) {
            ITextComponent itextcomponent1 = new TextComponentString(Strings.repeat(p_199850_1_, p_199850_2_ + 1));
            itextcomponent1.appendSibling(this.tagList.get(i).toFormattedComponent(p_199850_1_, p_199850_2_ + 1));
            if (i != this.tagList.size() - 1) {
               itextcomponent1.appendText(String.valueOf(',')).appendText(p_199850_1_.isEmpty() ? " " : "\n");
            }

            itextcomponent.appendSibling(itextcomponent1);
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.appendText("\n").appendText(Strings.repeat(p_199850_1_, p_199850_2_));
         }

         itextcomponent.appendText("]");
         return itextcomponent;
      }
   }

   public int getTagType() {
      return this.tagType;
   }
}
