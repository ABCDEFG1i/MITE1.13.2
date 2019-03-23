package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraft.world.gen.feature.structure.Structure;

public class EndBiomeProvider extends BiomeProvider {
   private final NoiseGeneratorSimplex field_201546_a;
   private final SharedSeedRandom random;
   private final Biome[] field_205009_d = new Biome[]{Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS};

   public EndBiomeProvider(EndBiomeProviderSettings p_i48970_1_) {
      this.random = new SharedSeedRandom(p_i48970_1_.getSeed());
      this.random.skip(17292);
      this.field_201546_a = new NoiseGeneratorSimplex(this.random);
   }

   @Nullable
   public Biome getBiome(BlockPos p_180300_1_, @Nullable Biome p_180300_2_) {
      return this.func_201545_a(p_180300_1_.getX() >> 4, p_180300_1_.getZ() >> 4);
   }

   private Biome func_201545_a(int p_201545_1_, int p_201545_2_) {
      if ((long)p_201545_1_ * (long)p_201545_1_ + (long)p_201545_2_ * (long)p_201545_2_ <= 4096L) {
         return Biomes.THE_END;
      } else {
         float f = this.func_201536_c(p_201545_1_, p_201545_2_, 1, 1);
         if (f > 40.0F) {
            return Biomes.END_HIGHLANDS;
         } else if (f >= 0.0F) {
            return Biomes.END_MIDLANDS;
         } else {
            return f < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS;
         }
      }
   }

   public Biome[] getBiomes(int p_201535_1_, int p_201535_2_, int p_201535_3_, int p_201535_4_) {
      return this.func_201539_b(p_201535_1_, p_201535_2_, p_201535_3_, p_201535_4_);
   }

   public Biome[] getBiomes(int p_201537_1_, int p_201537_2_, int p_201537_3_, int p_201537_4_, boolean p_201537_5_) {
      Biome[] abiome = new Biome[p_201537_3_ * p_201537_4_];
      Long2ObjectMap<Biome> long2objectmap = new Long2ObjectOpenHashMap<>();

      for(int i = 0; i < p_201537_3_; ++i) {
         for(int j = 0; j < p_201537_4_; ++j) {
            int k = i + p_201537_1_ >> 4;
            int l = j + p_201537_2_ >> 4;
            long i1 = ChunkPos.asLong(k, l);
            Biome biome = long2objectmap.get(i1);
            if (biome == null) {
               biome = this.func_201545_a(k, l);
               long2objectmap.put(i1, biome);
            }

            abiome[i + j * p_201537_3_] = biome;
         }
      }

      return abiome;
   }

   public Set<Biome> getBiomesInSquare(int p_201538_1_, int p_201538_2_, int p_201538_3_) {
      int i = p_201538_1_ - p_201538_3_ >> 2;
      int j = p_201538_2_ - p_201538_3_ >> 2;
      int k = p_201538_1_ + p_201538_3_ >> 2;
      int l = p_201538_2_ + p_201538_3_ >> 2;
      int i1 = k - i + 1;
      int j1 = l - j + 1;
      return Sets.newHashSet(this.func_201539_b(i, j, i1, j1));
   }

   @Nullable
   public BlockPos findBiomePosition(int p_180630_1_, int p_180630_2_, int p_180630_3_, List<Biome> p_180630_4_, Random p_180630_5_) {
      int i = p_180630_1_ - p_180630_3_ >> 2;
      int j = p_180630_2_ - p_180630_3_ >> 2;
      int k = p_180630_1_ + p_180630_3_ >> 2;
      int l = p_180630_2_ + p_180630_3_ >> 2;
      int i1 = k - i + 1;
      int j1 = l - j + 1;
      Biome[] abiome = this.func_201539_b(i, j, i1, j1);
      BlockPos blockpos = null;
      int k1 = 0;

      for(int l1 = 0; l1 < i1 * j1; ++l1) {
         int i2 = i + l1 % i1 << 2;
         int j2 = j + l1 / i1 << 2;
         if (p_180630_4_.contains(abiome[l1])) {
            if (blockpos == null || p_180630_5_.nextInt(k1 + 1) == 0) {
               blockpos = new BlockPos(i2, 0, j2);
            }

            ++k1;
         }
      }

      return blockpos;
   }

   public float func_201536_c(int p_201536_1_, int p_201536_2_, int p_201536_3_, int p_201536_4_) {
      float f = (float)(p_201536_1_ * 2 + p_201536_3_);
      float f1 = (float)(p_201536_2_ * 2 + p_201536_4_);
      float f2 = 100.0F - MathHelper.sqrt(f * f + f1 * f1) * 8.0F;
      f2 = MathHelper.clamp(f2, -100.0F, 80.0F);

      for(int i = -12; i <= 12; ++i) {
         for(int j = -12; j <= 12; ++j) {
            long k = (long)(p_201536_1_ + i);
            long l = (long)(p_201536_2_ + j);
            if (k * k + l * l > 4096L && this.field_201546_a.getValue((double)k, (double)l) < (double)-0.9F) {
               float f3 = (MathHelper.abs((float)k) * 3439.0F + MathHelper.abs((float)l) * 147.0F) % 13.0F + 9.0F;
               f = (float)(p_201536_3_ - i * 2);
               f1 = (float)(p_201536_4_ - j * 2);
               float f4 = 100.0F - MathHelper.sqrt(f * f + f1 * f1) * f3;
               f4 = MathHelper.clamp(f4, -100.0F, 80.0F);
               f2 = Math.max(f2, f4);
            }
         }
      }

      return f2;
   }

   public boolean hasStructure(Structure<?> p_205004_1_) {
      return this.hasStructureCache.computeIfAbsent(p_205004_1_, (p_205008_1_) -> {
         for(Biome biome : this.field_205009_d) {
            if (biome.hasStructure(p_205008_1_)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<IBlockState> func_205706_b() {
      if (this.topBlocksCache.isEmpty()) {
         for(Biome biome : this.field_205009_d) {
            this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
         }
      }

      return this.topBlocksCache;
   }
}
