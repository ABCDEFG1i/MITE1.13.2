package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer2 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IContextExtended<R> p_202707_1_, IAreaFactory<R> p_202707_2_, IAreaFactory<R> p_202707_3_) {
      return (p_202710_4_) -> {
         R r = p_202707_2_.make(this.apply(p_202710_4_));
         R r1 = p_202707_3_.make(this.apply(p_202710_4_));
         return p_202707_1_.makeArea(p_202710_4_, (p_202708_5_, p_202708_6_) -> {
            p_202707_1_.setPosition((long)(p_202708_5_ + p_202710_4_.getStartX()), (long)(p_202708_6_ + p_202710_4_.getStartZ()));
            return this.apply(p_202707_1_, p_202710_4_, r, r1, p_202708_5_, p_202708_6_);
         }, r, r1);
      };
   }

   int apply(IContext p_202709_1_, AreaDimension p_202709_2_, IArea p_202709_3_, IArea p_202709_4_, int p_202709_5_, int p_202709_6_);
}
