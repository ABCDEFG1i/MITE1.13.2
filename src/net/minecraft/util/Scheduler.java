package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Scheduler<K, T extends ITaskType<K, T>, R> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final ExecutorService schedulerPool;
   private final ExecutorService workerPool;
   private final AtomicInteger nextWorkerId = new AtomicInteger(1);
   private final List<CompletableFuture<R>> unbatchedFutures = Lists.newArrayList();
   private CompletableFuture<R> field_202860_f = CompletableFuture.completedFuture(null);
   private CompletableFuture<R> field_202861_g = CompletableFuture.completedFuture(null);
   private final Supplier<Map<T, CompletableFuture<R>>> field_202862_h;
   private final Supplier<Map<T, CompletableFuture<Void>>> field_202863_i;
   private final T targetTaskType;

   public Scheduler(String p_i48724_1_, int p_i48724_2_, T p_i48724_3_, Supplier<Map<T, CompletableFuture<R>>> p_i48724_4_, Supplier<Map<T, CompletableFuture<Void>>> p_i48724_5_) {
      this.targetTaskType = p_i48724_3_;
      this.field_202862_h = p_i48724_4_;
      this.field_202863_i = p_i48724_5_;
      if (p_i48724_2_ == 0) {
         this.schedulerPool = MoreExecutors.newDirectExecutorService();
      } else {
         this.schedulerPool = Executors.newSingleThreadExecutor(new NamedThreadFactory(p_i48724_1_ + "-Scheduler"));
      }

      if (p_i48724_2_ <= 1) {
         this.workerPool = MoreExecutors.newDirectExecutorService();
      } else {
         this.workerPool = new ForkJoinPool(p_i48724_2_ - 1, (p_202853_2_) -> {
            return new ForkJoinWorkerThread(p_202853_2_) {
               {
                  this.setName(p_i48724_1_ + "-Worker-" + Scheduler.this.nextWorkerId.getAndIncrement());
               }
            };
         }, (p_202848_0_, p_202848_1_) -> {
            LOGGER.error(String.format("Caught exception in thread %s", p_202848_0_), p_202848_1_);
         }, true);
      }

   }

   public CompletableFuture<R> schedule(K p_202851_1_) {
      CompletableFuture<R> completablefuture = this.field_202860_f;
      Supplier<CompletableFuture<R>> supplier = () -> {
         return this.createFutureWrapper(p_202851_1_).getFuture(completablefuture, this.targetTaskType);
      };
      CompletableFuture<CompletableFuture<R>> completablefuture1 = CompletableFuture.supplyAsync(supplier, this.schedulerPool);
      CompletableFuture<R> completablefuture2 = completablefuture1.thenComposeAsync((p_202847_0_) -> {
         return p_202847_0_;
      }, this.workerPool);
      this.unbatchedFutures.add(completablefuture2);
      return completablefuture2;
   }

   public CompletableFuture<R> gather() {
      CompletableFuture<R> completablefuture = this.unbatchedFutures.remove(this.unbatchedFutures.size() - 1);
      CompletableFuture<R> completablefuture1 = CompletableFuture.allOf(this.unbatchedFutures.toArray(new CompletableFuture[0])).thenCompose((p_202850_1_) -> {
         return completablefuture;
      });
      this.field_202861_g = completablefuture1;
      this.unbatchedFutures.clear();
      this.field_202860_f = completablefuture1;
      return completablefuture1;
   }

   protected Scheduler<K, T, R>.FutureWrapper createFutureWrapper(K p_201494_1_) {
      return this.func_212252_a_(p_201494_1_, true);
   }

   @Nullable
   protected abstract Scheduler<K, T, R>.FutureWrapper func_212252_a_(K p_212252_1_, boolean p_212252_2_);

   public void shutdown() throws InterruptedException {
      this.schedulerPool.shutdown();
      this.schedulerPool.awaitTermination(1L, TimeUnit.DAYS);
      this.workerPool.shutdown();
      this.workerPool.awaitTermination(1L, TimeUnit.DAYS);
   }

   protected abstract R runTask(K p_201493_1_, T p_201493_2_, Map<K, R> p_201493_3_);

   @Nullable
   public R func_212537_b(K p_212537_1_, boolean p_212537_2_) {
      Scheduler<K, T, R>.FutureWrapper scheduler = this.func_212252_a_(p_212537_1_, p_212537_2_);
      return scheduler != null ? scheduler.getResult() : null;
   }

   public CompletableFuture<R> func_202846_c() {
      CompletableFuture<R> completablefuture = this.field_202861_g;
      return completablefuture.thenApply((p_202849_0_) -> {
         return p_202849_0_;
      });
   }

   protected abstract void onTaskFinish(K p_205607_1_, Scheduler<K, T, R>.FutureWrapper p_205607_2_);

   protected abstract Scheduler<K, T, R>.FutureWrapper onTaskStart(K p_205606_1_, Scheduler<K, T, R>.FutureWrapper p_205606_2_);

   public final class FutureWrapper {
      private final Map<T, CompletableFuture<R>> futures;
      private final K key;
      private final R result;

      public FutureWrapper(K p_i48755_2_, R p_i48755_3_, T p_i48755_4_) {
         this.futures = Scheduler.this.field_202862_h.get();
         this.key = p_i48755_2_;

         for(this.result = p_i48755_3_; p_i48755_4_ != null; p_i48755_4_ = p_i48755_4_.getPreviousTaskType()) {
            this.futures.put(p_i48755_4_, CompletableFuture.completedFuture(p_i48755_3_));
         }

      }

      public R getResult() {
         return this.result;
      }

      private CompletableFuture<R> getFuture(CompletableFuture<R> p_202914_1_, T p_202914_2_) {
         Map<K, CompletableFuture<R>> map = new ConcurrentHashMap<>();
         return this.futures.computeIfAbsent(p_202914_2_, (p_202915_4_) -> {
            if (p_202914_2_.getPreviousTaskType() == null) {
               return CompletableFuture.completedFuture(this.result);
            } else {
               p_202914_2_.acceptInRange(this.key, (p_202918_3_, p_202918_4_) -> {
                  CompletableFuture completablefuture2 = map.put(p_202918_3_, Scheduler.this.onTaskStart(p_202918_3_, Scheduler.this.createFutureWrapper(p_202918_3_)).getFuture(p_202914_1_, p_202918_4_));
               });
               CompletableFuture<?>[] completablefuture = Streams.concat(Stream.of(p_202914_1_), map.values().stream()).toArray((p_202916_0_) -> {
                  return new CompletableFuture[p_202916_0_];
               });
               CompletableFuture<R> completablefuture1 = CompletableFuture.allOf(completablefuture).thenApplyAsync((p_202912_3_) -> {
                  return Scheduler.this.runTask(this.key, p_202914_2_, Maps.transformValues(map, (p_202913_0_) -> {
                     try {
                        return p_202913_0_.get();
                     } catch (ExecutionException | InterruptedException interruptedexception) {
                        throw new RuntimeException(interruptedexception);
                     }
                  }));
               }, Scheduler.this.workerPool).thenApplyAsync((p_206825_2_) -> {
                  for(K k : map.keySet()) {
                     Scheduler.this.onTaskFinish(k, Scheduler.this.createFutureWrapper(k));
                  }

                  return p_206825_2_;
               }, Scheduler.this.schedulerPool);
               this.futures.put(p_202914_2_, completablefuture1);
               return completablefuture1;
            }
         });
      }
   }
}
