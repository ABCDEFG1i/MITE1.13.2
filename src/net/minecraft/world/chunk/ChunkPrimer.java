package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos pos;
   private boolean modified;
   private final AtomicInteger refCount = new AtomicInteger();
   private Biome[] biomes;
   private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
   private volatile ChunkStatus status = ChunkStatus.EMPTY;
   private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
   private final Map<BlockPos, NBTTagCompound> deferredTileEntities = Maps.newHashMap();
   private final ChunkSection[] sections = new ChunkSection[16];
   private final List<NBTTagCompound> entities = Lists.newArrayList();
   private final List<BlockPos> lightPositions = Lists.newArrayList();
   private final ShortList[] packedPositions = new ShortList[16];
   private final Map<String, StructureStart> structureStartMap = Maps.newHashMap();
   private final Map<String, LongSet> structureReferenceMap = Maps.newHashMap();
   private final UpgradeData upgradeData;
   private final ChunkPrimerTickList<Block> pendingBlockTicks;
   private final ChunkPrimerTickList<Fluid> pendingFluidTicks;
   private long inhabitedTime;
   private final Map<GenerationStage.Carving, BitSet> carvingMasks = Maps.newHashMap();
   private boolean updateHeightmaps;

   public ChunkPrimer(int p_i48699_1_, int p_i48699_2_, UpgradeData p_i48699_3_) {
      this(new ChunkPos(p_i48699_1_, p_i48699_2_), p_i48699_3_);
   }

   public ChunkPrimer(ChunkPos p_i48700_1_, UpgradeData p_i48700_2_) {
      this.pos = p_i48700_1_;
      this.upgradeData = p_i48700_2_;
      this.pendingBlockTicks = new ChunkPrimerTickList<>((p_205332_0_) -> {
         return p_205332_0_ == null || p_205332_0_.getDefaultState().isAir();
      }, IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, p_i48700_1_);
      this.pendingFluidTicks = new ChunkPrimerTickList<>((p_205766_0_) -> {
         return p_205766_0_ == null || p_205766_0_ == Fluids.EMPTY;
      }, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, p_i48700_1_);
   }

   public static ShortList getOrCreate(ShortList[] p_205330_0_, int p_205330_1_) {
      if (p_205330_0_[p_205330_1_] == null) {
         p_205330_0_[p_205330_1_] = new ShortArrayList();
      }

      return p_205330_0_[p_205330_1_];
   }

   @Nullable
   public IBlockState getBlockState(BlockPos p_180495_1_) {
      int i = p_180495_1_.getX();
      int j = p_180495_1_.getY();
      int k = p_180495_1_.getZ();
      if (j >= 0 && j < 256) {
         return this.sections[j >> 4] == Chunk.EMPTY_SECTION ? Blocks.AIR.getDefaultState() : this.sections[j >> 4].get(i & 15, j & 15, k & 15);
      } else {
         return Blocks.VOID_AIR.getDefaultState();
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      int i = p_204610_1_.getX();
      int j = p_204610_1_.getY();
      int k = p_204610_1_.getZ();
      return j >= 0 && j < 256 && this.sections[j >> 4] != Chunk.EMPTY_SECTION ? this.sections[j >> 4].func_206914_b(i & 15, j & 15, k & 15) : Fluids.EMPTY.getDefaultState();
   }

   public List<BlockPos> getLightBlockPositions() {
      return this.lightPositions;
   }

   public ShortList[] getPackedLightPositions() {
      ShortList[] ashortlist = new ShortList[16];

      for(BlockPos blockpos : this.lightPositions) {
         getOrCreate(ashortlist, blockpos.getY() >> 4).add(packToLocal(blockpos));
      }

      return ashortlist;
   }

   public void addLightValue(short p_201646_1_, int p_201646_2_) {
      this.addLightPosition(unpackToWorld(p_201646_1_, p_201646_2_, this.pos));
   }

   public void addLightPosition(BlockPos p_201637_1_) {
      this.lightPositions.add(p_201637_1_);
   }

   @Nullable
   public IBlockState setBlockState(BlockPos p_177436_1_, IBlockState p_177436_2_, boolean p_177436_3_) {
      int i = p_177436_1_.getX();
      int j = p_177436_1_.getY();
      int k = p_177436_1_.getZ();
      if (j >= 0 && j < 256) {
         if (p_177436_2_.getLightValue() > 0) {
            this.lightPositions.add(new BlockPos((i & 15) + this.getPos().getXStart(), j, (k & 15) + this.getPos().getZStart()));
         }

         if (this.sections[j >> 4] == Chunk.EMPTY_SECTION) {
            if (p_177436_2_.getBlock() == Blocks.AIR) {
               return p_177436_2_;
            }

            this.sections[j >> 4] = new ChunkSection(j >> 4 << 4, this.hasSkylight());
         }

         IBlockState iblockstate = this.sections[j >> 4].get(i & 15, j & 15, k & 15);
         this.sections[j >> 4].set(i & 15, j & 15, k & 15, p_177436_2_);
         if (this.updateHeightmaps) {
            this.getOrCreateHeightmap(Heightmap.Type.MOTION_BLOCKING).update(i & 15, j, k & 15, p_177436_2_);
            this.getOrCreateHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i & 15, j, k & 15, p_177436_2_);
            this.getOrCreateHeightmap(Heightmap.Type.OCEAN_FLOOR).update(i & 15, j, k & 15, p_177436_2_);
            this.getOrCreateHeightmap(Heightmap.Type.WORLD_SURFACE).update(i & 15, j, k & 15, p_177436_2_);
         }

         return iblockstate;
      } else {
         return Blocks.VOID_AIR.getDefaultState();
      }
   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
      p_177426_2_.setPos(p_177426_1_);
      this.tileEntities.put(p_177426_1_, p_177426_2_);
   }

   public Set<BlockPos> getTileEntityPositions() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.tileEntities.get(p_175625_1_);
   }

   public Map<BlockPos, TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addEntity(NBTTagCompound p_201626_1_) {
      this.entities.add(p_201626_1_);
   }

   public void addEntity(Entity p_76612_1_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      p_76612_1_.writeToNBTOptional(nbttagcompound);
      this.addEntity(nbttagcompound);
   }

   public List<NBTTagCompound> getEntities() {
      return this.entities;
   }

   public void setBiomes(Biome[] p_201577_1_) {
      this.biomes = p_201577_1_;
   }

   public Biome[] getBiomes() {
      return this.biomes;
   }

   public void setModified(boolean p_177427_1_) {
      this.modified = p_177427_1_;
   }

   public boolean isModified() {
      return this.modified;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus p_201574_1_) {
      this.status = p_201574_1_;
      this.setModified(true);
   }

   public void setStatus(String p_201650_1_) {
      this.setStatus(ChunkStatus.getByName(p_201650_1_));
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   public int getLight(EnumLightType p_201587_1_, BlockPos p_201587_2_, boolean p_201587_3_) {
      int i = p_201587_2_.getX() & 15;
      int j = p_201587_2_.getY();
      int k = p_201587_2_.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == Chunk.EMPTY_SECTION) {
            return this.canSeeSky(p_201587_2_) ? p_201587_1_.defaultLightValue : 0;
         } else if (p_201587_1_ == EnumLightType.SKY) {
            return !p_201587_3_ ? 0 : chunksection.getSkyLight(i, j & 15, k);
         } else {
            return p_201587_1_ == EnumLightType.BLOCK ? chunksection.getBlockLight(i, j & 15, k) : p_201587_1_.defaultLightValue;
         }
      } else {
         return 0;
      }
   }

   public int getLightSubtracted(BlockPos p_201586_1_, int p_201586_2_, boolean p_201586_3_) {
      int i = p_201586_1_.getX() & 15;
      int j = p_201586_1_.getY();
      int k = p_201586_1_.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == Chunk.EMPTY_SECTION) {
            return this.hasSkylight() && p_201586_2_ < EnumLightType.SKY.defaultLightValue ? EnumLightType.SKY.defaultLightValue - p_201586_2_ : 0;
         } else {
            int i1 = p_201586_3_ ? chunksection.getSkyLight(i, j & 15, k) : 0;
            i1 = i1 - p_201586_2_;
            int j1 = chunksection.getBlockLight(i, j & 15, k);
            if (j1 > i1) {
               i1 = j1;
            }

            return i1;
         }
      } else {
         return 0;
      }
   }

   public boolean canSeeSky(BlockPos p_177444_1_) {
      int i = p_177444_1_.getX() & 15;
      int j = p_177444_1_.getY();
      int k = p_177444_1_.getZ() & 15;
      return j >= this.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, i, k);
   }

   public void setChunkSections(ChunkSection[] p_201630_1_) {
      if (this.sections.length != p_201630_1_.length) {
         LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_201630_1_.length, this.sections.length);
      } else {
         System.arraycopy(p_201630_1_, 0, this.sections, 0, this.sections.length);
      }
   }

   public Set<Heightmap.Type> getHeightMapKeys() {
      return this.heightmaps.keySet();
   }

   @Nullable
   public Heightmap getHeightmap(Heightmap.Type p_201642_1_) {
      return this.heightmaps.get(p_201642_1_);
   }

   public void setHeightMap(Heightmap.Type p_201643_1_, long[] p_201643_2_) {
      this.getOrCreateHeightmap(p_201643_1_).setDataArray(p_201643_2_);
   }

   public void createHeightMap(Heightmap.Type... p_201588_1_) {
      for(Heightmap.Type heightmap$type : p_201588_1_) {
         this.getOrCreateHeightmap(heightmap$type);
      }

   }

   private Heightmap getOrCreateHeightmap(Heightmap.Type p_207902_1_) {
      return this.heightmaps.computeIfAbsent(p_207902_1_, (p_207903_1_) -> {
         Heightmap heightmap = new Heightmap(this, p_207903_1_);
         heightmap.generate();
         return heightmap;
      });
   }

   public int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      Heightmap heightmap = this.heightmaps.get(p_201576_1_);
      if (heightmap == null) {
         this.createHeightMap(p_201576_1_);
         heightmap = this.heightmaps.get(p_201576_1_);
      }

      return heightmap.getHeight(p_201576_2_ & 15, p_201576_3_ & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public void setLastSaveTime(long p_177432_1_) {
   }

   @Nullable
   public StructureStart getStructureStart(String p_201585_1_) {
      return this.structureStartMap.get(p_201585_1_);
   }

   public void putStructureStart(String p_201584_1_, StructureStart p_201584_2_) {
      this.structureStartMap.put(p_201584_1_, p_201584_2_);
      this.modified = true;
   }

   public Map<String, StructureStart> getStructureStarts() {
      return Collections.unmodifiableMap(this.structureStartMap);
   }

   public void setStructureStarts(Map<String, StructureStart> p_201648_1_) {
      this.structureStartMap.clear();
      this.structureStartMap.putAll(p_201648_1_);
      this.modified = true;
   }

   @Nullable
   public LongSet getStructureReferences(String p_201578_1_) {
      return this.structureReferenceMap.computeIfAbsent(p_201578_1_, (p_208302_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String p_201583_1_, long p_201583_2_) {
      this.structureReferenceMap.computeIfAbsent(p_201583_1_, (p_201628_0_) -> {
         return new LongOpenHashSet();
      }).add(p_201583_2_);
      this.modified = true;
   }

   public Map<String, LongSet> getStructureReferences() {
      return Collections.unmodifiableMap(this.structureReferenceMap);
   }

   public void setStructureReferences(Map<String, LongSet> p_201641_1_) {
      this.structureReferenceMap.clear();
      this.structureReferenceMap.putAll(p_201641_1_);
      this.modified = true;
   }

   public void setLightFor(EnumLightType p_201580_1_, boolean p_201580_2_, BlockPos p_201580_3_, int p_201580_4_) {
      int i = p_201580_3_.getX() & 15;
      int j = p_201580_3_.getY();
      int k = p_201580_3_.getZ() & 15;
      int l = j >> 4;
      if (l < 16 && l >= 0) {
         if (this.sections[l] == Chunk.EMPTY_SECTION) {
            if (p_201580_4_ == p_201580_1_.defaultLightValue) {
               return;
            }

            this.sections[l] = new ChunkSection(l << 4, this.hasSkylight());
         }

         if (p_201580_1_ == EnumLightType.SKY) {
            if (p_201580_2_) {
               this.sections[l].setSkyLight(i, j & 15, k, p_201580_4_);
            }
         } else if (p_201580_1_ == EnumLightType.BLOCK) {
            this.sections[l].setBlockLight(i, j & 15, k, p_201580_4_);
         }

      }
   }

   public static short packToLocal(BlockPos p_201651_0_) {
      int i = p_201651_0_.getX();
      int j = p_201651_0_.getY();
      int k = p_201651_0_.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackToWorld(short p_201635_0_, int p_201635_1_, ChunkPos p_201635_2_) {
      int i = (p_201635_0_ & 15) + (p_201635_2_.x << 4);
      int j = (p_201635_0_ >>> 4 & 15) + (p_201635_1_ << 4);
      int k = (p_201635_0_ >>> 8 & 15) + (p_201635_2_.z << 4);
      return new BlockPos(i, j, k);
   }

   public void markBlockForPostprocessing(BlockPos p_201594_1_) {
      if (!World.isOutsideBuildHeight(p_201594_1_)) {
         getOrCreate(this.packedPositions, p_201594_1_.getY() >> 4).add(packToLocal(p_201594_1_));
      }

   }

   public ShortList[] getPackedPositions() {
      return this.packedPositions;
   }

   public void func_201636_b(short p_201636_1_, int p_201636_2_) {
      getOrCreate(this.packedPositions, p_201636_2_).add(p_201636_1_);
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return this.pendingBlockTicks;
   }

   public ChunkPrimerTickList<Fluid> func_212247_j() {
      return this.pendingFluidTicks;
   }

   private boolean hasSkylight() {
      return true;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long p_209215_1_) {
      this.inhabitedTime = p_209215_1_;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void addTileEntity(NBTTagCompound p_201591_1_) {
      this.deferredTileEntities.put(new BlockPos(p_201591_1_.getInteger("x"), p_201591_1_.getInteger("y"), p_201591_1_.getInteger("z")), p_201591_1_);
   }

   public Map<BlockPos, NBTTagCompound> getDeferredTileEntities() {
      return Collections.unmodifiableMap(this.deferredTileEntities);
   }

   public NBTTagCompound getDeferredTileEntity(BlockPos p_201579_1_) {
      return this.deferredTileEntities.get(p_201579_1_);
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
      this.tileEntities.remove(p_177425_1_);
      this.deferredTileEntities.remove(p_177425_1_);
   }

   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      return this.carvingMasks.computeIfAbsent(p_205749_1_, (p_205761_0_) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStage.Carving p_205767_1_, BitSet p_205767_2_) {
      this.carvingMasks.put(p_205767_1_, p_205767_2_);
   }

   public void addRefCount(int p_205747_1_) {
      this.refCount.addAndGet(p_205747_1_);
   }

   public boolean isAlive() {
      return this.refCount.get() > 0;
   }

   public void setUpdateHeightmaps(boolean p_207739_1_) {
      this.updateHeightmaps = p_207739_1_;
   }
}
