package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;

public class FinializeChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus p_202840_1_, World p_202840_2_, IChunkGenerator<?> p_202840_3_, ChunkPrimer[] p_202840_4_, int p_202840_5_, int p_202840_6_) {
      ChunkPrimer chunkprimer = p_202840_4_[p_202840_4_.length / 2];
      chunkprimer.setStatus(ChunkStatus.FINALIZED);
      chunkprimer.createHeightMap(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.LIGHT_BLOCKING, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE);
      return chunkprimer;
   }
}
