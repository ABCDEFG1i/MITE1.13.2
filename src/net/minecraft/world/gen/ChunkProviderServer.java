package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.TaskManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.tasks.ProtoChunkScheduler;
import net.minecraft.world.storage.SessionLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LongSet droppedChunks = new LongOpenHashSet();
   public final IChunkGenerator<?> chunkGenerator;
   public final IChunkLoader chunkLoader;
   public final Long2ObjectMap<Chunk> loadedChunks = Long2ObjectMaps.synchronize(new ChunkCacheNeighborNotification(8192));
   private Chunk field_212472_f;
   private final ProtoChunkScheduler chunkScheduler;
   private final TaskManager<ChunkPos, ChunkStatus, ChunkPrimer> taskManager;
   public final WorldServer world;
   private final IThreadListener field_212473_j;

   public ChunkProviderServer(WorldServer p_i48731_1_, IChunkLoader p_i48731_2_, IChunkGenerator<?> p_i48731_3_, IThreadListener p_i48731_4_) {
      this.world = p_i48731_1_;
      this.chunkLoader = p_i48731_2_;
      this.chunkGenerator = p_i48731_3_;
      this.field_212473_j = p_i48731_4_;
      this.chunkScheduler = new ProtoChunkScheduler(2, p_i48731_1_, p_i48731_3_, p_i48731_2_, p_i48731_4_);
      this.taskManager = new TaskManager<>(this.chunkScheduler);
   }

   public Collection<Chunk> getLoadedChunks() {
      return this.loadedChunks.values();
   }

   public void queueUnload(Chunk p_189549_1_) {
      if (this.world.dimension.canDropChunk(p_189549_1_.x, p_189549_1_.z)) {
         this.droppedChunks.add(ChunkPos.asLong(p_189549_1_.x, p_189549_1_.z));
      }

   }

   public void queueUnloadAll() {
      for(Chunk chunk : this.loadedChunks.values()) {
         this.queueUnload(chunk);
      }

   }

   public void func_212469_a(int p_212469_1_, int p_212469_2_) {
      this.droppedChunks.remove(ChunkPos.asLong(p_212469_1_, p_212469_2_));
   }

   @Nullable
   public Chunk func_186025_d(int p_186025_1_, int p_186025_2_, boolean p_186025_3_, boolean p_186025_4_) {
      Chunk chunk;
      synchronized(this.chunkLoader) {
         if (this.field_212472_f != null && this.field_212472_f.getPos().x == p_186025_1_ && this.field_212472_f.getPos().z == p_186025_2_) {
            return this.field_212472_f;
         }

         long i = ChunkPos.asLong(p_186025_1_, p_186025_2_);
         chunk = this.loadedChunks.get(i);
         if (chunk != null) {
            this.field_212472_f = chunk;
            return chunk;
         }

         if (p_186025_3_) {
            try {
               chunk = this.chunkLoader.loadChunk(this.world, p_186025_1_, p_186025_2_, (p_212471_3_) -> {
                  p_212471_3_.setLastSaveTime(this.world.getTotalWorldTime());
                  this.loadedChunks.put(ChunkPos.asLong(p_186025_1_, p_186025_2_), p_212471_3_);
               });
            } catch (Exception exception) {
               LOGGER.error("Couldn't load chunk", (Throwable)exception);
            }
         }
      }

      if (chunk != null) {
         this.field_212473_j.addScheduledTask(chunk::onLoad);
         return chunk;
      } else if (p_186025_4_) {
         try {
            this.taskManager.startBatch();
            this.taskManager.addToBatch(new ChunkPos(p_186025_1_, p_186025_2_));
            CompletableFuture<ChunkPrimer> completablefuture = this.taskManager.finishBatch();
            return completablefuture.thenApply(this::convertToChunk).join();
         } catch (RuntimeException runtimeexception) {
            throw this.makeReportedException(p_186025_1_, p_186025_2_, runtimeexception);
         }
      } else {
         return null;
      }
   }

   public IChunk func_201713_d(int p_201713_1_, int p_201713_2_, boolean p_201713_3_) {
      IChunk ichunk = this.func_186025_d(p_201713_1_, p_201713_2_, true, false);
      return ichunk != null ? ichunk : this.chunkScheduler.func_212537_b(new ChunkPos(p_201713_1_, p_201713_2_), p_201713_3_);
   }

   public CompletableFuture<ChunkPrimer> loadChunks(Iterable<ChunkPos> p_201720_1_, Consumer<Chunk> p_201720_2_) {
      this.taskManager.startBatch();

      for(ChunkPos chunkpos : p_201720_1_) {
         Chunk chunk = this.func_186025_d(chunkpos.x, chunkpos.z, true, false);
         if (chunk != null) {
            p_201720_2_.accept(chunk);
         } else {
            this.taskManager.addToBatch(chunkpos).thenApply(this::convertToChunk).thenAccept(p_201720_2_);
         }
      }

      return this.taskManager.finishBatch();
   }

   private ReportedException makeReportedException(int p_201722_1_, int p_201722_2_, Throwable p_201722_3_) {
      CrashReport crashreport = CrashReport.makeCrashReport(p_201722_3_, "Exception generating new chunk");
      CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
      crashreportcategory.addCrashSection("Location", String.format("%d,%d", p_201722_1_, p_201722_2_));
      crashreportcategory.addCrashSection("Position hash", ChunkPos.asLong(p_201722_1_, p_201722_2_));
      crashreportcategory.addCrashSection("Generator", this.chunkGenerator);
      return new ReportedException(crashreport);
   }

   private Chunk convertToChunk(IChunk p_201719_1_) {
      ChunkPos chunkpos = p_201719_1_.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      long k = ChunkPos.asLong(i, j);
      Chunk chunk;
      synchronized(this.loadedChunks) {
         Chunk chunk1 = this.loadedChunks.get(k);
         if (chunk1 != null) {
            return chunk1;
         }

         if (p_201719_1_ instanceof Chunk) {
            chunk = (Chunk)p_201719_1_;
         } else {
            if (!(p_201719_1_ instanceof ChunkPrimer)) {
               throw new IllegalStateException();
            }

            chunk = new Chunk(this.world, (ChunkPrimer)p_201719_1_, i, j);
         }

         this.loadedChunks.put(k, chunk);
         this.field_212472_f = chunk;
      }

      this.field_212473_j.addScheduledTask(chunk::onLoad);
      return chunk;
   }

   private void saveChunkData(IChunk p_73242_1_) {
      try {
         p_73242_1_.setLastSaveTime(this.world.getTotalWorldTime());
         this.chunkLoader.saveChunk(this.world, p_73242_1_);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save chunk", (Throwable)ioexception);
      } catch (SessionLockException sessionlockexception) {
         LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)sessionlockexception);
      }

   }

   public boolean saveChunks(boolean p_186027_1_) {
      int i = 0;
      this.chunkScheduler.func_208484_a(() -> {
         return true;
      });
      synchronized(this.chunkLoader) {
         for(Chunk chunk : this.loadedChunks.values()) {
            if (chunk.needsSaving(p_186027_1_)) {
               this.saveChunkData(chunk);
               chunk.setModified(false);
               ++i;
               if (i == 24 && !p_186027_1_) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public void close() {
      try {
         this.taskManager.shutdown();
      } catch (InterruptedException interruptedexception) {
         LOGGER.error("Couldn't stop taskManager", (Throwable)interruptedexception);
      }

   }

   public void flushToDisk() {
      synchronized(this.chunkLoader) {
         this.chunkLoader.flush();
      }
   }

   public boolean func_73156_b(BooleanSupplier p_73156_1_) {
      if (!this.world.disableLevelSaving) {
         if (!this.droppedChunks.isEmpty()) {
            Iterator<Long> iterator = this.droppedChunks.iterator();

            for(int i = 0; iterator.hasNext() && (p_73156_1_.getAsBoolean() || i < 200 || this.droppedChunks.size() > 2000); iterator.remove()) {
               Long olong = iterator.next();
               synchronized(this.chunkLoader) {
                  Chunk chunk = this.loadedChunks.get(olong);
                  if (chunk != null) {
                     chunk.onUnload();
                     this.saveChunkData(chunk);
                     this.loadedChunks.remove(olong);
                     this.field_212472_f = null;
                     ++i;
                  }
               }
            }
         }

         this.chunkScheduler.func_208484_a(p_73156_1_);
      }

      return false;
   }

   public boolean canSave() {
      return !this.world.disableLevelSaving;
   }

   public String makeString() {
      return "ServerChunkCache: " + this.loadedChunks.size() + " Drop: " + this.droppedChunks.size();
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType p_177458_1_, BlockPos p_177458_2_) {
      return this.chunkGenerator.getPossibleCreatures(p_177458_1_, p_177458_2_);
   }

   public int spawnMobs(World p_203082_1_, boolean p_203082_2_, boolean p_203082_3_) {
      return this.chunkGenerator.spawnMobs(p_203082_1_, p_203082_2_, p_203082_3_);
   }

   @Nullable
   public BlockPos func_211268_a(World p_211268_1_, String p_211268_2_, BlockPos p_211268_3_, int p_211268_4_, boolean p_211268_5_) {
      return this.chunkGenerator.func_211403_a(p_211268_1_, p_211268_2_, p_211268_3_, p_211268_4_, p_211268_5_);
   }

   public IChunkGenerator<?> getChunkGenerator() {
      return this.chunkGenerator;
   }

   public int getLoadedChunkCount() {
      return this.loadedChunks.size();
   }

   public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
      return this.loadedChunks.containsKey(ChunkPos.asLong(p_73149_1_, p_73149_2_));
   }
}
