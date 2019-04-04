package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProvider extends BiomeProvider {
   private final BiomeCache cache = new BiomeCache(this);
   private final GenLayer genBiomes;
   private final GenLayer biomeFactoryLayer;
   private final Biome[] biomes = new Biome[]{Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU};

   public OverworldBiomeProvider(OverworldBiomeProviderSettings p_i48971_1_) {
      WorldInfo worldinfo = p_i48971_1_.getWorldInfo();
      OverworldGenSettings overworldgensettings = p_i48971_1_.getSettings();
      GenLayer[] agenlayer = LayerUtil.func_202824_a(worldinfo.getSeed(), worldinfo.getTerrainType(), overworldgensettings);
      this.genBiomes = agenlayer[0];
      this.biomeFactoryLayer = agenlayer[1];
   }

   @Nullable
   public Biome getBiome(BlockPos p_180300_1_, @Nullable Biome p_180300_2_) {
      return this.cache.getBiome(p_180300_1_.getX(), p_180300_1_.getZ(), p_180300_2_);
   }

   public Biome[] getBiomes(int p_201535_1_, int p_201535_2_, int p_201535_3_, int p_201535_4_) {
      return this.genBiomes.generateBiomes(p_201535_1_, p_201535_2_, p_201535_3_, p_201535_4_, Biomes.DEFAULT);
   }

   public Biome[] getBiomes(int p_201537_1_, int p_201537_2_, int p_201537_3_, int p_201537_4_, boolean p_201537_5_) {
      return p_201537_5_ && p_201537_3_ == 16 && p_201537_4_ == 16 && (p_201537_1_ & 15) == 0 && (p_201537_2_ & 15) == 0 ? this.cache.getCachedBiomes(p_201537_1_, p_201537_2_) : this.biomeFactoryLayer.generateBiomes(p_201537_1_, p_201537_2_, p_201537_3_, p_201537_4_, Biomes.DEFAULT);
   }

   public Set<Biome> getBiomesInSquare(int p_201538_1_, int p_201538_2_, int p_201538_3_) {
      int i = p_201538_1_ - p_201538_3_ >> 2;
      int j = p_201538_2_ - p_201538_3_ >> 2;
      int k = p_201538_1_ + p_201538_3_ >> 2;
      int l = p_201538_2_ + p_201538_3_ >> 2;
      int i1 = k - i + 1;
      int j1 = l - j + 1;
      Set<Biome> set = Sets.newHashSet();
      Collections.addAll(set, this.genBiomes.generateBiomes(i, j, i1, j1, null));
      return set;
   }

   @Nullable
   public BlockPos findBiomePosition(int p_180630_1_, int p_180630_2_, int p_180630_3_, List<Biome> p_180630_4_, Random p_180630_5_) {
      int i = p_180630_1_ - p_180630_3_ >> 2;
      int j = p_180630_2_ - p_180630_3_ >> 2;
      int k = p_180630_1_ + p_180630_3_ >> 2;
      int l = p_180630_2_ + p_180630_3_ >> 2;
      int i1 = k - i + 1;
      int j1 = l - j + 1;
      Biome[] abiome = this.genBiomes.generateBiomes(i, j, i1, j1, null);
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

   public boolean hasStructure(Structure<?> p_205004_1_) {
      return this.hasStructureCache.computeIfAbsent(p_205004_1_, (p_205006_1_) -> {
         for(Biome biome : this.biomes) {
            if (biome.hasStructure(p_205006_1_)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<IBlockState> func_205706_b() {
      if (this.topBlocksCache.isEmpty()) {
         for(Biome biome : this.biomes) {
            this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
         }
      }

      return this.topBlocksCache;
   }

   public void tick() {
      this.cache.cleanupCache();
   }
}
