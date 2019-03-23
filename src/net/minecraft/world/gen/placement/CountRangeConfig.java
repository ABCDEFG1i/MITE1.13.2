package net.minecraft.world.gen.placement;

public class CountRangeConfig implements IPlacementConfig {
   public final int count;
   public final int minHeight;
   public final int maxHeightBase;
   public final int maxHeight;

   public CountRangeConfig(int p_i48686_1_, int p_i48686_2_, int p_i48686_3_, int p_i48686_4_) {
      this.count = p_i48686_1_;
      this.minHeight = p_i48686_2_;
      this.maxHeightBase = p_i48686_3_;
      this.maxHeight = p_i48686_4_;
   }
}
