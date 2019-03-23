package net.minecraft.world.gen.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public final class LazyArea implements IArea {
   private final IPixelTransformer pixelTransformer;
   private final Long2IntLinkedOpenHashMap cachedValues;
   private final int maxCacheSize;
   private final AreaDimension dimension;

   public LazyArea(Long2IntLinkedOpenHashMap p_i48649_1_, int p_i48649_2_, AreaDimension p_i48649_3_, IPixelTransformer p_i48649_4_) {
      this.cachedValues = p_i48649_1_;
      this.maxCacheSize = p_i48649_2_;
      this.dimension = p_i48649_3_;
      this.pixelTransformer = p_i48649_4_;
   }

   public int getValue(int p_202678_1_, int p_202678_2_) {
      long i = this.getCacheKey(p_202678_1_, p_202678_2_);
      synchronized(this.cachedValues) {
         int j = this.cachedValues.get(i);
         if (j != Integer.MIN_VALUE) {
            return j;
         } else {
            int k = this.pixelTransformer.apply(p_202678_1_, p_202678_2_);
            this.cachedValues.put(i, k);
            if (this.cachedValues.size() > this.maxCacheSize) {
               for(int l = 0; l < this.maxCacheSize / 16; ++l) {
                  this.cachedValues.removeFirstInt();
               }
            }

            return k;
         }
      }
   }

   private long getCacheKey(int p_202679_1_, int p_202679_2_) {
      long i = 1L;
      i = i << 26;
      i = i | (long)(p_202679_1_ + this.dimension.getStartX()) & 67108863L;
      i = i << 26;
      i = i | (long)(p_202679_2_ + this.dimension.getStartZ()) & 67108863L;
      return i;
   }

   public int getmaxCacheSize() {
      return this.maxCacheSize;
   }
}
