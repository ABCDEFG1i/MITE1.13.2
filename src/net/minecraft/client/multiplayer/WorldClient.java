package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldClient extends World {
   private final NetHandlerPlayClient connection;
   private ChunkProviderClient clientChunkProvider;
   private final Set<Entity> entityList = Sets.newHashSet();
   private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
   private final Minecraft mc = Minecraft.getInstance();
   private final Set<ChunkPos> previousActiveChunkSet = Sets.newHashSet();
   private int ambienceTicks;
   protected Set<ChunkPos> visibleChunks;
   private Scoreboard scoreboard;

   public WorldClient(NetHandlerPlayClient p_i49845_1_, WorldSettings p_i49845_2_, DimensionType p_i49845_3_, EnumDifficulty p_i49845_4_, Profiler p_i49845_5_) {
      super(new SaveHandlerMP(), new SaveDataMemoryStorage(), new WorldInfo(p_i49845_2_, "MpServer"), p_i49845_3_.create(), p_i49845_5_, true);
      this.ambienceTicks = this.rand.nextInt(12000);
      this.visibleChunks = Sets.newHashSet();
      this.scoreboard = new Scoreboard();
      this.connection = p_i49845_1_;
      this.getWorldInfo().setDifficulty(p_i49845_4_);
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.dimension.setWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
   }

   public void tick(BooleanSupplier p_72835_1_) {
      super.tick(p_72835_1_);
      this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setWorldTime(this.getWorldTime() + 1L);
      }

      this.profiler.startSection("reEntryProcessing");

      for(int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i) {
         Entity entity = this.entitySpawnQueue.iterator().next();
         this.entitySpawnQueue.remove(entity);
         if (!this.loadedEntityList.contains(entity)) {
            this.spawnEntity(entity);
         }
      }

      this.profiler.endStartSection("chunkCache");
      this.clientChunkProvider.func_73156_b(p_72835_1_);
      this.profiler.endStartSection("blocks");
      this.tickBlocks();
      this.profiler.endSection();
   }

   protected IChunkProvider createChunkProvider() {
      this.clientChunkProvider = new ChunkProviderClient(this);
      return this.clientChunkProvider;
   }

   public boolean isChunkLoaded(int p_175680_1_, int p_175680_2_, boolean p_175680_3_) {
      return p_175680_3_ || this.getChunkProvider().func_186025_d(p_175680_1_, p_175680_2_, true, false) != null;
   }

   protected void refreshVisibleChunks() {
      this.visibleChunks.clear();
      int i = this.mc.gameSettings.renderDistanceChunks;
      this.profiler.startSection("buildList");
      int j = MathHelper.floor(this.mc.player.posX / 16.0D);
      int k = MathHelper.floor(this.mc.player.posZ / 16.0D);

      for(int l = -i; l <= i; ++l) {
         for(int i1 = -i; i1 <= i; ++i1) {
            this.visibleChunks.add(new ChunkPos(l + j, i1 + k));
         }
      }

      this.profiler.endSection();
   }

   protected void tickBlocks() {
      this.refreshVisibleChunks();
      if (this.ambienceTicks > 0) {
         --this.ambienceTicks;
      }

      this.previousActiveChunkSet.retainAll(this.visibleChunks);
      if (this.previousActiveChunkSet.size() == this.visibleChunks.size()) {
         this.previousActiveChunkSet.clear();
      }

      int i = 0;

      for(ChunkPos chunkpos : this.visibleChunks) {
         if (!this.previousActiveChunkSet.contains(chunkpos)) {
            int j = chunkpos.x * 16;
            int k = chunkpos.z * 16;
            this.profiler.startSection("getChunk");
            Chunk chunk = this.getChunk(chunkpos.x, chunkpos.z);
            this.playMoodSoundAndCheckLight(j, k, chunk);
            this.profiler.endSection();
            this.previousActiveChunkSet.add(chunkpos);
            ++i;
            if (i >= 10) {
               return;
            }
         }
      }

   }

   public boolean spawnEntity(Entity p_72838_1_) {
      boolean flag = super.spawnEntity(p_72838_1_);
      this.entityList.add(p_72838_1_);
      if (flag) {
         if (p_72838_1_ instanceof EntityMinecart) {
            this.mc.getSoundHandler().play(new MovingSoundMinecart((EntityMinecart)p_72838_1_));
         }
      } else {
         this.entitySpawnQueue.add(p_72838_1_);
      }

      return flag;
   }

   public void removeEntity(Entity p_72900_1_) {
      super.removeEntity(p_72900_1_);
      this.entityList.remove(p_72900_1_);
   }

   public void onEntityAdded(Entity p_72923_1_) {
      super.onEntityAdded(p_72923_1_);
       this.entitySpawnQueue.remove(p_72923_1_);

   }

   public void onEntityRemoved(Entity p_72847_1_) {
      super.onEntityRemoved(p_72847_1_);
      if (this.entityList.contains(p_72847_1_)) {
         if (p_72847_1_.isEntityAlive()) {
            this.entitySpawnQueue.add(p_72847_1_);
         } else {
            this.entityList.remove(p_72847_1_);
         }
      }

   }

   public void addEntityToWorld(int p_73027_1_, Entity p_73027_2_) {
      Entity entity = this.getEntityByID(p_73027_1_);
      if (entity != null) {
         this.removeEntity(entity);
      }

      this.entityList.add(p_73027_2_);
      p_73027_2_.setEntityId(p_73027_1_);
      if (!this.spawnEntity(p_73027_2_)) {
         this.entitySpawnQueue.add(p_73027_2_);
      }

      this.entitiesById.addKey(p_73027_1_, p_73027_2_);
   }

   @Nullable
   public Entity getEntityByID(int p_73045_1_) {
      return p_73045_1_ == this.mc.player.getEntityId() ? this.mc.player : super.getEntityByID(p_73045_1_);
   }

   public Entity removeEntityFromWorld(int p_73028_1_) {
      Entity entity = this.entitiesById.removeObject(p_73028_1_);
      if (entity != null) {
         this.entityList.remove(entity);
         this.removeEntity(entity);
      }

      return entity;
   }

   public void func_195597_b(BlockPos p_195597_1_, IBlockState p_195597_2_) {
      this.setBlockState(p_195597_1_, p_195597_2_, 19);
   }

   public void sendQuittingDisconnectingPacket() {
      this.connection.getNetworkManager().closeChannel(new TextComponentTranslation("multiplayer.status.quitting"));
   }

   protected void tickWeather() {
   }

   protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk p_147467_3_) {
      super.playMoodSoundAndCheckLight(p_147467_1_, p_147467_2_, p_147467_3_);
      if (this.ambienceTicks == 0) {
         this.updateLCG = this.updateLCG * 3 + 1013904223;
         int i = this.updateLCG >> 2;
         int j = i & 15;
         int k = i >> 8 & 15;
         int l = i >> 16 & 255;
         BlockPos blockpos = new BlockPos(j + p_147467_1_, l, k + p_147467_2_);
         IBlockState iblockstate = p_147467_3_.getBlockState(blockpos);
         j = j + p_147467_1_;
         k = k + p_147467_2_;
         if (iblockstate.isAir() && this.getLightSubtracted(blockpos, 0) <= this.rand.nextInt(8) && this.getLightFor(EnumLightType.SKY, blockpos) <= 0) {
            double d0 = this.mc.player.getDistanceSq((double)j + 0.5D, (double)l + 0.5D, (double)k + 0.5D);
            if (this.mc.player != null && d0 > 4.0D && d0 < 256.0D) {
               this.playSound((double)j + 0.5D, (double)l + 0.5D, (double)k + 0.5D, SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7F, 0.8F + this.rand.nextFloat() * 0.2F, false);
               this.ambienceTicks = this.rand.nextInt(12000) + 6000;
            }
         }
      }

   }

   public void animateTick(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
      int i = 32;
      Random random = new Random();
      ItemStack itemstack = this.mc.player.getHeldItemMainhand();
      boolean flag = this.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Blocks.BARRIER.asItem();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = 0; j < 667; ++j) {
         this.animateTick(p_73029_1_, p_73029_2_, p_73029_3_, 16, random, flag, blockpos$mutableblockpos);
         this.animateTick(p_73029_1_, p_73029_2_, p_73029_3_, 32, random, flag, blockpos$mutableblockpos);
      }

   }

   public void animateTick(int p_184153_1_, int p_184153_2_, int p_184153_3_, int p_184153_4_, Random p_184153_5_, boolean p_184153_6_, BlockPos.MutableBlockPos p_184153_7_) {
      int i = p_184153_1_ + this.rand.nextInt(p_184153_4_) - this.rand.nextInt(p_184153_4_);
      int j = p_184153_2_ + this.rand.nextInt(p_184153_4_) - this.rand.nextInt(p_184153_4_);
      int k = p_184153_3_ + this.rand.nextInt(p_184153_4_) - this.rand.nextInt(p_184153_4_);
      p_184153_7_.setPos(i, j, k);
      IBlockState iblockstate = this.getBlockState(p_184153_7_);
      iblockstate.getBlock().animateTick(iblockstate, this, p_184153_7_, p_184153_5_);
      IFluidState ifluidstate = this.getFluidState(p_184153_7_);
      if (!ifluidstate.isEmpty()) {
         ifluidstate.animateTick(this, p_184153_7_, p_184153_5_);
         IParticleData iparticledata = ifluidstate.getDripParticleData();
         if (iparticledata != null && this.rand.nextInt(10) == 0) {
            boolean flag = iblockstate.getBlockFaceShape(this, p_184153_7_, EnumFacing.DOWN) == BlockFaceShape.SOLID;
            BlockPos blockpos = p_184153_7_.down();
            this.func_211530_a(blockpos, this.getBlockState(blockpos), iparticledata, flag);
         }
      }

      if (p_184153_6_ && iblockstate.getBlock() == Blocks.BARRIER) {
         this.spawnParticle(Particles.BARRIER, (double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), 0.0D, 0.0D, 0.0D);
      }

   }

   private void func_211530_a(BlockPos p_211530_1_, IBlockState p_211530_2_, IParticleData p_211530_3_, boolean p_211530_4_) {
      if (p_211530_2_.getFluidState().isEmpty()) {
         VoxelShape voxelshape = p_211530_2_.getCollisionShape(this, p_211530_1_);
         double d0 = voxelshape.getEnd(EnumFacing.Axis.Y);
         if (d0 < 1.0D) {
            if (p_211530_4_) {
               this.func_211834_a((double)p_211530_1_.getX(), (double)(p_211530_1_.getX() + 1), (double)p_211530_1_.getZ(), (double)(p_211530_1_.getZ() + 1), (double)(p_211530_1_.getY() + 1) - 0.05D, p_211530_3_);
            }
         } else if (!p_211530_2_.isIn(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.getStart(EnumFacing.Axis.Y);
            if (d1 > 0.0D) {
               this.func_211835_a(p_211530_1_, p_211530_3_, voxelshape, (double)p_211530_1_.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = p_211530_1_.down();
               IBlockState iblockstate = this.getBlockState(blockpos);
               VoxelShape voxelshape1 = iblockstate.getCollisionShape(this, blockpos);
               double d2 = voxelshape1.getEnd(EnumFacing.Axis.Y);
               if (d2 < 1.0D && iblockstate.getFluidState().isEmpty()) {
                  this.func_211835_a(p_211530_1_, p_211530_3_, voxelshape, (double)p_211530_1_.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void func_211835_a(BlockPos p_211835_1_, IParticleData p_211835_2_, VoxelShape p_211835_3_, double p_211835_4_) {
      this.func_211834_a((double)p_211835_1_.getX() + p_211835_3_.getStart(EnumFacing.Axis.X), (double)p_211835_1_.getX() + p_211835_3_.getEnd(EnumFacing.Axis.X), (double)p_211835_1_.getZ() + p_211835_3_.getStart(EnumFacing.Axis.Z), (double)p_211835_1_.getZ() + p_211835_3_.getEnd(EnumFacing.Axis.Z), p_211835_4_, p_211835_2_);
   }

   private void func_211834_a(double p_211834_1_, double p_211834_3_, double p_211834_5_, double p_211834_7_, double p_211834_9_, IParticleData p_211834_11_) {
      this.spawnParticle(p_211834_11_, p_211834_1_ + (p_211834_3_ - p_211834_1_) * this.rand.nextDouble(), p_211834_9_, p_211834_5_ + (p_211834_7_ - p_211834_5_) * this.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
   }

   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for(int i = 0; i < this.unloadedEntityList.size(); ++i) {
         Entity entity = this.unloadedEntityList.get(i);
         int j = entity.chunkCoordX;
         int k = entity.chunkCoordZ;
         if (entity.addedToChunk && this.isChunkLoaded(j, k, true)) {
            this.getChunk(j, k).removeEntity(entity);
         }
      }

      for(int i1 = 0; i1 < this.unloadedEntityList.size(); ++i1) {
         this.onEntityRemoved(this.unloadedEntityList.get(i1));
      }

      this.unloadedEntityList.clear();

      for(int j1 = 0; j1 < this.loadedEntityList.size(); ++j1) {
         Entity entity1 = this.loadedEntityList.get(j1);
         Entity entity2 = entity1.getRidingEntity();
         if (entity2 != null) {
            if (!entity2.isDead && entity2.isPassenger(entity1)) {
               continue;
            }

            entity1.dismountRidingEntity();
         }

         if (entity1.isDead) {
            int k1 = entity1.chunkCoordX;
            int l = entity1.chunkCoordZ;
            if (entity1.addedToChunk && this.isChunkLoaded(k1, l, true)) {
               this.getChunk(k1, l).removeEntity(entity1);
            }

            this.loadedEntityList.remove(j1--);
            this.onEntityRemoved(entity1);
         }
      }

   }

   public CrashReportCategory addWorldInfoToCrashReport(CrashReport p_72914_1_) {
      CrashReportCategory crashreportcategory = super.addWorldInfoToCrashReport(p_72914_1_);
      crashreportcategory.addDetail("Forced entities", () -> {
         return this.entityList.size() + " total; " + this.entityList;
      });
      crashreportcategory.addDetail("Retry entities", () -> {
         return this.entitySpawnQueue.size() + " total; " + this.entitySpawnQueue;
      });
      crashreportcategory.addDetail("Server brand", () -> {
         return this.mc.player.getServerBrand();
      });
      crashreportcategory.addDetail("Server type", () -> {
         return this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return crashreportcategory;
   }

   public void playSound(@Nullable EntityPlayer p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
      if (p_184148_1_ == this.mc.player) {
         this.playSound(p_184148_2_, p_184148_4_, p_184148_6_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_, false);
      }

   }

   public void playSound(BlockPos p_184156_1_, SoundEvent p_184156_2_, SoundCategory p_184156_3_, float p_184156_4_, float p_184156_5_, boolean p_184156_6_) {
      this.playSound((double)p_184156_1_.getX() + 0.5D, (double)p_184156_1_.getY() + 0.5D, (double)p_184156_1_.getZ() + 0.5D, p_184156_2_, p_184156_3_, p_184156_4_, p_184156_5_, p_184156_6_);
   }

   public void playSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
      double d0 = this.mc.getRenderViewEntity().getDistanceSq(p_184134_1_, p_184134_3_, p_184134_5_);
      SimpleSound simplesound = new SimpleSound(p_184134_7_, p_184134_8_, p_184134_9_, p_184134_10_, (float)p_184134_1_, (float)p_184134_3_, (float)p_184134_5_);
      if (p_184134_11_ && d0 > 100.0D) {
         double d1 = Math.sqrt(d0) / 40.0D;
         this.mc.getSoundHandler().playDelayed(simplesound, (int)(d1 * 20.0D));
      } else {
         this.mc.getSoundHandler().play(simplesound);
      }

   }

   public void makeFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable NBTTagCompound p_92088_13_) {
      this.mc.effectRenderer.addEffect(new ParticleFirework.Starter(this, p_92088_1_, p_92088_3_, p_92088_5_, p_92088_7_, p_92088_9_, p_92088_11_, this.mc.effectRenderer, p_92088_13_));
   }

   public void sendPacketToServer(Packet<?> p_184135_1_) {
      this.connection.sendPacket(p_184135_1_);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setWorldScoreboard(Scoreboard p_96443_1_) {
      this.scoreboard = p_96443_1_;
   }

   public void setWorldTime(long p_72877_1_) {
      if (p_72877_1_ < 0L) {
         p_72877_1_ = -p_72877_1_;
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false", null);
      } else {
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true", null);
      }

      super.setWorldTime(p_72877_1_);
   }

   public ITickList<Block> getPendingBlockTicks() {
      return EmptyTickList.get();
   }

   public ITickList<Fluid> getPendingFluidTicks() {
      return EmptyTickList.get();
   }

   public ChunkProviderClient getChunkProvider() {
      return (ChunkProviderClient)super.getChunkProvider();
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public NetworkTagManager getTags() {
      return this.connection.getTags();
   }
}
