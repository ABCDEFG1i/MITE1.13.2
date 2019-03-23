package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SurfacePlus32WithNoise extends BasePlacement<NoiseDependant> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, NoiseDependant p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      double d0 = Biome.INFO_NOISE.getValue((double)p_201491_4_.getX() / 200.0D, (double)p_201491_4_.getZ() / 200.0D);
      int i = d0 < p_201491_5_.noiseThreshold ? p_201491_5_.lowNoiseCount : p_201491_5_.highNoiseCount;

      for(int j = 0; j < i; ++j) {
         int k = p_201491_3_.nextInt(16);
         int l = p_201491_3_.nextInt(16);
         int i1 = p_201491_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, p_201491_4_.add(k, 0, l)).getY() + 32;
         if (i1 > 0) {
            int j1 = p_201491_3_.nextInt(i1);
            p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, p_201491_4_.add(k, j1, l), p_201491_7_);
         }
      }

      return true;
   }
}
