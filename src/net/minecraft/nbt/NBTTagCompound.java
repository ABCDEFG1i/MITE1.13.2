package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound implements INBTBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   private final Map<String, INBTBase> tagMap = Maps.newHashMap();

   public void write(DataOutput p_74734_1_) throws IOException {
      for(String s : this.tagMap.keySet()) {
         INBTBase inbtbase = this.tagMap.get(s);
         writeEntry(s, inbtbase, p_74734_1_);
      }

      p_74734_1_.writeByte(0);
   }

   public void read(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
      p_152446_3_.read(384L);
      if (p_152446_2_ > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.tagMap.clear();

         byte b0;
         while((b0 = readType(p_152446_1_, p_152446_3_)) != 0) {
            String s = readKey(p_152446_1_, p_152446_3_);
            p_152446_3_.read((long)(224 + 16 * s.length()));
            INBTBase inbtbase = readNBT(b0, s, p_152446_1_, p_152446_2_ + 1, p_152446_3_);
            if (this.tagMap.put(s, inbtbase) != null) {
               p_152446_3_.read(288L);
            }
         }

      }
   }

   public Set<String> getKeySet() {
      return this.tagMap.keySet();
   }

   public byte getId() {
      return 10;
   }

   public int getSize() {
      return this.tagMap.size();
   }

   public void setTag(String p_74782_1_, INBTBase p_74782_2_) {
      this.tagMap.put(p_74782_1_, p_74782_2_);
   }

   public void setByte(String p_74774_1_, byte p_74774_2_) {
      this.tagMap.put(p_74774_1_, new NBTTagByte(p_74774_2_));
   }

   public void setShort(String p_74777_1_, short p_74777_2_) {
      this.tagMap.put(p_74777_1_, new NBTTagShort(p_74777_2_));
   }

   public void setInteger(String p_74768_1_, int p_74768_2_) {
      this.tagMap.put(p_74768_1_, new NBTTagInt(p_74768_2_));
   }

   public void setLong(String p_74772_1_, long p_74772_2_) {
      this.tagMap.put(p_74772_1_, new NBTTagLong(p_74772_2_));
   }

   public void setUniqueId(String p_186854_1_, UUID p_186854_2_) {
      this.setLong(p_186854_1_ + "Most", p_186854_2_.getMostSignificantBits());
      this.setLong(p_186854_1_ + "Least", p_186854_2_.getLeastSignificantBits());
   }

   @Nullable
   public UUID getUniqueId(String p_186857_1_) {
      return new UUID(this.getLong(p_186857_1_ + "Most"), this.getLong(p_186857_1_ + "Least"));
   }

   public boolean hasUniqueId(String p_186855_1_) {
      return this.hasKey(p_186855_1_ + "Most", 99) && this.hasKey(p_186855_1_ + "Least", 99);
   }

   public void setFloat(String p_74776_1_, float p_74776_2_) {
      this.tagMap.put(p_74776_1_, new NBTTagFloat(p_74776_2_));
   }

   public void setDouble(String p_74780_1_, double p_74780_2_) {
      this.tagMap.put(p_74780_1_, new NBTTagDouble(p_74780_2_));
   }

   public void setString(String p_74778_1_, String p_74778_2_) {
      this.tagMap.put(p_74778_1_, new NBTTagString(p_74778_2_));
   }

   public void setByteArray(String p_74773_1_, byte[] p_74773_2_) {
      this.tagMap.put(p_74773_1_, new NBTTagByteArray(p_74773_2_));
   }

   public void setIntArray(String p_74783_1_, int[] p_74783_2_) {
      this.tagMap.put(p_74783_1_, new NBTTagIntArray(p_74783_2_));
   }

   public void setIntArray(String p_197646_1_, List<Integer> p_197646_2_) {
      this.tagMap.put(p_197646_1_, new NBTTagIntArray(p_197646_2_));
   }

   public void setLongArray(String p_197644_1_, long[] p_197644_2_) {
      this.tagMap.put(p_197644_1_, new NBTTagLongArray(p_197644_2_));
   }

   public void setLongArray(String p_202168_1_, List<Long> p_202168_2_) {
      this.tagMap.put(p_202168_1_, new NBTTagLongArray(p_202168_2_));
   }

   public void setBoolean(String p_74757_1_, boolean p_74757_2_) {
      this.setByte(p_74757_1_, (byte)(p_74757_2_ ? 1 : 0));
   }

   public INBTBase getTag(String p_74781_1_) {
      return this.tagMap.get(p_74781_1_);
   }

   public byte getTagId(String p_150299_1_) {
      INBTBase inbtbase = this.tagMap.get(p_150299_1_);
      return inbtbase == null ? 0 : inbtbase.getId();
   }

   public boolean hasKey(String p_74764_1_) {
      return this.tagMap.containsKey(p_74764_1_);
   }

   public boolean hasKey(String p_150297_1_, int p_150297_2_) {
      int i = this.getTagId(p_150297_1_);
      if (i == p_150297_2_) {
         return true;
      } else if (p_150297_2_ != 99) {
         return false;
      } else {
         return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
      }
   }

   public byte getByte(String p_74771_1_) {
      try {
         if (this.hasKey(p_74771_1_, 99)) {
            return ((NBTPrimitive)this.tagMap.get(p_74771_1_)).getByte();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public short getShort(String p_74765_1_) {
      try {
         if (this.hasKey(p_74765_1_, 99)) {
            return ((NBTPrimitive)this.tagMap.get(p_74765_1_)).getShort();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public int getInteger(String p_74762_1_) {
      try {
         if (this.hasKey(p_74762_1_, 99)) {
            return ((NBTPrimitive)this.tagMap.get(p_74762_1_)).getInt();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public long getLong(String p_74763_1_) {
      try {
         if (this.hasKey(p_74763_1_, 99)) {
            return ((NBTPrimitive)this.tagMap.get(p_74763_1_)).getLong();
         }
      } catch (ClassCastException var3) {
      }

      return 0L;
   }

   public float getFloat(String p_74760_1_) {
      try {
         if (this.hasKey(p_74760_1_, 99)) {
            return ((NBTPrimitive)this.tagMap.get(p_74760_1_)).getFloat();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0F;
   }

   public double getDouble(String p_74769_1_) {
      try {
         if (this.hasKey(p_74769_1_, 99)) {
            return ((NBTPrimitive)this.tagMap.get(p_74769_1_)).getDouble();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0D;
   }

   public String getString(String p_74779_1_) {
      try {
         if (this.hasKey(p_74779_1_, 8)) {
            return this.tagMap.get(p_74779_1_).getString();
         }
      } catch (ClassCastException var3) {
      }

      return "";
   }

   public byte[] getByteArray(String p_74770_1_) {
      try {
         if (this.hasKey(p_74770_1_, 7)) {
            return ((NBTTagByteArray)this.tagMap.get(p_74770_1_)).getByteArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(p_74770_1_, 7, classcastexception));
      }

      return new byte[0];
   }

   public int[] getIntArray(String p_74759_1_) {
      try {
         if (this.hasKey(p_74759_1_, 11)) {
            return ((NBTTagIntArray)this.tagMap.get(p_74759_1_)).getIntArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(p_74759_1_, 11, classcastexception));
      }

      return new int[0];
   }

   public long[] readLongArray(String p_197645_1_) {
      try {
         if (this.hasKey(p_197645_1_, 12)) {
            return ((NBTTagLongArray)this.tagMap.get(p_197645_1_)).func_197652_h();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(p_197645_1_, 12, classcastexception));
      }

      return new long[0];
   }

   public NBTTagCompound getCompoundTag(String p_74775_1_) {
      try {
         if (this.hasKey(p_74775_1_, 10)) {
            return (NBTTagCompound)this.tagMap.get(p_74775_1_);
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(p_74775_1_, 10, classcastexception));
      }

      return new NBTTagCompound();
   }

   public NBTTagList getTagList(String p_150295_1_, int p_150295_2_) {
      try {
         if (this.getTagId(p_150295_1_) == 9) {
            NBTTagList nbttaglist = (NBTTagList)this.tagMap.get(p_150295_1_);
            if (!nbttaglist.isEmpty() && nbttaglist.getTagType() != p_150295_2_) {
               return new NBTTagList();
            }

            return nbttaglist;
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createCrashReport(p_150295_1_, 9, classcastexception));
      }

      return new NBTTagList();
   }

   public boolean getBoolean(String p_74767_1_) {
      return this.getByte(p_74767_1_) != 0;
   }

   public void removeTag(String p_82580_1_) {
      this.tagMap.remove(p_82580_1_);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");
      Collection<String> collection = this.tagMap.keySet();
      if (LOGGER.isDebugEnabled()) {
         List<String> list = Lists.newArrayList(this.tagMap.keySet());
         Collections.sort(list);
         collection = list;
      }

      for(String s : collection) {
         if (stringbuilder.length() != 1) {
            stringbuilder.append(',');
         }

         stringbuilder.append(handleEscape(s)).append(':').append(this.tagMap.get(s));
      }

      return stringbuilder.append('}').toString();
   }

   public boolean isEmpty() {
      return this.tagMap.isEmpty();
   }

   private CrashReport createCrashReport(String p_82581_1_, int p_82581_2_, ClassCastException p_82581_3_) {
      CrashReport crashreport = CrashReport.makeCrashReport(p_82581_3_, "Reading NBT data");
      CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
      crashreportcategory.addDetail("Tag type found", () -> {
         return NBT_TYPES[this.tagMap.get(p_82581_1_).getId()];
      });
      crashreportcategory.addDetail("Tag type expected", () -> {
         return NBT_TYPES[p_82581_2_];
      });
      crashreportcategory.addCrashSection("Tag name", p_82581_1_);
      return crashreport;
   }

   public NBTTagCompound copy() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(String s : this.tagMap.keySet()) {
         nbttagcompound.setTag(s, this.tagMap.get(s).copy());
      }

      return nbttagcompound;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof NBTTagCompound && Objects.equals(this.tagMap, ((NBTTagCompound)p_equals_1_).tagMap);
      }
   }

   public int hashCode() {
      return this.tagMap.hashCode();
   }

   private static void writeEntry(String p_150298_0_, INBTBase p_150298_1_, DataOutput p_150298_2_) throws IOException {
      p_150298_2_.writeByte(p_150298_1_.getId());
      if (p_150298_1_.getId() != 0) {
         p_150298_2_.writeUTF(p_150298_0_);
         p_150298_1_.write(p_150298_2_);
      }
   }

   private static byte readType(DataInput p_152447_0_, NBTSizeTracker p_152447_1_) throws IOException {
      return p_152447_0_.readByte();
   }

   private static String readKey(DataInput p_152448_0_, NBTSizeTracker p_152448_1_) throws IOException {
      return p_152448_0_.readUTF();
   }

   static INBTBase readNBT(byte p_152449_0_, String p_152449_1_, DataInput p_152449_2_, int p_152449_3_, NBTSizeTracker p_152449_4_) throws IOException {
      INBTBase inbtbase = INBTBase.create(p_152449_0_);

      try {
         inbtbase.read(p_152449_2_, p_152449_3_, p_152449_4_);
         return inbtbase;
      } catch (IOException ioexception) {
         CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
         crashreportcategory.addCrashSection("Tag name", p_152449_1_);
         crashreportcategory.addCrashSection("Tag type", p_152449_0_);
         throw new ReportedException(crashreport);
      }
   }

   public NBTTagCompound merge(NBTTagCompound p_197643_1_) {
      for(String s : p_197643_1_.tagMap.keySet()) {
         INBTBase inbtbase = p_197643_1_.tagMap.get(s);
         if (inbtbase.getId() == 10) {
            if (this.hasKey(s, 10)) {
               NBTTagCompound nbttagcompound = this.getCompoundTag(s);
               nbttagcompound.merge((NBTTagCompound)inbtbase);
            } else {
               this.setTag(s, inbtbase.copy());
            }
         } else {
            this.setTag(s, inbtbase.copy());
         }
      }

      return this;
   }

   protected static String handleEscape(String p_193582_0_) {
      return SIMPLE_VALUE.matcher(p_193582_0_).matches() ? p_193582_0_ : NBTTagString.func_197654_a(p_193582_0_, true);
   }

   protected static ITextComponent func_197642_t(String p_197642_0_) {
      if (SIMPLE_VALUE.matcher(p_197642_0_).matches()) {
         return (new TextComponentString(p_197642_0_)).applyTextStyle(field_197638_b);
      } else {
         ITextComponent itextcomponent = (new TextComponentString(NBTTagString.func_197654_a(p_197642_0_, false))).applyTextStyle(field_197638_b);
         return (new TextComponentString("\"")).appendSibling(itextcomponent).appendText("\"");
      }
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      if (this.tagMap.isEmpty()) {
         return new TextComponentString("{}");
      } else {
         ITextComponent itextcomponent = new TextComponentString("{");
         Collection<String> collection = this.tagMap.keySet();
         if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(this.tagMap.keySet());
            Collections.sort(list);
            collection = list;
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.appendText("\n");
         }

         ITextComponent itextcomponent1;
         for(Iterator<String> iterator = collection.iterator(); iterator.hasNext(); itextcomponent.appendSibling(itextcomponent1)) {
            String s = iterator.next();
            itextcomponent1 = (new TextComponentString(Strings.repeat(p_199850_1_, p_199850_2_ + 1))).appendSibling(func_197642_t(s)).appendText(String.valueOf(':')).appendText(" ").appendSibling(this.tagMap.get(s).toFormattedComponent(p_199850_1_, p_199850_2_ + 1));
            if (iterator.hasNext()) {
               itextcomponent1.appendText(String.valueOf(',')).appendText(p_199850_1_.isEmpty() ? " " : "\n");
            }
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.appendText("\n").appendText(Strings.repeat(p_199850_1_, p_199850_2_));
         }

         itextcomponent.appendText("}");
         return itextcomponent;
      }
   }
}
