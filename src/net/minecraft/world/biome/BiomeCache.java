package net.minecraft.world.biome;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeCache {
   private final BiomeProvider provider;
   private final LoadingCache<ChunkPos, BiomeCache.Entry> cacheMap = CacheBuilder.newBuilder().expireAfterAccess(30000L, TimeUnit.MILLISECONDS).build(new CacheLoader<ChunkPos, BiomeCache.Entry>() {
      public BiomeCache.Entry load(ChunkPos p_load_1_) throws Exception {
         return BiomeCache.this.new Entry(p_load_1_.x, p_load_1_.z);
      }
   });

   public BiomeCache(BiomeProvider p_i1973_1_) {
      this.provider = p_i1973_1_;
   }

   public BiomeCache.Entry getEntry(int p_76840_1_, int p_76840_2_) {
      p_76840_1_ = p_76840_1_ >> 4;
      p_76840_2_ = p_76840_2_ >> 4;
      return this.cacheMap.getUnchecked(new ChunkPos(p_76840_1_, p_76840_2_));
   }

   public Biome getBiome(int p_180284_1_, int p_180284_2_, Biome p_180284_3_) {
      Biome biome = this.getEntry(p_180284_1_, p_180284_2_).getBiome(p_180284_1_, p_180284_2_);
      return biome == null ? p_180284_3_ : biome;
   }

   public void cleanupCache() {
   }

   public Biome[] getCachedBiomes(int p_76839_1_, int p_76839_2_) {
      return this.getEntry(p_76839_1_, p_76839_2_).biomes;
   }

   public class Entry {
      private final Biome[] biomes;

      public Entry(int p_i1972_2_, int p_i1972_3_) {
         this.biomes = BiomeCache.this.provider.getBiomes(p_i1972_2_ << 4, p_i1972_3_ << 4, 16, 16, false);
      }

      public Biome getBiome(int p_76885_1_, int p_76885_2_) {
         return this.biomes[p_76885_1_ & 15 | (p_76885_2_ & 15) << 4];
      }
   }
}
