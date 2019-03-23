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

public class TopSolidWithNoise extends BasePlacement<TopSolidWithNoiseConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, TopSolidWithNoiseConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      double d0 = Biome.INFO_NOISE.getValue((double)p_201491_4_.getX() / p_201491_5_.noiseStretch, (double)p_201491_4_.getZ() / p_201491_5_.noiseStretch);
      int i = (int)Math.ceil(d0 * (double)p_201491_5_.maxCount);

      for(int j = 0; j < i; ++j) {
         int k = p_201491_3_.nextInt(16);
         int l = p_201491_3_.nextInt(16);
         int i1 = p_201491_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, p_201491_4_.getX() + k, p_201491_4_.getZ() + l);
         p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, new BlockPos(p_201491_4_.getX() + k, i1, p_201491_4_.getZ() + l), p_201491_7_);
      }

      return false;
   }
}
