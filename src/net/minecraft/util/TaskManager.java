package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskManager<K, T extends ITaskType<K, T>, R> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Scheduler<K, T, R> scheduler;
   private boolean batchStarted;
   private int remainingBatchSize = 1000;

   public TaskManager(Scheduler<K, T, R> p_i48723_1_) {
      this.scheduler = p_i48723_1_;
   }

   public void shutdown() throws InterruptedException {
      this.scheduler.shutdown();
   }

   public void startBatch() {
      if (this.batchStarted) {
         throw new RuntimeException("Batch already started.");
      } else {
         this.remainingBatchSize = 1000;
         this.batchStarted = true;
      }
   }

   public CompletableFuture<R> addToBatch(K p_202926_1_) {
      if (!this.batchStarted) {
         throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
      } else {
         CompletableFuture<R> completablefuture = this.scheduler.schedule(p_202926_1_);
         --this.remainingBatchSize;
         if (this.remainingBatchSize == 0) {
            completablefuture = this.scheduler.gather();
            this.remainingBatchSize = 1000;
         }

         return completablefuture;
      }
   }

   public CompletableFuture<R> finishBatch() {
      if (!this.batchStarted) {
         throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
      } else {
         if (this.remainingBatchSize != 1000) {
            this.scheduler.gather();
         }

         this.batchStarted = false;
         return this.scheduler.func_202846_c();
      }
   }
}
