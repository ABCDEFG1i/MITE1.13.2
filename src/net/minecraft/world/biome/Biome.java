package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.carver.IWorldCarver;
import net.minecraft.world.gen.carver.NetherCaveWorldCarver;
import net.minecraft.world.gen.carver.UnderwaterCanyonWorldCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.CompositeFlowerFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.placement.AtHeight64;
import net.minecraft.world.gen.placement.AtSurface;
import net.minecraft.world.gen.placement.AtSurfaceRandomCount;
import net.minecraft.world.gen.placement.AtSurfaceWithChance;
import net.minecraft.world.gen.placement.AtSurfaceWithChanceMultiple;
import net.minecraft.world.gen.placement.AtSurfaceWithExtra;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.CaveEdge;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.ChanceRange;
import net.minecraft.world.gen.placement.ChanceRangeConfig;
import net.minecraft.world.gen.placement.ChorusPlant;
import net.minecraft.world.gen.placement.CountRange;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverage;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.DungeonRoom;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.EndGateway;
import net.minecraft.world.gen.placement.EndIsland;
import net.minecraft.world.gen.placement.EndSpikes;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Height4To32;
import net.minecraft.world.gen.placement.HeightBiasedRange;
import net.minecraft.world.gen.placement.HeightVeryBiasedRange;
import net.minecraft.world.gen.placement.HeightWithChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.IcebergPlacement;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.LakeLava;
import net.minecraft.world.gen.placement.LakeWater;
import net.minecraft.world.gen.placement.NetherFire;
import net.minecraft.world.gen.placement.NetherGlowstone;
import net.minecraft.world.gen.placement.NetherMagma;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Passthrough;
import net.minecraft.world.gen.placement.RandomCountWithRange;
import net.minecraft.world.gen.placement.RoofedTree;
import net.minecraft.world.gen.placement.SurfacePlus32;
import net.minecraft.world.gen.placement.SurfacePlus32WithNoise;
import net.minecraft.world.gen.placement.TopSolid;
import net.minecraft.world.gen.placement.TopSolidOnce;
import net.minecraft.world.gen.placement.TopSolidRange;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.placement.TopSolidWithChance;
import net.minecraft.world.gen.placement.TopSolidWithNoise;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.placement.TwiceSurface;
import net.minecraft.world.gen.placement.TwiceSurfaceWithChance;
import net.minecraft.world.gen.placement.TwiceSurfaceWithChanceMultiple;
import net.minecraft.world.gen.placement.TwiceSurfaceWithNoise;
import net.minecraft.world.gen.placement.WithChance;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ExtremeHillsMutatedSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ExtremeHillsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.FrozenOceanSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.MesaBryceSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.MesaForestSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.MesaSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.NetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.NoopSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SavanaMutatedSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SwampSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.TaigaMegaSurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final WorldCarver<ProbabilityConfig> CAVE_WORLD_CARVER = new CaveWorldCarver();
   public static final WorldCarver<ProbabilityConfig> NETHER_CAVE_WORLD_CARVER = new NetherCaveWorldCarver();
   public static final WorldCarver<ProbabilityConfig> CANYON_WORLD_CARVER = new CanyonWorldCarver();
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON_WORLD_CARVER = new UnderwaterCanyonWorldCarver();
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE_WORLD_CARVER = new UnderwaterCaveWorldCarver();
   public static final BasePlacement<FrequencyConfig> AT_SURFACE = new AtSurface();
   public static final BasePlacement<FrequencyConfig> TOP_SOLID = new TopSolid();
   public static final BasePlacement<FrequencyConfig> SURFACE_PLUS_32 = new SurfacePlus32();
   public static final BasePlacement<FrequencyConfig> TWICE_SURFACE = new TwiceSurface();
   public static final BasePlacement<FrequencyConfig> AT_HEIGHT_64 = new AtHeight64();
   public static final BasePlacement<NoiseDependant> SURFACE_PLUS_32_WITH_NOISE = new SurfacePlus32WithNoise();
   public static final BasePlacement<NoiseDependant> TWICE_SURFACE_WITH_NOISE = new TwiceSurfaceWithNoise();
   public static final BasePlacement<NoPlacementConfig> PASSTHROUGH = new Passthrough();
   public static final BasePlacement<ChanceConfig> AT_SURFACE_WITH_CHANCE = new AtSurfaceWithChance();
   public static final BasePlacement<ChanceConfig> TWICE_SURFACE_WITH_CHANCE = new TwiceSurfaceWithChance();
   public static final BasePlacement<ChanceConfig> WITH_CHANCE = new WithChance();
   public static final BasePlacement<ChanceConfig> TOP_SURFACE_WITH_CHANCE = new TopSolidWithChance();
   public static final BasePlacement<AtSurfaceWithExtraConfig> AT_SURFACE_WITH_EXTRA = new AtSurfaceWithExtra();
   public static final BasePlacement<CountRangeConfig> COUNT_RANGE = new CountRange();
   public static final BasePlacement<CountRangeConfig> HEIGHT_BIASED_RANGE = new HeightBiasedRange();
   public static final BasePlacement<CountRangeConfig> HEIGHT_VERY_BIASED_RANGE = new HeightVeryBiasedRange();
   public static final BasePlacement<CountRangeConfig> RANDOM_COUNT_WITH_RANGE = new RandomCountWithRange();
   public static final BasePlacement<ChanceRangeConfig> CHANCE_RANGE = new ChanceRange();
   public static final BasePlacement<HeightWithChanceConfig> AT_SUFACE_WITH_CHANCE_MULTIPLE = new AtSurfaceWithChanceMultiple();
   public static final BasePlacement<HeightWithChanceConfig> TWICE_SURFACE_WITH_CHANCE_MULTPLE = new TwiceSurfaceWithChanceMultiple();
   public static final BasePlacement<DepthAverageConfig> DEPTH_AVERAGE = new DepthAverage();
   public static final BasePlacement<NoPlacementConfig> TOP_SOLID_ONCE = new TopSolidOnce();
   public static final BasePlacement<TopSolidRangeConfig> TOP_SOLID_RANGE = new TopSolidRange();
   public static final BasePlacement<TopSolidWithNoiseConfig> TOP_SOLID_WITH_NOISE = new TopSolidWithNoise();
   public static final BasePlacement<CaveEdgeConfig> CAVE_EDGE = new CaveEdge();
   public static final BasePlacement<FrequencyConfig> AT_SURFACE_RANDOM_COUNT = new AtSurfaceRandomCount();
   public static final BasePlacement<FrequencyConfig> NETHER_FIRE = new NetherFire();
   public static final BasePlacement<FrequencyConfig> NETHER_MAGMA = new NetherMagma();
   public static final BasePlacement<NoPlacementConfig> HEIGHT_4_TO_32 = new Height4To32();
   public static final BasePlacement<LakeChanceConfig> LAVA_LAKE = new LakeLava();
   public static final BasePlacement<LakeChanceConfig> LAKE_WATER = new LakeWater();
   public static final BasePlacement<DungeonRoomConfig> DUNGEON_ROOM = new DungeonRoom();
   public static final BasePlacement<NoPlacementConfig> ROOFED_TREE = new RoofedTree();
   public static final BasePlacement<ChanceConfig> ICEBERG_PLACEMENT = new IcebergPlacement();
   public static final BasePlacement<FrequencyConfig> NETHER_GLOWSTONE = new NetherGlowstone();
   public static final BasePlacement<NoPlacementConfig> END_SPIKES = new EndSpikes();
   public static final BasePlacement<NoPlacementConfig> END_ISLAND = new EndIsland();
   public static final BasePlacement<NoPlacementConfig> CHORUS_PLANT = new ChorusPlant();
   public static final BasePlacement<NoPlacementConfig> END_GATEWAY = new EndGateway();
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState DIRT = Blocks.DIRT.getDefaultState();
   protected static final IBlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
   protected static final IBlockState PODZOL = Blocks.PODZOL.getDefaultState();
   protected static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
   protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
   protected static final IBlockState COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
   protected static final IBlockState SAND = Blocks.SAND.getDefaultState();
   protected static final IBlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
   protected static final IBlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   protected static final IBlockState MYCELIUM = Blocks.MYCELIUM.getDefaultState();
   protected static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
   protected static final IBlockState END_STONE = Blocks.END_STONE.getDefaultState();
   public static final SurfaceBuilderConfig AIR_SURFACE = new SurfaceBuilderConfig(AIR, AIR, AIR);
   public static final SurfaceBuilderConfig DIRT_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig GRASS_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig STONE_STONE_GRAVEL_SURFACE = new SurfaceBuilderConfig(STONE, STONE, GRAVEL);
   public static final SurfaceBuilderConfig GRAVEL_SURFACE = new SurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
   public static final SurfaceBuilderConfig COARSE_DIRT_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(COARSE_DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig PODZOL_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(PODZOL, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig SAND_SURFACE = new SurfaceBuilderConfig(SAND, SAND, SAND);
   public static final SurfaceBuilderConfig GRASS_DIRT_SAND_SURFACE = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, SAND);
   public static final SurfaceBuilderConfig SAND_SAND_GRAVEL_SURFACE = new SurfaceBuilderConfig(SAND, SAND, GRAVEL);
   public static final SurfaceBuilderConfig RED_SAND_WHITE_TERRACOTTA_GRAVEL_SURFACE = new SurfaceBuilderConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
   public static final SurfaceBuilderConfig MYCELIUM_DIRT_GRAVEL_SURFACE = new SurfaceBuilderConfig(MYCELIUM, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig NETHERRACK_SURFACE = new SurfaceBuilderConfig(NETHERRACK, NETHERRACK, NETHERRACK);
   public static final SurfaceBuilderConfig END_STONE_SURFACE = new SurfaceBuilderConfig(END_STONE, END_STONE, END_STONE);
   public static final ISurfaceBuilder<SurfaceBuilderConfig> DEFAULT_SURFACE_BUILDER = new DefaultSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> EXTREME_HILL_SURFACE_BUILDER = new ExtremeHillsSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> SAVANA_MUTATED_SURFACE_BUILDER = new SavanaMutatedSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> EXTREME_HILLS_MUTATED_SURFACE_BUILDER = new ExtremeHillsMutatedSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> TAIGA_MEGA_SURFACE_BUILDER = new TaigaMegaSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> SWAMP_SURFACE_BUILDER = new SwampSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> MESA_SURFACE_BUILDER = new MesaSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> MESA_FOREST_SURFACE_BUILDER = new MesaForestSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> MESA_BRYCE_SURACE_BUILDER = new MesaBryceSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN_SURFACE_BUILDER = new FrozenOceanSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> NETHER_SURFACE_BUILDER = new NetherSurfaceBuilder();
   public static final ISurfaceBuilder<SurfaceBuilderConfig> NOOP_SURFACE_BUILDER = new NoopSurfaceBuilder();
   public static final Set<Biome> BIOMES = Sets.newHashSet();
   public static final ObjectIntIdentityMap<Biome> MUTATION_TO_BASE_ID_MAP = new ObjectIntIdentityMap<>();
   protected static final NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);
   public static final NoiseGeneratorPerlin INFO_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
   @Nullable
   protected String translationKey;
   protected final float depth;
   protected final float scale;
   protected final float temperature;
   protected final float downfall;
   protected final int waterColor;
   protected final int waterFogColor;
   @Nullable
   protected final String parent;
   protected final CompositeSurfaceBuilder<?> surfaceBuilder;
   protected final Biome.Category category;
   protected final Biome.RainType precipitation;
   protected final Map<GenerationStage.Carving, List<WorldCarverWrapper<?>>> carvers = Maps.newHashMap();
   protected final Map<GenerationStage.Decoration, List<CompositeFeature<?, ?>>> features = Maps.newHashMap();
   protected final List<CompositeFlowerFeature<?>> flowers = Lists.newArrayList();
   protected final Map<Structure<?>, IFeatureConfig> structures = Maps.newHashMap();
   private final Map<EnumCreatureType, List<Biome.SpawnListEntry>> creatures = Maps.newHashMap();

   @Nullable
   public static Biome getMutationForBiome(Biome p_185356_0_) {
      return MUTATION_TO_BASE_ID_MAP.getByValue(IRegistry.field_212624_m.func_148757_b(p_185356_0_));
   }

   public static <C extends IFeatureConfig> WorldCarverWrapper<C> createWorldCarverWrapper(IWorldCarver<C> p_203606_0_, C p_203606_1_) {
      return new WorldCarverWrapper<>(p_203606_0_, p_203606_1_);
   }

   public static <F extends IFeatureConfig, D extends IPlacementConfig> CompositeFeature<F, D> createCompositeFeature(Feature<F> p_201864_0_, F p_201864_1_, BasePlacement<D> p_201864_2_, D p_201864_3_) {
      return new CompositeFeature<>(p_201864_0_, p_201864_1_, p_201864_2_, p_201864_3_);
   }

   public static <D extends IPlacementConfig> CompositeFlowerFeature<D> createCompositeFlowerFeature(AbstractFlowersFeature p_201861_0_, BasePlacement<D> p_201861_1_, D p_201861_2_) {
      return new CompositeFlowerFeature<>(p_201861_0_, p_201861_1_, p_201861_2_);
   }

   protected Biome(Biome.BiomeBuilder p_i48975_1_) {
      if (p_i48975_1_.surfaceBuilder != null && p_i48975_1_.precipitation != null && p_i48975_1_.category != null && p_i48975_1_.depth != null && p_i48975_1_.scale != null && p_i48975_1_.temperature != null && p_i48975_1_.downfall != null && p_i48975_1_.waterColor != null && p_i48975_1_.waterFogColor != null) {
         this.surfaceBuilder = p_i48975_1_.surfaceBuilder;
         this.precipitation = p_i48975_1_.precipitation;
         this.category = p_i48975_1_.category;
         this.depth = p_i48975_1_.depth;
         this.scale = p_i48975_1_.scale;
         this.temperature = p_i48975_1_.temperature;
         this.downfall = p_i48975_1_.downfall;
         this.waterColor = p_i48975_1_.waterColor;
         this.waterFogColor = p_i48975_1_.waterFogColor;
         this.parent = p_i48975_1_.parent;

         for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
            this.features.put(generationstage$decoration, Lists.newArrayList());
         }

         for(EnumCreatureType enumcreaturetype : EnumCreatureType.values()) {
            this.creatures.put(enumcreaturetype, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + p_i48975_1_);
      }
   }

   protected void addStructureFeatures() {
      this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.MINESHAFT, new MineshaftConfig((double)0.004F, MineshaftStructure.Type.NORMAL), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.VILLAGE, new VillageConfig(0, VillagePieces.Type.OAK), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.STRONGHOLD, new StrongholdConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.SWAMP_HUT, new SwampHutConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.DESERT_PYRAMID, new DesertPyramidConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.JUNGLE_PYRAMID, new JunglePyramidConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.IGLOO, new IglooConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.SHIPWRECK, new ShipwreckConfig(false), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.OCEAN_MONUMENT, new OceanMonumentConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.WOODLAND_MANSION, new WoodlandMansionConfig(), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createCompositeFeature(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, createCompositeFeature(Feature.BURIED_TREASURE, new BuriedTreasureConfig(0.01F), PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG));
   }

   public boolean isMutation() {
      return this.parent != null;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyColorByTemp(float p_76731_1_) {
      p_76731_1_ = p_76731_1_ / 3.0F;
      p_76731_1_ = MathHelper.clamp(p_76731_1_, -1.0F, 1.0F);
      return MathHelper.hsvToRGB(0.62222224F - p_76731_1_ * 0.05F, 0.5F + p_76731_1_ * 0.1F, 1.0F);
   }

   protected void addSpawn(EnumCreatureType p_201866_1_, Biome.SpawnListEntry p_201866_2_) {
      this.creatures.get(p_201866_1_).add(p_201866_2_);
   }

   public List<Biome.SpawnListEntry> getSpawnableList(EnumCreatureType p_76747_1_) {
      return this.creatures.get(p_76747_1_);
   }

   public Biome.RainType getPrecipitation() {
      return this.precipitation;
   }

   public boolean isHighHumidity() {
      return this.getRainfall() > 0.85F;
   }

   public float getSpawningChance() {
      return 0.1F;
   }

   public float getTemperature(BlockPos p_180626_1_) {
      if (p_180626_1_.getY() > 64) {
         float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)p_180626_1_.getX() / 8.0F), (double)((float)p_180626_1_.getZ() / 8.0F)) * 4.0D);
         return this.getDefaultTemperature() - (f + (float)p_180626_1_.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.getDefaultTemperature();
      }
   }

   public boolean doesWaterFreeze(IWorldReaderBase p_201848_1_, BlockPos p_201848_2_) {
      return this.doesWaterFreeze(p_201848_1_, p_201848_2_, true);
   }

   public boolean doesWaterFreeze(IWorldReaderBase p_201854_1_, BlockPos p_201854_2_, boolean p_201854_3_) {
      if (this.getTemperature(p_201854_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201854_2_.getY() >= 0 && p_201854_2_.getY() < 256 && p_201854_1_.getLightFor(EnumLightType.BLOCK, p_201854_2_) < 10) {
            IBlockState iblockstate = p_201854_1_.getBlockState(p_201854_2_);
            IFluidState ifluidstate = p_201854_1_.getFluidState(p_201854_2_);
            if (ifluidstate.getFluid() == Fluids.WATER && iblockstate.getBlock() instanceof BlockFlowingFluid) {
               if (!p_201854_3_) {
                  return true;
               }

               boolean flag = p_201854_1_.hasWater(p_201854_2_.west()) && p_201854_1_.hasWater(p_201854_2_.east()) && p_201854_1_.hasWater(p_201854_2_.north()) && p_201854_1_.hasWater(p_201854_2_.south());
                return !flag;
            }
         }

         return false;
      }
   }

   public boolean doesSnowGenerate(IWorldReaderBase p_201850_1_, BlockPos p_201850_2_) {
      if (this.getTemperature(p_201850_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201850_2_.getY() >= 0 && p_201850_2_.getY() < 256 && p_201850_1_.getLightFor(EnumLightType.BLOCK, p_201850_2_) < 10) {
            IBlockState iblockstate = p_201850_1_.getBlockState(p_201850_2_);
             return iblockstate.isAir() && Blocks.SNOW.getDefaultState().isValidPosition(p_201850_1_, p_201850_2_);
         }

         return false;
      }
   }

   public void addFeature(GenerationStage.Decoration p_203611_1_, CompositeFeature<?, ?> p_203611_2_) {
      if (p_203611_2_ instanceof CompositeFlowerFeature) {
         this.flowers.add((CompositeFlowerFeature)p_203611_2_);
      }

      this.features.get(p_203611_1_).add(p_203611_2_);
   }

   public <C extends IFeatureConfig> void addCarver(GenerationStage.Carving p_203609_1_, WorldCarverWrapper<C> p_203609_2_) {
      this.carvers.computeIfAbsent(p_203609_1_, (p_203604_0_) -> {
         return Lists.newArrayList();
      }).add(p_203609_2_);
   }

   public List<WorldCarverWrapper<?>> getCarvers(GenerationStage.Carving p_203603_1_) {
      return this.carvers.computeIfAbsent(p_203603_1_, (p_203610_0_) -> {
         return Lists.newArrayList();
      });
   }

   public <C extends IFeatureConfig> void addStructure(Structure<C> p_201865_1_, C p_201865_2_) {
      this.structures.put(p_201865_1_, p_201865_2_);
   }

   public <C extends IFeatureConfig> boolean hasStructure(Structure<C> p_201858_1_) {
      return this.structures.containsKey(p_201858_1_);
   }

   @Nullable
   public <C extends IFeatureConfig> IFeatureConfig getStructureConfig(Structure<C> p_201857_1_) {
      return this.structures.get(p_201857_1_);
   }

   public List<CompositeFlowerFeature<?>> getFlowers() {
      return this.flowers;
   }

   public List<CompositeFeature<?, ?>> getFeatures(GenerationStage.Decoration p_203607_1_) {
      return this.features.get(p_203607_1_);
   }

   public void decorate(GenerationStage.Decoration p_203608_1_, IChunkGenerator<? extends IChunkGenSettings> p_203608_2_, IWorld p_203608_3_, long p_203608_4_, SharedSeedRandom p_203608_6_, BlockPos p_203608_7_) {
      int i = 0;

      for(CompositeFeature<?, ?> compositefeature : this.features.get(p_203608_1_)) {
         p_203608_6_.func_202426_b(p_203608_4_, i, p_203608_1_.ordinal());
         compositefeature.func_212245_a(p_203608_3_, p_203608_2_, p_203608_6_, p_203608_7_, IFeatureConfig.NO_FEATURE_CONFIG);
         ++i;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getGrassColor(BlockPos p_180627_1_) {
      double d0 = (double)MathHelper.clamp(this.getTemperature(p_180627_1_), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
      return GrassColors.func_77480_a(d0, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoliageColor(BlockPos p_180625_1_) {
      double d0 = (double)MathHelper.clamp(this.getTemperature(p_180625_1_), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
      return FoliageColors.func_77470_a(d0, d1);
   }

   public void buildSurface(Random p_206854_1_, IChunk p_206854_2_, int p_206854_3_, int p_206854_4_, int p_206854_5_, double p_206854_6_, IBlockState p_206854_8_, IBlockState p_206854_9_, int p_206854_10_, long p_206854_11_) {
      this.surfaceBuilder.setSeed(p_206854_11_);
      this.surfaceBuilder.buildSurface(p_206854_1_, p_206854_2_, this, p_206854_3_, p_206854_4_, p_206854_5_, p_206854_6_, p_206854_8_, p_206854_9_, p_206854_10_, p_206854_11_, AIR_SURFACE);
   }

   public Biome.TempCategory getTempCategory() {
      if (this.category == Biome.Category.OCEAN) {
         return Biome.TempCategory.OCEAN;
      } else if ((double)this.getDefaultTemperature() < 0.2D) {
         return Biome.TempCategory.COLD;
      } else {
         return (double)this.getDefaultTemperature() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM;
      }
   }

   public static Biome getBiome(int p_180276_0_, Biome p_180276_1_) {
      Biome biome = IRegistry.field_212624_m.func_148754_a(p_180276_0_);
      return biome == null ? p_180276_1_ : biome;
   }

   public final float getBaseHeight() {
      return this.depth;
   }

   public final float getRainfall() {
      return this.downfall;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return new TextComponentTranslation(this.getTranslationKey());
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("biome", IRegistry.field_212624_m.func_177774_c(this));
      }

      return this.translationKey;
   }

   public final float getHeightVariation() {
      return this.scale;
   }

   public final float getDefaultTemperature() {
      return this.temperature;
   }

   public final int getWaterColor() {
      return this.waterColor;
   }

   public final int getWaterFogColor() {
      return this.waterFogColor;
   }

   public final Biome.Category getBiomeCategory() {
      return this.category;
   }

   public CompositeSurfaceBuilder<?> getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public ISurfaceBuilderConfig getSurfaceBuilderConfig() {
      return this.surfaceBuilder.getConfig();
   }

   @Nullable
   public String getParent() {
      return this.parent;
   }

   public static void registerBiomes() {
      registerBiome(0, "ocean", new OceanBiome());
      registerBiome(1, "plains", new PlainsBiome());
      registerBiome(2, "desert", new DesertBiome());
      registerBiome(3, "mountains", new MountainsBiome());
      registerBiome(4, "forest", new ForestBiome());
      registerBiome(5, "taiga", new TaigaBiome());
      registerBiome(6, "swamp", new SwampBiome());
      registerBiome(7, "river", new RiverBiome());
      registerBiome(8, "nether", new NetherBiome());
      registerBiome(9, "the_end", new TheEndBiome());
      registerBiome(10, "frozen_ocean", new FrozenOceanBiome());
      registerBiome(11, "frozen_river", new FrozenRiverBiome());
      registerBiome(12, "snowy_tundra", new SnowyTundraBiome());
      registerBiome(13, "snowy_mountains", new SnowyMountainsBiome());
      registerBiome(14, "mushroom_fields", new MushroomFieldsBiome());
      registerBiome(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
      registerBiome(16, "beach", new BeachBiome());
      registerBiome(17, "desert_hills", new DesertHillsBiome());
      registerBiome(18, "wooded_hills", new WoodedHillsBiome());
      registerBiome(19, "taiga_hills", new TaigaHillsBiome());
      registerBiome(20, "mountain_edge", new MountainEdgeBiome());
      registerBiome(21, "jungle", new JungleBiome());
      registerBiome(22, "jungle_hills", new JungleHillsBiome());
      registerBiome(23, "jungle_edge", new JungleEdgeBiome());
      registerBiome(24, "deep_ocean", new DeepOceanBiome());
      registerBiome(25, "stone_shore", new StoneShoreBiome());
      registerBiome(26, "snowy_beach", new SnowyBeachBiome());
      registerBiome(27, "birch_forest", new BirchForestBiome());
      registerBiome(28, "birch_forest_hills", new BirchForestHillsBiome());
      registerBiome(29, "dark_forest", new DarkForestBiome());
      registerBiome(30, "snowy_taiga", new SnowyTaigaBiome());
      registerBiome(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
      registerBiome(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
      registerBiome(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
      registerBiome(34, "wooded_mountains", new WoodedMountainsBiome());
      registerBiome(35, "savanna", new SavannaBiome());
      registerBiome(36, "savanna_plateau", new SavannaPlateauBiome());
      registerBiome(37, "badlands", new BadlandsBiome());
      registerBiome(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
      registerBiome(39, "badlands_plateau", new BadlandsPlateauBiome());
      registerBiome(40, "small_end_islands", new SmallEndIslandsBiome());
      registerBiome(41, "end_midlands", new EndMidlandsBiome());
      registerBiome(42, "end_highlands", new EndHighlandsBiome());
      registerBiome(43, "end_barrens", new EndBarrensBiome());
      registerBiome(44, "warm_ocean", new WarmOceanBiome());
      registerBiome(45, "lukewarm_ocean", new LukewarmOceanBiome());
      registerBiome(46, "cold_ocean", new ColdOceanBiome());
      registerBiome(47, "deep_warm_ocean", new DeepWarmOceanBiome());
      registerBiome(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
      registerBiome(49, "deep_cold_ocean", new DeepColdOceanBiome());
      registerBiome(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
      registerBiome(127, "the_void", new TheVoidBiome());
      registerBiome(129, "sunflower_plains", new SunflowerPlainsBiome());
      registerBiome(130, "desert_lakes", new DesertLakesBiome());
      registerBiome(131, "gravelly_mountains", new GravellyMountainsBiome());
      registerBiome(132, "flower_forest", new FlowerForestBiome());
      registerBiome(133, "taiga_mountains", new TaigaMountainsBiome());
      registerBiome(134, "swamp_hills", new SwampHillsBiome());
      registerBiome(140, "ice_spikes", new IceSpikesBiome());
      registerBiome(149, "modified_jungle", new ModifiedJungleBiome());
      registerBiome(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
      registerBiome(155, "tall_birch_forest", new TallBirchForestBiome());
      registerBiome(156, "tall_birch_hills", new TallBirchHillsBiome());
      registerBiome(157, "dark_forest_hills", new DarkForestHillsBiome());
      registerBiome(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
      registerBiome(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
      registerBiome(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
      registerBiome(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
      registerBiome(163, "shattered_savanna", new ShatteredSavannaBiome());
      registerBiome(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
      registerBiome(165, "eroded_badlands", new ErodedBadlandsBiome());
      registerBiome(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
      registerBiome(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
      Collections.addAll(BIOMES, Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU);
   }

   private static void registerBiome(int p_185354_0_, String p_185354_1_, Biome p_185354_2_) {
      IRegistry.field_212624_m.func_177775_a(p_185354_0_, new ResourceLocation(p_185354_1_), p_185354_2_);
      if (p_185354_2_.isMutation()) {
         MUTATION_TO_BASE_ID_MAP.put(p_185354_2_, IRegistry.field_212624_m.func_148757_b(IRegistry.field_212624_m.func_212608_b(new ResourceLocation(p_185354_2_.parent))));
      }

   }

   public static class BiomeBuilder {
      @Nullable
      private CompositeSurfaceBuilder<?> surfaceBuilder;
      @Nullable
      private Biome.RainType precipitation;
      @Nullable
      private Biome.Category category;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      @Nullable
      private Float downfall;
      @Nullable
      private Integer waterColor;
      @Nullable
      private Integer waterFogColor;
      @Nullable
      private String parent;

      public Biome.BiomeBuilder surfaceBuilder(CompositeSurfaceBuilder<?> p_205416_1_) {
         this.surfaceBuilder = p_205416_1_;
         return this;
      }

      public Biome.BiomeBuilder precipitation(Biome.RainType p_205415_1_) {
         this.precipitation = p_205415_1_;
         return this;
      }

      public Biome.BiomeBuilder category(Biome.Category p_205419_1_) {
         this.category = p_205419_1_;
         return this;
      }

      public Biome.BiomeBuilder depth(float p_205421_1_) {
         this.depth = p_205421_1_;
         return this;
      }

      public Biome.BiomeBuilder scale(float p_205420_1_) {
         this.scale = p_205420_1_;
         return this;
      }

      public Biome.BiomeBuilder temperature(float p_205414_1_) {
         this.temperature = p_205414_1_;
         return this;
      }

      public Biome.BiomeBuilder downfall(float p_205417_1_) {
         this.downfall = p_205417_1_;
         return this;
      }

      public Biome.BiomeBuilder waterColor(int p_205412_1_) {
         this.waterColor = p_205412_1_;
         return this;
      }

      public Biome.BiomeBuilder waterFogColor(int p_205413_1_) {
         this.waterFogColor = p_205413_1_;
         return this;
      }

      public Biome.BiomeBuilder parent(@Nullable String p_205418_1_) {
         this.parent = p_205418_1_;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent='" + this.parent + '\'' + "\n" + '}';
      }
   }

   public enum Category {
      NONE,
      TAIGA,
      EXTREME_HILLS,
      JUNGLE,
      MESA,
      PLAINS,
      SAVANNA,
      ICY,
      THEEND,
      BEACH,
      FOREST,
      OCEAN,
      DESERT,
      RIVER,
      SWAMP,
      MUSHROOM,
      NETHER
   }

   public enum RainType {
      NONE,
      RAIN,
      SNOW
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public EntityType<? extends EntityLiving> entityType;
      public int minGroupCount;
      public int maxGroupCount;

      public SpawnListEntry(EntityType<? extends EntityLiving> entityType, int itemWeight, int minGroupCount, int maxGroupCount) {
         super(itemWeight);
         this.entityType = entityType;
         this.minGroupCount = minGroupCount;
         this.maxGroupCount = maxGroupCount;
      }

      public String toString() {
         return EntityType.getId(this.entityType) + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
      }
   }

   public enum TempCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM
   }
}
