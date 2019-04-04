package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger field_212776_a = LogManager.getLogger();
   private final DimensionType field_212777_b;
   private Map<String, WorldSavedData> field_212778_c = Maps.newHashMap();
   private final Object2IntMap<String> field_212779_d = new Object2IntOpenHashMap<>();
   @Nullable
   private final ISaveHandler field_212780_e;

   public DimensionSavedDataManager(DimensionType p_i49854_1_, @Nullable ISaveHandler p_i49854_2_) {
      this.field_212777_b = p_i49854_1_;
      this.field_212780_e = p_i49854_2_;
      this.field_212779_d.defaultReturnValue(-1);
   }

   @Nullable
   public <T extends WorldSavedData> T func_201067_a(Function<String, T> p_201067_1_, String p_201067_2_) {
      WorldSavedData worldsaveddata = this.field_212778_c.get(p_201067_2_);
      if (worldsaveddata == null && this.field_212780_e != null) {
         try {
            File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, p_201067_2_);
            if (file1 != null && file1.exists()) {
               worldsaveddata = p_201067_1_.apply(p_201067_2_);
               worldsaveddata.readFromNBT(func_212774_a(this.field_212780_e, this.field_212777_b, p_201067_2_, 1631).getCompoundTag("data"));
               this.field_212778_c.put(p_201067_2_, worldsaveddata);
            }
         } catch (Exception exception) {
            field_212776_a.error("Error loading saved data: {}", p_201067_2_, exception);
         }
      }

      return (T)worldsaveddata;
   }

   public void func_75745_a(String p_75745_1_, WorldSavedData p_75745_2_) {
      this.field_212778_c.put(p_75745_1_, p_75745_2_);
   }

   public void func_75746_b() {
      try {
         this.field_212779_d.clear();
         if (this.field_212780_e == null) {
            return;
         }

         File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, "idcounts");
         if (file1 != null && file1.exists()) {
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
            datainputstream.close();

            for(String s : nbttagcompound.getKeySet()) {
               if (nbttagcompound.hasKey(s, 99)) {
                  this.field_212779_d.put(s, nbttagcompound.getInteger(s));
               }
            }
         }
      } catch (Exception exception) {
         field_212776_a.error("Could not load aux values", exception);
      }

   }

   public int func_75743_a(String p_75743_1_) {
      int i = this.field_212779_d.getInt(p_75743_1_) + 1;
      this.field_212779_d.put(p_75743_1_, i);
      if (this.field_212780_e == null) {
         return i;
      } else {
         try {
            File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, "idcounts");
            if (file1 != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();

               for(Entry<String> entry : this.field_212779_d.object2IntEntrySet()) {
                  nbttagcompound.setInteger(entry.getKey(), entry.getIntValue());
               }

               DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
               CompressedStreamTools.write(nbttagcompound, dataoutputstream);
               dataoutputstream.close();
            }
         } catch (Exception exception) {
            field_212776_a.error("Could not get free aux value {}", p_75743_1_, exception);
         }

         return i;
      }
   }

   public static NBTTagCompound func_212774_a(ISaveHandler p_212774_0_, DimensionType p_212774_1_, String p_212774_2_, int p_212774_3_) throws IOException {
      File file1 = p_212774_0_.func_212423_a(p_212774_1_, p_212774_2_);

      NBTTagCompound nbttagcompound1;
      try (FileInputStream fileinputstream = new FileInputStream(file1)) {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
         int i = nbttagcompound.hasKey("DataVersion", 99) ? nbttagcompound.getInteger("DataVersion") : 1343;
         nbttagcompound1 = NBTUtil.update(p_212774_0_.getFixer(), DataFixTypes.SAVED_DATA, nbttagcompound, i, p_212774_3_);
      }

      return nbttagcompound1;
   }

   public void func_212775_b() {
      if (this.field_212780_e != null) {
         for(WorldSavedData worldsaveddata : this.field_212778_c.values()) {
            if (worldsaveddata.isDirty()) {
               this.func_75747_a(worldsaveddata);
               worldsaveddata.setDirty(false);
            }
         }

      }
   }

   private void func_75747_a(WorldSavedData p_75747_1_) {
      if (this.field_212780_e != null) {
         try {
            File file1 = this.field_212780_e.func_212423_a(this.field_212777_b, p_75747_1_.getName());
            if (file1 != null) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.setTag("data", p_75747_1_.writeToNBT(new NBTTagCompound()));
               nbttagcompound.setInteger("DataVersion", 1631);
               FileOutputStream fileoutputstream = new FileOutputStream(file1);
               CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
               fileoutputstream.close();
            }
         } catch (Exception exception) {
            field_212776_a.error("Could not save data {}", p_75747_1_, exception);
         }

      }
   }
}
