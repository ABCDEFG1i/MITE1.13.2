package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum GenLayerIsland implements IAreaTransformer0 {
   INSTANCE;

   public int apply(IContext p_202821_1_, AreaDimension p_202821_2_, int p_202821_3_, int p_202821_4_) {
      if (p_202821_3_ == -p_202821_2_.getStartX() && p_202821_4_ == -p_202821_2_.getStartZ() && p_202821_2_.getStartX() > -p_202821_2_.getXSize() && p_202821_2_.getStartX() <= 0 && p_202821_2_.getStartZ() > -p_202821_2_.getZSize() && p_202821_2_.getStartZ() <= 0) {
         return 1;
      } else {
         return p_202821_1_.random(10) == 0 ? 1 : LayerUtil.OCEAN;
      }
   }
}
