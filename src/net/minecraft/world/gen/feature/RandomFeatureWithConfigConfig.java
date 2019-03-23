package net.minecraft.world.gen.feature;

public class RandomFeatureWithConfigConfig implements IFeatureConfig {
   public final Feature<?>[] features;
   public final IFeatureConfig[] configs;

   public RandomFeatureWithConfigConfig(Feature<?>[] p_i48899_1_, IFeatureConfig[] p_i48899_2_) {
      this.features = p_i48899_1_;
      this.configs = p_i48899_2_;
   }
}
