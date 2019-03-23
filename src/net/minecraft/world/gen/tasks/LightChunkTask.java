package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.SkyLightEngine;

public class LightChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus p_202840_1_, World p_202840_2_, IChunkGenerator<?> p_202840_3_, ChunkPrimer[] p_202840_4_, int p_202840_5_, int p_202840_6_) {
      ChunkPrimer chunkprimer = p_202840_4_[p_202840_4_.length / 2];
      WorldGenRegion worldgenregion = new WorldGenRegion(p_202840_4_, p_202840_1_.getTaskRange() * 2 + 1, p_202840_1_.getTaskRange() * 2 + 1, p_202840_5_, p_202840_6_, p_202840_2_);
      chunkprimer.createHeightMap(Heightmap.Type.LIGHT_BLOCKING);
      if (worldgenregion.getDimension().hasSkyLight()) {
         (new SkyLightEngine()).calculateLight(worldgenregion, chunkprimer);
      }

      (new BlockLightEngine()).calculateLight(worldgenregion, chunkprimer);
      chunkprimer.setStatus(ChunkStatus.LIGHTED);
      return chunkprimer;
   }
}
