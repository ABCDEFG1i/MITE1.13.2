package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;

public class RegionFileCache {
   private static final Map<File, RegionFile> REGIONS_BY_FILE = Maps.newHashMap();

   public static synchronized RegionFile createOrLoadRegionFile(File p_76550_0_, int p_76550_1_, int p_76550_2_) {
      File file1 = new File(p_76550_0_, "region");
      File file2 = new File(file1, "r." + (p_76550_1_ >> 5) + "." + (p_76550_2_ >> 5) + ".mca");
      RegionFile regionfile = REGIONS_BY_FILE.get(file2);
      if (regionfile != null) {
         return regionfile;
      } else {
         if (!file1.exists()) {
            file1.mkdirs();
         }

         if (REGIONS_BY_FILE.size() >= 256) {
            clearRegionFileReferences();
         }

         RegionFile regionfile1 = new RegionFile(file2);
         REGIONS_BY_FILE.put(file2, regionfile1);
         return regionfile1;
      }
   }

   public static synchronized void clearRegionFileReferences() {
      for(RegionFile regionfile : REGIONS_BY_FILE.values()) {
         try {
            if (regionfile != null) {
               regionfile.close();
            }
         } catch (IOException ioexception) {
            ioexception.printStackTrace();
         }
      }

      REGIONS_BY_FILE.clear();
   }

   @Nullable
   public static DataInputStream getChunkInputStream(File p_76549_0_, int p_76549_1_, int p_76549_2_) {
      RegionFile regionfile = createOrLoadRegionFile(p_76549_0_, p_76549_1_, p_76549_2_);
      return regionfile.getChunkDataInputStream(p_76549_1_ & 31, p_76549_2_ & 31);
   }

   @Nullable
   public static DataOutputStream getChunkOutputStream(File p_76552_0_, int p_76552_1_, int p_76552_2_) {
      RegionFile regionfile = createOrLoadRegionFile(p_76552_0_, p_76552_1_, p_76552_2_);
      return regionfile.getChunkDataOutputStream(p_76552_1_ & 31, p_76552_2_ & 31);
   }
}
