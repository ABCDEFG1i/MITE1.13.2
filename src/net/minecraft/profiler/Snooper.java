package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Snooper {
   private final Map<String, Object> snooperStats = Maps.newHashMap();
   private final Map<String, Object> clientStats = Maps.newHashMap();
   private final String uniqueID = UUID.randomUUID().toString();
   private final URL serverUrl;
   private final ISnooperInfo playerStatsCollector;
   private final Timer timer = new Timer("Snooper Timer", true);
   private final Object syncLock = new Object();
   private final long minecraftStartTimeMilis;
   private boolean isRunning;

   public Snooper(String p_i1563_1_, ISnooperInfo p_i1563_2_, long p_i1563_3_) {
      try {
         this.serverUrl = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.playerStatsCollector = p_i1563_2_;
      this.minecraftStartTimeMilis = p_i1563_3_;
   }

   public void start() {
      if (!this.isRunning) {
      }

   }

   public void addMemoryStatsToSnooper() {
      this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
      this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
      this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
      this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.playerStatsCollector.addServerStatsToSnooper(this);
   }

   public void addClientStat(String p_152768_1_, Object p_152768_2_) {
      synchronized(this.syncLock) {
         this.clientStats.put(p_152768_1_, p_152768_2_);
      }
   }

   public void addStatToSnooper(String p_152767_1_, Object p_152767_2_) {
      synchronized(this.syncLock) {
         this.snooperStats.put(p_152767_1_, p_152767_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Map<String, String> getCurrentStats() {
      Map<String, String> map = Maps.newLinkedHashMap();
      synchronized(this.syncLock) {
         this.addMemoryStatsToSnooper();

         for(Entry<String, Object> entry : this.snooperStats.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
         }

         for(Entry<String, Object> entry1 : this.clientStats.entrySet()) {
            map.put(entry1.getKey(), entry1.getValue().toString());
         }

         return map;
      }
   }

   public boolean isSnooperRunning() {
      return this.isRunning;
   }

   public void stop() {
      this.timer.cancel();
   }

   @OnlyIn(Dist.CLIENT)
   public String getUniqueID() {
      return this.uniqueID;
   }

   public long getMinecraftStartTimeMillis() {
      return this.minecraftStartTimeMilis;
   }
}
