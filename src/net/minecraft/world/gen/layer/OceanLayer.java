package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.NoiseGeneratorImproved;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum OceanLayer implements IAreaTransformer0 {
   INSTANCE;

   public int apply(IContext p_202821_1_, AreaDimension p_202821_2_, int p_202821_3_, int p_202821_4_) {
      NoiseGeneratorImproved noisegeneratorimproved = p_202821_1_.getNoiseGenerator();
      double d0 = noisegeneratorimproved.func_205562_a((double)(p_202821_3_ + p_202821_2_.getStartX()) / 8.0D, (double)(p_202821_4_ + p_202821_2_.getStartZ()) / 8.0D);
      if (d0 > 0.4D) {
         return LayerUtil.WARM_OCEAN;
      } else if (d0 > 0.2D) {
         return LayerUtil.LUKEWARM_OCEAN;
      } else if (d0 < -0.4D) {
         return LayerUtil.FROZEN_OCEAN;
      } else {
         return d0 < -0.2D ? LayerUtil.COLD_OCEAN : LayerUtil.OCEAN;
      }
   }
}
