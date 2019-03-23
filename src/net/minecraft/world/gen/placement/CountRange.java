package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CountRange extends BasePlacement<CountRangeConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, CountRangeConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      for(int i = 0; i < p_201491_5_.count; ++i) {
         int j = p_201491_3_.nextInt(16);
         int k = p_201491_3_.nextInt(p_201491_5_.maxHeight - p_201491_5_.maxHeightBase) + p_201491_5_.minHeight;
         int l = p_201491_3_.nextInt(16);
         p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, p_201491_4_.add(j, k, l), p_201491_7_);
      }

      return true;
   }
}
