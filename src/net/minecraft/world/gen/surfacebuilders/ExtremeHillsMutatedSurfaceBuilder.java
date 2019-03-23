package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ExtremeHillsMutatedSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, IBlockState p_205610_9_, IBlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      if (!(p_205610_7_ < -1.0D) && !(p_205610_7_ > 2.0D)) {
         if (p_205610_7_ > 1.0D) {
            Biome.DEFAULT_SURFACE_BUILDER.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, Biome.STONE_STONE_GRAVEL_SURFACE);
         } else {
            Biome.DEFAULT_SURFACE_BUILDER.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, Biome.GRASS_DIRT_GRAVEL_SURFACE);
         }
      } else {
         Biome.DEFAULT_SURFACE_BUILDER.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, Biome.GRAVEL_SURFACE);
      }

   }
}
