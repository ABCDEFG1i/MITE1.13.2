package net.minecraft.world.gen.tasks;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ExpiringMap;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Scheduler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.SessionLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Scheduler.FutureWrapper;

public class ProtoChunkScheduler extends Scheduler<ChunkPos, ChunkStatus, ChunkPrimer> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final World world;
   private final IChunkGenerator<?> chunkGenerator;
   private final IChunkLoader chunkLoader;
   private final IThreadListener threadListener;
   private final Long2ObjectMap<Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper> scheduledChunkMap = new ExpiringMap<Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper>(8192, 5000) {
      protected boolean shouldExpire(Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper p_205609_1_) {
         ChunkPrimer chunkprimer = p_205609_1_.getResult();
         return !chunkprimer.isAlive() && !chunkprimer.isModified();
      }
   };

   public ProtoChunkScheduler(int p_i48728_1_, World p_i48728_2_, IChunkGenerator<?> p_i48728_3_, IChunkLoader p_i48728_4_, IThreadListener p_i48728_5_) {
      super("WorldGen", p_i48728_1_, ChunkStatus.FINALIZED, () -> {
         return new EnumMap<>(ChunkStatus.class);
      }, () -> {
         return new EnumMap<>(ChunkStatus.class);
      });
      this.world = p_i48728_2_;
      this.chunkGenerator = p_i48728_3_;
      this.chunkLoader = p_i48728_4_;
      this.threadListener = p_i48728_5_;
   }

   @Nullable
   protected Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper func_212252_a_(ChunkPos p_212252_1_, boolean p_212252_2_) {
      synchronized(this.chunkLoader) {
         return p_212252_2_ ? this.scheduledChunkMap.computeIfAbsent(p_212252_1_.asLong(), (p_212539_2_) -> {
            ChunkPrimer chunkprimer;
            try {
               chunkprimer = this.chunkLoader.loadChunkPrimer(this.world, p_212252_1_.x, p_212252_1_.z, (p_212538_0_) -> {
               });
            } catch (ReportedException reportedexception) {
               throw reportedexception;
            } catch (Exception exception) {
               LOGGER.error("Couldn't load protochunk", (Throwable)exception);
               chunkprimer = null;
            }

            if (chunkprimer != null) {
               chunkprimer.setLastSaveTime(this.world.getTotalWorldTime());
               return new Scheduler.FutureWrapper(p_212252_1_, chunkprimer, chunkprimer.getStatus());
            } else {
               return new Scheduler.FutureWrapper(p_212252_1_, new ChunkPrimer(p_212252_1_, UpgradeData.EMPTY), ChunkStatus.EMPTY);
            }
         }) : this.scheduledChunkMap.get(p_212252_1_.asLong());
      }
   }

   protected ChunkPrimer runTask(ChunkPos p_201493_1_, ChunkStatus p_201493_2_, Map<ChunkPos, ChunkPrimer> p_201493_3_) {
      return p_201493_2_.runTask(this.world, this.chunkGenerator, p_201493_3_, p_201493_1_.x, p_201493_1_.z);
   }

   protected Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper onTaskStart(ChunkPos p_205606_1_, Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper p_205606_2_) {
      p_205606_2_.getResult().addRefCount(1);
      return p_205606_2_;
   }

   protected void onTaskFinish(ChunkPos p_205607_1_, Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper p_205607_2_) {
      p_205607_2_.getResult().addRefCount(-1);
   }

   public void func_208484_a(BooleanSupplier p_208484_1_) {
      synchronized(this.chunkLoader) {
         for(Scheduler<ChunkPos, ChunkStatus, ChunkPrimer>.FutureWrapper scheduler : this.scheduledChunkMap.values()) {
            ChunkPrimer chunkprimer = scheduler.getResult();
            if (chunkprimer.isModified() && chunkprimer.getStatus().getType() == ChunkStatus.Type.PROTOCHUNK) {
               try {
                  chunkprimer.setLastSaveTime(this.world.getTotalWorldTime());
                  this.chunkLoader.saveChunk(this.world, chunkprimer);
                  chunkprimer.setModified(false);
               } catch (IOException ioexception) {
                  LOGGER.error("Couldn't save chunk", (Throwable)ioexception);
               } catch (SessionLockException sessionlockexception) {
                  LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)sessionlockexception);
               }
            }

            if (!p_208484_1_.getAsBoolean()) {
               return;
            }
         }

      }
   }
}
