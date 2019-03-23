package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class WithChance extends BasePlacement<ChanceConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld p_201491_1_, IChunkGenerator<? extends IChunkGenSettings> p_201491_2_, Random p_201491_3_, BlockPos p_201491_4_, ChanceConfig p_201491_5_, Feature<C> p_201491_6_, C p_201491_7_) {
      if (p_201491_3_.nextFloat() < 1.0F / (float)p_201491_5_.chance) {
         p_201491_6_.func_212245_a(p_201491_1_, p_201491_2_, p_201491_3_, p_201491_4_, p_201491_7_);
      }

      return true;
   }
}
