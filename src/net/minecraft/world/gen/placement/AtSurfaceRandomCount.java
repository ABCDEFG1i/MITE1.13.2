package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class AtSurfaceRandomCount extends BasePlacement<FrequencyConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, FrequencyConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      int i = p_201491_3_.nextInt(p_201491_5_.frequency);

      for(int j = 0; j < i; ++j) {
         int k = p_201491_3_.nextInt(16);
         int l = p_201491_3_.nextInt(16);
         BlockPos blockpos = p_201491_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, p_201491_4_.add(k, 0, l));
         p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, blockpos, p_201491_7_);
      }

      return true;
   }
}
