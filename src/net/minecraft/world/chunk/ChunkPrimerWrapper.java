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
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;

public class ChunkPrimerWrapper extends ChunkPrimer {
   private final IChunk chunk;

   public ChunkPrimerWrapper(IChunk p_i49380_1_) {
      super(p_i49380_1_.getPos(), UpgradeData.EMPTY);
      this.chunk = p_i49380_1_;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.chunk.getTileEntity(p_175625_1_);
   }

   @Nullable
   public IBlockState getBlockState(BlockPos p_180495_1_) {
      return this.chunk.getBlockState(p_180495_1_);
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.chunk.getFluidState(p_204610_1_);
   }

   public int getMaxLightLevel() {
      return this.chunk.getMaxLightLevel();
   }

   @Nullable
   public IBlockState setBlockState(BlockPos p_177436_1_, IBlockState p_177436_2_, boolean p_177436_3_) {
      return null;
   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
   }

   public void addEntity(Entity p_76612_1_) {
   }

   public void setStatus(ChunkStatus p_201574_1_) {
   }

   public ChunkSection[] getSections() {
      return this.chunk.getSections();
   }

   public int getLight(EnumLightType p_201587_1_, BlockPos p_201587_2_, boolean p_201587_3_) {
      return this.chunk.getLight(p_201587_1_, p_201587_2_, p_201587_3_);
   }

   public int getLightSubtracted(BlockPos p_201586_1_, int p_201586_2_, boolean p_201586_3_) {
      return this.chunk.getLightSubtracted(p_201586_1_, p_201586_2_, p_201586_3_);
   }

   public boolean canSeeSky(BlockPos p_177444_1_) {
      return this.chunk.canSeeSky(p_177444_1_);
   }

   public void setHeightMap(Heightmap.Type p_201643_1_, long[] p_201643_2_) {
   }

   private Heightmap.Type func_209532_c(Heightmap.Type p_209532_1_) {
      if (p_209532_1_ == Heightmap.Type.WORLD_SURFACE_WG) {
         return Heightmap.Type.WORLD_SURFACE;
      } else {
         return p_209532_1_ == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : p_209532_1_;
      }
   }

   public int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      return this.chunk.getTopBlockY(this.func_209532_c(p_201576_1_), p_201576_2_, p_201576_3_);
   }

   public ChunkPos getPos() {
      return this.chunk.getPos();
   }

   public void setLastSaveTime(long p_177432_1_) {
   }

   @Nullable
   public StructureStart getStructureStart(String p_201585_1_) {
      return this.chunk.getStructureStart(p_201585_1_);
   }

   public void putStructureStart(String p_201584_1_, StructureStart p_201584_2_) {
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.chunk.getStructureStarts();
   }

   public void setStructureStarts(Map<String, StructureStart> p_201648_1_) {
   }

   @Nullable
   public LongSet getStructureReferences(String p_201578_1_) {
      return this.chunk.getStructureReferences(p_201578_1_);
   }

   public void addStructureReference(String p_201583_1_, long p_201583_2_) {
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.chunk.getStructureReferences();
   }

   public void setStructureReferences(Map<String, LongSet> p_201641_1_) {
   }

   public Biome[] getBiomes() {
      return this.chunk.getBiomes();
   }

   public void setModified(boolean p_177427_1_) {
   }

   public boolean isModified() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.chunk.getStatus();
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
   }

   public void setLightFor(EnumLightType p_201580_1_, boolean p_201580_2_, BlockPos p_201580_3_, int p_201580_4_) {
      this.chunk.setLightFor(p_201580_1_, p_201580_2_, p_201580_3_, p_201580_4_);
   }

   public void markBlockForPostprocessing(BlockPos p_201594_1_) {
   }

   public void addTileEntity(NBTTagCompound p_201591_1_) {
   }

   @Nullable
   public NBTTagCompound getDeferredTileEntity(BlockPos p_201579_1_) {
      return this.chunk.getDeferredTileEntity(p_201579_1_);
   }

   public void setBiomes(Biome[] p_201577_1_) {
   }

   public void createHeightMap(Heightmap.Type... p_201588_1_) {
   }

   public List<BlockPos> getLightBlockPositions() {
      return this.chunk.getLightBlockPositions();
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return new ChunkPrimerTickList<>((p_209219_0_) -> {
         return p_209219_0_.getDefaultState().isAir();
      }, IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, this.getPos());
   }

   public ChunkPrimerTickList<Fluid> func_212247_j() {
      return new ChunkPrimerTickList<>((p_209218_0_) -> {
         return p_209218_0_ == Fluids.EMPTY;
      }, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      return this.chunk.getCarvingMask(p_205749_1_);
   }

   public void setUpdateHeightmaps(boolean p_207739_1_) {
   }
}
