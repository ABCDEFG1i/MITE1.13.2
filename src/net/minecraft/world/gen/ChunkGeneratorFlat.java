package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.PhantomSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkGeneratorFlat extends AbstractChunkGenerator<FlatGenSettings> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final FlatGenSettings settings;
   private final Biome field_202103_f;
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();

   public ChunkGeneratorFlat(IWorld p_i48958_1_, BiomeProvider p_i48958_2_, FlatGenSettings p_i48958_3_) {
      super(p_i48958_1_, p_i48958_2_);
      this.settings = p_i48958_3_;
      this.field_202103_f = this.func_202099_e();
   }

   private Biome func_202099_e() {
      Biome biome = this.settings.getBiome();
      ChunkGeneratorFlat.BiomeWrapper chunkgeneratorflat$biomewrapper = new ChunkGeneratorFlat.BiomeWrapper(biome.getSurfaceBuilder(), biome.getPrecipitation(), biome.getBiomeCategory(), biome.getBaseHeight(), biome.getHeightVariation(), biome.getDefaultTemperature(), biome.getRainfall(), biome.getWaterColor(), biome.getWaterFogColor(), biome.getParent());
      Map<String, Map<String, String>> map = this.settings.getWorldFeatures();

      for(String s : map.keySet()) {
         CompositeFeature<?, ?>[] compositefeature = FlatGenSettings.field_202247_j.get(s);
         if (compositefeature != null) {
            for(CompositeFeature<?, ?> compositefeature1 : compositefeature) {
               chunkgeneratorflat$biomewrapper.addFeature(FlatGenSettings.field_202248_k.get(compositefeature1), compositefeature1);
               Feature<?> feature = compositefeature1.getFeature();
               if (feature instanceof Structure) {
                  IFeatureConfig ifeatureconfig = biome.getStructureConfig((Structure)feature);
                  chunkgeneratorflat$biomewrapper.addStructure((Structure)feature, ifeatureconfig != null ? ifeatureconfig : FlatGenSettings.field_202249_l.get(compositefeature1));
               }
            }
         }
      }

      boolean flag = (!this.settings.func_202238_o() || biome == Biomes.THE_VOID) && map.containsKey("decoration");
      if (flag) {
         List<GenerationStage.Decoration> list = Lists.newArrayList();
         list.add(GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         list.add(GenerationStage.Decoration.SURFACE_STRUCTURES);

         for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
            if (!list.contains(generationstage$decoration)) {
               for(CompositeFeature<?, ?> compositefeature2 : biome.getFeatures(generationstage$decoration)) {
                  chunkgeneratorflat$biomewrapper.addFeature(generationstage$decoration, compositefeature2);
               }
            }
         }
      }

      return chunkgeneratorflat$biomewrapper;
   }

   public void makeBase(IChunk p_202088_1_) {
      ChunkPos chunkpos = p_202088_1_.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      Biome[] abiome = this.biomeProvider.func_201539_b(i * 16, j * 16, 16, 16);
      p_202088_1_.setBiomes(abiome);
      this.func_202100_a(i, j, p_202088_1_);
      p_202088_1_.createHeightMap(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      p_202088_1_.setStatus(ChunkStatus.BASE);
   }

   public void carve(WorldGenRegion p_202091_1_, GenerationStage.Carving p_202091_2_) {
      int i = 8;
      int j = p_202091_1_.getMainChunkX();
      int k = p_202091_1_.getMainChunkZ();
      BitSet bitset = new BitSet(65536);
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<WorldCarverWrapper<?>> list = this.field_202103_f.getCarvers(GenerationStage.Carving.AIR);
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

   public FlatGenSettings getSettings() {
      return this.settings;
   }

   public double[] generateNoiseRegion(int p_205473_1_, int p_205473_2_) {
      return new double[0];
   }

   public int getGroundHeight() {
      IChunk ichunk = this.world.getChunk(0, 0);
      return ichunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 8, 8);
   }

   public void decorate(WorldGenRegion p_202092_1_) {
      int i = p_202092_1_.getMainChunkX();
      int j = p_202092_1_.getMainChunkZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setSeed(p_202092_1_.getSeed(), k, l);

      for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
         this.field_202103_f.decorate(generationstage$decoration, this, p_202092_1_, i1, sharedseedrandom, blockpos);
      }

   }

   public void spawnMobs(WorldGenRegion p_202093_1_) {
   }

   public void func_202100_a(int p_202100_1_, int p_202100_2_, IChunk p_202100_3_) {
      IBlockState[] aiblockstate = this.settings.func_202233_q();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < aiblockstate.length; ++i) {
         IBlockState iblockstate = aiblockstate[i];
         if (iblockstate != null) {
            for(int j = 0; j < 16; ++j) {
               for(int k = 0; k < 16; ++k) {
                  p_202100_3_.setBlockState(blockpos$mutableblockpos.setPos(j, i, k), iblockstate, false);
               }
            }
         }
      }

   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType p_177458_1_, BlockPos p_177458_2_) {
      Biome biome = this.world.getBiome(p_177458_2_);
      return biome.getSpawnableList(p_177458_1_);
   }

   public int spawnMobs(World p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
      int i = 0;
      i = i + this.phantomSpawner.spawnMobs(p_203222_1_, p_203222_2_, p_203222_3_);
      return i;
   }

   public boolean hasStructure(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_) {
      return this.field_202103_f.hasStructure(p_202094_2_);
   }

   @Nullable
   public IFeatureConfig getStructureConfig(Biome p_202087_1_, Structure<? extends IFeatureConfig> p_202087_2_) {
      return this.field_202103_f.getStructureConfig(p_202087_2_);
   }

   @Nullable
   public BlockPos func_211403_a(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_) {
      return !this.settings.getWorldFeatures().keySet().contains(p_211403_2_) ? null : super.func_211403_a(p_211403_1_, p_211403_2_, p_211403_3_, p_211403_4_, p_211403_5_);
   }

   class BiomeWrapper extends Biome {
      protected BiomeWrapper(CompositeSurfaceBuilder<?> p_i48991_2_, Biome.RainType p_i48991_3_, Biome.Category p_i48991_4_, float p_i48991_5_, float p_i48991_6_, float p_i48991_7_, float p_i48991_8_, int p_i48991_9_, int p_i48991_10_, @Nullable String p_i48991_11_) {
         super((new Biome.BiomeBuilder()).surfaceBuilder(p_i48991_2_).precipitation(p_i48991_3_).category(p_i48991_4_).depth(p_i48991_5_).scale(p_i48991_6_).temperature(p_i48991_7_).downfall(p_i48991_8_).waterColor(p_i48991_9_).waterFogColor(p_i48991_10_).parent(p_i48991_11_));
      }
   }
}
