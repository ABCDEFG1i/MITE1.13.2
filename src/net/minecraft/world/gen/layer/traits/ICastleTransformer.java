package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;

public interface ICastleTransformer extends IAreaTransformer1, IDimOffset1Transformer {
   int apply(IContext p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_);

   default int apply(IContextExtended<?> p_202712_1_, AreaDimension p_202712_2_, IArea p_202712_3_, int p_202712_4_, int p_202712_5_) {
      return this.apply(p_202712_1_, p_202712_3_.getValue(p_202712_4_ + 1, p_202712_5_ + 0), p_202712_3_.getValue(p_202712_4_ + 2, p_202712_5_ + 1), p_202712_3_.getValue(p_202712_4_ + 1, p_202712_5_ + 2), p_202712_3_.getValue(p_202712_4_ + 0, p_202712_5_ + 1), p_202712_3_.getValue(p_202712_4_ + 1, p_202712_5_ + 1));
   }
}
