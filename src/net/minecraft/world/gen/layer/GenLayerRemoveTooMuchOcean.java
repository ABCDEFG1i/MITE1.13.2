package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerRemoveTooMuchOcean implements ICastleTransformer {
   INSTANCE;

   public int apply(IContext p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      return LayerUtil.isShallowOcean(p_202748_6_) && LayerUtil.isShallowOcean(p_202748_2_) && LayerUtil.isShallowOcean(p_202748_3_) && LayerUtil.isShallowOcean(p_202748_5_) && LayerUtil.isShallowOcean(p_202748_4_) && p_202748_1_.random(2) == 0 ? 1 : p_202748_6_;
   }
}
