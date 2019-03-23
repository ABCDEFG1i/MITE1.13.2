package net.minecraft.world.gen.tasks;

import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTask {
   private static final Logger LOGGER = LogManager.getLogger();

   protected ChunkPrimer[] makeChunkPrimerArray(ChunkStatus p_202838_1_, int p_202838_2_, int p_202838_3_, Map<ChunkPos, ChunkPrimer> p_202838_4_) {
      int i = p_202838_1_.getTaskRange();
      ChunkPrimer[] achunkprimer = new ChunkPrimer[(1 + 2 * i) * (1 + 2 * i)];
      int j = 0;

      for(int k = -i; k <= i; ++k) {
         for(int l = -i; l <= i; ++l) {
            ChunkPrimer chunkprimer = p_202838_4_.get(new ChunkPos(p_202838_2_ + l, p_202838_3_ + k));
            chunkprimer.setUpdateHeightmaps(p_202838_1_.shouldUpdateHeightmaps());
            achunkprimer[j++] = chunkprimer;
         }
      }

      return achunkprimer;
   }

   public ChunkPrimer run(ChunkStatus p_202839_1_, World p_202839_2_, IChunkGenerator<?> p_202839_3_, Map<ChunkPos, ChunkPrimer> p_202839_4_, int p_202839_5_, int p_202839_6_) {
      ChunkPrimer[] achunkprimer = this.makeChunkPrimerArray(p_202839_1_, p_202839_5_, p_202839_6_, p_202839_4_);
      return this.run(p_202839_1_, p_202839_2_, p_202839_3_, achunkprimer, p_202839_5_, p_202839_6_);
   }

   protected abstract ChunkPrimer run(ChunkStatus p_202840_1_, World p_202840_2_, IChunkGenerator<?> p_202840_3_, ChunkPrimer[] p_202840_4_, int p_202840_5_, int p_202840_6_);
}
