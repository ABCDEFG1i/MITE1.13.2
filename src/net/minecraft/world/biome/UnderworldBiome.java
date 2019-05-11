package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.MinableConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class UnderworldBiome extends Biome {
    protected UnderworldBiome() {
        super((new Biome.BiomeBuilder()).surfaceBuilder(
                new CompositeSurfaceBuilder(DEFAULT_SURFACE_BUILDER, STONE_SURFACE))
                                        .precipitation(RainType.RAIN)
                                        .category(Category.NETHER)
                                        .depth(0.1F)
                                        .scale(0.2F)
                                        .temperature(0.05F)
                                        .downfall(0.5F)
                                        .waterColor(4159204)
                                        .waterFogColor(329011)
                                        .parent(null));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.GRANITE.getDefaultState(), 33), COUNT_RANGE,
                new CountRangeConfig(10, 0, 0, 256)));
        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIORITE.getDefaultState(), 33), COUNT_RANGE,
                new CountRangeConfig(10, 0, 0, 256)));
        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.ANDESITE.getDefaultState(), 33), COUNT_RANGE,
                new CountRangeConfig(10, 0, 0, 256)));
        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.INFESTED_STONE.getDefaultState(), 3), COUNT_RANGE,
                new CountRangeConfig(10, 0, 0, 256)));


        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.COAL_ORE.getDefaultState(), 16), COUNT_RANGE,
                new CountRangeConfig(20, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.COPPER_ORE.getDefaultState(), 6), COUNT_RANGE,
                new CountRangeConfig(27, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.SILVER_ORE.getDefaultState(), 6), COUNT_RANGE,
                new CountRangeConfig(25, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.IRON_ORE.getDefaultState(), 6), COUNT_RANGE,
                new CountRangeConfig(20, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.GOLD_ORE.getDefaultState(), 4), COUNT_RANGE,
                new CountRangeConfig(10, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.REDSTONE_ORE.getDefaultState(), 5), RANDOM_COUNT_WITH_RANGE,
                new CountRangeConfig(9, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.DIAMOND_ORE.getDefaultState(), 6), RANDOM_COUNT_WITH_RANGE,
                new CountRangeConfig(2, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.LAPIS_ORE.getDefaultState(), 3), DEPTH_AVERAGE,
                new DepthAverageConfig(5, 170, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.MITHRIL_ORE.getDefaultState(), 3), RANDOM_COUNT_WITH_RANGE,
                new CountRangeConfig(2, 0, 0, 255)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, createCompositeFeature(Feature.MINABLE,
                new MinableConfig(MinableConfig.IS_ROCK, Blocks.TUNGSTEN_ORE.getDefaultState(), 4), COUNT_RANGE,
                new CountRangeConfig(1, 0, 0, 60)));

        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                createCompositeFeature(Feature.LIQUIDS, new LiquidsConfig(Fluids.WATER), HEIGHT_BIASED_RANGE,
                        new CountRangeConfig(50, 8, 8, 256)));
        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                createCompositeFeature(Feature.LIQUIDS, new LiquidsConfig(Fluids.WATER), HEIGHT_BIASED_RANGE,
                        new CountRangeConfig(50, 8, 8, 256)));
        this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                createCompositeFeature(Feature.LIQUIDS, new LiquidsConfig(Fluids.LAVA), HEIGHT_VERY_BIASED_RANGE,
                        new CountRangeConfig(20, 8, 16, 256)));

        this.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES,
                createCompositeFeature(Feature.DUNGEONS, IFeatureConfig.NO_FEATURE_CONFIG, DUNGEON_ROOM,
                        new DungeonRoomConfig(8)));

        this.addSpawn(EnumCreatureType.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
        this.addSpawn(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
    }
}
