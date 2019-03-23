package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;

public interface IC0Transformer extends IAreaTransformer1, IDimOffset0Transformer {
   int apply(IContext p_202726_1_, int p_202726_2_);

   default int apply(IContextExtended<?> p_202712_1_, AreaDimension p_202712_2_, IArea p_202712_3_, int p_202712_4_, int p_202712_5_) {
      return this.apply(p_202712_1_, p_202712_3_.getValue(p_202712_4_, p_202712_5_));
   }
}
