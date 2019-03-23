package net.minecraft.world.gen.placement;

public class NoiseDependant implements IPlacementConfig {
   public final double noiseThreshold;
   public final int lowNoiseCount;
   public final int highNoiseCount;

   public NoiseDependant(double p_i48685_1_, int p_i48685_3_, int p_i48685_4_) {
      this.noiseThreshold = p_i48685_1_;
      this.lowNoiseCount = p_i48685_3_;
      this.highNoiseCount = p_i48685_4_;
   }
}
