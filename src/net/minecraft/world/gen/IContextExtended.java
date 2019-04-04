package net.minecraft.world.gen;

import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public interface IContextExtended<R extends IArea> extends IContext {
   void setPosition(long p_202698_1_, long p_202698_3_);

   R makeArea(AreaDimension p_201490_1_, IPixelTransformer p_201490_2_);

   default R makeArea(AreaDimension p_201489_1_, IPixelTransformer p_201489_2_, R p_201489_3_) {
      return this.makeArea(p_201489_1_, p_201489_2_);
   }

   default R makeArea(AreaDimension p_201488_1_, IPixelTransformer p_201488_2_, R p_201488_3_, R p_201488_4_) {
      return this.makeArea(p_201488_1_, p_201488_2_);
   }

   int selectRandomly(int... p_202697_1_);
}
