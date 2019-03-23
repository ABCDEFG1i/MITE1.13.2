package net.minecraft.world.gen.placement;

public class TopSolidWithNoiseConfig implements IPlacementConfig {
   public final int maxCount;
   public final double noiseStretch;

   public TopSolidWithNoiseConfig(int p_i48898_1_, double p_i48898_2_) {
      this.maxCount = p_i48898_1_;
      this.noiseStretch = p_i48898_2_;
   }
}
