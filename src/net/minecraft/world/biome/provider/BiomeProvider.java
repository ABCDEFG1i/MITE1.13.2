package net.minecraft.world.biome.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public abstract class BiomeProvider implements ITickable {
   public static final List<Biome> BIOMES_TO_SPAWN_IN = Lists.newArrayList(Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS);
   protected final Map<Structure<?>, Boolean> hasStructureCache = Maps.newHashMap();
   protected final Set<IBlockState> topBlocksCache = Sets.newHashSet();

   public List<Biome> getBiomesToSpawnIn() {
      return BIOMES_TO_SPAWN_IN;
   }

   public void tick() {
   }

   @Nullable
   public abstract Biome getBiome(BlockPos p_180300_1_, @Nullable Biome p_180300_2_);

   public abstract Biome[] getBiomes(int p_201535_1_, int p_201535_2_, int p_201535_3_, int p_201535_4_);

   public Biome[] func_201539_b(int p_201539_1_, int p_201539_2_, int p_201539_3_, int p_201539_4_) {
      return this.getBiomes(p_201539_1_, p_201539_2_, p_201539_3_, p_201539_4_, true);
   }

   public abstract Biome[] getBiomes(int p_201537_1_, int p_201537_2_, int p_201537_3_, int p_201537_4_, boolean p_201537_5_);

   public abstract Set<Biome> getBiomesInSquare(int p_201538_1_, int p_201538_2_, int p_201538_3_);

   @Nullable
   public abstract BlockPos findBiomePosition(int p_180630_1_, int p_180630_2_, int p_180630_3_, List<Biome> p_180630_4_, Random p_180630_5_);

   public float func_201536_c(int p_201536_1_, int p_201536_2_, int p_201536_3_, int p_201536_4_) {
      return 0.0F;
   }

   public abstract boolean hasStructure(Structure<?> p_205004_1_);

   public abstract Set<IBlockState> func_205706_b();
}
