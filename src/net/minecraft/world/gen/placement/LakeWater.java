package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class LakeWater extends BasePlacement<LakeChanceConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, LakeChanceConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      if (p_201491_3_.nextInt(p_201491_5_.rarity) == 0) {
         int i = p_201491_3_.nextInt(16);
         int j = p_201491_3_.nextInt(p_201491_2_.getMaxHeight());
         int k = p_201491_3_.nextInt(16);
         p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, p_201491_4_.add(i, j, k), p_201491_7_);
      }

      return true;
   }
}
