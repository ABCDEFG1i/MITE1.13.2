package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class DepthAverage extends BasePlacement<DepthAverageConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, DepthAverageConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      int i = p_201491_5_.count;
      int j = p_201491_5_.averageHeight;
      int k = p_201491_5_.heightSpread;

      for(int l = 0; l < i; ++l) {
         int i1 = p_201491_3_.nextInt(16);
         int j1 = p_201491_3_.nextInt(k) + p_201491_3_.nextInt(k) - k + j;
         int k1 = p_201491_3_.nextInt(16);
         BlockPos blockpos = p_201491_4_.add(i1, j1, k1);
         p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, blockpos, p_201491_7_);
      }

      return true;
   }
}
