package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IContextExtended<R> p_202713_1_, IAreaFactory<R> p_202713_2_) {
      return (p_202714_3_) -> {
         R r = p_202713_2_.make(this.apply(p_202714_3_));
         return p_202713_1_.makeArea(p_202714_3_, (p_202711_4_, p_202711_5_) -> {
            p_202713_1_.setPosition((long)(p_202711_4_ + p_202714_3_.getStartX()), (long)(p_202711_5_ + p_202714_3_.getStartZ()));
            return this.apply(p_202713_1_, p_202714_3_, r, p_202711_4_, p_202711_5_);
         }, r);
      };
   }

   int apply(IContextExtended<?> p_202712_1_, AreaDimension p_202712_2_, IArea p_202712_3_, int p_202712_4_, int p_202712_5_);
}
