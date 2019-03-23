package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum GenLayerMixOceans implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   public int apply(IContext p_202709_1_, AreaDimension p_202709_2_, IArea p_202709_3_, IArea p_202709_4_, int p_202709_5_, int p_202709_6_) {
      int i = p_202709_3_.getValue(p_202709_5_, p_202709_6_);
      int j = p_202709_4_.getValue(p_202709_5_, p_202709_6_);
      if (!LayerUtil.isOcean(i)) {
         return i;
      } else {
         int k = 8;
         int l = 4;

         for(int i1 = -8; i1 <= 8; i1 += 4) {
            for(int j1 = -8; j1 <= 8; j1 += 4) {
               int k1 = p_202709_3_.getValue(p_202709_5_ + i1, p_202709_6_ + j1);
               if (!LayerUtil.isOcean(k1)) {
                  if (j == LayerUtil.WARM_OCEAN) {
                     return LayerUtil.LUKEWARM_OCEAN;
                  }

                  if (j == LayerUtil.FROZEN_OCEAN) {
                     return LayerUtil.COLD_OCEAN;
                  }
               }
            }
         }

         if (i == LayerUtil.DEEP_OCEAN) {
            if (j == LayerUtil.LUKEWARM_OCEAN) {
               return LayerUtil.DEEP_LUKEWARM_OCEAN;
            }

            if (j == LayerUtil.OCEAN) {
               return LayerUtil.DEEP_OCEAN;
            }

            if (j == LayerUtil.COLD_OCEAN) {
               return LayerUtil.DEEP_COLD_OCEAN;
            }

            if (j == LayerUtil.FROZEN_OCEAN) {
               return LayerUtil.DEEP_FROZEN_OCEAN;
            }
         }

         return j;
      }
   }
}
