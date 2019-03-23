package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class CompositeSurfaceBuilder<C extends ISurfaceBuilderConfig> implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private final ISurfaceBuilder<C> surfaceBuilder;
   private final C config;

   public CompositeSurfaceBuilder(ISurfaceBuilder<C> p_i48955_1_, C p_i48955_2_) {
      this.surfaceBuilder = p_i48955_1_;
      this.config = p_i48955_2_;
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      this.surfaceBuilder.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, this.config);
   }

   public void setSeed(long p_205548_1_) {
      this.surfaceBuilder.setSeed(p_205548_1_);
   }

   public C getConfig() {
      return this.config;
   }
}
