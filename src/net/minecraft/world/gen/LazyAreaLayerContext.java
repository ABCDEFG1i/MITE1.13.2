package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class LazyAreaLayerContext extends LayerContext<LazyArea> {
   private final Long2IntLinkedOpenHashMap cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
   private final int maxCacheSize;
   private final int field_202705_d;

   public LazyAreaLayerContext(int p_i48647_1_, int p_i48647_2_, long p_i48647_3_, long p_i48647_5_) {
      super(p_i48647_5_);
      this.cache.defaultReturnValue(Integer.MIN_VALUE);
      this.maxCacheSize = p_i48647_1_;
      this.field_202705_d = p_i48647_2_;
      this.setSeed(p_i48647_3_);
   }

   public LazyArea makeArea(AreaDimension p_201490_1_, IPixelTransformer p_201490_2_) {
      return new LazyArea(this.cache, this.maxCacheSize, p_201490_1_, p_201490_2_);
   }

   public LazyArea makeArea(AreaDimension p_201489_1_, IPixelTransformer p_201489_2_, LazyArea p_201489_3_) {
      return new LazyArea(this.cache, Math.min(256, p_201489_3_.getmaxCacheSize() * 4), p_201489_1_, p_201489_2_);
   }

   public LazyArea makeArea(AreaDimension p_201488_1_, IPixelTransformer p_201488_2_, LazyArea p_201488_3_, LazyArea p_201488_4_) {
      return new LazyArea(this.cache, Math.min(256, Math.max(p_201488_3_.getmaxCacheSize(), p_201488_4_.getmaxCacheSize()) * 4), p_201488_1_, p_201488_2_);
   }
}
