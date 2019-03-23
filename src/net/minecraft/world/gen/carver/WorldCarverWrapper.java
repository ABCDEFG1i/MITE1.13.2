package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldCarverWrapper<C extends IFeatureConfig> implements IWorldCarver<NoFeatureConfig> {
   private final IWorldCarver<C> carver;
   private final C config;

   public WorldCarverWrapper(IWorldCarver<C> p_i48846_1_, C p_i48846_2_) {
      this.carver = p_i48846_1_;
      this.config = p_i48846_2_;
   }

   public boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, NoFeatureConfig p_212246_5_) {
      return this.carver.func_212246_a(p_212246_1_, p_212246_2_, p_212246_3_, p_212246_4_, this.config);
   }

   public boolean carve(IWorld p_202522_1_, Random p_202522_2_, int p_202522_3_, int p_202522_4_, int p_202522_5_, int p_202522_6_, BitSet p_202522_7_, NoFeatureConfig p_202522_8_) {
      return this.carver.carve(p_202522_1_, p_202522_2_, p_202522_3_, p_202522_4_, p_202522_5_, p_202522_6_, p_202522_7_, this.config);
   }
}
