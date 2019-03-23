package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public class CheckerboardBiomeProvider extends BiomeProvider {
   private final Biome[] field_205320_b;
   private final int field_205321_c;

   public CheckerboardBiomeProvider(CheckerboardBiomeProviderSettings p_i48973_1_) {
      this.field_205320_b = p_i48973_1_.func_205432_a();
      this.field_205321_c = p_i48973_1_.func_205433_b() + 4;
   }

   public Biome getBiome(BlockPos p_180300_1_, @Nullable Biome p_180300_2_) {
      return this.field_205320_b[Math.abs(((p_180300_1_.getX() >> this.field_205321_c) + (p_180300_1_.getZ() >> this.field_205321_c)) % this.field_205320_b.length)];
   }

   public Biome[] getBiomes(int p_201535_1_, int p_201535_2_, int p_201535_3_, int p_201535_4_) {
      return this.func_201539_b(p_201535_1_, p_201535_2_, p_201535_3_, p_201535_4_);
   }

   public Biome[] getBiomes(int p_201537_1_, int p_201537_2_, int p_201537_3_, int p_201537_4_, boolean p_201537_5_) {
      Biome[] abiome = new Biome[p_201537_3_ * p_201537_4_];

      for(int i = 0; i < p_201537_4_; ++i) {
         for(int j = 0; j < p_201537_3_; ++j) {
            int k = Math.abs(((p_201537_1_ + i >> this.field_205321_c) + (p_201537_2_ + j >> this.field_205321_c)) % this.field_205320_b.length);
            Biome biome = this.field_205320_b[k];
            abiome[i * p_201537_3_ + j] = biome;
         }
      }

      return abiome;
   }

   @Nullable
   public BlockPos findBiomePosition(int p_180630_1_, int p_180630_2_, int p_180630_3_, List<Biome> p_180630_4_, Random p_180630_5_) {
      return null;
   }

   public boolean hasStructure(Structure<?> p_205004_1_) {
      return this.hasStructureCache.computeIfAbsent(p_205004_1_, (p_205319_1_) -> {
         for(Biome biome : this.field_205320_b) {
            if (biome.hasStructure(p_205319_1_)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<IBlockState> func_205706_b() {
      if (this.topBlocksCache.isEmpty()) {
         for(Biome biome : this.field_205320_b) {
            this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
         }
      }

      return this.topBlocksCache;
   }

   public Set<Biome> getBiomesInSquare(int p_201538_1_, int p_201538_2_, int p_201538_3_) {
      return Sets.newHashSet(this.field_205320_b);
   }
}
