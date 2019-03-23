package net.minecraft.world.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   private static final BiomeColors.ColorResolver field_180291_a = Biome::getGrassColor;
   private static final BiomeColors.ColorResolver field_180289_b = Biome::getFoliageColor;
   private static final BiomeColors.ColorResolver field_180290_c = (p_210280_0_, p_210280_1_) -> {
      return p_210280_0_.getWaterColor();
   };
   private static final BiomeColors.ColorResolver field_204277_d = (p_210279_0_, p_210279_1_) -> {
      return p_210279_0_.getWaterFogColor();
   };

   private static int func_180285_a(IWorldReaderBase p_180285_0_, BlockPos p_180285_1_, BiomeColors.ColorResolver p_180285_2_) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = Minecraft.getInstance().gameSettings.biomeBlendRadius;
      int i1 = (l * 2 + 1) * (l * 2 + 1);

      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_180285_1_.getX() - l, p_180285_1_.getY(), p_180285_1_.getZ() - l, p_180285_1_.getX() + l, p_180285_1_.getY(), p_180285_1_.getZ() + l)) {
         int j1 = p_180285_2_.getColor(p_180285_0_.getBiome(blockpos$mutableblockpos), blockpos$mutableblockpos);
         i += (j1 & 16711680) >> 16;
         j += (j1 & '\uff00') >> 8;
         k += j1 & 255;
      }

      return (i / i1 & 255) << 16 | (j / i1 & 255) << 8 | k / i1 & 255;
   }

   public static int func_180286_a(IWorldReaderBase p_180286_0_, BlockPos p_180286_1_) {
      return func_180285_a(p_180286_0_, p_180286_1_, field_180291_a);
   }

   public static int func_180287_b(IWorldReaderBase p_180287_0_, BlockPos p_180287_1_) {
      return func_180285_a(p_180287_0_, p_180287_1_, field_180289_b);
   }

   public static int func_180288_c(IWorldReaderBase p_180288_0_, BlockPos p_180288_1_) {
      return func_180285_a(p_180288_0_, p_180288_1_, field_180290_c);
   }

   @OnlyIn(Dist.CLIENT)
   interface ColorResolver {
      int getColor(Biome p_getColor_1_, BlockPos p_getColor_2_);
   }
}
