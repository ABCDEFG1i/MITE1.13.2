package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IChunkGenerator<C extends IChunkGenSettings> {
   void makeBase(IChunk p_202088_1_);

   void carve(WorldGenRegion p_202091_1_, GenerationStage.Carving p_202091_2_);

   void decorate(WorldGenRegion p_202092_1_);

   void spawnMobs(WorldGenRegion p_202093_1_);

   List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType p_177458_1_, BlockPos p_177458_2_);

   @Nullable
   BlockPos func_211403_a(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_);

   C getSettings();

   int spawnMobs(World p_203222_1_, boolean p_203222_2_, boolean p_203222_3_);

   boolean hasStructure(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_);

   @Nullable
   IFeatureConfig getStructureConfig(Biome p_202087_1_, Structure<? extends IFeatureConfig> p_202087_2_);

   Long2ObjectMap<StructureStart> getStructureReferenceToStartMap(Structure<? extends IFeatureConfig> p_203224_1_);

   Long2ObjectMap<LongSet> getStructurePositionToReferenceMap(Structure<? extends IFeatureConfig> p_203223_1_);

   BiomeProvider getBiomeProvider();

   long getSeed();

   int getGroundHeight();

   int getMaxHeight();
}
