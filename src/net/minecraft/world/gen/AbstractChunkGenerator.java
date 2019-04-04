package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.util.ExpiringMap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public abstract class AbstractChunkGenerator<C extends IChunkGenSettings> implements IChunkGenerator<C> {
   protected final IWorld world;
   protected final long seed;
   protected final BiomeProvider biomeProvider;
   protected final Map<Structure<? extends IFeatureConfig>, Long2ObjectMap<StructureStart>> structureStartCache = Maps.newHashMap();
   protected final Map<Structure<? extends IFeatureConfig>, Long2ObjectMap<LongSet>> structureReferenceCache = Maps.newHashMap();

   public AbstractChunkGenerator(IWorld p_i48967_1_, BiomeProvider p_i48967_2_) {
      this.world = p_i48967_1_;
      this.seed = p_i48967_1_.getSeed();
      this.biomeProvider = p_i48967_2_;
   }

   public void carve(WorldGenRegion p_202091_1_, GenerationStage.Carving p_202091_2_) {
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom(this.seed);
      int i = 8;
      int j = p_202091_1_.getMainChunkX();
      int k = p_202091_1_.getMainChunkZ();
      BitSet bitset = p_202091_1_.getChunk(j, k).getCarvingMask(p_202091_2_);

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<WorldCarverWrapper<?>> list = p_202091_1_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(new BlockPos(l * 16, 0, i1 * 16),
                    null).getCarvers(p_202091_2_);
            ListIterator<WorldCarverWrapper<?>> listiterator = list.listIterator();

            while(listiterator.hasNext()) {
               int j1 = listiterator.nextIndex();
               WorldCarverWrapper<?> worldcarverwrapper = listiterator.next();
               sharedseedrandom.func_202425_c(p_202091_1_.getWorld().getSeed() + (long)j1, l, i1);
               if (worldcarverwrapper.func_212246_a(p_202091_1_, sharedseedrandom, l, i1, IFeatureConfig.NO_FEATURE_CONFIG)) {
                  worldcarverwrapper.carve(p_202091_1_, sharedseedrandom, l, i1, j, k, bitset, IFeatureConfig.NO_FEATURE_CONFIG);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos func_211403_a(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_) {
      Structure<?> structure = Feature.STRUCTURES.get(p_211403_2_.toLowerCase(Locale.ROOT));
      return structure != null ? structure.func_211405_a(p_211403_1_, this, p_211403_3_, p_211403_4_, p_211403_5_) : null;
   }

   protected void makeBedrock(IChunk p_205472_1_, Random p_205472_2_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = p_205472_1_.getPos().getXStart();
      int j = p_205472_1_.getPos().getZStart();

      for(BlockPos blockpos : BlockPos.getAllInBox(i, 0, j, i + 16, 0, j + 16)) {
         for(int k = 4; k >= 0; --k) {
            if (k <= p_205472_2_.nextInt(5)) {
               p_205472_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), k, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
         }
      }

   }

   public void decorate(WorldGenRegion p_202092_1_) {
      BlockFalling.fallInstantly = true;
      int i = p_202092_1_.getMainChunkX();
      int j = p_202092_1_.getMainChunkZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      Biome biome = p_202092_1_.getChunk(i + 1, j + 1).getBiomes()[0];
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setSeed(p_202092_1_.getSeed(), k, l);

      for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
         biome.decorate(generationstage$decoration, this, p_202092_1_, i1, sharedseedrandom, blockpos);
      }

      BlockFalling.fallInstantly = false;
   }

   public void buildSurface(IChunk p_205471_1_, Biome[] p_205471_2_, SharedSeedRandom p_205471_3_, int p_205471_4_) {
      double d0 = 0.03125D;
      ChunkPos chunkpos = p_205471_1_.getPos();
      int i = chunkpos.getXStart();
      int j = chunkpos.getZStart();
      double[] adouble = this.generateNoiseRegion(chunkpos.x, chunkpos.z);

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = i + k;
            int j1 = j + l;
            int k1 = p_205471_1_.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
            p_205471_2_[l * 16 + k].buildSurface(p_205471_3_, p_205471_1_, i1, j1, k1, adouble[l * 16 + k], this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), p_205471_4_, this.world.getSeed());
         }
      }

   }

   public abstract C getSettings();

   public abstract double[] generateNoiseRegion(int p_205473_1_, int p_205473_2_);

   public boolean hasStructure(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_) {
      return p_202094_1_.hasStructure(p_202094_2_);
   }

   @Nullable
   public IFeatureConfig getStructureConfig(Biome p_202087_1_, Structure<? extends IFeatureConfig> p_202087_2_) {
      return p_202087_1_.getStructureConfig(p_202087_2_);
   }

   public BiomeProvider getBiomeProvider() {
      return this.biomeProvider;
   }

   public long getSeed() {
      return this.seed;
   }

   public Long2ObjectMap<StructureStart> getStructureReferenceToStartMap(Structure<? extends IFeatureConfig> p_203224_1_) {
      return this.structureStartCache.computeIfAbsent(p_203224_1_, (p_203225_0_) -> {
         return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
      });
   }

   public Long2ObjectMap<LongSet> getStructurePositionToReferenceMap(Structure<? extends IFeatureConfig> p_203223_1_) {
      return this.structureReferenceCache.computeIfAbsent(p_203223_1_, (p_203226_0_) -> {
         return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
      });
   }

   public int getMaxHeight() {
      return 256;
   }
}
