package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;

public class WorldChunkEnumerator {
   private static final Pattern field_212158_a = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final File field_212159_b;
   private final Map<DimensionType, List<ChunkPos>> field_212162_e;

   public WorldChunkEnumerator(File p_i49790_1_) {
      this.field_212159_b = p_i49790_1_;
      Builder<DimensionType, List<ChunkPos>> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         builder.put(dimensiontype, this.func_212153_a(dimensiontype));
      }

      this.field_212162_e = builder.build();
   }

   private List<ChunkPos> func_212153_a(DimensionType p_212153_1_) {
      ArrayList<ChunkPos> arraylist = Lists.newArrayList();
      File file1 = p_212153_1_.func_212679_a(this.field_212159_b);
      List<File> list = this.func_212155_b(file1);

      for(File file2 : list) {
         arraylist.addAll(this.func_212150_a(file2));
      }

      list.sort(File::compareTo);
      return arraylist;
   }

   private List<ChunkPos> func_212150_a(File p_212150_1_) {
      List<ChunkPos> list = Lists.newArrayList();
      RegionFile regionfile = null;

      List<ChunkPos> arraylist;
      try {
         Matcher matcher = field_212158_a.matcher(p_212150_1_.getName());
         if (matcher.matches()) {
            int l = Integer.parseInt(matcher.group(1)) << 5;
            int i = Integer.parseInt(matcher.group(2)) << 5;
            regionfile = new RegionFile(p_212150_1_);

            for(int j = 0; j < 32; ++j) {
               for(int k = 0; k < 32; ++k) {
                  if (regionfile.func_212167_b(j, k)) {
                     list.add(new ChunkPos(j + l, k + i));
                  }
               }
            }

            return list;
         }

         arraylist = list;
      } catch (Throwable var18) {
         arraylist = Lists.newArrayList();
         return arraylist;
      } finally {
         if (regionfile != null) {
            try {
               regionfile.close();
            } catch (IOException var17) {
            }
         }

      }

      return arraylist;
   }

   private List<File> func_212155_b(File p_212155_1_) {
      File file1 = new File(p_212155_1_, "region");
      File[] afile = file1.listFiles((p_212152_0_, p_212152_1_) -> {
         return p_212152_1_.endsWith(".mca");
      });
      return afile != null ? Lists.newArrayList(afile) : Lists.newArrayList();
   }

   public List<ChunkPos> func_212541_a(DimensionType p_212541_1_) {
      return this.field_212162_e.get(p_212541_1_);
   }
}
