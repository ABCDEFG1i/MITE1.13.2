package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk extends IBlockReader {
   @Nullable
   IBlockState setBlockState(BlockPos p_177436_1_, IBlockState p_177436_2_, boolean p_177436_3_);

   void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_);

   void addEntity(Entity p_76612_1_);

   void setStatus(ChunkStatus p_201574_1_);

   @Nullable
   default ChunkSection getLastExtendedBlockStorage() {
      ChunkSection[] achunksection = this.getSections();

      for(int i = achunksection.length - 1; i >= 0; --i) {
         if (achunksection[i] != Chunk.EMPTY_SECTION) {
            return achunksection[i];
         }
      }

      return null;
   }

   default int getTopFilledSegment() {
      ChunkSection chunksection = this.getLastExtendedBlockStorage();
      return chunksection == null ? 0 : chunksection.getYLocation();
   }

   ChunkSection[] getSections();

   int getLight(EnumLightType p_201587_1_, BlockPos p_201587_2_, boolean p_201587_3_);

   int getLightSubtracted(BlockPos p_201586_1_, int p_201586_2_, boolean p_201586_3_);

   boolean canSeeSky(BlockPos p_177444_1_);

   int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_);

   ChunkPos getPos();

   void setLastSaveTime(long p_177432_1_);

   @Nullable
   StructureStart getStructureStart(String p_201585_1_);

   void putStructureStart(String p_201584_1_, StructureStart p_201584_2_);

   Map<String, StructureStart> getStructureStarts();

   @Nullable
   LongSet getStructureReferences(String p_201578_1_);

   void addStructureReference(String p_201583_1_, long p_201583_2_);

   Map<String, LongSet> getStructureReferences();

   Biome[] getBiomes();

   ChunkStatus getStatus();

   void removeTileEntity(BlockPos p_177425_1_);

   void setLightFor(EnumLightType p_201580_1_, boolean p_201580_2_, BlockPos p_201580_3_, int p_201580_4_);

   default void markBlockForPostprocessing(BlockPos p_201594_1_) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)p_201594_1_);
   }

   default void addTileEntity(NBTTagCompound p_201591_1_) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   default NBTTagCompound getDeferredTileEntity(BlockPos p_201579_1_) {
      throw new UnsupportedOperationException();
   }

   default void setBiomes(Biome[] p_201577_1_) {
      throw new UnsupportedOperationException();
   }

   default void createHeightMap(Heightmap.Type... p_201588_1_) {
      throw new UnsupportedOperationException();
   }

   default List<BlockPos> getLightBlockPositions() {
      throw new UnsupportedOperationException();
   }

   ITickList<Block> getBlocksToBeTicked();

   ITickList<Fluid> func_212247_j();

   BitSet getCarvingMask(GenerationStage.Carving p_205749_1_);
}
