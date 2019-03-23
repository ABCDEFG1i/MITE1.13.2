package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.area.AreaDimension;

public interface IDimOffset1Transformer extends IDimTransformer {
   default AreaDimension apply(AreaDimension p_202706_1_) {
      return new AreaDimension(p_202706_1_.getStartX() - 1, p_202706_1_.getStartZ() - 1, p_202706_1_.getXSize() + 2, p_202706_1_.getZSize() + 2);
   }
}
