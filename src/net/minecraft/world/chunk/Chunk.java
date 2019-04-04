package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements IChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ChunkSection EMPTY_SECTION = null;
   private final ChunkSection[] sections = new ChunkSection[16];
   private final Biome[] blockBiomeArray;
   private final boolean[] updateSkylightColumns = new boolean[256];
   private final Map<BlockPos, NBTTagCompound> deferredTileEntities = Maps.newHashMap();
   private boolean loaded;
   private final World world;
   private final Map<Heightmap.Type, Heightmap> heightMap = Maps.newEnumMap(Heightmap.Type.class);
   public final int x;
   public final int z;
   private boolean isGapLightingUpdated;
   private final UpgradeData upgradeData;
   private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
   private final ClassInheritanceMultiMap<Entity>[] entityLists;
   private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
   private final Map<String, LongSet> structureReferences = Maps.newHashMap();
   private final ShortList[] packedBlockPositions = new ShortList[16];
   private final ITickList<Block> blocksToBeTicked;
   private final ITickList<Fluid> fluidsToBeTicked;
   private boolean ticked;
   private boolean hasEntities;
   private long lastSaveTime;
   private boolean dirty;
   private int heightMapMinimum;
   private long inhabitedTime;
   private int queuedLightChecks = 4096;
   private final ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
   private ChunkStatus status = ChunkStatus.EMPTY;
   private int neighborCount;
   private final AtomicInteger field_205757_F = new AtomicInteger();
   private final ChunkPos field_212816_F;

   @OnlyIn(Dist.CLIENT)
   public Chunk(World p_i48701_1_, int p_i48701_2_, int p_i48701_3_, Biome[] p_i48701_4_) {
      this(p_i48701_1_, p_i48701_2_, p_i48701_3_, p_i48701_4_, UpgradeData.EMPTY, EmptyTickList.get(), EmptyTickList.get(), 0L);
   }

   public Chunk(World p_i49379_1_, int p_i49379_2_, int p_i49379_3_, Biome[] p_i49379_4_, UpgradeData p_i49379_5_, ITickList<Block> p_i49379_6_, ITickList<Fluid> p_i49379_7_, long p_i49379_8_) {
      this.entityLists = new ClassInheritanceMultiMap[16];
      this.world = p_i49379_1_;
      this.x = p_i49379_2_;
      this.z = p_i49379_3_;
      this.field_212816_F = new ChunkPos(p_i49379_2_, p_i49379_3_);
      this.upgradeData = p_i49379_5_;

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            this.heightMap.put(heightmap$type, new Heightmap(this, heightmap$type));
         }
      }

      for(int i = 0; i < this.entityLists.length; ++i) {
         this.entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      this.blockBiomeArray = p_i49379_4_;
      this.blocksToBeTicked = p_i49379_6_;
      this.fluidsToBeTicked = p_i49379_7_;
      this.inhabitedTime = p_i49379_8_;
   }

   public Chunk(World p_i48703_1_, ChunkPrimer p_i48703_2_, int p_i48703_3_, int p_i48703_4_) {
      this(p_i48703_1_, p_i48703_3_, p_i48703_4_, p_i48703_2_.getBiomes(), p_i48703_2_.getUpgradeData(), p_i48703_2_.getBlocksToBeTicked(), p_i48703_2_.func_212247_j(), p_i48703_2_.getInhabitedTime());

      for(int i = 0; i < this.sections.length; ++i) {
         this.sections[i] = p_i48703_2_.getSections()[i];
      }

      for(NBTTagCompound nbttagcompound : p_i48703_2_.getEntities()) {
         AnvilChunkLoader.readChunkEntity(nbttagcompound, p_i48703_1_, this);
      }

      for(TileEntity tileentity : p_i48703_2_.getTileEntities().values()) {
         this.addTileEntity(tileentity);
      }

      this.deferredTileEntities.putAll(p_i48703_2_.getDeferredTileEntities());

      for(int j = 0; j < p_i48703_2_.getPackedPositions().length; ++j) {
         this.packedBlockPositions[j] = p_i48703_2_.getPackedPositions()[j];
      }

      this.setStructureStarts(p_i48703_2_.getStructureStarts());
      this.setStructureReferences(p_i48703_2_.getStructureReferences());

      for(Heightmap.Type heightmap$type : p_i48703_2_.getHeightMapKeys()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            this.heightMap.computeIfAbsent(heightmap$type, (p_205750_1_) -> {
               return new Heightmap(this, p_205750_1_);
            }).setDataArray(p_i48703_2_.getHeightmap(heightmap$type).getDataArray());
         }
      }

      this.dirty = true;
      this.setStatus(ChunkStatus.FULLCHUNK);
   }

   public Set<BlockPos> func_203066_o() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   public boolean isAtLocation(int p_76600_1_, int p_76600_2_) {
      return p_76600_1_ == this.x && p_76600_2_ == this.z;
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   @OnlyIn(Dist.CLIENT)
   protected void generateHeightMap() {
      for(Heightmap heightmap : this.heightMap.values()) {
         heightmap.generate();
      }

      this.dirty = true;
   }

   public void generateSkylightMap() {
      int i = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for(Heightmap heightmap : this.heightMap.values()) {
         heightmap.generate();
      }

      for(int i1 = 0; i1 < 16; ++i1) {
         for(int j1 = 0; j1 < 16; ++j1) {
            if (this.world.dimension.hasSkyLight()) {
               int j = 15;
               int k = i + 16 - 1;

               while(true) {
                  int l = this.getBlockLightOpacity(i1, k, j1);
                  if (l == 0 && j != 15) {
                     l = 1;
                  }

                  j -= l;
                  if (j > 0) {
                     ChunkSection chunksection = this.sections[k >> 4];
                     if (chunksection != EMPTY_SECTION) {
                        chunksection.setSkyLight(i1, k & 15, j1, j);
                        this.world.notifyLightSet(new BlockPos((this.x << 4) + i1, k, (this.z << 4) + j1));
                     }
                  }

                  --k;
                  if (k <= 0 || j <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.dirty = true;
   }

   private void propagateSkylightOcclusion(int p_76595_1_, int p_76595_2_) {
      this.updateSkylightColumns[p_76595_1_ + p_76595_2_ * 16] = true;
      this.isGapLightingUpdated = true;
   }

   private void recheckGaps(boolean p_150803_1_) {
      this.world.profiler.startSection("recheckGaps");
      if (this.world.isAreaLoaded(new BlockPos(this.x * 16 + 8, 0, this.z * 16 + 8), 16)) {
         for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
               if (this.updateSkylightColumns[i + j * 16]) {
                  this.updateSkylightColumns[i + j * 16] = false;
                  int k = this.getTopBlockY(Heightmap.Type.LIGHT_BLOCKING, i, j);
                  int l = this.x * 16 + i;
                  int i1 = this.z * 16 + j;
                  int j1 = Integer.MAX_VALUE;

                  for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                     j1 = Math.min(j1, this.world.getChunksLowestHorizon(l + enumfacing.getXOffset(), i1 + enumfacing.getZOffset()));
                  }

                  this.checkSkylightNeighborHeight(l, i1, j1);

                  for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                     this.checkSkylightNeighborHeight(l + enumfacing1.getXOffset(), i1 + enumfacing1.getZOffset(), k);
                  }

                  if (p_150803_1_) {
                     this.world.profiler.endSection();
                     return;
                  }
               }
            }
         }

         this.isGapLightingUpdated = false;
      }

      this.world.profiler.endSection();
   }

   private void checkSkylightNeighborHeight(int p_76599_1_, int p_76599_2_, int p_76599_3_) {
      int i = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(p_76599_1_, 0, p_76599_2_)).getY();
      if (i > p_76599_3_) {
         this.updateSkylightNeighborHeight(p_76599_1_, p_76599_2_, p_76599_3_, i + 1);
      } else if (i < p_76599_3_) {
         this.updateSkylightNeighborHeight(p_76599_1_, p_76599_2_, i, p_76599_3_ + 1);
      }

   }

   private void updateSkylightNeighborHeight(int p_76609_1_, int p_76609_2_, int p_76609_3_, int p_76609_4_) {
      if (p_76609_4_ > p_76609_3_ && this.world.isAreaLoaded(new BlockPos(p_76609_1_, 0, p_76609_2_), 16)) {
         for(int i = p_76609_3_; i < p_76609_4_; ++i) {
            this.world.checkLightFor(EnumLightType.SKY, new BlockPos(p_76609_1_, i, p_76609_2_));
         }

         this.dirty = true;
      }

   }

   private void relightBlock(int p_76615_1_, int p_76615_2_, int p_76615_3_, IBlockState p_76615_4_) {
      Heightmap heightmap = this.heightMap.get(Heightmap.Type.LIGHT_BLOCKING);
      int i = heightmap.getHeight(p_76615_1_ & 15, p_76615_3_ & 15) & 255;
      if (heightmap.update(p_76615_1_, p_76615_2_, p_76615_3_, p_76615_4_)) {
         int j = heightmap.getHeight(p_76615_1_ & 15, p_76615_3_ & 15);
         int k = this.x * 16 + p_76615_1_;
         int l = this.z * 16 + p_76615_3_;
         this.world.markBlocksDirtyVertical(k, l, j, i);
         if (this.world.dimension.hasSkyLight()) {
            int i1 = Math.min(i, j);
            int j1 = Math.max(i, j);
            int k1 = j < i ? 15 : 0;

            for(int l1 = i1; l1 < j1; ++l1) {
               ChunkSection chunksection = this.sections[l1 >> 4];
               if (chunksection != EMPTY_SECTION) {
                  chunksection.setSkyLight(p_76615_1_, l1 & 15, p_76615_3_, k1);
                  this.world.notifyLightSet(new BlockPos((this.x << 4) + p_76615_1_, l1, (this.z << 4) + p_76615_3_));
               }
            }

            int l2 = 15;

            while(j > 0 && l2 > 0) {
               --j;
               int i3 = this.getBlockLightOpacity(p_76615_1_, j, p_76615_3_);
               i3 = i3 == 0 ? 1 : i3;
               l2 = l2 - i3;
               l2 = Math.max(0, l2);
               ChunkSection chunksection1 = this.sections[j >> 4];
               if (chunksection1 != EMPTY_SECTION) {
                  chunksection1.setSkyLight(p_76615_1_, j & 15, p_76615_3_, l2);
               }
            }
         }

         if (j < this.heightMapMinimum) {
            this.heightMapMinimum = j;
         }

         if (this.world.dimension.hasSkyLight()) {
            int i2 = heightmap.getHeight(p_76615_1_ & 15, p_76615_3_ & 15);
            int j2 = Math.min(i, i2);
            int k2 = Math.max(i, i2);

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               this.updateSkylightNeighborHeight(k + enumfacing.getXOffset(), l + enumfacing.getZOffset(), j2, k2);
            }

            this.updateSkylightNeighborHeight(k, l, j2, k2);
         }

         this.dirty = true;
      }
   }

   private int getBlockLightOpacity(int p_150808_1_, int p_150808_2_, int p_150808_3_) {
      return this.getBlockState(p_150808_1_, p_150808_2_, p_150808_3_).getOpacity(this.world, new BlockPos(p_150808_1_, p_150808_2_, p_150808_3_));
   }

   public IBlockState getBlockState(BlockPos p_180495_1_) {
      return this.getBlockState(p_180495_1_.getX(), p_180495_1_.getY(), p_180495_1_.getZ());
   }

   public IBlockState getBlockState(int p_186032_1_, int p_186032_2_, int p_186032_3_) {
      if (this.world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         IBlockState iblockstate = null;
         if (p_186032_2_ == 60) {
            iblockstate = Blocks.BARRIER.getDefaultState();
         }

         if (p_186032_2_ == 70) {
            iblockstate = ChunkGeneratorDebug.getBlockStateFor(p_186032_1_, p_186032_3_);
         }

         return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
      } else {
         try {
            if (p_186032_2_ >= 0 && p_186032_2_ >> 4 < this.sections.length) {
               ChunkSection chunksection = this.sections[p_186032_2_ >> 4];
               if (chunksection != EMPTY_SECTION) {
                  return chunksection.get(p_186032_1_ & 15, p_186032_2_ & 15, p_186032_3_ & 15);
               }
            }

            return Blocks.AIR.getDefaultState();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", () -> {
               return CrashReportCategory.getCoordinateInfo(p_186032_1_, p_186032_2_, p_186032_3_);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.getFluidState(p_204610_1_.getX(), p_204610_1_.getY(), p_204610_1_.getZ());
   }

   public IFluidState getFluidState(int p_205751_1_, int p_205751_2_, int p_205751_3_) {
      try {
         if (p_205751_2_ >= 0 && p_205751_2_ >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[p_205751_2_ >> 4];
            if (chunksection != EMPTY_SECTION) {
               return chunksection.func_206914_b(p_205751_1_ & 15, p_205751_2_ & 15, p_205751_3_ & 15);
            }
         }

         return Fluids.EMPTY.getDefaultState();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
         crashreportcategory.addDetail("Location", () -> {
            return CrashReportCategory.getCoordinateInfo(p_205751_1_, p_205751_2_, p_205751_3_);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public IBlockState setBlockState(BlockPos p_177436_1_, IBlockState p_177436_2_, boolean p_177436_3_) {
      int i = p_177436_1_.getX() & 15;
      int j = p_177436_1_.getY();
      int k = p_177436_1_.getZ() & 15;
      int l = this.heightMap.get(Heightmap.Type.LIGHT_BLOCKING).getHeight(i, k);
      IBlockState iblockstate = this.getBlockState(p_177436_1_);
      if (iblockstate == p_177436_2_) {
         return null;
      } else {
         Block block = p_177436_2_.getBlock();
         Block block1 = iblockstate.getBlock();
         ChunkSection chunksection = this.sections[j >> 4];
         boolean flag = false;
         if (chunksection == EMPTY_SECTION) {
            if (p_177436_2_.isAir()) {
               return null;
            }

            chunksection = new ChunkSection(j >> 4 << 4, this.world.dimension.hasSkyLight());
            this.sections[j >> 4] = chunksection;
            flag = j >= l;
         }

         chunksection.set(i, j & 15, k, p_177436_2_);
         this.heightMap.get(Heightmap.Type.MOTION_BLOCKING).update(i, j, k, p_177436_2_);
         this.heightMap.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i, j, k, p_177436_2_);
         this.heightMap.get(Heightmap.Type.OCEAN_FLOOR).update(i, j, k, p_177436_2_);
         this.heightMap.get(Heightmap.Type.WORLD_SURFACE).update(i, j, k, p_177436_2_);
         if (!this.world.isRemote) {
            iblockstate.onReplaced(this.world, p_177436_1_, p_177436_2_, p_177436_3_);
         } else if (block1 != block && block1 instanceof ITileEntityProvider) {
            this.world.removeTileEntity(p_177436_1_);
         }

         if (chunksection.get(i, j & 15, k).getBlock() != block) {
            return null;
         } else {
            if (flag) {
               this.generateSkylightMap();
            } else {
               int i1 = p_177436_2_.getOpacity(this.world, p_177436_1_);
               int j1 = iblockstate.getOpacity(this.world, p_177436_1_);
               this.relightBlock(i, j, k, p_177436_2_);
               if (i1 != j1 && (i1 < j1 || this.getLightFor(EnumLightType.SKY, p_177436_1_) > 0 || this.getLightFor(EnumLightType.BLOCK, p_177436_1_) > 0)) {
                  this.propagateSkylightOcclusion(i, k);
               }
            }

            if (block1 instanceof ITileEntityProvider) {
               TileEntity tileentity = this.getTileEntity(p_177436_1_, Chunk.EnumCreateEntityType.CHECK);
               if (tileentity != null) {
                  tileentity.updateContainingBlockInfo();
               }
            }

            if (!this.world.isRemote) {
               p_177436_2_.onBlockAdded(this.world, p_177436_1_, iblockstate);
            }

            if (block instanceof ITileEntityProvider) {
               TileEntity tileentity1 = this.getTileEntity(p_177436_1_, Chunk.EnumCreateEntityType.CHECK);
               if (tileentity1 == null) {
                  tileentity1 = ((ITileEntityProvider)block).createNewTileEntity(this.world);
                  this.world.setTileEntity(p_177436_1_, tileentity1);
               } else {
                  tileentity1.updateContainingBlockInfo();
               }
            }

            this.dirty = true;
            return iblockstate;
         }
      }
   }

   public int getLightFor(EnumLightType p_177413_1_, BlockPos p_177413_2_) {
      return this.getLight(p_177413_1_, p_177413_2_, this.world.getDimension().hasSkyLight());
   }

   public int getLight(EnumLightType p_201587_1_, BlockPos p_201587_2_, boolean p_201587_3_) {
      int i = p_201587_2_.getX() & 15;
      int j = p_201587_2_.getY();
      int k = p_201587_2_.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == EMPTY_SECTION) {
            return this.canSeeSky(p_201587_2_) ? p_201587_1_.defaultLightValue : 0;
         } else if (p_201587_1_ == EnumLightType.SKY) {
            return !p_201587_3_ ? 0 : chunksection.getSkyLight(i, j & 15, k);
         } else {
            return p_201587_1_ == EnumLightType.BLOCK ? chunksection.getBlockLight(i, j & 15, k) : p_201587_1_.defaultLightValue;
         }
      } else {
         return (p_201587_1_ != EnumLightType.SKY || !p_201587_3_) && p_201587_1_ != EnumLightType.BLOCK ? 0 : p_201587_1_.defaultLightValue;
      }
   }

   public void setLightFor(EnumLightType p_177431_1_, BlockPos p_177431_2_, int p_177431_3_) {
      this.setLightFor(p_177431_1_, this.world.getDimension().hasSkyLight(), p_177431_2_, p_177431_3_);
   }

   public void setLightFor(EnumLightType p_201580_1_, boolean p_201580_2_, BlockPos p_201580_3_, int p_201580_4_) {
      int i = p_201580_3_.getX() & 15;
      int j = p_201580_3_.getY();
      int k = p_201580_3_.getZ() & 15;
      int l = j >> 4;
      if (l < 16 && l >= 0) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == EMPTY_SECTION) {
            if (p_201580_4_ == p_201580_1_.defaultLightValue) {
               return;
            }

            chunksection = new ChunkSection(l << 4, p_201580_2_);
            this.sections[l] = chunksection;
            this.generateSkylightMap();
         }

         if (p_201580_1_ == EnumLightType.SKY) {
            if (this.world.dimension.hasSkyLight()) {
               chunksection.setSkyLight(i, j & 15, k, p_201580_4_);
            }
         } else if (p_201580_1_ == EnumLightType.BLOCK) {
            chunksection.setBlockLight(i, j & 15, k, p_201580_4_);
         }

         this.dirty = true;
      }
   }

   public int getLightSubtracted(BlockPos p_177443_1_, int p_177443_2_) {
      return this.getLightSubtracted(p_177443_1_, p_177443_2_, this.world.getDimension().hasSkyLight());
   }

   public int getLightSubtracted(BlockPos p_201586_1_, int p_201586_2_, boolean p_201586_3_) {
      int i = p_201586_1_.getX() & 15;
      int j = p_201586_1_.getY();
      int k = p_201586_1_.getZ() & 15;
      int l = j >> 4;
      if (l >= 0 && l <= this.sections.length - 1) {
         ChunkSection chunksection = this.sections[l];
         if (chunksection == EMPTY_SECTION) {
            return p_201586_3_ && p_201586_2_ < EnumLightType.SKY.defaultLightValue ? EnumLightType.SKY.defaultLightValue - p_201586_2_ : 0;
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

   public void addEntity(Entity p_76612_1_) {
      this.hasEntities = true;
      int i = MathHelper.floor(p_76612_1_.posX / 16.0D);
      int j = MathHelper.floor(p_76612_1_.posZ / 16.0D);
      if (i != this.x || j != this.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.x, this.z, p_76612_1_);
         p_76612_1_.setDead();
      }

      int k = MathHelper.floor(p_76612_1_.posY / 16.0D);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entityLists.length) {
         k = this.entityLists.length - 1;
      }

      p_76612_1_.addedToChunk = true;
      p_76612_1_.chunkCoordX = this.x;
      p_76612_1_.chunkCoordY = k;
      p_76612_1_.chunkCoordZ = this.z;
      this.entityLists[k].add(p_76612_1_);
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
      this.heightMap.get(p_201607_1_).setDataArray(p_201607_2_);
   }

   public void removeEntity(Entity p_76622_1_) {
      this.removeEntityAtIndex(p_76622_1_, p_76622_1_.chunkCoordY);
   }

   public void removeEntityAtIndex(Entity p_76608_1_, int p_76608_2_) {
      if (p_76608_2_ < 0) {
         p_76608_2_ = 0;
      }

      if (p_76608_2_ >= this.entityLists.length) {
         p_76608_2_ = this.entityLists.length - 1;
      }

      this.entityLists[p_76608_2_].remove(p_76608_1_);
   }

   public boolean canSeeSky(BlockPos p_177444_1_) {
      int i = p_177444_1_.getX() & 15;
      int j = p_177444_1_.getY();
      int k = p_177444_1_.getZ() & 15;
      return j >= this.heightMap.get(Heightmap.Type.LIGHT_BLOCKING).getHeight(i, k);
   }

   public int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      return this.heightMap.get(p_201576_1_).getHeight(p_201576_2_ & 15, p_201576_3_ & 15) - 1;
   }

   @Nullable
   private TileEntity createNewTileEntity(BlockPos p_177422_1_) {
      IBlockState iblockstate = this.getBlockState(p_177422_1_);
      Block block = iblockstate.getBlock();
      return !block.hasTileEntity() ? null : ((ITileEntityProvider)block).createNewTileEntity(this.world);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.getTileEntity(p_175625_1_, Chunk.EnumCreateEntityType.CHECK);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_177424_1_, Chunk.EnumCreateEntityType p_177424_2_) {
      TileEntity tileentity = this.tileEntities.get(p_177424_1_);
      if (tileentity == null) {
         NBTTagCompound nbttagcompound = this.deferredTileEntities.remove(p_177424_1_);
         if (nbttagcompound != null) {
            TileEntity tileentity1 = this.func_212815_a(p_177424_1_, nbttagcompound);
            if (tileentity1 != null) {
               return tileentity1;
            }
         }
      }

      if (tileentity == null) {
         if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE) {
            tileentity = this.createNewTileEntity(p_177424_1_);
            this.world.setTileEntity(p_177424_1_, tileentity);
         } else if (p_177424_2_ == Chunk.EnumCreateEntityType.QUEUED) {
            this.tileEntityPosQueue.add(p_177424_1_);
         }
      } else if (tileentity.isInvalid()) {
         this.tileEntities.remove(p_177424_1_);
         return null;
      }

      return tileentity;
   }

   public void addTileEntity(TileEntity p_150813_1_) {
      this.addTileEntity(p_150813_1_.getPos(), p_150813_1_);
      if (this.loaded) {
         this.world.addTileEntity(p_150813_1_);
      }

   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
      p_177426_2_.setWorld(this.world);
      p_177426_2_.setPos(p_177426_1_);
      if (this.getBlockState(p_177426_1_).getBlock() instanceof ITileEntityProvider) {
         if (this.tileEntities.containsKey(p_177426_1_)) {
            this.tileEntities.get(p_177426_1_).invalidate();
         }

         p_177426_2_.validate();
         this.tileEntities.put(p_177426_1_.toImmutable(), p_177426_2_);
      }
   }

   public void addTileEntity(NBTTagCompound p_201591_1_) {
      this.deferredTileEntities.put(new BlockPos(p_201591_1_.getInteger("x"), p_201591_1_.getInteger("y"), p_201591_1_.getInteger("z")), p_201591_1_);
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
      if (this.loaded) {
         TileEntity tileentity = this.tileEntities.remove(p_177425_1_);
         if (tileentity != null) {
            tileentity.invalidate();
         }
      }

   }

   public void onLoad() {
      this.loaded = true;
      this.world.addTileEntities(this.tileEntities.values());

      for(ClassInheritanceMultiMap<Entity> classinheritancemultimap : this.entityLists) {
         this.world.func_212420_a(classinheritancemultimap.stream().filter((p_212383_0_) -> {
            return !(p_212383_0_ instanceof EntityPlayer);
         }));
      }

   }

   public void onUnload() {
      this.loaded = false;

      for(TileEntity tileentity : this.tileEntities.values()) {
         this.world.markTileEntityForRemoval(tileentity);
      }

      for(ClassInheritanceMultiMap<Entity> classinheritancemultimap : this.entityLists) {
         this.world.unloadEntities(classinheritancemultimap);
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   public void getEntitiesWithinAABBForEntity(@Nullable Entity p_177414_1_, AxisAlignedBB p_177414_2_, List<Entity> p_177414_3_, Predicate<? super Entity> p_177414_4_) {
      int i = MathHelper.floor((p_177414_2_.minY - 2.0D) / 16.0D);
      int j = MathHelper.floor((p_177414_2_.maxY + 2.0D) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         if (!this.entityLists[k].isEmpty()) {
            for(Entity entity : this.entityLists[k]) {
               if (entity.getEntityBoundingBox().intersects(p_177414_2_) && entity != p_177414_1_) {
                  if (p_177414_4_ == null || p_177414_4_.test(entity)) {
                     p_177414_3_.add(entity);
                  }

                  Entity[] aentity = entity.getParts();
                  if (aentity != null) {
                     for(Entity entity1 : aentity) {
                        if (entity1 != p_177414_1_ && entity1.getEntityBoundingBox().intersects(p_177414_2_) && (p_177414_4_ == null || p_177414_4_.test(entity1))) {
                           p_177414_3_.add(entity1);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> p_177430_1_, AxisAlignedBB p_177430_2_, List<T> p_177430_3_, @Nullable Predicate<? super T> p_177430_4_) {
      int i = MathHelper.floor((p_177430_2_.minY - 2.0D) / 16.0D);
      int j = MathHelper.floor((p_177430_2_.maxY + 2.0D) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         for(T t : this.entityLists[k].getByClass(p_177430_1_)) {
            if (t.getEntityBoundingBox().intersects(p_177430_2_) && (p_177430_4_ == null || p_177430_4_.test(t))) {
               p_177430_3_.add(t);
            }
         }
      }

   }

   public boolean needsSaving(boolean p_76601_1_) {
      if (p_76601_1_) {
         if (this.hasEntities && this.world.getTotalWorldTime() != this.lastSaveTime || this.dirty) {
            return true;
         }
      } else if (this.hasEntities && this.world.getTotalWorldTime() >= this.lastSaveTime + 600L) {
         return true;
      }

      return this.dirty;
   }

   public boolean isEmpty() {
      return false;
   }

   public void tick(boolean p_150804_1_) {
      if (this.isGapLightingUpdated && this.world.dimension.hasSkyLight() && !p_150804_1_) {
         this.recheckGaps(this.world.isRemote);
      }

      this.ticked = true;

      while(!this.tileEntityPosQueue.isEmpty()) {
         BlockPos blockpos = this.tileEntityPosQueue.poll();
         if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlockState(blockpos).getBlock().hasTileEntity()) {
            TileEntity tileentity = this.createNewTileEntity(blockpos);
            this.world.setTileEntity(blockpos, tileentity);
            this.world.markBlockRangeForRenderUpdate(blockpos, blockpos);
         }
      }

   }

   public boolean isPopulated() {
      return this.status.comesAfter(ChunkStatus.POSTPROCESSED);
   }

   public boolean wasTicked() {
      return this.ticked;
   }

   public ChunkPos getPos() {
      return this.field_212816_F;
   }

   public boolean isEmptyBetween(int p_76606_1_, int p_76606_2_) {
      if (p_76606_1_ < 0) {
         p_76606_1_ = 0;
      }

      if (p_76606_2_ >= 256) {
         p_76606_2_ = 255;
      }

      for(int i = p_76606_1_; i <= p_76606_2_; i += 16) {
         ChunkSection chunksection = this.sections[i >> 4];
         if (chunksection != EMPTY_SECTION && !chunksection.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setSections(ChunkSection[] p_76602_1_) {
      if (this.sections.length != p_76602_1_.length) {
         LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_76602_1_.length, this.sections.length);
      } else {
         System.arraycopy(p_76602_1_, 0, this.sections, 0, this.sections.length);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186033_1_, int p_186033_2_, boolean p_186033_3_) {
      if (p_186033_3_) {
         this.tileEntities.clear();
      } else {
         Iterator<BlockPos> iterator = this.tileEntities.keySet().iterator();

         while(iterator.hasNext()) {
            BlockPos blockpos = iterator.next();
            int i = blockpos.getY() >> 4;
            if ((p_186033_2_ & 1 << i) != 0) {
               iterator.remove();
            }
         }
      }

      boolean flag = this.world.dimension.hasSkyLight();

      for(int j = 0; j < this.sections.length; ++j) {
         ChunkSection chunksection = this.sections[j];
         if ((p_186033_2_ & 1 << j) == 0) {
            if (p_186033_3_ && chunksection != EMPTY_SECTION) {
               this.sections[j] = EMPTY_SECTION;
            }
         } else {
            if (chunksection == EMPTY_SECTION) {
               chunksection = new ChunkSection(j << 4, flag);
               this.sections[j] = chunksection;
            }

            chunksection.getData().read(p_186033_1_);
            p_186033_1_.readBytes(chunksection.getBlockLight().getData());
            if (flag) {
               p_186033_1_.readBytes(chunksection.getSkyLight().getData());
            }
         }
      }

      if (p_186033_3_) {
         for(int k = 0; k < this.blockBiomeArray.length; ++k) {
            this.blockBiomeArray[k] = IRegistry.field_212624_m.func_148754_a(p_186033_1_.readInt());
         }
      }

      for(int l = 0; l < this.sections.length; ++l) {
         if (this.sections[l] != EMPTY_SECTION && (p_186033_2_ & 1 << l) != 0) {
            this.sections[l].recalculateRefCounts();
         }
      }

      this.generateHeightMap();

      for(TileEntity tileentity : this.tileEntities.values()) {
         tileentity.updateContainingBlockInfo();
      }

   }

   public Biome getBiome(BlockPos p_201600_1_) {
      int i = p_201600_1_.getX() & 15;
      int j = p_201600_1_.getZ() & 15;
      return this.blockBiomeArray[j << 4 | i];
   }

   public Biome[] getBiomes() {
      return this.blockBiomeArray;
   }

   @OnlyIn(Dist.CLIENT)
   public void resetRelightChecks() {
      this.queuedLightChecks = 0;
   }

   public void enqueueRelightChecks() {
      if (this.queuedLightChecks < 4096) {
         BlockPos blockpos = new BlockPos(this.x << 4, 0, this.z << 4);

         for(int i = 0; i < 8; ++i) {
            if (this.queuedLightChecks >= 4096) {
               return;
            }

            int j = this.queuedLightChecks % 16;
            int k = this.queuedLightChecks / 16 % 16;
            int l = this.queuedLightChecks / 256;
            ++this.queuedLightChecks;

            for(int i1 = 0; i1 < 16; ++i1) {
               BlockPos blockpos1 = blockpos.add(k, (j << 4) + i1, l);
               boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;
               if (this.sections[j] == EMPTY_SECTION && flag || this.sections[j] != EMPTY_SECTION && this.sections[j].get(k, i1, l).isAir()) {
                  for(EnumFacing enumfacing : EnumFacing.values()) {
                     BlockPos blockpos2 = blockpos1.offset(enumfacing);
                     if (this.world.getBlockState(blockpos2).getLightValue() > 0) {
                        this.world.checkLight(blockpos2);
                     }
                  }

                  this.world.checkLight(blockpos1);
               }
            }
         }

      }
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   @OnlyIn(Dist.CLIENT)
   public void markLoaded(boolean p_177417_1_) {
      this.loaded = p_177417_1_;
   }

   public World getWorld() {
      return this.world;
   }

   public Set<Heightmap.Type> getHeightmaps() {
      return this.heightMap.keySet();
   }

   public Heightmap getHeightmap(Heightmap.Type p_201608_1_) {
      return this.heightMap.get(p_201608_1_);
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.tileEntities;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public NBTTagCompound getDeferredTileEntity(BlockPos p_201579_1_) {
      return this.deferredTileEntities.get(p_201579_1_);
   }

   public ITickList<Block> getBlocksToBeTicked() {
      return this.blocksToBeTicked;
   }

   public ITickList<Fluid> func_212247_j() {
      return this.fluidsToBeTicked;
   }

   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      throw new RuntimeException("Not yet implemented");
   }

   public void setModified(boolean p_177427_1_) {
      this.dirty = p_177427_1_;
   }

   public void setHasEntities(boolean p_177409_1_) {
      this.hasEntities = p_177409_1_;
   }

   public void setLastSaveTime(long p_177432_1_) {
      this.lastSaveTime = p_177432_1_;
   }

   @Nullable
   public StructureStart getStructureStart(String p_201585_1_) {
      return this.structureStarts.get(p_201585_1_);
   }

   public void putStructureStart(String p_201584_1_, StructureStart p_201584_2_) {
      this.structureStarts.put(p_201584_1_, p_201584_2_);
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.structureStarts;
   }

   public void setStructureStarts(Map<String, StructureStart> p_201612_1_) {
      this.structureStarts.clear();
      this.structureStarts.putAll(p_201612_1_);
   }

   @Nullable
   public LongSet getStructureReferences(String p_201578_1_) {
      return this.structureReferences.computeIfAbsent(p_201578_1_, (p_201603_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String p_201583_1_, long p_201583_2_) {
      this.structureReferences.computeIfAbsent(p_201583_1_, (p_201598_0_) -> {
         return new LongOpenHashSet();
      }).add(p_201583_2_);
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.structureReferences;
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
      this.structureReferences.clear();
      this.structureReferences.putAll(p_201606_1_);
   }

   public int getLowestHeight() {
      return this.heightMapMinimum;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long p_177415_1_) {
      this.inhabitedTime = p_177415_1_;
   }

   public void postProcess() {
      if (!this.status.comesAfter(ChunkStatus.POSTPROCESSED) && this.neighborCount == 8) {
         ChunkPos chunkpos = this.getPos();

         for(int i = 0; i < this.packedBlockPositions.length; ++i) {
            if (this.packedBlockPositions[i] != null) {
               for(Short oshort : this.packedBlockPositions[i]) {
                  BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, chunkpos);
                  IBlockState iblockstate = this.world.getBlockState(blockpos);
                  IBlockState iblockstate1 = Block.getValidBlockForPosition(iblockstate, this.world, blockpos);
                  this.world.setBlockState(blockpos, iblockstate1, 20);
               }

               this.packedBlockPositions[i].clear();
            }
         }

         if (this.blocksToBeTicked instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList<Block>)this.blocksToBeTicked).postProcess(this.world.getPendingBlockTicks(), (p_205323_1_) -> {
               return this.world.getBlockState(p_205323_1_).getBlock();
            });
         }

         if (this.fluidsToBeTicked instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList<Fluid>)this.fluidsToBeTicked).postProcess(this.world.getPendingFluidTicks(), (p_205324_1_) -> {
               return this.world.getFluidState(p_205324_1_).getFluid();
            });
         }

         for(BlockPos blockpos1 : new HashSet<>(this.deferredTileEntities.keySet())) {
            this.getTileEntity(blockpos1);
         }

         this.deferredTileEntities.clear();
         this.setStatus(ChunkStatus.POSTPROCESSED);
         this.upgradeData.postProcessChunk(this);
      }
   }

   @Nullable
   private TileEntity func_212815_a(BlockPos p_212815_1_, NBTTagCompound p_212815_2_) {
      TileEntity tileentity;
      if ("DUMMY".equals(p_212815_2_.getString("id"))) {
         Block block = this.getBlockState(p_212815_1_).getBlock();
         if (block instanceof ITileEntityProvider) {
            tileentity = ((ITileEntityProvider)block).createNewTileEntity(this.world);
         } else {
            tileentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", p_212815_1_, this.getBlockState(p_212815_1_));
         }
      } else {
         tileentity = TileEntity.create(p_212815_2_);
      }

      if (tileentity != null) {
         tileentity.setPos(p_212815_1_);
         this.addTileEntity(tileentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(p_212815_1_), p_212815_1_);
      }

      return tileentity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPackedPositions() {
      return this.packedBlockPositions;
   }

   public void addPackedPos(short p_201610_1_, int p_201610_2_) {
      ChunkPrimer.getOrCreate(this.packedBlockPositions, p_201610_2_).add(p_201610_1_);
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus p_201574_1_) {
      this.status = p_201574_1_;
   }

   public void setStatus(String p_201613_1_) {
      this.setStatus(ChunkStatus.getByName(p_201613_1_));
   }

   public void neighborAdded() {
      ++this.neighborCount;
      if (this.neighborCount > 8) {
         throw new RuntimeException("Error while adding chunk to cache. Too many neighbors");
      } else {
         if (this.areAllNeighborsLoaded()) {
            ((IThreadListener)this.world).addScheduledTask(this::postProcess);
         }

      }
   }

   public void neighborRemoved() {
      --this.neighborCount;
      if (this.neighborCount < 0) {
         throw new RuntimeException("Error while removing chunk from cache. Not enough neighbors");
      }
   }

   public boolean areAllNeighborsLoaded() {
      return this.neighborCount == 8;
   }

   public enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK
   }
}
