package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;
   private boolean shouldRun = true;

   public ChunkRenderWorker(ChunkRenderDispatcher p_i46201_1_) {
      this(p_i46201_1_, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher p_i46202_1_, @Nullable RegionRenderCacheBuilder p_i46202_2_) {
      this.chunkRenderDispatcher = p_i46202_1_;
      this.regionRenderCacheBuilder = p_i46202_2_;
   }

   public void run() {
      while(this.shouldRun) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException var3) {
            LOGGER.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Batching chunks");
            Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
            return;
         }
      }

   }

   protected void processTask(final ChunkRenderTask p_178474_1_) throws InterruptedException {
      p_178474_1_.getLock().lock();

      try {
         if (p_178474_1_.getStatus() != ChunkRenderTask.Status.PENDING) {
            if (!p_178474_1_.isFinished()) {
               LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", (Object)p_178474_1_.getStatus());
            }

            return;
         }

         BlockPos blockpos = new BlockPos(Minecraft.getInstance().player);
         BlockPos blockpos1 = p_178474_1_.getRenderChunk().getPosition();
         int i = 16;
         int j = 8;
         int k = 24;
         if (blockpos1.add(8, 8, 8).distanceSq(blockpos) > 576.0D) {
            World world = p_178474_1_.getRenderChunk().getWorld();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(blockpos1);
            if (!this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.WEST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.NORTH, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.EAST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.SOUTH, 16), world)) {
               return;
            }
         }

         p_178474_1_.setStatus(ChunkRenderTask.Status.COMPILING);
      } finally {
         p_178474_1_.getLock().unlock();
      }

      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      if (entity == null) {
         p_178474_1_.finish();
      } else {
         p_178474_1_.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
         Vec3d vec3d = ActiveRenderInfo.projectViewFromEntity(entity, 1.0D);
         float f = (float)vec3d.x;
         float f1 = (float)vec3d.y;
         float f2 = (float)vec3d.z;
         ChunkRenderTask.Type chunkrendertask$type = p_178474_1_.getType();
         if (chunkrendertask$type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            p_178474_1_.getRenderChunk().rebuildChunk(f, f1, f2, p_178474_1_);
         } else if (chunkrendertask$type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            p_178474_1_.getRenderChunk().resortTransparency(f, f1, f2, p_178474_1_);
         }

         p_178474_1_.getLock().lock();

         try {
            if (p_178474_1_.getStatus() != ChunkRenderTask.Status.COMPILING) {
               if (!p_178474_1_.isFinished()) {
                  LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", (Object)p_178474_1_.getStatus());
               }

               this.freeRenderBuilder(p_178474_1_);
               return;
            }

            p_178474_1_.setStatus(ChunkRenderTask.Status.UPLOADING);
         } finally {
            p_178474_1_.getLock().unlock();
         }

         final CompiledChunk compiledchunk = p_178474_1_.getCompiledChunk();
         ArrayList lvt_9_1_ = Lists.newArrayList();
         if (chunkrendertask$type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
               if (compiledchunk.isLayerStarted(blockrenderlayer)) {
                  lvt_9_1_.add(this.chunkRenderDispatcher.uploadChunk(blockrenderlayer, p_178474_1_.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), p_178474_1_.getRenderChunk(), compiledchunk, p_178474_1_.getDistanceSq()));
               }
            }
         } else if (chunkrendertask$type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            lvt_9_1_.add(this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT, p_178474_1_.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), p_178474_1_.getRenderChunk(), compiledchunk, p_178474_1_.getDistanceSq()));
         }

         ListenableFuture<List<Object>> listenablefuture = Futures.allAsList(lvt_9_1_);
         p_178474_1_.addFinishRunnable(() -> {
            listenablefuture.cancel(false);
         });
         Futures.addCallback(listenablefuture, new FutureCallback<List<Object>>() {
            public void onSuccess(@Nullable List<Object> p_onSuccess_1_) {
               ChunkRenderWorker.this.freeRenderBuilder(p_178474_1_);
               p_178474_1_.getLock().lock();

               label49: {
                  try {
                     if (p_178474_1_.getStatus() == ChunkRenderTask.Status.UPLOADING) {
                        p_178474_1_.setStatus(ChunkRenderTask.Status.DONE);
                        break label49;
                     }

                     if (!p_178474_1_.isFinished()) {
                        ChunkRenderWorker.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", (Object)p_178474_1_.getStatus());
                     }
                  } finally {
                     p_178474_1_.getLock().unlock();
                  }

                  return;
               }

               p_178474_1_.getRenderChunk().setCompiledChunk(compiledchunk);
            }

            public void onFailure(Throwable p_onFailure_1_) {
               ChunkRenderWorker.this.freeRenderBuilder(p_178474_1_);
               if (!(p_onFailure_1_ instanceof CancellationException) && !(p_onFailure_1_ instanceof InterruptedException)) {
                  Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_onFailure_1_, "Rendering chunk"));
               }

            }
         });
      }
   }

   private boolean isChunkExisting(BlockPos p_188263_1_, World p_188263_2_) {
      return !p_188263_2_.getChunk(p_188263_1_.getX() >> 4, p_188263_1_.getZ() >> 4).isEmpty();
   }

   private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   private void freeRenderBuilder(ChunkRenderTask p_178473_1_) {
      if (this.regionRenderCacheBuilder == null) {
         this.chunkRenderDispatcher.freeRenderBuilder(p_178473_1_.getRegionRenderCacheBuilder());
      }

   }

   public void notifyToStop() {
      this.shouldRun = false;
   }
}
