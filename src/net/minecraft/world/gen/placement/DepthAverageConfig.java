package net.minecraft.world.gen.placement;

public class DepthAverageConfig implements IPlacementConfig {
   public final int count;
   public final int averageHeight;
   public final int heightSpread;

   public DepthAverageConfig(int p_i48661_1_, int p_i48661_2_, int p_i48661_3_) {
      this.count = p_i48661_1_;
      this.averageHeight = p_i48661_2_;
      this.heightSpread = p_i48661_3_;
   }
}
