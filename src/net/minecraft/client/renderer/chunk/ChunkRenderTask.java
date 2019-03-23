package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
   private final RenderChunk renderChunk;
   private final ReentrantLock lock = new ReentrantLock();
   private final List<Runnable> listFinishRunnables = Lists.newArrayList();
   private final ChunkRenderTask.Type type;
   private final double distanceSq;
   private RegionRenderCacheBuilder regionRenderCacheBuilder;
   private CompiledChunk compiledChunk;
   private ChunkRenderTask.Status status = ChunkRenderTask.Status.PENDING;
   private boolean finished;

   public ChunkRenderTask(RenderChunk p_i46560_1_, ChunkRenderTask.Type p_i46560_2_, double p_i46560_3_) {
      this.renderChunk = p_i46560_1_;
      this.type = p_i46560_2_;
      this.distanceSq = p_i46560_3_;
   }

   public ChunkRenderTask.Status getStatus() {
      return this.status;
   }

   public RenderChunk getRenderChunk() {
      return this.renderChunk;
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk p_178543_1_) {
      this.compiledChunk = p_178543_1_;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder p_178541_1_) {
      this.regionRenderCacheBuilder = p_178541_1_;
   }

   public void setStatus(ChunkRenderTask.Status p_178535_1_) {
      this.lock.lock();

      try {
         this.status = p_178535_1_;
      } finally {
         this.lock.unlock();
      }

   }

   public void finish() {
      this.lock.lock();

      try {
         if (this.type == ChunkRenderTask.Type.REBUILD_CHUNK && this.status != ChunkRenderTask.Status.DONE) {
            this.renderChunk.setNeedsUpdate(false);
         }

         this.finished = true;
         this.status = ChunkRenderTask.Status.DONE;

         for(Runnable runnable : this.listFinishRunnables) {
            runnable.run();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public void addFinishRunnable(Runnable p_178539_1_) {
      this.lock.lock();

      try {
         this.listFinishRunnables.add(p_178539_1_);
         if (this.finished) {
            p_178539_1_.run();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public ReentrantLock getLock() {
      return this.lock;
   }

   public ChunkRenderTask.Type getType() {
      return this.type;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public int compareTo(ChunkRenderTask p_compareTo_1_) {
      return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
   }

   public double getDistanceSq() {
      return this.distanceSq;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
}
