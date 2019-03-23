package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class ChunkPrimerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   protected final Function<T, ResourceLocation> serializer;
   protected final Function<ResourceLocation, T> deserializer;
   private final ChunkPos chunkPos;
   private final ShortList[] packedPositions = new ShortList[16];

   public ChunkPrimerTickList(Predicate<T> p_i48960_1_, Function<T, ResourceLocation> p_i48960_2_, Function<ResourceLocation, T> p_i48960_3_, ChunkPos p_i48960_4_) {
      this.filter = p_i48960_1_;
      this.serializer = p_i48960_2_;
      this.deserializer = p_i48960_3_;
      this.chunkPos = p_i48960_4_;
   }

   public NBTTagList write() {
      return AnvilChunkLoader.listArrayToTag(this.packedPositions);
   }

   public void readToBeTickedListFromNBT(NBTTagList p_205380_1_) {
      for(int i = 0; i < p_205380_1_.size(); ++i) {
         NBTTagList nbttaglist = p_205380_1_.getTagListAt(i);

         for(int j = 0; j < nbttaglist.size(); ++j) {
            ChunkPrimer.getOrCreate(this.packedPositions, i).add(nbttaglist.getShortAt(j));
         }
      }

   }

   public void postProcess(ITickList<T> p_205381_1_, Function<BlockPos, T> p_205381_2_) {
      for(int i = 0; i < this.packedPositions.length; ++i) {
         if (this.packedPositions[i] != null) {
            for(Short oshort : this.packedPositions[i]) {
               BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, this.chunkPos);
               p_205381_1_.scheduleTick(blockpos, p_205381_2_.apply(blockpos), 0);
            }

            this.packedPositions[i].clear();
         }
      }

   }

   public boolean isTickScheduled(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      ChunkPrimer.getOrCreate(this.packedPositions, p_205362_1_.getY() >> 4).add(ChunkPrimer.packToLocal(p_205362_1_));
   }

   public boolean isTickPending(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }
}
