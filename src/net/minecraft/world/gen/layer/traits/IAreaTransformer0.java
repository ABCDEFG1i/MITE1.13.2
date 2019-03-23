package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0 {
   default <R extends IArea> IAreaFactory<R> apply(IContextExtended<R> p_202823_1_) {
      return (p_202822_2_) -> {
         return p_202823_1_.makeArea(p_202822_2_, (p_202820_3_, p_202820_4_) -> {
            p_202823_1_.setPosition((long)(p_202820_3_ + p_202822_2_.getStartX()), (long)(p_202820_4_ + p_202822_2_.getStartZ()));
            return this.apply(p_202823_1_, p_202822_2_, p_202820_3_, p_202820_4_);
         });
      };
   }

   int apply(IContext p_202821_1_, AreaDimension p_202821_2_, int p_202821_3_, int p_202821_4_);
}
