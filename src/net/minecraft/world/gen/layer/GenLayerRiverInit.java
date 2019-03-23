package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum GenLayerRiverInit implements IC0Transformer {
   INSTANCE;

   public int apply(IContext p_202726_1_, int p_202726_2_) {
      return LayerUtil.isShallowOcean(p_202726_2_) ? p_202726_2_ : p_202726_1_.random(299999) + 2;
   }
}
