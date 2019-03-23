package net.minecraft.world.gen.tasks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;

public class LiquidCarveChunkTask extends ChunkTask {
   protected ChunkPrimer run(ChunkStatus p_202840_1_, World p_202840_2_, IChunkGenerator<?> p_202840_3_, ChunkPrimer[] p_202840_4_, int p_202840_5_, int p_202840_6_) {
      p_202840_3_.carve(new WorldGenRegion(p_202840_4_, p_202840_1_.getTaskRange() * 2 + 1, p_202840_1_.getTaskRange() * 2 + 1, p_202840_5_, p_202840_6_, p_202840_2_), GenerationStage.Carving.LIQUID);
      ChunkPrimer chunkprimer = p_202840_4_[p_202840_4_.length / 2];
      chunkprimer.createHeightMap(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
      chunkprimer.setStatus(ChunkStatus.LIQUID_CARVED);
      return chunkprimer;
   }
}
