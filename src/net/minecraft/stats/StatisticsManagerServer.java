package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsManagerServer extends StatisticsManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File statsFile;
   private final Set<Stat<?>> dirty = Sets.newHashSet();
   private int lastStatRequest = -300;

   public StatisticsManagerServer(MinecraftServer p_i45306_1_, File p_i45306_2_) {
      this.server = p_i45306_1_;
      this.statsFile = p_i45306_2_;
      if (p_i45306_2_.isFile()) {
         try {
            this.func_199062_a(p_i45306_1_.getDataFixer(), FileUtils.readFileToString(p_i45306_2_));
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read statistics file {}", p_i45306_2_, ioexception);
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse statistics file {}", p_i45306_2_, jsonparseexception);
         }
      }

   }

   public void saveStatFile() {
      try {
         FileUtils.writeStringToFile(this.statsFile, this.func_199061_b());
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save stats", (Throwable)ioexception);
      }

   }

   public void func_150873_a(EntityPlayer p_150873_1_, Stat<?> p_150873_2_, int p_150873_3_) {
      super.func_150873_a(p_150873_1_, p_150873_2_, p_150873_3_);
      this.dirty.add(p_150873_2_);
   }

   private Set<Stat<?>> getDirty() {
      Set<Stat<?>> set = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return set;
   }

   public void func_199062_a(DataFixer p_199062_1_, String p_199062_2_) {
      try (JsonReader jsonreader = new JsonReader(new StringReader(p_199062_2_))) {
         jsonreader.setLenient(false);
         JsonElement jsonelement = Streams.parse(jsonreader);
         if (!jsonelement.isJsonNull()) {
            NBTTagCompound nbttagcompound = func_199065_a(jsonelement.getAsJsonObject());
            if (!nbttagcompound.hasKey("DataVersion", 99)) {
               nbttagcompound.setInteger("DataVersion", 1343);
            }

            nbttagcompound = NBTUtil.func_210822_a(p_199062_1_, DataFixTypes.STATS, nbttagcompound, nbttagcompound.getInteger("DataVersion"));
            if (nbttagcompound.hasKey("stats", 10)) {
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("stats");

               for(String s : nbttagcompound1.getKeySet()) {
                  if (nbttagcompound1.hasKey(s, 10)) {
                     StatType<?> stattype = IRegistry.field_212634_w.func_212608_b(new ResourceLocation(s));
                     if (stattype == null) {
                        LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.statsFile, s);
                     } else {
                        NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompoundTag(s);

                        for(String s1 : nbttagcompound2.getKeySet()) {
                           if (nbttagcompound2.hasKey(s1, 99)) {
                              Stat<?> stat = this.func_199063_a(stattype, s1);
                              if (stat == null) {
                                 LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.statsFile, s1);
                              } else {
                                 this.statsData.put(stat, nbttagcompound2.getInteger(s1));
                              }
                           } else {
                              LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.statsFile, nbttagcompound2.getTag(s1), s1);
                           }
                        }
                     }
                  }
               }
            }
         } else {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.statsFile);
         }
      } catch (IOException | JsonParseException jsonparseexception) {
         LOGGER.error("Unable to parse Stat data from {}", this.statsFile, jsonparseexception);
      }

   }

   @Nullable
   private <T> Stat<T> func_199063_a(StatType<T> p_199063_1_, String p_199063_2_) {
      ResourceLocation resourcelocation = ResourceLocation.makeResourceLocation(p_199063_2_);
      if (resourcelocation == null) {
         return null;
      } else {
         T t = p_199063_1_.func_199080_a().func_212608_b(resourcelocation);
         return t == null ? null : p_199063_1_.func_199076_b(t);
      }
   }

   private static NBTTagCompound func_199065_a(JsonObject p_199065_0_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();

      for(Entry<String, JsonElement> entry : p_199065_0_.entrySet()) {
         JsonElement jsonelement = entry.getValue();
         if (jsonelement.isJsonObject()) {
            nbttagcompound.setTag(entry.getKey(), func_199065_a(jsonelement.getAsJsonObject()));
         } else if (jsonelement.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               nbttagcompound.setInteger(entry.getKey(), jsonprimitive.getAsInt());
            }
         }
      }

      return nbttagcompound;
   }

   protected String func_199061_b() {
      Map<StatType<?>, JsonObject> map = Maps.newHashMap();

      for(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.statsData.object2IntEntrySet()) {
         Stat<?> stat = entry.getKey();
         map.computeIfAbsent(stat.func_197921_a(), (p_199064_0_) -> {
            return new JsonObject();
         }).addProperty(func_199066_b(stat).toString(), entry.getIntValue());
      }

      JsonObject jsonobject = new JsonObject();

      for(Entry<StatType<?>, JsonObject> entry1 : map.entrySet()) {
         jsonobject.add(IRegistry.field_212634_w.func_177774_c(entry1.getKey()).toString(), entry1.getValue());
      }

      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.add("stats", jsonobject);
      jsonobject1.addProperty("DataVersion", 1631);
      return jsonobject1.toString();
   }

   private static <T> ResourceLocation func_199066_b(Stat<T> p_199066_0_) {
      return p_199066_0_.func_197921_a().func_199080_a().func_177774_c(p_199066_0_.func_197920_b());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.statsData.keySet());
   }

   public void sendStats(EntityPlayerMP p_150876_1_) {
      int i = this.server.getTickCounter();
      Object2IntMap<Stat<?>> object2intmap = new Object2IntOpenHashMap<>();
      if (i - this.lastStatRequest > 300) {
         this.lastStatRequest = i;

         for(Stat<?> stat : this.getDirty()) {
            object2intmap.put(stat, this.func_77444_a(stat));
         }
      }

      p_150876_1_.connection.sendPacket(new SPacketStatistics(object2intmap));
   }
}
