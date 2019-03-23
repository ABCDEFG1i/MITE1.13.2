package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public class SingleBiomeProvider extends BiomeProvider {
   private final Biome biome;

   public SingleBiomeProvider(SingleBiomeProviderSettings p_i48972_1_) {
      this.biome = p_i48972_1_.getBiome();
   }

   public Biome getBiome(BlockPos p_180300_1_, @Nullable Biome p_180300_2_) {
      return this.biome;
   }

   public Biome[] getBiomes(int p_201535_1_, int p_201535_2_, int p_201535_3_, int p_201535_4_) {
      return this.func_201539_b(p_201535_1_, p_201535_2_, p_201535_3_, p_201535_4_);
   }

   public Biome[] getBiomes(int p_201537_1_, int p_201537_2_, int p_201537_3_, int p_201537_4_, boolean p_201537_5_) {
      Biome[] abiome = new Biome[p_201537_3_ * p_201537_4_];
      Arrays.fill(abiome, 0, p_201537_3_ * p_201537_4_, this.biome);
      return abiome;
   }

   @Nullable
   public BlockPos findBiomePosition(int p_180630_1_, int p_180630_2_, int p_180630_3_, List<Biome> p_180630_4_, Random p_180630_5_) {
      return p_180630_4_.contains(this.biome) ? new BlockPos(p_180630_1_ - p_180630_3_ + p_180630_5_.nextInt(p_180630_3_ * 2 + 1), 0, p_180630_2_ - p_180630_3_ + p_180630_5_.nextInt(p_180630_3_ * 2 + 1)) : null;
   }

   public boolean hasStructure(Structure<?> p_205004_1_) {
      return this.hasStructureCache.computeIfAbsent(p_205004_1_, this.biome::hasStructure);
   }

   public Set<IBlockState> func_205706_b() {
      if (this.topBlocksCache.isEmpty()) {
         this.topBlocksCache.add(this.biome.getSurfaceBuilderConfig().getTop());
      }

      return this.topBlocksCache;
   }

   public Set<Biome> getBiomesInSquare(int p_201538_1_, int p_201538_2_, int p_201538_3_) {
      return Sets.newHashSet(this.biome);
   }
}
