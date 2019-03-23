package net.minecraft.world.gen.placement;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CaveEdge extends BasePlacement<CaveEdgeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, CaveEdgeConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      IChunk ichunk = p_201491_1_.getChunkDefault(p_201491_4_);
      ChunkPos chunkpos = ichunk.getPos();
      BitSet bitset = ichunk.getCarvingMask(p_201491_5_.carvingStage);

      for(int i = 0; i < bitset.length(); ++i) {
         if (bitset.get(i) && p_201491_3_.nextFloat() < p_201491_5_.chance) {
            int j = i & 15;
            int k = i >> 4 & 15;
            int l = i >> 8;
            p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, new BlockPos(chunkpos.getXStart() + j, l, chunkpos.getZStart() + k), p_201491_7_);
         }
      }

      return true;
   }
}
