package net.minecraft.world;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements IEntityReader, IWorld, IWorldReader, AutoCloseable {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final EnumFacing[] FACING_VALUES = EnumFacing.values();
   private int seaLevel = 63;
   public final List<Entity> loadedEntityList = Lists.newArrayList();
   protected final List<Entity> unloadedEntityList = Lists.newArrayList();
   public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
   public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
   private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
   private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
   public final List<EntityPlayer> playerEntities = Lists.newArrayList();
   public final List<Entity> weatherEffects = Lists.newArrayList();
   protected final IntHashMap<Entity> entitiesById = new IntHashMap<>();
   private final long cloudColour = 16777215L;
   private int skylightSubtracted;
   protected int updateLCG = (new Random()).nextInt();
   protected final int DIST_HASH_MAGIC = 1013904223;
   public float prevRainingStrength;
   public float rainingStrength;
   public float prevThunderingStrength;
   public float thunderingStrength;
   private int lastLightningBolt;
   public final Random rand = new Random();
   public final Dimension dimension;
   protected PathWorldListener pathListener = new PathWorldListener();
   protected List<IWorldEventListener> eventListeners = Lists.newArrayList(this.pathListener);
   protected IChunkProvider chunkProvider;
   protected final ISaveHandler saveHandler;
   protected WorldInfo worldInfo;
   @Nullable
   private final WorldSavedDataStorage mapStorage;
   public VillageCollection villageCollection;
   public final Profiler profiler;
   public final boolean isRemote;
   protected boolean spawnHostileMobs = true;
   protected boolean spawnPeacefulMobs = true;
   private boolean processingLoadedTiles;
   private final WorldBorder worldBorder;
   int[] lightUpdateBlockList = new int['\u8000'];

   protected World(ISaveHandler p_i49813_1_, @Nullable WorldSavedDataStorage p_i49813_2_, WorldInfo p_i49813_3_, Dimension p_i49813_4_, Profiler p_i49813_5_, boolean p_i49813_6_) {
      this.saveHandler = p_i49813_1_;
      this.mapStorage = p_i49813_2_;
      this.profiler = p_i49813_5_;
      this.worldInfo = p_i49813_3_;
      this.dimension = p_i49813_4_;
      this.isRemote = p_i49813_6_;
      this.worldBorder = p_i49813_4_.createWorldBorder();
   }

   public Biome getBiome(BlockPos p_180494_1_) {
      if (this.isBlockLoaded(p_180494_1_)) {
         Chunk chunk = this.getChunk(p_180494_1_);

         try {
            return chunk.getBiome(p_180494_1_);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting biome");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Coordinates of biome request");
            crashreportcategory.addDetail("Location", () -> {
               return CrashReportCategory.getCoordinateInfo(p_180494_1_);
            });
            throw new ReportedException(crashreport);
         }
      } else {
         return this.chunkProvider.getChunkGenerator().getBiomeProvider().getBiome(p_180494_1_, Biomes.PLAINS);
      }
   }

   protected abstract IChunkProvider createChunkProvider();

   public void initialize(WorldSettings p_72963_1_) {
      this.worldInfo.setServerInitialized(true);
   }

   public boolean isRemote() {
      return this.isRemote;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public IBlockState getGroundAboveSeaLevel(BlockPos p_184141_1_) {
      BlockPos blockpos;
      for(blockpos = new BlockPos(p_184141_1_.getX(), this.getSeaLevel(), p_184141_1_.getZ()); !this.isAirBlock(blockpos.up()); blockpos = blockpos.up()) {
         ;
      }

      return this.getBlockState(blockpos);
   }

   public static boolean isValid(BlockPos p_175701_0_) {
      return !isOutsideBuildHeight(p_175701_0_) && p_175701_0_.getX() >= -30000000 && p_175701_0_.getZ() >= -30000000 && p_175701_0_.getX() < 30000000 && p_175701_0_.getZ() < 30000000;
   }

   public static boolean isOutsideBuildHeight(BlockPos p_189509_0_) {
      return p_189509_0_.getY() < 0 || p_189509_0_.getY() >= 256;
   }

   public boolean isAirBlock(BlockPos p_175623_1_) {
      return this.getBlockState(p_175623_1_).isAir();
   }

   public Chunk getChunk(BlockPos p_175726_1_) {
      return this.getChunk(p_175726_1_.getX() >> 4, p_175726_1_.getZ() >> 4);
   }

   public Chunk getChunk(int p_72964_1_, int p_72964_2_) {
      Chunk chunk = this.chunkProvider.func_186025_d(p_72964_1_, p_72964_2_, true, true);
      if (chunk == null) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return chunk;
      }
   }

   public boolean setBlockState(BlockPos p_180501_1_, IBlockState p_180501_2_, int p_180501_3_) {
      if (isOutsideBuildHeight(p_180501_1_)) {
         return false;
      } else if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         Chunk chunk = this.getChunk(p_180501_1_);
         Block block = p_180501_2_.getBlock();
         IBlockState iblockstate = chunk.setBlockState(p_180501_1_, p_180501_2_, (p_180501_3_ & 64) != 0);
         if (iblockstate == null) {
            return false;
         } else {
            IBlockState iblockstate1 = this.getBlockState(p_180501_1_);
            if (iblockstate1.getOpacity(this, p_180501_1_) != iblockstate.getOpacity(this, p_180501_1_) || iblockstate1.getLightValue() != iblockstate.getLightValue()) {
               this.profiler.startSection("checkLight");
               this.checkLight(p_180501_1_);
               this.profiler.endSection();
            }

            if (iblockstate1 == p_180501_2_) {
               if (iblockstate != iblockstate1) {
                  this.markBlockRangeForRenderUpdate(p_180501_1_, p_180501_1_);
               }

               if ((p_180501_3_ & 2) != 0 && (!this.isRemote || (p_180501_3_ & 4) == 0) && chunk.isPopulated()) {
                  this.notifyBlockUpdate(p_180501_1_, iblockstate, p_180501_2_, p_180501_3_);
               }

               if (!this.isRemote && (p_180501_3_ & 1) != 0) {
                  this.notifyNeighbors(p_180501_1_, iblockstate.getBlock());
                  if (p_180501_2_.hasComparatorInputOverride()) {
                     this.updateComparatorOutputLevel(p_180501_1_, block);
                  }
               }

               if ((p_180501_3_ & 16) == 0) {
                  int i = p_180501_3_ & -2;
                  iblockstate.updateDiagonalNeighbors(this, p_180501_1_, i);
                  p_180501_2_.updateNeighbors(this, p_180501_1_, i);
                  p_180501_2_.updateDiagonalNeighbors(this, p_180501_1_, i);
               }
            }

            return true;
         }
      }
   }

   public boolean removeBlock(BlockPos p_175698_1_) {
      IFluidState ifluidstate = this.getFluidState(p_175698_1_);
      return this.setBlockState(p_175698_1_, ifluidstate.getBlockState(), 3);
   }

   public boolean destroyBlock(BlockPos p_175655_1_, boolean p_175655_2_) {
      IBlockState iblockstate = this.getBlockState(p_175655_1_);
      if (iblockstate.isAir()) {
         return false;
      } else {
         IFluidState ifluidstate = this.getFluidState(p_175655_1_);
         this.playEvent(2001, p_175655_1_, Block.getStateId(iblockstate));
         if (p_175655_2_) {
            iblockstate.dropBlockAsItem(this, p_175655_1_, 0);
         }

         return this.setBlockState(p_175655_1_, ifluidstate.getBlockState(), 3);
      }
   }

   public boolean setBlockState(BlockPos p_175656_1_, IBlockState p_175656_2_) {
      return this.setBlockState(p_175656_1_, p_175656_2_, 3);
   }

   public void notifyBlockUpdate(BlockPos p_184138_1_, IBlockState p_184138_2_, IBlockState p_184138_3_, int p_184138_4_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).notifyBlockUpdate(this, p_184138_1_, p_184138_2_, p_184138_3_, p_184138_4_);
      }

   }

   public void notifyNeighbors(BlockPos p_195592_1_, Block p_195592_2_) {
      if (this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.notifyNeighborsOfStateChange(p_195592_1_, p_195592_2_);
      }

   }

   public void markBlocksDirtyVertical(int p_72975_1_, int p_72975_2_, int p_72975_3_, int p_72975_4_) {
      if (p_72975_3_ > p_72975_4_) {
         int i = p_72975_4_;
         p_72975_4_ = p_72975_3_;
         p_72975_3_ = i;
      }

      if (this.dimension.hasSkyLight()) {
         for(int j = p_72975_3_; j <= p_72975_4_; ++j) {
            this.checkLightFor(EnumLightType.SKY, new BlockPos(p_72975_1_, j, p_72975_2_));
         }
      }

      this.markBlockRangeForRenderUpdate(p_72975_1_, p_72975_3_, p_72975_2_, p_72975_1_, p_72975_4_, p_72975_2_);
   }

   public void markBlockRangeForRenderUpdate(BlockPos p_175704_1_, BlockPos p_175704_2_) {
      this.markBlockRangeForRenderUpdate(p_175704_1_.getX(), p_175704_1_.getY(), p_175704_1_.getZ(), p_175704_2_.getX(), p_175704_2_.getY(), p_175704_2_.getZ());
   }

   public void markBlockRangeForRenderUpdate(int p_147458_1_, int p_147458_2_, int p_147458_3_, int p_147458_4_, int p_147458_5_, int p_147458_6_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).markBlockRangeForRenderUpdate(p_147458_1_, p_147458_2_, p_147458_3_, p_147458_4_, p_147458_5_, p_147458_6_);
      }

   }

   public void notifyNeighborsOfStateChange(BlockPos p_195593_1_, Block p_195593_2_) {
      this.neighborChanged(p_195593_1_.west(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.east(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.down(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.up(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.north(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.south(), p_195593_2_, p_195593_1_);
   }

   public void notifyNeighborsOfStateExcept(BlockPos p_175695_1_, Block p_175695_2_, EnumFacing p_175695_3_) {
      if (p_175695_3_ != EnumFacing.WEST) {
         this.neighborChanged(p_175695_1_.west(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != EnumFacing.EAST) {
         this.neighborChanged(p_175695_1_.east(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != EnumFacing.DOWN) {
         this.neighborChanged(p_175695_1_.down(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != EnumFacing.UP) {
         this.neighborChanged(p_175695_1_.up(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != EnumFacing.NORTH) {
         this.neighborChanged(p_175695_1_.north(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != EnumFacing.SOUTH) {
         this.neighborChanged(p_175695_1_.south(), p_175695_2_, p_175695_1_);
      }

   }

   public void neighborChanged(BlockPos p_190524_1_, Block p_190524_2_, BlockPos p_190524_3_) {
      if (!this.isRemote) {
         IBlockState iblockstate = this.getBlockState(p_190524_1_);

         try {
            iblockstate.neighborChanged(this, p_190524_1_, p_190524_2_, p_190524_3_);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
            crashreportcategory.addDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", IRegistry.field_212618_g.func_177774_c(p_190524_2_), p_190524_2_.getTranslationKey(), p_190524_2_.getClass().getCanonicalName());
               } catch (Throwable var2) {
                  return "ID #" + IRegistry.field_212618_g.func_177774_c(p_190524_2_);
               }
            });
            CrashReportCategory.addBlockInfo(crashreportcategory, p_190524_1_, iblockstate);
            throw new ReportedException(crashreport);
         }
      }
   }

   public boolean canSeeSky(BlockPos p_175678_1_) {
      return this.getChunk(p_175678_1_).canSeeSky(p_175678_1_);
   }

   public int getLightSubtracted(BlockPos p_201669_1_, int p_201669_2_) {
      if (p_201669_1_.getX() >= -30000000 && p_201669_1_.getZ() >= -30000000 && p_201669_1_.getX() < 30000000 && p_201669_1_.getZ() < 30000000) {
         if (p_201669_1_.getY() < 0) {
            return 0;
         } else {
            if (p_201669_1_.getY() >= 256) {
               p_201669_1_ = new BlockPos(p_201669_1_.getX(), 255, p_201669_1_.getZ());
            }

            return this.getChunk(p_201669_1_).getLightSubtracted(p_201669_1_, p_201669_2_);
         }
      } else {
         return 15;
      }
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      int i;
      if (p_201676_2_ >= -30000000 && p_201676_3_ >= -30000000 && p_201676_2_ < 30000000 && p_201676_3_ < 30000000) {
         if (this.isChunkLoaded(p_201676_2_ >> 4, p_201676_3_ >> 4, true)) {
            i = this.getChunk(p_201676_2_ >> 4, p_201676_3_ >> 4).getTopBlockY(p_201676_1_, p_201676_2_ & 15, p_201676_3_ & 15) + 1;
         } else {
            i = 0;
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return i;
   }

   @Deprecated
   public int getChunksLowestHorizon(int p_82734_1_, int p_82734_2_) {
      if (p_82734_1_ >= -30000000 && p_82734_2_ >= -30000000 && p_82734_1_ < 30000000 && p_82734_2_ < 30000000) {
         if (!this.isChunkLoaded(p_82734_1_ >> 4, p_82734_2_ >> 4, true)) {
            return 0;
         } else {
            Chunk chunk = this.getChunk(p_82734_1_ >> 4, p_82734_2_ >> 4);
            return chunk.getLowestHeight();
         }
      } else {
         return this.getSeaLevel() + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getLightFromNeighborsFor(EnumLightType p_175705_1_, BlockPos p_175705_2_) {
      if (!this.dimension.hasSkyLight() && p_175705_1_ == EnumLightType.SKY) {
         return 0;
      } else {
         if (p_175705_2_.getY() < 0) {
            p_175705_2_ = new BlockPos(p_175705_2_.getX(), 0, p_175705_2_.getZ());
         }

         if (!isValid(p_175705_2_)) {
            return p_175705_1_.defaultLightValue;
         } else if (!this.isBlockLoaded(p_175705_2_)) {
            return p_175705_1_.defaultLightValue;
         } else if (this.getBlockState(p_175705_2_).useNeighborBrightness(this, p_175705_2_)) {
            int i = this.getLightFor(p_175705_1_, p_175705_2_.up());
            int j = this.getLightFor(p_175705_1_, p_175705_2_.east());
            int k = this.getLightFor(p_175705_1_, p_175705_2_.west());
            int l = this.getLightFor(p_175705_1_, p_175705_2_.south());
            int i1 = this.getLightFor(p_175705_1_, p_175705_2_.north());
            if (j > i) {
               i = j;
            }

            if (k > i) {
               i = k;
            }

            if (l > i) {
               i = l;
            }

            if (i1 > i) {
               i = i1;
            }

            return i;
         } else {
            return this.getChunk(p_175705_2_).getLightFor(p_175705_1_, p_175705_2_);
         }
      }
   }

   public int getLightFor(EnumLightType p_175642_1_, BlockPos p_175642_2_) {
      if (p_175642_2_.getY() < 0) {
         p_175642_2_ = new BlockPos(p_175642_2_.getX(), 0, p_175642_2_.getZ());
      }

      if (!isValid(p_175642_2_)) {
         return p_175642_1_.defaultLightValue;
      } else {
         return !this.isBlockLoaded(p_175642_2_) ? p_175642_1_.defaultLightValue : this.getChunk(p_175642_2_).getLightFor(p_175642_1_, p_175642_2_);
      }
   }

   public void setLightFor(EnumLightType p_175653_1_, BlockPos p_175653_2_, int p_175653_3_) {
      if (isValid(p_175653_2_)) {
         if (this.isBlockLoaded(p_175653_2_)) {
            this.getChunk(p_175653_2_).setLightFor(p_175653_1_, p_175653_2_, p_175653_3_);
            this.notifyLightSet(p_175653_2_);
         }
      }
   }

   public void notifyLightSet(BlockPos p_175679_1_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).notifyLightSet(p_175679_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getCombinedLight(BlockPos p_175626_1_, int p_175626_2_) {
      int i = this.getLightFromNeighborsFor(EnumLightType.SKY, p_175626_1_);
      int j = this.getLightFromNeighborsFor(EnumLightType.BLOCK, p_175626_1_);
      if (j < p_175626_2_) {
         j = p_175626_2_;
      }

      return i << 20 | j << 4;
   }

   public IBlockState getBlockState(BlockPos p_180495_1_) {
      if (isOutsideBuildHeight(p_180495_1_)) {
         return Blocks.VOID_AIR.getDefaultState();
      } else {
         Chunk chunk = this.getChunk(p_180495_1_);
         return chunk.getBlockState(p_180495_1_);
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      if (isOutsideBuildHeight(p_204610_1_)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         Chunk chunk = this.getChunk(p_204610_1_);
         return chunk.getFluidState(p_204610_1_);
      }
   }

   public boolean isDaytime() {
      return this.skylightSubtracted < 4;
   }

   @Nullable
   public RayTraceResult rayTraceBlocks(Vec3d p_72933_1_, Vec3d p_72933_2_) {
      return this.rayTraceBlocks(p_72933_1_, p_72933_2_, RayTraceFluidMode.NEVER, false, false);
   }

   @Nullable
   public RayTraceResult rayTraceBlocks(Vec3d p_200260_1_, Vec3d p_200260_2_, RayTraceFluidMode p_200260_3_) {
      return this.rayTraceBlocks(p_200260_1_, p_200260_2_, p_200260_3_, false, false);
   }

   @Nullable
   public RayTraceResult rayTraceBlocks(Vec3d p_200259_1_, Vec3d p_200259_2_, RayTraceFluidMode p_200259_3_, boolean p_200259_4_, boolean p_200259_5_) {
      double d0 = p_200259_1_.x;
      double d1 = p_200259_1_.y;
      double d2 = p_200259_1_.z;
      if (!Double.isNaN(d0) && !Double.isNaN(d1) && !Double.isNaN(d2)) {
         if (!Double.isNaN(p_200259_2_.x) && !Double.isNaN(p_200259_2_.y) && !Double.isNaN(p_200259_2_.z)) {
            int i = MathHelper.floor(p_200259_2_.x);
            int j = MathHelper.floor(p_200259_2_.y);
            int k = MathHelper.floor(p_200259_2_.z);
            int l = MathHelper.floor(d0);
            int i1 = MathHelper.floor(d1);
            int j1 = MathHelper.floor(d2);
            BlockPos blockpos = new BlockPos(l, i1, j1);
            IBlockState iblockstate = this.getBlockState(blockpos);
            IFluidState ifluidstate = this.getFluidState(blockpos);
            if (!p_200259_4_ || !iblockstate.getCollisionShape(this, blockpos).isEmpty()) {
               boolean flag = iblockstate.getBlock().isCollidable(iblockstate);
               boolean flag1 = p_200259_3_.predicate.test(ifluidstate);
               if (flag || flag1) {
                  RayTraceResult raytraceresult = null;
                  if (flag) {
                     raytraceresult = Block.collisionRayTrace(iblockstate, this, blockpos, p_200259_1_, p_200259_2_);
                  }

                  if (raytraceresult == null && flag1) {
                     raytraceresult = VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)ifluidstate.getHeight(), 1.0D).func_212433_a(p_200259_1_, p_200259_2_, blockpos);
                  }

                  if (raytraceresult != null) {
                     return raytraceresult;
                  }
               }
            }

            RayTraceResult raytraceresult2 = null;
            int k1 = 200;

            while(k1-- >= 0) {
               if (Double.isNaN(d0) || Double.isNaN(d1) || Double.isNaN(d2)) {
                  return null;
               }

               if (l == i && i1 == j && j1 == k) {
                  return p_200259_5_ ? raytraceresult2 : null;
               }

               boolean flag4 = true;
               boolean flag5 = true;
               boolean flag6 = true;
               double d3 = 999.0D;
               double d4 = 999.0D;
               double d5 = 999.0D;
               if (i > l) {
                  d3 = (double)l + 1.0D;
               } else if (i < l) {
                  d3 = (double)l + 0.0D;
               } else {
                  flag4 = false;
               }

               if (j > i1) {
                  d4 = (double)i1 + 1.0D;
               } else if (j < i1) {
                  d4 = (double)i1 + 0.0D;
               } else {
                  flag5 = false;
               }

               if (k > j1) {
                  d5 = (double)j1 + 1.0D;
               } else if (k < j1) {
                  d5 = (double)j1 + 0.0D;
               } else {
                  flag6 = false;
               }

               double d6 = 999.0D;
               double d7 = 999.0D;
               double d8 = 999.0D;
               double d9 = p_200259_2_.x - d0;
               double d10 = p_200259_2_.y - d1;
               double d11 = p_200259_2_.z - d2;
               if (flag4) {
                  d6 = (d3 - d0) / d9;
               }

               if (flag5) {
                  d7 = (d4 - d1) / d10;
               }

               if (flag6) {
                  d8 = (d5 - d2) / d11;
               }

               if (d6 == -0.0D) {
                  d6 = -1.0E-4D;
               }

               if (d7 == -0.0D) {
                  d7 = -1.0E-4D;
               }

               if (d8 == -0.0D) {
                  d8 = -1.0E-4D;
               }

               EnumFacing enumfacing;
               if (d6 < d7 && d6 < d8) {
                  enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                  d0 = d3;
                  d1 += d10 * d6;
                  d2 += d11 * d6;
               } else if (d7 < d8) {
                  enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                  d0 += d9 * d7;
                  d1 = d4;
                  d2 += d11 * d7;
               } else {
                  enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  d0 += d9 * d8;
                  d1 += d10 * d8;
                  d2 = d5;
               }

               l = MathHelper.floor(d0) - (enumfacing == EnumFacing.EAST ? 1 : 0);
               i1 = MathHelper.floor(d1) - (enumfacing == EnumFacing.UP ? 1 : 0);
               j1 = MathHelper.floor(d2) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
               blockpos = new BlockPos(l, i1, j1);
               IBlockState iblockstate1 = this.getBlockState(blockpos);
               IFluidState ifluidstate1 = this.getFluidState(blockpos);
               if (!p_200259_4_ || iblockstate1.getMaterial() == Material.PORTAL || !iblockstate1.getCollisionShape(this, blockpos).isEmpty()) {
                  boolean flag2 = iblockstate1.getBlock().isCollidable(iblockstate1);
                  boolean flag3 = p_200259_3_.predicate.test(ifluidstate1);
                  if (!flag2 && !flag3) {
                     raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(d0, d1, d2), enumfacing, blockpos);
                  } else {
                     RayTraceResult raytraceresult1 = null;
                     if (flag2) {
                        raytraceresult1 = Block.collisionRayTrace(iblockstate1, this, blockpos, p_200259_1_, p_200259_2_);
                     }

                     if (raytraceresult1 == null && flag3) {
                        raytraceresult1 = VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)ifluidstate1.getHeight(), 1.0D).func_212433_a(p_200259_1_, p_200259_2_, blockpos);
                     }

                     if (raytraceresult1 != null) {
                        return raytraceresult1;
                     }
                  }
               }
            }

            return p_200259_5_ ? raytraceresult2 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void playSound(@Nullable EntityPlayer p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
      this.playSound(p_184133_1_, (double)p_184133_2_.getX() + 0.5D, (double)p_184133_2_.getY() + 0.5D, (double)p_184133_2_.getZ() + 0.5D, p_184133_3_, p_184133_4_, p_184133_5_, p_184133_6_);
   }

   public void playSound(@Nullable EntityPlayer p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).playSoundToAllNearExcept(p_184148_1_, p_184148_8_, p_184148_9_, p_184148_2_, p_184148_4_, p_184148_6_, p_184148_10_, p_184148_11_);
      }

   }

   public void playSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
   }

   public void playRecord(BlockPos p_184149_1_, @Nullable SoundEvent p_184149_2_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).playRecord(p_184149_2_, p_184149_1_);
      }

   }

   public void spawnParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).addParticle(p_195594_1_, p_195594_1_.getType().getAlwaysShow(), p_195594_2_, p_195594_4_, p_195594_6_, p_195594_8_, p_195594_10_, p_195594_12_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double p_195590_3_, double p_195590_5_, double p_195590_7_, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).addParticle(p_195590_1_, p_195590_1_.getType().getAlwaysShow() || p_195590_2_, p_195590_3_, p_195590_5_, p_195590_7_, p_195590_9_, p_195590_11_, p_195590_13_);
      }

   }

   public void addOptionalParticle(IParticleData p_195589_1_, double p_195589_2_, double p_195589_4_, double p_195589_6_, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).addParticle(p_195589_1_, false, true, p_195589_2_, p_195589_4_, p_195589_6_, p_195589_8_, p_195589_10_, p_195589_12_);
      }

   }

   public boolean addWeatherEffect(Entity p_72942_1_) {
      this.weatherEffects.add(p_72942_1_);
      return true;
   }

   public boolean spawnEntity(Entity p_72838_1_) {
      int i = MathHelper.floor(p_72838_1_.posX / 16.0D);
      int j = MathHelper.floor(p_72838_1_.posZ / 16.0D);
      boolean flag = p_72838_1_.forceSpawn;
      if (p_72838_1_ instanceof EntityPlayer) {
         flag = true;
      }

      if (!flag && !this.isChunkLoaded(i, j, false)) {
         return false;
      } else {
         if (p_72838_1_ instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)p_72838_1_;
            this.playerEntities.add(entityplayer);
            this.updateAllPlayersSleepingFlag();
         }

         this.getChunk(i, j).addEntity(p_72838_1_);
         this.loadedEntityList.add(p_72838_1_);
         this.onEntityAdded(p_72838_1_);
         return true;
      }
   }

   public void onEntityAdded(Entity p_72923_1_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).onEntityAdded(p_72923_1_);
      }

   }

   public void onEntityRemoved(Entity p_72847_1_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).onEntityRemoved(p_72847_1_);
      }

   }

   public void removeEntity(Entity p_72900_1_) {
      if (p_72900_1_.isBeingRidden()) {
         p_72900_1_.removePassengers();
      }

      if (p_72900_1_.isRiding()) {
         p_72900_1_.dismountRidingEntity();
      }

      p_72900_1_.setDead();
      if (p_72900_1_ instanceof EntityPlayer) {
         this.playerEntities.remove(p_72900_1_);
         this.updateAllPlayersSleepingFlag();
         this.onEntityRemoved(p_72900_1_);
      }

   }

   public void removeEntityDangerously(Entity p_72973_1_) {
      p_72973_1_.setDropItemsWhenDead(false);
      p_72973_1_.setDead();
      if (p_72973_1_ instanceof EntityPlayer) {
         this.playerEntities.remove(p_72973_1_);
         this.updateAllPlayersSleepingFlag();
      }

      int i = p_72973_1_.chunkCoordX;
      int j = p_72973_1_.chunkCoordZ;
      if (p_72973_1_.addedToChunk && this.isChunkLoaded(i, j, true)) {
         this.getChunk(i, j).removeEntity(p_72973_1_);
      }

      this.loadedEntityList.remove(p_72973_1_);
      this.onEntityRemoved(p_72973_1_);
   }

   public void addEventListener(IWorldEventListener p_72954_1_) {
      this.eventListeners.add(p_72954_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void removeEventListener(IWorldEventListener p_72848_1_) {
      this.eventListeners.remove(p_72848_1_);
   }

   public int calculateSkylightSubtracted(float p_72967_1_) {
      float f = this.getCelestialAngle(p_72967_1_);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(p_72967_1_) * 5.0F) / 16.0D));
      f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(p_72967_1_) * 5.0F) / 16.0D));
      f1 = 1.0F - f1;
      return (int)(f1 * 11.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getSunBrightness(float p_72971_1_) {
      float f = this.getCelestialAngle(p_72971_1_);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(p_72971_1_) * 5.0F) / 16.0D));
      f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(p_72971_1_) * 5.0F) / 16.0D));
      return f1 * 0.8F + 0.2F;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getSkyColor(Entity p_72833_1_, float p_72833_2_) {
      float f = this.getCelestialAngle(p_72833_2_);
      float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      int i = MathHelper.floor(p_72833_1_.posX);
      int j = MathHelper.floor(p_72833_1_.posY);
      int k = MathHelper.floor(p_72833_1_.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      Biome biome = this.getBiome(blockpos);
      float f2 = biome.getTemperature(blockpos);
      int l = biome.getSkyColorByTemp(f2);
      float f3 = (float)(l >> 16 & 255) / 255.0F;
      float f4 = (float)(l >> 8 & 255) / 255.0F;
      float f5 = (float)(l & 255) / 255.0F;
      f3 = f3 * f1;
      f4 = f4 * f1;
      f5 = f5 * f1;
      float f6 = this.getRainStrength(p_72833_2_);
      if (f6 > 0.0F) {
         float f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
         float f8 = 1.0F - f6 * 0.75F;
         f3 = f3 * f8 + f7 * (1.0F - f8);
         f4 = f4 * f8 + f7 * (1.0F - f8);
         f5 = f5 * f8 + f7 * (1.0F - f8);
      }

      float f10 = this.getThunderStrength(p_72833_2_);
      if (f10 > 0.0F) {
         float f11 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
         float f9 = 1.0F - f10 * 0.75F;
         f3 = f3 * f9 + f11 * (1.0F - f9);
         f4 = f4 * f9 + f11 * (1.0F - f9);
         f5 = f5 * f9 + f11 * (1.0F - f9);
      }

      if (this.lastLightningBolt > 0) {
         float f12 = (float)this.lastLightningBolt - p_72833_2_;
         if (f12 > 1.0F) {
            f12 = 1.0F;
         }

         f12 = f12 * 0.45F;
         f3 = f3 * (1.0F - f12) + 0.8F * f12;
         f4 = f4 * (1.0F - f12) + 0.8F * f12;
         f5 = f5 * (1.0F - f12) + 1.0F * f12;
      }

      return new Vec3d((double)f3, (double)f4, (double)f5);
   }

   public float getCelestialAngleRadians(float p_72929_1_) {
      float f = this.getCelestialAngle(p_72929_1_);
      return f * ((float)Math.PI * 2F);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getCloudColour(float p_72824_1_) {
      float f = this.getCelestialAngle(p_72824_1_);
      float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = this.getRainStrength(p_72824_1_);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.95F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      f2 = f2 * (f1 * 0.9F + 0.1F);
      f3 = f3 * (f1 * 0.9F + 0.1F);
      f4 = f4 * (f1 * 0.85F + 0.15F);
      float f9 = this.getThunderStrength(p_72824_1_);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.95F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      return new Vec3d((double)f2, (double)f3, (double)f4);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_72948_1_) {
      float f = this.getCelestialAngle(p_72948_1_);
      return this.dimension.getFogColor(f, p_72948_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getStarBrightness(float p_72880_1_) {
      float f = this.getCelestialAngle(p_72880_1_);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      return f1 * f1 * 0.5F;
   }

   public void tickEntities() {
      this.profiler.startSection("entities");
      this.profiler.startSection("global");

      for(int i = 0; i < this.weatherEffects.size(); ++i) {
         Entity entity = this.weatherEffects.get(i);

         try {
            ++entity.ticksExisted;
            entity.tick();
         } catch (Throwable throwable2) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");
            if (entity == null) {
               crashreportcategory.addCrashSection("Entity", "~~NULL~~");
            } else {
               entity.fillCrashReport(crashreportcategory);
            }

            throw new ReportedException(crashreport);
         }

         if (entity.isDead) {
            this.weatherEffects.remove(i--);
         }
      }

      this.profiler.endStartSection("remove");
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for(int k = 0; k < this.unloadedEntityList.size(); ++k) {
         Entity entity1 = this.unloadedEntityList.get(k);
         int j = entity1.chunkCoordX;
         int k1 = entity1.chunkCoordZ;
         if (entity1.addedToChunk && this.isChunkLoaded(j, k1, true)) {
            this.getChunk(j, k1).removeEntity(entity1);
         }
      }

      for(int l = 0; l < this.unloadedEntityList.size(); ++l) {
         this.onEntityRemoved(this.unloadedEntityList.get(l));
      }

      this.unloadedEntityList.clear();
      this.tickPlayers();
      this.profiler.endStartSection("regular");

      for(int i1 = 0; i1 < this.loadedEntityList.size(); ++i1) {
         Entity entity2 = this.loadedEntityList.get(i1);
         Entity entity3 = entity2.getRidingEntity();
         if (entity3 != null) {
            if (!entity3.isDead && entity3.isPassenger(entity2)) {
               continue;
            }

            entity2.dismountRidingEntity();
         }

         this.profiler.startSection("tick");
         if (!entity2.isDead && !(entity2 instanceof EntityPlayerMP)) {
            try {
               this.tickEntity(entity2);
            } catch (Throwable throwable1) {
               CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
               CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Entity being ticked");
               entity2.fillCrashReport(crashreportcategory1);
               throw new ReportedException(crashreport1);
            }
         }

         this.profiler.endSection();
         this.profiler.startSection("remove");
         if (entity2.isDead) {
            int l1 = entity2.chunkCoordX;
            int i2 = entity2.chunkCoordZ;
            if (entity2.addedToChunk && this.isChunkLoaded(l1, i2, true)) {
               this.getChunk(l1, i2).removeEntity(entity2);
            }

            this.loadedEntityList.remove(i1--);
            this.onEntityRemoved(entity2);
         }

         this.profiler.endSection();
      }

      this.profiler.endStartSection("blockEntities");
      if (!this.tileEntitiesToBeRemoved.isEmpty()) {
         this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
         this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
         this.tileEntitiesToBeRemoved.clear();
      }

      this.processingLoadedTiles = true;
      Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();

      while(iterator.hasNext()) {
         TileEntity tileentity = iterator.next();
         if (!tileentity.isInvalid() && tileentity.hasWorld()) {
            BlockPos blockpos = tileentity.getPos();
            if (this.isBlockLoaded(blockpos) && this.worldBorder.contains(blockpos)) {
               try {
                  this.profiler.startSection(() -> {
                     return String.valueOf((Object)TileEntityType.getId(tileentity.getType()));
                  });
                  ((ITickable)tileentity).tick();
                  this.profiler.endSection();
               } catch (Throwable throwable) {
                  CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                  CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Block entity being ticked");
                  tileentity.addInfoToCrashReport(crashreportcategory2);
                  throw new ReportedException(crashreport2);
               }
            }
         }

         if (tileentity.isInvalid()) {
            iterator.remove();
            this.loadedTileEntityList.remove(tileentity);
            if (this.isBlockLoaded(tileentity.getPos())) {
               this.getChunk(tileentity.getPos()).removeTileEntity(tileentity.getPos());
            }
         }
      }

      this.processingLoadedTiles = false;
      this.profiler.endStartSection("pendingBlockEntities");
      if (!this.addedTileEntityList.isEmpty()) {
         for(int j1 = 0; j1 < this.addedTileEntityList.size(); ++j1) {
            TileEntity tileentity1 = this.addedTileEntityList.get(j1);
            if (!tileentity1.isInvalid()) {
               if (!this.loadedTileEntityList.contains(tileentity1)) {
                  this.addTileEntity(tileentity1);
               }

               if (this.isBlockLoaded(tileentity1.getPos())) {
                  Chunk chunk = this.getChunk(tileentity1.getPos());
                  IBlockState iblockstate = chunk.getBlockState(tileentity1.getPos());
                  chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                  this.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
               }
            }
         }

         this.addedTileEntityList.clear();
      }

      this.profiler.endSection();
      this.profiler.endSection();
   }

   protected void tickPlayers() {
   }

   public boolean addTileEntity(TileEntity p_175700_1_) {
      boolean flag = this.loadedTileEntityList.add(p_175700_1_);
      if (flag && p_175700_1_ instanceof ITickable) {
         this.tickableTileEntities.add(p_175700_1_);
      }

      if (this.isRemote) {
         BlockPos blockpos = p_175700_1_.getPos();
         IBlockState iblockstate = this.getBlockState(blockpos);
         this.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 2);
      }

      return flag;
   }

   public void addTileEntities(Collection<TileEntity> p_147448_1_) {
      if (this.processingLoadedTiles) {
         this.addedTileEntityList.addAll(p_147448_1_);
      } else {
         for(TileEntity tileentity : p_147448_1_) {
            this.addTileEntity(tileentity);
         }
      }

   }

   public void tickEntity(Entity p_72870_1_) {
      this.updateEntityWithOptionalForce(p_72870_1_, true);
   }

   public void updateEntityWithOptionalForce(Entity p_72866_1_, boolean p_72866_2_) {
      if (!(p_72866_1_ instanceof EntityPlayer)) {
         int i = MathHelper.floor(p_72866_1_.posX);
         int j = MathHelper.floor(p_72866_1_.posZ);
         int k = 32;
         if (p_72866_2_ && !this.isAreaLoaded(i - 32, 0, j - 32, i + 32, 0, j + 32, true)) {
            return;
         }
      }

      p_72866_1_.lastTickPosX = p_72866_1_.posX;
      p_72866_1_.lastTickPosY = p_72866_1_.posY;
      p_72866_1_.lastTickPosZ = p_72866_1_.posZ;
      p_72866_1_.prevRotationYaw = p_72866_1_.rotationYaw;
      p_72866_1_.prevRotationPitch = p_72866_1_.rotationPitch;
      if (p_72866_2_ && p_72866_1_.addedToChunk) {
         ++p_72866_1_.ticksExisted;
         if (p_72866_1_.isRiding()) {
            p_72866_1_.updateRidden();
         } else {
            this.profiler.startSection(() -> {
               return IRegistry.field_212629_r.func_177774_c(p_72866_1_.getType()).toString();
            });
            p_72866_1_.tick();
            this.profiler.endSection();
         }
      }

      this.profiler.startSection("chunkCheck");
      if (Double.isNaN(p_72866_1_.posX) || Double.isInfinite(p_72866_1_.posX)) {
         p_72866_1_.posX = p_72866_1_.lastTickPosX;
      }

      if (Double.isNaN(p_72866_1_.posY) || Double.isInfinite(p_72866_1_.posY)) {
         p_72866_1_.posY = p_72866_1_.lastTickPosY;
      }

      if (Double.isNaN(p_72866_1_.posZ) || Double.isInfinite(p_72866_1_.posZ)) {
         p_72866_1_.posZ = p_72866_1_.lastTickPosZ;
      }

      if (Double.isNaN((double)p_72866_1_.rotationPitch) || Double.isInfinite((double)p_72866_1_.rotationPitch)) {
         p_72866_1_.rotationPitch = p_72866_1_.prevRotationPitch;
      }

      if (Double.isNaN((double)p_72866_1_.rotationYaw) || Double.isInfinite((double)p_72866_1_.rotationYaw)) {
         p_72866_1_.rotationYaw = p_72866_1_.prevRotationYaw;
      }

      int l = MathHelper.floor(p_72866_1_.posX / 16.0D);
      int i1 = MathHelper.floor(p_72866_1_.posY / 16.0D);
      int j1 = MathHelper.floor(p_72866_1_.posZ / 16.0D);
      if (!p_72866_1_.addedToChunk || p_72866_1_.chunkCoordX != l || p_72866_1_.chunkCoordY != i1 || p_72866_1_.chunkCoordZ != j1) {
         if (p_72866_1_.addedToChunk && this.isChunkLoaded(p_72866_1_.chunkCoordX, p_72866_1_.chunkCoordZ, true)) {
            this.getChunk(p_72866_1_.chunkCoordX, p_72866_1_.chunkCoordZ).removeEntityAtIndex(p_72866_1_, p_72866_1_.chunkCoordY);
         }

         if (!p_72866_1_.setPositionNonDirty() && !this.isChunkLoaded(l, j1, true)) {
            p_72866_1_.addedToChunk = false;
         } else {
            this.getChunk(l, j1).addEntity(p_72866_1_);
         }
      }

      this.profiler.endSection();
      if (p_72866_2_ && p_72866_1_.addedToChunk) {
         for(Entity entity : p_72866_1_.getPassengers()) {
            if (!entity.isDead && entity.getRidingEntity() == p_72866_1_) {
               this.tickEntity(entity);
            } else {
               entity.dismountRidingEntity();
            }
         }
      }

   }

   public boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      if (p_195585_2_.isEmpty()) {
         return true;
      } else {
         List<Entity> list = this.func_72839_b((Entity)null, p_195585_2_.getBoundingBox());

         for(int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!entity.isDead && entity.preventEntitySpawning && entity != p_195585_1_ && (p_195585_1_ == null || !entity.isRidingSameEntity(p_195585_1_)) && VoxelShapes.func_197879_c(p_195585_2_, VoxelShapes.func_197881_a(entity.getEntityBoundingBox()), IBooleanFunction.AND)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean checkBlockCollision(AxisAlignedBB p_72829_1_) {
      int i = MathHelper.floor(p_72829_1_.minX);
      int j = MathHelper.ceil(p_72829_1_.maxX);
      int k = MathHelper.floor(p_72829_1_.minY);
      int l = MathHelper.ceil(p_72829_1_.maxY);
      int i1 = MathHelper.floor(p_72829_1_.minZ);
      int j1 = MathHelper.ceil(p_72829_1_.maxZ);

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                  if (!iblockstate.isAir()) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean isFlammableWithin(AxisAlignedBB p_147470_1_) {
      int i = MathHelper.floor(p_147470_1_.minX);
      int j = MathHelper.ceil(p_147470_1_.maxX);
      int k = MathHelper.floor(p_147470_1_.minY);
      int l = MathHelper.ceil(p_147470_1_.maxY);
      int i1 = MathHelper.floor(p_147470_1_.minZ);
      int j1 = MathHelper.ceil(p_147470_1_.maxZ);
      if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     Block block = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2)).getBlock();
                     if (block == Blocks.FIRE || block == Blocks.LAVA) {
                        boolean flag = true;
                        return flag;
                     }
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public IBlockState findBlockstateInArea(AxisAlignedBB p_203067_1_, Block p_203067_2_) {
      int i = MathHelper.floor(p_203067_1_.minX);
      int j = MathHelper.ceil(p_203067_1_.maxX);
      int k = MathHelper.floor(p_203067_1_.minY);
      int l = MathHelper.ceil(p_203067_1_.maxY);
      int i1 = MathHelper.floor(p_203067_1_.minZ);
      int j1 = MathHelper.ceil(p_203067_1_.maxZ);
      if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                     if (iblockstate.getBlock() == p_203067_2_) {
                        IBlockState iblockstate1 = iblockstate;
                        return iblockstate1;
                     }
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public boolean isMaterialInBB(AxisAlignedBB p_72875_1_, Material p_72875_2_) {
      int i = MathHelper.floor(p_72875_1_.minX);
      int j = MathHelper.ceil(p_72875_1_.maxX);
      int k = MathHelper.floor(p_72875_1_.minY);
      int l = MathHelper.ceil(p_72875_1_.maxY);
      int i1 = MathHelper.floor(p_72875_1_.minZ);
      int j1 = MathHelper.ceil(p_72875_1_.maxZ);
      BlockMaterialMatcher blockmaterialmatcher = BlockMaterialMatcher.forMaterial(p_72875_2_);

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  if (blockmaterialmatcher.test(this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2)))) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }
         }

         return false;
      }
   }

   public Explosion createExplosion(@Nullable Entity p_72876_1_, double p_72876_2_, double p_72876_4_, double p_72876_6_, float p_72876_8_, boolean p_72876_9_) {
      return this.createExplosion(p_72876_1_, (DamageSource)null, p_72876_2_, p_72876_4_, p_72876_6_, p_72876_8_, false, p_72876_9_);
   }

   public Explosion newExplosion(@Nullable Entity p_72885_1_, double p_72885_2_, double p_72885_4_, double p_72885_6_, float p_72885_8_, boolean p_72885_9_, boolean p_72885_10_) {
      return this.createExplosion(p_72885_1_, (DamageSource)null, p_72885_2_, p_72885_4_, p_72885_6_, p_72885_8_, p_72885_9_, p_72885_10_);
   }

   public Explosion createExplosion(@Nullable Entity p_211529_1_, @Nullable DamageSource p_211529_2_, double p_211529_3_, double p_211529_5_, double p_211529_7_, float p_211529_9_, boolean p_211529_10_, boolean p_211529_11_) {
      Explosion explosion = new Explosion(this, p_211529_1_, p_211529_3_, p_211529_5_, p_211529_7_, p_211529_9_, p_211529_10_, p_211529_11_);
      if (p_211529_2_ != null) {
         explosion.setDamageSource(p_211529_2_);
      }

      explosion.doExplosionA();
      explosion.doExplosionB(true);
      return explosion;
   }

   public float getBlockDensity(Vec3d p_72842_1_, AxisAlignedBB p_72842_2_) {
      double d0 = 1.0D / ((p_72842_2_.maxX - p_72842_2_.minX) * 2.0D + 1.0D);
      double d1 = 1.0D / ((p_72842_2_.maxY - p_72842_2_.minY) * 2.0D + 1.0D);
      double d2 = 1.0D / ((p_72842_2_.maxZ - p_72842_2_.minZ) * 2.0D + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
         int i = 0;
         int j = 0;

         for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
            for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
               for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
                  double d5 = p_72842_2_.minX + (p_72842_2_.maxX - p_72842_2_.minX) * (double)f;
                  double d6 = p_72842_2_.minY + (p_72842_2_.maxY - p_72842_2_.minY) * (double)f1;
                  double d7 = p_72842_2_.minZ + (p_72842_2_.maxZ - p_72842_2_.minZ) * (double)f2;
                  if (this.rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), p_72842_1_) == null) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (float)i / (float)j;
      } else {
         return 0.0F;
      }
   }

   public boolean extinguishFire(@Nullable EntityPlayer p_175719_1_, BlockPos p_175719_2_, EnumFacing p_175719_3_) {
      p_175719_2_ = p_175719_2_.offset(p_175719_3_);
      if (this.getBlockState(p_175719_2_).getBlock() == Blocks.FIRE) {
         this.playEvent(p_175719_1_, 1009, p_175719_2_, 0);
         this.removeBlock(p_175719_2_);
         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugLoadedEntities() {
      return "All: " + this.loadedEntityList.size();
   }

   @OnlyIn(Dist.CLIENT)
   public String getProviderName() {
      return this.chunkProvider.makeString();
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      if (isOutsideBuildHeight(p_175625_1_)) {
         return null;
      } else {
         TileEntity tileentity = null;
         if (this.processingLoadedTiles) {
            tileentity = this.getPendingTileEntityAt(p_175625_1_);
         }

         if (tileentity == null) {
            tileentity = this.getChunk(p_175625_1_).getTileEntity(p_175625_1_, Chunk.EnumCreateEntityType.IMMEDIATE);
         }

         if (tileentity == null) {
            tileentity = this.getPendingTileEntityAt(p_175625_1_);
         }

         return tileentity;
      }
   }

   @Nullable
   private TileEntity getPendingTileEntityAt(BlockPos p_189508_1_) {
      for(int i = 0; i < this.addedTileEntityList.size(); ++i) {
         TileEntity tileentity = this.addedTileEntityList.get(i);
         if (!tileentity.isInvalid() && tileentity.getPos().equals(p_189508_1_)) {
            return tileentity;
         }
      }

      return null;
   }

   public void setTileEntity(BlockPos p_175690_1_, @Nullable TileEntity p_175690_2_) {
      if (!isOutsideBuildHeight(p_175690_1_)) {
         if (p_175690_2_ != null && !p_175690_2_.isInvalid()) {
            if (this.processingLoadedTiles) {
               p_175690_2_.setPos(p_175690_1_);
               Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();

               while(iterator.hasNext()) {
                  TileEntity tileentity = iterator.next();
                  if (tileentity.getPos().equals(p_175690_1_)) {
                     tileentity.invalidate();
                     iterator.remove();
                  }
               }

               this.addedTileEntityList.add(p_175690_2_);
            } else {
               this.getChunk(p_175690_1_).addTileEntity(p_175690_1_, p_175690_2_);
               this.addTileEntity(p_175690_2_);
            }
         }

      }
   }

   public void removeTileEntity(BlockPos p_175713_1_) {
      TileEntity tileentity = this.getTileEntity(p_175713_1_);
      if (tileentity != null && this.processingLoadedTiles) {
         tileentity.invalidate();
         this.addedTileEntityList.remove(tileentity);
      } else {
         if (tileentity != null) {
            this.addedTileEntityList.remove(tileentity);
            this.loadedTileEntityList.remove(tileentity);
            this.tickableTileEntities.remove(tileentity);
         }

         this.getChunk(p_175713_1_).removeTileEntity(p_175713_1_);
      }

   }

   public void markTileEntityForRemoval(TileEntity p_147457_1_) {
      this.tileEntitiesToBeRemoved.add(p_147457_1_);
   }

   public boolean isBlockFullCube(BlockPos p_175665_1_) {
      return Block.isOpaque(this.getBlockState(p_175665_1_).getCollisionShape(this, p_175665_1_));
   }

   public boolean isBlockPresent(BlockPos p_195588_1_) {
      if (isOutsideBuildHeight(p_195588_1_)) {
         return false;
      } else {
         Chunk chunk = this.chunkProvider.func_186025_d(p_195588_1_.getX() >> 4, p_195588_1_.getZ() >> 4, false, false);
         return chunk != null && !chunk.isEmpty();
      }
   }

   public boolean isTopSolid(BlockPos p_195595_1_) {
      return this.isBlockPresent(p_195595_1_) && this.getBlockState(p_195595_1_).isTopSolid();
   }

   public void calculateInitialSkylight() {
      int i = this.calculateSkylightSubtracted(1.0F);
      if (i != this.skylightSubtracted) {
         this.skylightSubtracted = i;
      }

   }

   public void setAllowedSpawnTypes(boolean p_72891_1_, boolean p_72891_2_) {
      this.spawnHostileMobs = p_72891_1_;
      this.spawnPeacefulMobs = p_72891_2_;
   }

   public void tick(BooleanSupplier p_72835_1_) {
      this.worldBorder.func_212673_r();
      this.tickWeather();
   }

   protected void calculateInitialWeather() {
      if (this.worldInfo.isRaining()) {
         this.rainingStrength = 1.0F;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = 1.0F;
         }
      }

   }

   public void close() {
      this.chunkProvider.close();
   }

   protected void tickWeather() {
      if (this.dimension.hasSkyLight()) {
         if (!this.isRemote) {
            boolean flag = this.getGameRules().getBoolean("doWeatherCycle");
            if (flag) {
               int i = this.worldInfo.getCleanWeatherTime();
               if (i > 0) {
                  --i;
                  this.worldInfo.setCleanWeatherTime(i);
                  this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                  this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
               }

               int j = this.worldInfo.getThunderTime();
               if (j <= 0) {
                  if (this.worldInfo.isThundering()) {
                     this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                  } else {
                     this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                  }
               } else {
                  --j;
                  this.worldInfo.setThunderTime(j);
                  if (j <= 0) {
                     this.worldInfo.setThundering(!this.worldInfo.isThundering());
                  }
               }

               int k = this.worldInfo.getRainTime();
               if (k <= 0) {
                  if (this.worldInfo.isRaining()) {
                     this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                  } else {
                     this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                  }
               } else {
                  --k;
                  this.worldInfo.setRainTime(k);
                  if (k <= 0) {
                     this.worldInfo.setRaining(!this.worldInfo.isRaining());
                  }
               }
            }

            this.prevThunderingStrength = this.thunderingStrength;
            if (this.worldInfo.isThundering()) {
               this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
            } else {
               this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
            }

            this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
            this.prevRainingStrength = this.rainingStrength;
            if (this.worldInfo.isRaining()) {
               this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
            } else {
               this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
            }

            this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk p_147467_3_) {
      p_147467_3_.enqueueRelightChecks();
   }

   protected void tickBlocks() {
   }

   public boolean checkLight(BlockPos p_175664_1_) {
      boolean flag = false;
      if (this.dimension.hasSkyLight()) {
         flag |= this.checkLightFor(EnumLightType.SKY, p_175664_1_);
      }

      flag = flag | this.checkLightFor(EnumLightType.BLOCK, p_175664_1_);
      return flag;
   }

   private int getRawLight(BlockPos p_175638_1_, EnumLightType p_175638_2_) {
      if (p_175638_2_ == EnumLightType.SKY && this.canSeeSky(p_175638_1_)) {
         return 15;
      } else {
         IBlockState iblockstate = this.getBlockState(p_175638_1_);
         int i = p_175638_2_ == EnumLightType.SKY ? 0 : iblockstate.getLightValue();
         int j = iblockstate.getOpacity(this, p_175638_1_);
         if (j >= 15 && iblockstate.getLightValue() > 0) {
            j = 1;
         }

         if (j < 1) {
            j = 1;
         }

         if (j >= 15) {
            return 0;
         } else if (i >= 14) {
            return i;
         } else {
            try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
               for(EnumFacing enumfacing : FACING_VALUES) {
                  blockpos$pooledmutableblockpos.setPos(p_175638_1_).move(enumfacing);
                  int k = this.getLightFor(p_175638_2_, blockpos$pooledmutableblockpos) - j;
                  if (k > i) {
                     i = k;
                  }

                  if (i >= 14) {
                     int l = i;
                     return l;
                  }
               }

               return i;
            }
         }
      }
   }

   public boolean checkLightFor(EnumLightType p_180500_1_, BlockPos p_180500_2_) {
      if (!this.isAreaLoaded(p_180500_2_, 17, false)) {
         return false;
      } else {
         int i = 0;
         int j = 0;
         this.profiler.startSection("getBrightness");
         int k = this.getLightFor(p_180500_1_, p_180500_2_);
         int l = this.getRawLight(p_180500_2_, p_180500_1_);
         int i1 = p_180500_2_.getX();
         int j1 = p_180500_2_.getY();
         int k1 = p_180500_2_.getZ();
         if (l > k) {
            this.lightUpdateBlockList[j++] = 133152;
         } else if (l < k) {
            this.lightUpdateBlockList[j++] = 133152 | k << 18;

            while(i < j) {
               int l1 = this.lightUpdateBlockList[i++];
               int i2 = (l1 & 63) - 32 + i1;
               int j2 = (l1 >> 6 & 63) - 32 + j1;
               int k2 = (l1 >> 12 & 63) - 32 + k1;
               int l2 = l1 >> 18 & 15;
               BlockPos blockpos = new BlockPos(i2, j2, k2);
               int i3 = this.getLightFor(p_180500_1_, blockpos);
               if (i3 == l2) {
                  this.setLightFor(p_180500_1_, blockpos, 0);
                  if (l2 > 0) {
                     int j3 = MathHelper.abs(i2 - i1);
                     int k3 = MathHelper.abs(j2 - j1);
                     int l3 = MathHelper.abs(k2 - k1);
                     if (j3 + k3 + l3 < 17) {
                        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
                           for(EnumFacing enumfacing : FACING_VALUES) {
                              int i4 = i2 + enumfacing.getXOffset();
                              int j4 = j2 + enumfacing.getYOffset();
                              int k4 = k2 + enumfacing.getZOffset();
                              blockpos$pooledmutableblockpos.setPos(i4, j4, k4);
                              int l4 = Math.max(1, this.getBlockState(blockpos$pooledmutableblockpos).getOpacity(this, blockpos$pooledmutableblockpos));
                              i3 = this.getLightFor(p_180500_1_, blockpos$pooledmutableblockpos);
                              if (i3 == l2 - l4 && j < this.lightUpdateBlockList.length) {
                                 this.lightUpdateBlockList[j++] = i4 - i1 + 32 | j4 - j1 + 32 << 6 | k4 - k1 + 32 << 12 | l2 - l4 << 18;
                              }
                           }
                        }
                     }
                  }
               }
            }

            i = 0;
         }

         this.profiler.endSection();
         this.profiler.startSection("checkedPosition < toCheckCount");

         while(i < j) {
            int i5 = this.lightUpdateBlockList[i++];
            int j5 = (i5 & 63) - 32 + i1;
            int k5 = (i5 >> 6 & 63) - 32 + j1;
            int l5 = (i5 >> 12 & 63) - 32 + k1;
            BlockPos blockpos1 = new BlockPos(j5, k5, l5);
            int i6 = this.getLightFor(p_180500_1_, blockpos1);
            int j6 = this.getRawLight(blockpos1, p_180500_1_);
            if (j6 != i6) {
               this.setLightFor(p_180500_1_, blockpos1, j6);
               if (j6 > i6) {
                  int k6 = Math.abs(j5 - i1);
                  int l6 = Math.abs(k5 - j1);
                  int i7 = Math.abs(l5 - k1);
                  boolean flag = j < this.lightUpdateBlockList.length - 6;
                  if (k6 + l6 + i7 < 17 && flag) {
                     if (this.getLightFor(p_180500_1_, blockpos1.west()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(p_180500_1_, blockpos1.east()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 + 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(p_180500_1_, blockpos1.down()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(p_180500_1_, blockpos1.up()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 + 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(p_180500_1_, blockpos1.north()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - 1 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(p_180500_1_, blockpos1.south()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 + 1 - k1 + 32 << 12);
                     }
                  }
               }
            }
         }

         this.profiler.endSection();
         return true;
      }
   }

   public Stream<VoxelShape> func_212392_a(@Nullable Entity p_212392_1_, VoxelShape p_212392_2_, VoxelShape p_212392_3_, Set<Entity> p_212392_4_) {
      Stream<VoxelShape> stream = IWorld.super.func_212392_a(p_212392_1_, p_212392_2_, p_212392_3_, p_212392_4_);
      return p_212392_1_ == null ? stream : Stream.concat(stream, this.func_211155_a(p_212392_1_, p_212392_2_, p_212392_4_));
   }

   public List<Entity> func_175674_a(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
      List<Entity> list = Lists.newArrayList();
      int i = MathHelper.floor((p_175674_2_.minX - 2.0D) / 16.0D);
      int j = MathHelper.floor((p_175674_2_.maxX + 2.0D) / 16.0D);
      int k = MathHelper.floor((p_175674_2_.minZ - 2.0D) / 16.0D);
      int l = MathHelper.floor((p_175674_2_.maxZ + 2.0D) / 16.0D);

      for(int i1 = i; i1 <= j; ++i1) {
         for(int j1 = k; j1 <= l; ++j1) {
            if (this.isChunkLoaded(i1, j1, true)) {
               this.getChunk(i1, j1).getEntitiesWithinAABBForEntity(p_175674_1_, p_175674_2_, list, p_175674_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntities(Class<? extends T> p_175644_1_, Predicate<? super T> p_175644_2_) {
      List<T> list = Lists.newArrayList();

      for(Entity entity : this.loadedEntityList) {
         if (p_175644_1_.isAssignableFrom(entity.getClass()) && p_175644_2_.test((T)entity)) {
            list.add((T)entity);
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getPlayers(Class<? extends T> p_175661_1_, Predicate<? super T> p_175661_2_) {
      List<T> list = Lists.newArrayList();

      for(Entity entity : this.playerEntities) {
         if (p_175661_1_.isAssignableFrom(entity.getClass()) && p_175661_2_.test((T)entity)) {
            list.add((T)entity);
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_72872_1_, AxisAlignedBB p_72872_2_) {
      return this.getEntitiesWithinAABB(p_72872_1_, p_72872_2_, EntitySelectors.NOT_SPECTATING);
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
      int i = MathHelper.floor((p_175647_2_.minX - 2.0D) / 16.0D);
      int j = MathHelper.ceil((p_175647_2_.maxX + 2.0D) / 16.0D);
      int k = MathHelper.floor((p_175647_2_.minZ - 2.0D) / 16.0D);
      int l = MathHelper.ceil((p_175647_2_.maxZ + 2.0D) / 16.0D);
      List<T> list = Lists.newArrayList();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            if (this.isChunkLoaded(i1, j1, true)) {
               this.getChunk(i1, j1).getEntitiesOfTypeWithinAABB(p_175647_1_, p_175647_2_, list, p_175647_3_);
            }
         }
      }

      return list;
   }

   @Nullable
   public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> p_72857_1_, AxisAlignedBB p_72857_2_, T p_72857_3_) {
      List<T> list = this.getEntitiesWithinAABB(p_72857_1_, p_72857_2_);
      T t = null;
      double d0 = Double.MAX_VALUE;

      for(int i = 0; i < list.size(); ++i) {
         T t1 = list.get(i);
         if (t1 != p_72857_3_ && EntitySelectors.NOT_SPECTATING.test(t1)) {
            double d1 = p_72857_3_.getDistanceSq(t1);
            if (!(d1 > d0)) {
               t = t1;
               d0 = d1;
            }
         }
      }

      return t;
   }

   @Nullable
   public Entity getEntityByID(int p_73045_1_) {
      return this.entitiesById.lookup(p_73045_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_212419_R() {
      return this.loadedEntityList.size();
   }

   public void markChunkDirty(BlockPos p_175646_1_, TileEntity p_175646_2_) {
      if (this.isBlockLoaded(p_175646_1_)) {
         this.getChunk(p_175646_1_).markDirty();
      }

   }

   public int func_72907_a(Class<?> p_72907_1_, int p_72907_2_) {
      int i = 0;
      Iterator iterator = this.loadedEntityList.iterator();

      while(true) {
         if (!iterator.hasNext()) {
            return i;
         }

         Entity entity = (Entity)iterator.next();
         if (!(entity instanceof EntityLiving) || !((EntityLiving)entity).isNoDespawnRequired()) {
            if (p_72907_1_.isAssignableFrom(entity.getClass())) {
               ++i;
            }

            if (i > p_72907_2_) {
               break;
            }
         }
      }

      return i;
   }

   public void func_212420_a(Stream<Entity> p_212420_1_) {
      p_212420_1_.forEach((p_212418_1_) -> {
         this.loadedEntityList.add(p_212418_1_);
         this.onEntityAdded(p_212418_1_);
      });
   }

   public void unloadEntities(Collection<Entity> p_175681_1_) {
      this.unloadedEntityList.addAll(p_175681_1_);
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public World getWorld() {
      return this;
   }

   public void setSeaLevel(int p_181544_1_) {
      this.seaLevel = p_181544_1_;
   }

   public int getStrongPower(BlockPos p_175627_1_, EnumFacing p_175627_2_) {
      return this.getBlockState(p_175627_1_).getStrongPower(this, p_175627_1_, p_175627_2_);
   }

   public WorldType getWorldType() {
      return this.worldInfo.getTerrainType();
   }

   public int getStrongPower(BlockPos p_175676_1_) {
      int i = 0;
      i = Math.max(i, this.getStrongPower(p_175676_1_.down(), EnumFacing.DOWN));
      if (i >= 15) {
         return i;
      } else {
         i = Math.max(i, this.getStrongPower(p_175676_1_.up(), EnumFacing.UP));
         if (i >= 15) {
            return i;
         } else {
            i = Math.max(i, this.getStrongPower(p_175676_1_.north(), EnumFacing.NORTH));
            if (i >= 15) {
               return i;
            } else {
               i = Math.max(i, this.getStrongPower(p_175676_1_.south(), EnumFacing.SOUTH));
               if (i >= 15) {
                  return i;
               } else {
                  i = Math.max(i, this.getStrongPower(p_175676_1_.west(), EnumFacing.WEST));
                  if (i >= 15) {
                     return i;
                  } else {
                     i = Math.max(i, this.getStrongPower(p_175676_1_.east(), EnumFacing.EAST));
                     return i >= 15 ? i : i;
                  }
               }
            }
         }
      }
   }

   public boolean isSidePowered(BlockPos p_175709_1_, EnumFacing p_175709_2_) {
      return this.getRedstonePower(p_175709_1_, p_175709_2_) > 0;
   }

   public int getRedstonePower(BlockPos p_175651_1_, EnumFacing p_175651_2_) {
      IBlockState iblockstate = this.getBlockState(p_175651_1_);
      return iblockstate.isNormalCube() ? this.getStrongPower(p_175651_1_) : iblockstate.getWeakPower(this, p_175651_1_, p_175651_2_);
   }

   public boolean isBlockPowered(BlockPos p_175640_1_) {
      if (this.getRedstonePower(p_175640_1_.down(), EnumFacing.DOWN) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.up(), EnumFacing.UP) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.north(), EnumFacing.NORTH) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.south(), EnumFacing.SOUTH) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.west(), EnumFacing.WEST) > 0) {
         return true;
      } else {
         return this.getRedstonePower(p_175640_1_.east(), EnumFacing.EAST) > 0;
      }
   }

   public int getRedstonePowerFromNeighbors(BlockPos p_175687_1_) {
      int i = 0;

      for(EnumFacing enumfacing : FACING_VALUES) {
         int j = this.getRedstonePower(p_175687_1_.offset(enumfacing), enumfacing);
         if (j >= 15) {
            return 15;
         }

         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer1 = this.playerEntities.get(i);
         if (p_190525_9_.test(entityplayer1)) {
            double d1 = entityplayer1.getDistanceSq(p_190525_1_, p_190525_3_, p_190525_5_);
            if ((p_190525_7_ < 0.0D || d1 < p_190525_7_ * p_190525_7_) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   public boolean isAnyPlayerWithinRangeAt(double p_175636_1_, double p_175636_3_, double p_175636_5_, double p_175636_7_) {
      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer = this.playerEntities.get(i);
         if (EntitySelectors.NOT_SPECTATING.test(entityplayer)) {
            double d0 = entityplayer.getDistanceSq(p_175636_1_, p_175636_3_, p_175636_5_);
            if (p_175636_7_ < 0.0D || d0 < p_175636_7_ * p_175636_7_) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_212417_b(double p_212417_1_, double p_212417_3_, double p_212417_5_, double p_212417_7_) {
      for(EntityPlayer entityplayer : this.playerEntities) {
         if (EntitySelectors.NOT_SPECTATING.test(entityplayer) && EntitySelectors.field_212545_b.test(entityplayer)) {
            double d0 = entityplayer.getDistanceSq(p_212417_1_, p_212417_3_, p_212417_5_);
            if (p_212417_7_ < 0.0D || d0 < p_212417_7_ * p_212417_7_) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   public EntityPlayer func_212817_a(double p_212817_1_, double p_212817_3_, double p_212817_5_) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer1 = this.playerEntities.get(i);
         if (EntitySelectors.NOT_SPECTATING.test(entityplayer1)) {
            double d1 = entityplayer1.getDistanceSq(p_212817_1_, entityplayer1.posY, p_212817_3_);
            if ((p_212817_5_ < 0.0D || d1 < p_212817_5_ * p_212817_5_) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   @Nullable
   public EntityPlayer getNearestAttackablePlayer(Entity p_184142_1_, double p_184142_2_, double p_184142_4_) {
      return this.getNearestAttackablePlayer(p_184142_1_.posX, p_184142_1_.posY, p_184142_1_.posZ, p_184142_2_, p_184142_4_, (Function<EntityPlayer, Double>)null, (Predicate<EntityPlayer>)null);
   }

   @Nullable
   public EntityPlayer getNearestAttackablePlayer(BlockPos p_184139_1_, double p_184139_2_, double p_184139_4_) {
      return this.getNearestAttackablePlayer((double)((float)p_184139_1_.getX() + 0.5F), (double)((float)p_184139_1_.getY() + 0.5F), (double)((float)p_184139_1_.getZ() + 0.5F), p_184139_2_, p_184139_4_, (Function<EntityPlayer, Double>)null, (Predicate<EntityPlayer>)null);
   }

   @Nullable
   public EntityPlayer getNearestAttackablePlayer(double p_184150_1_, double p_184150_3_, double p_184150_5_, double p_184150_7_, double p_184150_9_, @Nullable Function<EntityPlayer, Double> p_184150_11_, @Nullable Predicate<EntityPlayer> p_184150_12_) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer1 = this.playerEntities.get(i);
         if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive() && !entityplayer1.isSpectator() && (p_184150_12_ == null || p_184150_12_.test(entityplayer1))) {
            double d1 = entityplayer1.getDistanceSq(p_184150_1_, entityplayer1.posY, p_184150_5_);
            double d2 = p_184150_7_;
            if (entityplayer1.isSneaking()) {
               d2 = p_184150_7_ * (double)0.8F;
            }

            if (entityplayer1.isInvisible()) {
               float f = entityplayer1.getArmorVisibility();
               if (f < 0.1F) {
                  f = 0.1F;
               }

               d2 *= (double)(0.7F * f);
            }

            if (p_184150_11_ != null) {
               d2 *= MoreObjects.firstNonNull(p_184150_11_.apply(entityplayer1), 1.0D);
            }

            if ((p_184150_9_ < 0.0D || Math.abs(entityplayer1.posY - p_184150_3_) < p_184150_9_ * p_184150_9_) && (p_184150_7_ < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   @Nullable
   public EntityPlayer getPlayerEntityByName(String p_72924_1_) {
      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer = this.playerEntities.get(i);
         if (p_72924_1_.equals(entityplayer.getName().getString())) {
            return entityplayer;
         }
      }

      return null;
   }

   @Nullable
   public EntityPlayer getPlayerEntityByUUID(UUID p_152378_1_) {
      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer = this.playerEntities.get(i);
         if (p_152378_1_.equals(entityplayer.getUniqueID())) {
            return entityplayer;
         }
      }

      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void sendQuittingDisconnectingPacket() {
   }

   public void checkSessionLock() throws SessionLockException {
      this.saveHandler.checkSessionLock();
   }

   @OnlyIn(Dist.CLIENT)
   public void setTotalWorldTime(long p_82738_1_) {
      this.worldInfo.setWorldTotalTime(p_82738_1_);
   }

   public long getSeed() {
      return this.worldInfo.getSeed();
   }

   public long getTotalWorldTime() {
      return this.worldInfo.getWorldTotalTime();
   }

   public long getWorldTime() {
      return this.worldInfo.getWorldTime();
   }

   public void setWorldTime(long p_72877_1_) {
      this.worldInfo.setWorldTime(p_72877_1_);
   }

   public BlockPos getSpawnPoint() {
      BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
      if (!this.getWorldBorder().contains(blockpos)) {
         blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockpos;
   }

   public void setSpawnPoint(BlockPos p_175652_1_) {
      this.worldInfo.setSpawn(p_175652_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void joinEntityInSurroundings(Entity p_72897_1_) {
      int i = MathHelper.floor(p_72897_1_.posX / 16.0D);
      int j = MathHelper.floor(p_72897_1_.posZ / 16.0D);
      int k = 2;

      for(int l = -2; l <= 2; ++l) {
         for(int i1 = -2; i1 <= 2; ++i1) {
            this.getChunk(i + l, j + i1);
         }
      }

      if (!this.loadedEntityList.contains(p_72897_1_)) {
         this.loadedEntityList.add(p_72897_1_);
      }

   }

   public boolean isBlockModifiable(EntityPlayer p_175660_1_, BlockPos p_175660_2_) {
      return true;
   }

   public void setEntityState(Entity p_72960_1_, byte p_72960_2_) {
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void addBlockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
      this.getBlockState(p_175641_1_).onBlockEventReceived(this, p_175641_1_, p_175641_3_, p_175641_4_);
   }

   public ISaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public GameRules getGameRules() {
      return this.worldInfo.getGameRulesInstance();
   }

   public void updateAllPlayersSleepingFlag() {
   }

   public float getThunderStrength(float p_72819_1_) {
      return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * p_72819_1_) * this.getRainStrength(p_72819_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setThunderStrength(float p_147442_1_) {
      this.prevThunderingStrength = p_147442_1_;
      this.thunderingStrength = p_147442_1_;
   }

   public float getRainStrength(float p_72867_1_) {
      return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * p_72867_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setRainStrength(float p_72894_1_) {
      this.prevRainingStrength = p_72894_1_;
      this.rainingStrength = p_72894_1_;
   }

   public boolean isThundering() {
      if (this.dimension.hasSkyLight() && !this.dimension.isNether()) {
         return (double)this.getThunderStrength(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean isRaining() {
      return (double)this.getRainStrength(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos p_175727_1_) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.canSeeSky(p_175727_1_)) {
         return false;
      } else if (this.getHeight(Heightmap.Type.MOTION_BLOCKING, p_175727_1_).getY() > p_175727_1_.getY()) {
         return false;
      } else {
         return this.getBiome(p_175727_1_).getPrecipitation() == Biome.RainType.RAIN;
      }
   }

   public boolean isBlockinHighHumidity(BlockPos p_180502_1_) {
      Biome biome = this.getBiome(p_180502_1_);
      return biome.isHighHumidity();
   }

   @Nullable
   public WorldSavedDataStorage func_175693_T() {
      return this.mapStorage;
   }

   public void playBroadcastSound(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).broadcastSound(p_175669_1_, p_175669_2_, p_175669_3_);
      }

   }

   public void playEvent(int p_175718_1_, BlockPos p_175718_2_, int p_175718_3_) {
      this.playEvent((EntityPlayer)null, p_175718_1_, p_175718_2_, p_175718_3_);
   }

   public void playEvent(@Nullable EntityPlayer p_180498_1_, int p_180498_2_, BlockPos p_180498_3_, int p_180498_4_) {
      try {
         for(int i = 0; i < this.eventListeners.size(); ++i) {
            this.eventListeners.get(i).playEvent(p_180498_1_, p_180498_2_, p_180498_3_, p_180498_4_);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
         crashreportcategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(p_180498_3_));
         crashreportcategory.addCrashSection("Event source", p_180498_1_);
         crashreportcategory.addCrashSection("Event type", p_180498_2_);
         crashreportcategory.addCrashSection("Event data", p_180498_4_);
         throw new ReportedException(crashreport);
      }
   }

   public int getHeight() {
      return 256;
   }

   public int getActualHeight() {
      return this.dimension.isNether() ? 128 : 256;
   }

   @OnlyIn(Dist.CLIENT)
   public double getHorizon() {
      return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0D : 63.0D;
   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport p_72914_1_) {
      CrashReportCategory crashreportcategory = p_72914_1_.makeCategoryDepth("Affected level", 1);
      crashreportcategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
      crashreportcategory.addDetail("All players", () -> {
         return this.playerEntities.size() + " total; " + this.playerEntities;
      });
      crashreportcategory.addDetail("Chunk stats", () -> {
         return this.chunkProvider.makeString();
      });

      try {
         this.worldInfo.addToCrashReport(crashreportcategory);
      } catch (Throwable throwable) {
         crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
      }

      return crashreportcategory;
   }

   public void sendBlockBreakProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         IWorldEventListener iworldeventlistener = this.eventListeners.get(i);
         iworldeventlistener.sendBlockBreakProgress(p_175715_1_, p_175715_2_, p_175715_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void makeFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable NBTTagCompound p_92088_13_) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateComparatorOutputLevel(BlockPos p_175666_1_, Block p_175666_2_) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_175666_1_.offset(enumfacing);
         if (this.isBlockLoaded(blockpos)) {
            IBlockState iblockstate = this.getBlockState(blockpos);
            if (iblockstate.getBlock() == Blocks.COMPARATOR) {
               iblockstate.neighborChanged(this, blockpos, p_175666_2_, p_175666_1_);
            } else if (iblockstate.isNormalCube()) {
               blockpos = blockpos.offset(enumfacing);
               iblockstate = this.getBlockState(blockpos);
               if (iblockstate.getBlock() == Blocks.COMPARATOR) {
                  iblockstate.neighborChanged(this, blockpos, p_175666_2_, p_175666_1_);
               }
            }
         }
      }

   }

   public DifficultyInstance getDifficultyForLocation(BlockPos p_175649_1_) {
      long i = 0L;
      float f = 0.0F;
      if (this.isBlockLoaded(p_175649_1_)) {
         f = this.getCurrentMoonPhaseFactor();
         i = this.getChunk(p_175649_1_).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), i, f);
   }

   public int getSkylightSubtracted() {
      return this.skylightSubtracted;
   }

   public void setSkylightSubtracted(int p_175692_1_) {
      this.skylightSubtracted = p_175692_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLastLightningBolt() {
      return this.lastLightningBolt;
   }

   public void setLastLightningBolt(int p_175702_1_) {
      this.lastLightningBolt = p_175702_1_;
   }

   public VillageCollection getVillageCollection() {
      return this.villageCollection;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public boolean isSpawnChunk(int p_72916_1_, int p_72916_2_) {
      BlockPos blockpos = this.getSpawnPoint();
      int i = p_72916_1_ * 16 + 8 - blockpos.getX();
      int j = p_72916_2_ * 16 + 8 - blockpos.getZ();
      int k = 128;
      return i >= -128 && i <= 128 && j >= -128 && j <= 128;
   }

   public LongSet func_212412_ag() {
      ForcedChunksSaveData forcedchunkssavedata = this.func_212411_a(this.dimension.getType(), ForcedChunksSaveData::new, "chunks");
      return (LongSet)(forcedchunkssavedata != null ? LongSets.unmodifiable(forcedchunkssavedata.func_212438_a()) : LongSets.EMPTY_SET);
   }

   public boolean func_212416_f(int p_212416_1_, int p_212416_2_) {
      ForcedChunksSaveData forcedchunkssavedata = this.func_212411_a(this.dimension.getType(), ForcedChunksSaveData::new, "chunks");
      return forcedchunkssavedata != null && forcedchunkssavedata.func_212438_a().contains(ChunkPos.asLong(p_212416_1_, p_212416_2_));
   }

   public boolean func_212414_b(int p_212414_1_, int p_212414_2_, boolean p_212414_3_) {
      String s = "chunks";
      ForcedChunksSaveData forcedchunkssavedata = this.func_212411_a(this.dimension.getType(), ForcedChunksSaveData::new, "chunks");
      if (forcedchunkssavedata == null) {
         forcedchunkssavedata = new ForcedChunksSaveData("chunks");
         this.func_212409_a(this.dimension.getType(), "chunks", forcedchunkssavedata);
      }

      long i = ChunkPos.asLong(p_212414_1_, p_212414_2_);
      boolean flag;
      if (p_212414_3_) {
         flag = forcedchunkssavedata.func_212438_a().add(i);
         if (flag) {
            this.getChunk(p_212414_1_, p_212414_2_);
         }
      } else {
         flag = forcedchunkssavedata.func_212438_a().remove(i);
      }

      forcedchunkssavedata.setDirty(flag);
      return flag;
   }

   public void sendPacketToServer(Packet<?> p_184135_1_) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   @Nullable
   public BlockPos func_211157_a(String p_211157_1_, BlockPos p_211157_2_, int p_211157_3_, boolean p_211157_4_) {
      return null;
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.rand;
   }

   public abstract RecipeManager getRecipeManager();

   public abstract NetworkTagManager getTags();
}
