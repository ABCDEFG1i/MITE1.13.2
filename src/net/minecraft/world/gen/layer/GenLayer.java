package net.minecraft.world.gen.layer;

import javax.annotation.Nullable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

public class GenLayer {
   private final IAreaFactory<LazyArea> lazyAreaFactory;

   public GenLayer(IAreaFactory<LazyArea> p_i48639_1_) {
      this.lazyAreaFactory = p_i48639_1_;
   }

   public Biome[] generateBiomes(int p_202833_1_, int p_202833_2_, int p_202833_3_, int p_202833_4_, @Nullable Biome p_202833_5_) {
      AreaDimension areadimension = new AreaDimension(p_202833_1_, p_202833_2_, p_202833_3_, p_202833_4_);
      LazyArea lazyarea = this.lazyAreaFactory.make(areadimension);
      Biome[] abiome = new Biome[p_202833_3_ * p_202833_4_];

      for(int i = 0; i < p_202833_4_; ++i) {
         for(int j = 0; j < p_202833_3_; ++j) {
            abiome[j + i * p_202833_3_] = Biome.getBiome(lazyarea.getValue(j, i), p_202833_5_);
         }
      }

      return abiome;
   }
}
