package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final EntityTracker entityTracker;
   private final PlayerChunkMap playerChunkMap;
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   public boolean disableLevelSaving;
   private boolean allPlayersSleeping;
   private int updateEntityTick;
   private final Teleporter worldTeleporter;
   private final WorldEntitySpawner entitySpawner = new WorldEntitySpawner();
   private final ServerTickList<Block> pendingBlockTicks = new ServerTickList<>(this, (p_205341_0_) ->
           p_205341_0_ == null || p_205341_0_.getDefaultState().isAir(), IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, this::tickBlock);
   private final ServerTickList<Fluid> pendingFluidTicks = new ServerTickList<>(this, (p_205774_0_) ->
           p_205774_0_ == null || p_205774_0_ == Fluids.EMPTY, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, this::tickFluid);
   protected final VillageSiege villageSiege = new VillageSiege(this);
   ObjectLinkedOpenHashSet<BlockEventData> blockEventQueue = new ObjectLinkedOpenHashSet<>();
   private boolean insideTick;

   public WorldServer(MinecraftServer p_i49819_1_, ISaveHandler p_i49819_2_, WorldSavedDataStorage p_i49819_3_, WorldInfo p_i49819_4_, DimensionType p_i49819_5_, Profiler p_i49819_6_) {
      super(p_i49819_2_, p_i49819_3_, p_i49819_4_, p_i49819_5_.create(), p_i49819_6_, false);
      this.server = p_i49819_1_;
      this.entityTracker = new EntityTracker(this);
      this.playerChunkMap = new PlayerChunkMap(this);
      this.dimension.setWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(p_i49819_1_.getMaxWorldSize());
   }

   public WorldServer func_212251_i__() {
      String s = VillageCollection.fileNameForProvider(this.dimension);
      VillageCollection villagecollection = this.func_212411_a(DimensionType.OVERWORLD, VillageCollection::new, s);
      if (villagecollection == null) {
         this.villageCollection = new VillageCollection(this);
         this.func_212409_a(DimensionType.OVERWORLD, s, this.villageCollection);
      } else {
         this.villageCollection = villagecollection;
         this.villageCollection.setWorldsForAll(this);
      }

      ScoreboardSaveData scoreboardsavedata = this.func_212411_a(DimensionType.OVERWORLD, ScoreboardSaveData::new, "scoreboard");
      if (scoreboardsavedata == null) {
         scoreboardsavedata = new ScoreboardSaveData();
         this.func_212409_a(DimensionType.OVERWORLD, "scoreboard", scoreboardsavedata);
      }

      scoreboardsavedata.setScoreboard(this.server.getWorldScoreboard());
      this.server.getWorldScoreboard().addDirtyRunnable(new WorldSavedDataCallableSave(scoreboardsavedata));
      this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
      this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
      this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
      this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
      this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());
      if (this.worldInfo.getBorderLerpTime() > 0L) {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
      } else {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
      }

      return this;
   }

   public void tick(BooleanSupplier p_72835_1_) {
      this.insideTick = true;
      super.tick(p_72835_1_);
      if (this.getWorldInfo().isHardcoreModeEnabled() && this.getDifficulty() != EnumDifficulty.HARD) {
         this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
      }

      this.chunkProvider.getChunkGenerator().getBiomeProvider().tick();
      if (this.areAllPlayersAsleep()) {
         if (this.getGameRules().getBoolean("doDaylightCycle")) {
            if (this.worldInfo.isThundering()|!this.getWorld().isDaytime()) {
               this.worldInfo.setWorldTime(22000L);
            }
         }
      //MITEMODDED Removed
//         this.wakeAllPlayers();
      }

      this.profiler.startSection("spawner");
      if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         //MITEMODDED spawn animals per 128 day
         this.entitySpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTime() / 24000L % 2147483647L % 128L == 0L);
         this.getChunkProvider().spawnMobs(this, this.spawnHostileMobs, this.spawnPeacefulMobs);
      }

      this.profiler.endStartSection("chunkSource");
      this.chunkProvider.func_73156_b(p_72835_1_);
      int j = this.calculateSkylightSubtracted(1.0F);
      if (j != this.getSkylightSubtracted()) {
         this.setSkylightSubtracted(j);
      }

      this.worldInfo.setWorldTotalTime(this.worldInfo.getWorldTotalTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
      }

      this.profiler.endStartSection("tickPending");
      this.tickPending();
      this.profiler.endStartSection("tickBlocks");
      this.tickBlocks();
      this.profiler.endStartSection("chunkMap");
      this.playerChunkMap.tick();
      this.profiler.endStartSection("village");
      this.villageCollection.tick();
      this.villageSiege.tick();
      this.profiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
      this.profiler.endSection();
      this.sendQueuedBlockEvents();
      this.insideTick = false;
   }

   public boolean isInsideTick() {
      return this.insideTick;
   }

   @Nullable
   public Biome.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType p_175734_1_, BlockPos p_175734_2_) {
      List<Biome.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(p_175734_1_, p_175734_2_);
      return list.isEmpty() ? null : WeightedRandom.getRandomItem(this.rand, list);
   }

   public boolean canCreatureTypeSpawnHere(EnumCreatureType p_175732_1_, Biome.SpawnListEntry p_175732_2_, BlockPos p_175732_3_) {
      List<Biome.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(p_175732_1_, p_175732_3_);
      return (list != null && !list.isEmpty()) && list.contains(p_175732_2_);
   }

   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.playerEntities.isEmpty()) {
         int i = 0;
         int j = 0;

         for(EntityPlayer entityplayer : this.playerEntities) {
            if (entityplayer.isSpectator()) {
               ++i;
            } else if (entityplayer.isPlayerSleeping()) {
               ++j;
            }
         }

         this.allPlayersSleeping = j > 0 && j >= this.playerEntities.size() - i;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getWorldScoreboard();
   }

   protected void wakeAllPlayers() {
      this.allPlayersSleeping = false;

      for(EntityPlayer entityplayer : this.playerEntities.stream().filter(EntityPlayer::isPlayerSleeping).collect(Collectors.toList())) {
         entityplayer.wakeUpPlayer(false, false, true);
      }

      if (this.getGameRules().getBoolean("doWeatherCycle")) {
         this.resetRainAndThunder();
      }

   }

   private void resetRainAndThunder() {
      this.worldInfo.setRainTime(0);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThunderTime(0);
      this.worldInfo.setThundering(false);
   }

   public boolean areAllPlayersAsleep() {
      if (this.allPlayersSleeping && !this.isRemote) {
         for(EntityPlayer entityplayer : this.playerEntities) {
            if (!entityplayer.isSpectator() && !entityplayer.isPlayerFullyAsleep()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      if (this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
      }

      int i = this.worldInfo.getSpawnX();
      int j = this.worldInfo.getSpawnZ();
      int k = 0;

      while(this.getGroundAboveSeaLevel(new BlockPos(i, 0, j)).isAir()) {
         i += this.rand.nextInt(8) - this.rand.nextInt(8);
         j += this.rand.nextInt(8) - this.rand.nextInt(8);
         ++k;
         if (k == 10000) {
            break;
         }
      }

      this.worldInfo.setSpawnX(i);
      this.worldInfo.setSpawnZ(j);
   }

   public boolean isChunkLoaded(int p_175680_1_, int p_175680_2_, boolean p_175680_3_) {
      return this.isChunkLoaded(p_175680_1_, p_175680_2_);
   }

   public boolean isChunkLoaded(int p_201697_1_, int p_201697_2_) {
      return this.getChunkProvider().chunkExists(p_201697_1_, p_201697_2_);
   }

   protected void playerCheckLight() {
      this.profiler.startSection("playerCheckLight");
      if (!this.playerEntities.isEmpty()) {
         int i = this.rand.nextInt(this.playerEntities.size());
         EntityPlayer entityplayer = this.playerEntities.get(i);
         int j = MathHelper.floor(entityplayer.posX) + this.rand.nextInt(11) - 5;
         int k = MathHelper.floor(entityplayer.posY) + this.rand.nextInt(11) - 5;
         int l = MathHelper.floor(entityplayer.posZ) + this.rand.nextInt(11) - 5;
         this.checkLight(new BlockPos(j, k, l));
      }

      this.profiler.endSection();
   }

   protected void tickBlocks() {
      this.playerCheckLight();
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         Iterator<Chunk> iterator1 = this.playerChunkMap.getChunkIterator();

         while(iterator1.hasNext()) {
            iterator1.next().tick(false);
         }

      } else {
         int i = this.getGameRules().getInt("randomTickSpeed");
         boolean flag = this.isRaining();
         boolean flag1 = this.isThundering();
         this.profiler.startSection("pollingChunks");

         for(Iterator<Chunk> iterator = this.playerChunkMap.getChunkIterator(); iterator.hasNext(); this.profiler.endSection()) {
            this.profiler.startSection("getChunk");
            Chunk chunk = iterator.next();
            int j = chunk.x * 16;
            int k = chunk.z * 16;
            this.profiler.endStartSection("checkNextLight");
            chunk.enqueueRelightChecks();
            this.profiler.endStartSection("tickChunk");
            chunk.tick(false);
            this.profiler.endStartSection("thunder");
            if (flag && flag1 && this.rand.nextInt(100000) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int l = this.updateLCG >> 2;
               BlockPos blockpos = this.adjustPosToNearbyEntity(new BlockPos(j + (l & 15), 0, k + (l >> 8 & 15)));
               if (this.isRainingAt(blockpos)) {
                  DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos);
                  boolean flag2 = this.getGameRules().getBoolean("doMobSpawning") && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D;
                  if (flag2) {
                     EntitySkeletonHorse entityskeletonhorse = new EntitySkeletonHorse(this);
                     entityskeletonhorse.setTrap(true);
                     entityskeletonhorse.setGrowingAge(0);
                     entityskeletonhorse.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                     this.spawnEntity(entityskeletonhorse);
                  }

                  this.addWeatherEffect(new EntityLightningBolt(this, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, flag2));
               }
            }

            this.profiler.endStartSection("iceandsnow");
            if (this.rand.nextInt(16) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int i2 = this.updateLCG >> 2;
               BlockPos blockpos1 = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(j + (i2 & 15), 0, k + (i2 >> 8 & 15)));
               BlockPos blockpos2 = blockpos1.down();
               Biome biome = this.getBiome(blockpos1);
               if (biome.doesWaterFreeze(this, blockpos2)) {
                  this.setBlockState(blockpos2, Blocks.ICE.getDefaultState());
               }

               if (flag && biome.doesSnowGenerate(this, blockpos1)) {
                  this.setBlockState(blockpos1, Blocks.SNOW.getDefaultState());
               }

               if (flag && this.getBiome(blockpos2).getPrecipitation() == Biome.RainType.RAIN) {
                  this.getBlockState(blockpos2).getBlock().fillWithRain(this, blockpos2);
               }
            }

            this.profiler.endStartSection("tickBlocks");
            if (i > 0) {
               for(ChunkSection chunksection : chunk.getSections()) {
                  if (chunksection != Chunk.EMPTY_SECTION && chunksection.func_206915_b()) {
                     for(int j2 = 0; j2 < i; ++j2) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int i1 = this.updateLCG >> 2;
                        int j1 = i1 & 15;
                        int k1 = i1 >> 8 & 15;
                        int l1 = i1 >> 16 & 15;
                        IBlockState iblockstate = chunksection.get(j1, l1, k1);
                        IFluidState ifluidstate = chunksection.func_206914_b(j1, l1, k1);
                        this.profiler.startSection("randomTick");
                        if (iblockstate.needsRandomTick()) {
                           iblockstate.randomTick(this, new BlockPos(j1 + j, l1 + chunksection.getYLocation(), k1 + k), this.rand);
                        }

                        if (ifluidstate.getTickRandomly()) {
                           ifluidstate.randomTick(this, new BlockPos(j1 + j, l1 + chunksection.getYLocation(), k1 + k), this.rand);
                        }

                        this.profiler.endSection();
                     }
                  }
               }
            }
         }

         this.profiler.endSection();
      }
   }

   protected BlockPos adjustPosToNearbyEntity(BlockPos p_175736_1_) {
      BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, p_175736_1_);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getHeight(), blockpos.getZ()))).grow(3.0D);
      List<EntityLivingBase> list = this.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, (p_210194_1_) -> {
         return p_210194_1_ != null && p_210194_1_.isEntityAlive() && this.canSeeSky(p_210194_1_.getPosition());
      });
      if (!list.isEmpty()) {
         return list.get(this.rand.nextInt(list.size())).getPosition();
      } else {
         if (blockpos.getY() == -1) {
            blockpos = blockpos.up(2);
         }

         return blockpos;
      }
   }

   public void tickEntities() {
      if (this.playerEntities.isEmpty()) {
         if (this.updateEntityTick++ >= 300) {
            return;
         }
      } else {
         this.resetUpdateEntityTick();
      }

      this.dimension.tick();
      super.tickEntities();
   }

   protected void tickPlayers() {
      super.tickPlayers();
      this.profiler.endStartSection("players");

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         Entity entity = this.playerEntities.get(i);
         Entity entity1 = entity.getRidingEntity();
         if (entity1 != null) {
            if (!entity1.isDead && entity1.isPassenger(entity)) {
               continue;
            }

            entity.dismountRidingEntity();
         }

         this.profiler.startSection("tick");
         if (!entity.isDead) {
            try {
               this.tickEntity(entity);
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking player");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Player being ticked");
               entity.fillCrashReport(crashreportcategory);
               throw new ReportedException(crashreport);
            }
         }

         this.profiler.endSection();
         this.profiler.startSection("remove");
         if (entity.isDead) {
            int j = entity.chunkCoordX;
            int k = entity.chunkCoordZ;
            if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
               this.getChunk(j, k).removeEntity(entity);
            }

            this.loadedEntityList.remove(entity);
            this.onEntityRemoved(entity);
         }

         this.profiler.endSection();
      }

   }

   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   public void tickPending() {
      if (this.worldInfo.getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.pendingBlockTicks.tick();
         this.pendingFluidTicks.tick();
      }
   }

   private void tickFluid(NextTickListEntry<Fluid> p_205339_1_) {
      IFluidState ifluidstate = this.getFluidState(p_205339_1_.position);
      if (ifluidstate.getFluid() == p_205339_1_.getTarget()) {
         ifluidstate.tick(this, p_205339_1_.position);
      }

   }

   private void tickBlock(NextTickListEntry<Block> p_205338_1_) {
      IBlockState iblockstate = this.getBlockState(p_205338_1_.position);
      if (iblockstate.getBlock() == p_205338_1_.getTarget()) {
         iblockstate.tick(this, p_205338_1_.position, this.rand);
      }

   }

   public void updateEntityWithOptionalForce(Entity p_72866_1_, boolean p_72866_2_) {
      if (!this.canSpawnAnimals() && (p_72866_1_ instanceof EntityAnimal || p_72866_1_ instanceof EntityWaterMob)) {
         p_72866_1_.setDead();
      }

      if (!this.canSpawnNPCs() && p_72866_1_ instanceof INpc) {
         p_72866_1_.setDead();
      }

      super.updateEntityWithOptionalForce(p_72866_1_, p_72866_2_);
   }

   private boolean canSpawnNPCs() {
      return this.server.getCanSpawnNPCs();
   }

   private boolean canSpawnAnimals() {
      return this.server.getCanSpawnAnimals();
   }

   protected IChunkProvider createChunkProvider() {
      IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.dimension);
      return new ChunkProviderServer(this, ichunkloader, this.dimension.createChunkGenerator(), this.server);
   }

   public boolean isBlockModifiable(EntityPlayer p_175660_1_, BlockPos p_175660_2_) {
      return !this.server.isBlockProtected(this, p_175660_2_, p_175660_1_) && this.getWorldBorder().contains(p_175660_2_);
   }

   public void initialize(WorldSettings p_72963_1_) {
      if (!this.worldInfo.isInitialized()) {
         try {
            this.createSpawnPosition(p_72963_1_);
            if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
               this.setDebugWorldSettings();
            }

            super.initialize(p_72963_1_);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

            try {
               this.addWorldInfoToCrashReport(crashreport);
            } catch (Throwable var5) {
            }

            throw new ReportedException(crashreport);
         }

         this.worldInfo.setServerInitialized(true);
      }

   }

   private void setDebugWorldSettings() {
      this.worldInfo.setMapFeaturesEnabled(false);
      this.worldInfo.setAllowCommands(true);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThundering(false);
      this.worldInfo.setCleanWeatherTime(1000000000);
      this.worldInfo.setWorldTime(6000L);
      this.worldInfo.setGameType(GameType.SPECTATOR);
      this.worldInfo.setHardcore(false);
      this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
      this.worldInfo.setDifficultyLocked(true);
      this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false", this.server);
   }

   private void createSpawnPosition(WorldSettings p_73052_1_) {
      if (!this.dimension.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.chunkProvider.getChunkGenerator().getGroundHeight()));
      } else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
      } else {
         BiomeProvider biomeprovider = this.chunkProvider.getChunkGenerator().getBiomeProvider();
         List<Biome> list = biomeprovider.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos blockpos = biomeprovider.findBiomePosition(0, 0, 256, list, random);
         ChunkPos chunkpos = blockpos == null ? new ChunkPos(0, 0) : new ChunkPos(blockpos);
         if (blockpos == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean flag = false;

         for(Block block : BlockTags.VALID_SPAWN.getAllElements()) {
            if (biomeprovider.func_205706_b().contains(block.getDefaultState())) {
               flag = true;
               break;
            }
         }

         this.worldInfo.setSpawn(chunkpos.asBlockPos().add(8, this.chunkProvider.getChunkGenerator().getGroundHeight(), 8));
         int i1 = 0;
         int j1 = 0;
         int i = 0;
         int j = -1;
         int k = 32;

         for(int l = 0; l < 1024; ++l) {
            if (i1 > -16 && i1 <= 16 && j1 > -16 && j1 <= 16) {
               BlockPos blockpos1 = this.dimension.findSpawn(new ChunkPos(chunkpos.x + i1, chunkpos.z + j1), flag);
               if (blockpos1 != null) {
                  this.worldInfo.setSpawn(blockpos1);
                  break;
               }
            }

            if (i1 == j1 || i1 < 0 && i1 == -j1 || i1 > 0 && i1 == 1 - j1) {
               int k1 = i;
               i = -j;
               j = k1;
            }

            i1 += i;
            j1 += j;
         }

         if (p_73052_1_.isBonusChestEnabled()) {
            this.createBonusChest();
         }

      }
   }

   protected void createBonusChest() {
      BonusChestFeature bonuschestfeature = new BonusChestFeature();

      for(int i = 0; i < 10; ++i) {
         int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k)).up();
         if (bonuschestfeature.func_212245_a(this, this.chunkProvider.getChunkGenerator(), this.rand, blockpos, IFeatureConfig.NO_FEATURE_CONFIG)) {
            break;
         }
      }

   }

   @Nullable
   public BlockPos getSpawnCoordinate() {
      return this.dimension.getSpawnCoordinate();
   }

   public void saveAllChunks(boolean p_73044_1_, @Nullable IProgressUpdate p_73044_2_) throws SessionLockException {
      ChunkProviderServer chunkproviderserver = this.getChunkProvider();
      if (chunkproviderserver.canSave()) {
         if (p_73044_2_ != null) {
            p_73044_2_.func_200210_a(new TextComponentTranslation("menu.savingLevel"));
         }

         this.saveLevel();
         if (p_73044_2_ != null) {
            p_73044_2_.func_200209_c(new TextComponentTranslation("menu.savingChunks"));
         }

         chunkproviderserver.saveChunks(p_73044_1_);

         for(Chunk chunk : Lists.newArrayList(chunkproviderserver.getLoadedChunks())) {
            if (chunk != null && !this.playerChunkMap.contains(chunk.x, chunk.z)) {
               chunkproviderserver.queueUnload(chunk);
            }
         }

      }
   }

   public void flushToDisk() {
      ChunkProviderServer chunkproviderserver = this.getChunkProvider();
      if (chunkproviderserver.canSave()) {
         chunkproviderserver.flushToDisk();
      }
   }

   protected void saveLevel() throws SessionLockException {
      this.checkSessionLock();

      for(WorldServer worldserver : this.server.func_212370_w()) {
         if (worldserver instanceof WorldServerMulti) {
            ((WorldServerMulti)worldserver).saveAdditionalData();
         }
      }

      this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
      this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
      this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
      this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
      this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
      this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
      this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
      this.worldInfo.setCustomBossEvents(this.server.getCustomBossEvents().write());
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.server.getPlayerList().getHostPlayerData());
      this.func_175693_T().saveAllData();
   }

   public boolean spawnEntity(Entity p_72838_1_) {
      return this.canAddEntity(p_72838_1_) && super.spawnEntity(p_72838_1_);
   }

   public void func_212420_a(Stream<Entity> p_212420_1_) {
      p_212420_1_.forEach((p_212421_1_) -> {
         if (this.canAddEntity(p_212421_1_)) {
            this.loadedEntityList.add(p_212421_1_);
            this.onEntityAdded(p_212421_1_);
         }

      });
   }

   private boolean canAddEntity(Entity p_184165_1_) {
      if (p_184165_1_.isDead) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already",
                 EntityType.getId(p_184165_1_.getType()));
         return false;
      } else {
         UUID uuid = p_184165_1_.getUniqueID();
         if (this.entitiesByUuid.containsKey(uuid)) {
            Entity entity = this.entitiesByUuid.get(uuid);
            if (this.unloadedEntityList.contains(entity)) {
               this.unloadedEntityList.remove(entity);
            } else {
               if (!(p_184165_1_ instanceof EntityPlayer)) {
                  LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity.getType()), uuid.toString());
                  return false;
               }

               LOGGER.warn("Force-added player with duplicate UUID {}", uuid.toString());
            }

            this.removeEntityDangerously(entity);
         }

         return true;
      }
   }

   public void onEntityAdded(Entity p_72923_1_) {
      super.onEntityAdded(p_72923_1_);
      this.entitiesById.addKey(p_72923_1_.getEntityId(), p_72923_1_);
      this.entitiesByUuid.put(p_72923_1_.getUniqueID(), p_72923_1_);
      Entity[] aentity = p_72923_1_.getParts();
      if (aentity != null) {
         for(Entity entity : aentity) {
            this.entitiesById.addKey(entity.getEntityId(), entity);
         }
      }

   }

   public void onEntityRemoved(Entity p_72847_1_) {
      super.onEntityRemoved(p_72847_1_);
      this.entitiesById.removeObject(p_72847_1_.getEntityId());
      this.entitiesByUuid.remove(p_72847_1_.getUniqueID());
      Entity[] aentity = p_72847_1_.getParts();
      if (aentity != null) {
         for(Entity entity : aentity) {
            this.entitiesById.removeObject(entity.getEntityId());
         }
      }

   }

   public boolean addWeatherEffect(Entity p_72942_1_) {
      if (super.addWeatherEffect(p_72942_1_)) {
         this.server.getPlayerList().func_148543_a(null, p_72942_1_.posX, p_72942_1_.posY, p_72942_1_.posZ, 512.0D, this.dimension.getType(), new SPacketSpawnGlobalEntity(p_72942_1_));
         return true;
      } else {
         return false;
      }
   }

   public void setEntityState(Entity p_72960_1_, byte p_72960_2_) {
      this.getEntityTracker().sendToTrackingAndSelf(p_72960_1_, new SPacketEntityStatus(p_72960_1_, p_72960_2_));
   }

   public ChunkProviderServer getChunkProvider() {
      return (ChunkProviderServer)super.getChunkProvider();
   }

   public Explosion createExplosion(@Nullable Entity p_211529_1_, DamageSource p_211529_2_, double p_211529_3_, double p_211529_5_, double p_211529_7_, float p_211529_9_, boolean p_211529_10_, boolean p_211529_11_) {
      Explosion explosion = new Explosion(this, p_211529_1_, p_211529_3_, p_211529_5_, p_211529_7_, p_211529_9_, p_211529_10_, p_211529_11_);
      if (p_211529_2_ != null) {
         explosion.setDamageSource(p_211529_2_);
      }

      explosion.doExplosionA();
      explosion.doExplosionB(false);
      if (!p_211529_11_) {
         explosion.clearAffectedBlockPositions();
      }

      for(EntityPlayer entityplayer : this.playerEntities) {
         if (entityplayer.getDistanceSq(p_211529_3_, p_211529_5_, p_211529_7_) < 4096.0D) {
            ((EntityPlayerMP)entityplayer).connection.sendPacket(new SPacketExplosion(p_211529_3_, p_211529_5_, p_211529_7_, p_211529_9_, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityplayer)));
         }
      }

      return explosion;
   }

   public void addBlockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
      this.blockEventQueue.add(new BlockEventData(p_175641_1_, p_175641_2_, p_175641_3_, p_175641_4_));
   }

   private void sendQueuedBlockEvents() {
      while(!this.blockEventQueue.isEmpty()) {
         BlockEventData blockeventdata = this.blockEventQueue.removeFirst();
         if (this.fireBlockEvent(blockeventdata)) {
            this.server.getPlayerList().func_148543_a(null, (double)blockeventdata.getPosition().getX(), (double)blockeventdata.getPosition().getY(), (double)blockeventdata.getPosition().getZ(), 64.0D, this.dimension.getType(), new SPacketBlockAction(blockeventdata.getPosition(), blockeventdata.getBlock(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
         }
      }

   }

   private boolean fireBlockEvent(BlockEventData p_147485_1_) {
      IBlockState iblockstate = this.getBlockState(p_147485_1_.getPosition());
      return iblockstate.getBlock() == p_147485_1_.getBlock() && iblockstate.onBlockEventReceived(this,
              p_147485_1_.getPosition(), p_147485_1_.getEventID(), p_147485_1_.getEventParameter());
   }

   public void close() {
      this.saveHandler.flush();
      super.close();
   }

   protected void tickWeather() {
      boolean flag = this.isRaining();
      super.tickWeather();
      if (this.prevRainingStrength != this.rainingStrength) {
         this.server.getPlayerList().func_148537_a(new SPacketChangeGameState(7, this.rainingStrength), this.dimension.getType());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.server.getPlayerList().func_148537_a(new SPacketChangeGameState(8, this.thunderingStrength), this.dimension.getType());
      }

      if (flag != this.isRaining()) {
         if (flag) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SPacketChangeGameState(2, 0.0F));
         } else {
            this.server.getPlayerList().sendPacketToAllPlayers(new SPacketChangeGameState(1, 0.0F));
         }

         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketChangeGameState(7, this.rainingStrength));
         this.server.getPlayerList().sendPacketToAllPlayers(new SPacketChangeGameState(8, this.thunderingStrength));
      }

   }

   public ServerTickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTicks;
   }

   public ServerTickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityTracker getEntityTracker() {
      return this.entityTracker;
   }

   public PlayerChunkMap getPlayerChunkMap() {
      return this.playerChunkMap;
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   public TemplateManager getStructureTemplateManager() {
      return this.saveHandler.getStructureTemplateManager();
   }

   public <T extends IParticleData> int spawnParticle(T p_195598_1_, double p_195598_2_, double p_195598_4_, double p_195598_6_, int p_195598_8_, double p_195598_9_, double p_195598_11_, double p_195598_13_, double p_195598_15_) {
      SPacketParticles spacketparticles = new SPacketParticles(p_195598_1_, false, (float)p_195598_2_, (float)p_195598_4_, (float)p_195598_6_, (float)p_195598_9_, (float)p_195598_11_, (float)p_195598_13_, (float)p_195598_15_, p_195598_8_);
      int i = 0;

      for(int j = 0; j < this.playerEntities.size(); ++j) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntities.get(j);
         if (this.sendPacketWithinDistance(entityplayermp, false, p_195598_2_, p_195598_4_, p_195598_6_, spacketparticles)) {
            ++i;
         }
      }

      return i;
   }

   public <T extends IParticleData> boolean spawnParticle(EntityPlayerMP p_195600_1_, T p_195600_2_, boolean p_195600_3_, double p_195600_4_, double p_195600_6_, double p_195600_8_, int p_195600_10_, double p_195600_11_, double p_195600_13_, double p_195600_15_, double p_195600_17_) {
      Packet<?> packet = new SPacketParticles(p_195600_2_, p_195600_3_, (float)p_195600_4_, (float)p_195600_6_, (float)p_195600_8_, (float)p_195600_11_, (float)p_195600_13_, (float)p_195600_15_, (float)p_195600_17_, p_195600_10_);
      return this.sendPacketWithinDistance(p_195600_1_, p_195600_3_, p_195600_4_, p_195600_6_, p_195600_8_, packet);
   }

   private boolean sendPacketWithinDistance(EntityPlayerMP p_195601_1_, boolean p_195601_2_, double p_195601_3_, double p_195601_5_, double p_195601_7_, Packet<?> p_195601_9_) {
      if (p_195601_1_.getServerWorld() != this) {
         return false;
      } else {
         BlockPos blockpos = p_195601_1_.getPosition();
         double d0 = blockpos.distanceSq(p_195601_3_, p_195601_5_, p_195601_7_);
         if (!(d0 <= 1024.0D) && (!p_195601_2_ || !(d0 <= 262144.0D))) {
            return false;
         } else {
            p_195601_1_.connection.sendPacket(p_195601_9_);
            return true;
         }
      }
   }

   @Nullable
   public Entity getEntityFromUuid(UUID p_175733_1_) {
      return this.entitiesByUuid.get(p_175733_1_);
   }

   public ListenableFuture<Object> addScheduledTask(Runnable p_152344_1_) {
      return this.server.addScheduledTask(p_152344_1_);
   }

   public boolean isCallingFromMinecraftThread() {
      return this.server.isCallingFromMinecraftThread();
   }

   @Nullable
   public BlockPos func_211157_a(String p_211157_1_, BlockPos p_211157_2_, int p_211157_3_, boolean p_211157_4_) {
      return this.getChunkProvider().func_211268_a(this, p_211157_1_, p_211157_2_, p_211157_3_, p_211157_4_);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public NetworkTagManager getTags() {
      return this.server.getNetworkTagManager();
   }
}
