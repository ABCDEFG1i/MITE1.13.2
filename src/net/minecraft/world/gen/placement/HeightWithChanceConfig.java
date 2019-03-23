package net.minecraft.world.gen.placement;

public class HeightWithChanceConfig implements IPlacementConfig {
   public final int height;
   public final float chance;

   public HeightWithChanceConfig(int p_i48663_1_, float p_i48663_2_) {
      this.height = p_i48663_1_;
      this.chance = p_i48663_2_;
   }
}
