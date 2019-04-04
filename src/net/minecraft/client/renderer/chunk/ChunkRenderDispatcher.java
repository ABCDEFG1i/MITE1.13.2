package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build();
   private final int countRenderBuilders;
   private final List<Thread> listWorkerThreads = Lists.newArrayList();
   private final List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
   private final PriorityBlockingQueue<ChunkRenderTask> queueChunkUpdates = Queues.newPriorityBlockingQueue();
   private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
   private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
   private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
   private final Queue<ChunkRenderDispatcher.PendingUpload> queueChunkUploads = Queues.newPriorityQueue();
   private final ChunkRenderWorker renderWorker;

   public ChunkRenderDispatcher() {
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / 10485760);
      int j = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, i / 5));
      this.countRenderBuilders = MathHelper.clamp(j * 10, 1, i);
      if (j > 1) {
         for(int k = 0; k < j; ++k) {
            ChunkRenderWorker chunkrenderworker = new ChunkRenderWorker(this);
            Thread thread = THREAD_FACTORY.newThread(chunkrenderworker);
            thread.start();
            this.listThreadedWorkers.add(chunkrenderworker);
            this.listWorkerThreads.add(thread);
         }
      }

      this.queueFreeRenderBuilders = Queues.newArrayBlockingQueue(this.countRenderBuilders);

      for(int l = 0; l < this.countRenderBuilders; ++l) {
         this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
      }

      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String getDebugInfo() {
      return this.listWorkerThreads.isEmpty() ? String.format("pC: %03d, single-threaded", this.queueChunkUpdates.size()) : String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public boolean runChunkUploads(long p_178516_1_) {
      boolean flag = false;

      while(true) {
         boolean flag1 = false;
         if (this.listWorkerThreads.isEmpty()) {
            ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
            if (chunkrendertask != null) {
               try {
                  this.renderWorker.processTask(chunkrendertask);
                  flag1 = true;
               } catch (InterruptedException var8) {
                  LOGGER.warn("Skipped task due to interrupt");
               }
            }
         }

         synchronized(this.queueChunkUploads) {
            if (!this.queueChunkUploads.isEmpty()) {
               (this.queueChunkUploads.poll()).uploadTask.run();
               flag1 = true;
               flag = true;
            }
         }

         if (p_178516_1_ == 0L || !flag1 || p_178516_1_ < Util.nanoTime()) {
            break;
         }
      }

      return flag;
   }

   public boolean updateChunkLater(RenderChunk p_178507_1_) {
      p_178507_1_.getLockCompileTask().lock();

      boolean flag1;
      try {
         ChunkRenderTask chunkrendertask = p_178507_1_.makeCompileTaskChunk();
         chunkrendertask.addFinishRunnable(() -> {
            this.queueChunkUpdates.remove(chunkrendertask);
         });
         boolean flag = this.queueChunkUpdates.offer(chunkrendertask);
         if (!flag) {
            chunkrendertask.finish();
         }

         flag1 = flag;
      } finally {
         p_178507_1_.getLockCompileTask().unlock();
      }

      return flag1;
   }

   public boolean updateChunkNow(RenderChunk p_178505_1_) {
      p_178505_1_.getLockCompileTask().lock();

      boolean flag;
      try {
         ChunkRenderTask chunkrendertask = p_178505_1_.makeCompileTaskChunk();

         try {
            this.renderWorker.processTask(chunkrendertask);
         } catch (InterruptedException var7) {
         }

         flag = true;
      } finally {
         p_178505_1_.getLockCompileTask().unlock();
      }

      return flag;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();
      List<RegionRenderCacheBuilder> list = Lists.newArrayList();

      while(list.size() != this.countRenderBuilders) {
         this.runChunkUploads(Long.MAX_VALUE);

         try {
            list.add(this.allocateRenderBuilder());
         } catch (InterruptedException var3) {
         }
      }

      this.queueFreeRenderBuilders.addAll(list);
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder p_178512_1_) {
      this.queueFreeRenderBuilders.add(p_178512_1_);
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.take();
   }

   public ChunkRenderTask getNextChunkUpdate() throws InterruptedException {
      return this.queueChunkUpdates.take();
   }

   public boolean updateTransparencyLater(RenderChunk p_178509_1_) {
      p_178509_1_.getLockCompileTask().lock();

      boolean flag;
      try {
         ChunkRenderTask chunkrendertask = p_178509_1_.makeCompileTaskTransparency();
         if (chunkrendertask == null) {
            flag = true;
            return flag;
         }

         chunkrendertask.addFinishRunnable(() -> {
            this.queueChunkUpdates.remove(chunkrendertask);
         });
         flag = this.queueChunkUpdates.offer(chunkrendertask);
      } finally {
         p_178509_1_.getLockCompileTask().unlock();
      }

      return flag;
   }

   public ListenableFuture<Object> uploadChunk(BlockRenderLayer p_188245_1_, BufferBuilder p_188245_2_, RenderChunk p_188245_3_, CompiledChunk p_188245_4_, double p_188245_5_) {
      if (Minecraft.getInstance().isCallingFromMinecraftThread()) {
         if (OpenGlHelper.useVbo()) {
            this.uploadVertexBuffer(p_188245_2_, p_188245_3_.getVertexBufferByLayer(p_188245_1_.ordinal()));
         } else {
            this.uploadDisplayList(p_188245_2_, ((ListedRenderChunk)p_188245_3_).getDisplayList(p_188245_1_, p_188245_4_), p_188245_3_);
         }

         p_188245_2_.setTranslation(0.0D, 0.0D, 0.0D);
         return Futures.immediateFuture(null);
      } else {
         ListenableFutureTask<Object> listenablefuturetask = ListenableFutureTask.create(() -> {
            this.uploadChunk(p_188245_1_, p_188245_2_, p_188245_3_, p_188245_4_, p_188245_5_);
         }, null);
         synchronized(this.queueChunkUploads) {
            this.queueChunkUploads.add(new ChunkRenderDispatcher.PendingUpload(listenablefuturetask, p_188245_5_));
            return listenablefuturetask;
         }
      }
   }

   private void uploadDisplayList(BufferBuilder p_178510_1_, int p_178510_2_, RenderChunk p_178510_3_) {
      GlStateManager.newList(p_178510_2_, 4864);
      GlStateManager.pushMatrix();
      p_178510_3_.multModelviewMatrix();
      this.worldVertexUploader.draw(p_178510_1_);
      GlStateManager.popMatrix();
      GlStateManager.endList();
   }

   private void uploadVertexBuffer(BufferBuilder p_178506_1_, VertexBuffer p_178506_2_) {
      this.vertexUploader.setVertexBuffer(p_178506_2_);
      this.vertexUploader.draw(p_178506_1_);
   }

   public void clearChunkUpdates() {
      while(!this.queueChunkUpdates.isEmpty()) {
         ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
         if (chunkrendertask != null) {
            chunkrendertask.finish();
         }
      }

   }

   public boolean hasNoChunkUpdates() {
      return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
   }

   public void stopWorkerThreads() {
      this.clearChunkUpdates();

      for(ChunkRenderWorker chunkrenderworker : this.listThreadedWorkers) {
         chunkrenderworker.notifyToStop();
      }

      for(Thread thread : this.listWorkerThreads) {
         try {
            thread.interrupt();
            thread.join();
         } catch (InterruptedException interruptedexception) {
            LOGGER.warn("Interrupted whilst waiting for worker to die", interruptedexception);
         }
      }

      this.queueFreeRenderBuilders.clear();
   }

   public boolean hasNoFreeRenderBuilders() {
      return this.queueFreeRenderBuilders.isEmpty();
   }

   @OnlyIn(Dist.CLIENT)
   class PendingUpload implements Comparable<ChunkRenderDispatcher.PendingUpload> {
      private final ListenableFutureTask<Object> uploadTask;
      private final double distanceSq;

      public PendingUpload(ListenableFutureTask<Object> p_i46994_2_, double p_i46994_3_) {
         this.uploadTask = p_i46994_2_;
         this.distanceSq = p_i46994_3_;
      }

      public int compareTo(ChunkRenderDispatcher.PendingUpload p_compareTo_1_) {
         return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
      }
   }
}
