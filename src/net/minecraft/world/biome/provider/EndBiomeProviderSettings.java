package net.minecraft.world.biome.provider;

public class EndBiomeProviderSettings implements IBiomeProviderSettings {
   private long seed;

   public EndBiomeProviderSettings setSeed(long p_205446_1_) {
      this.seed = p_205446_1_;
      return this;
   }

   public long getSeed() {
      return this.seed;
   }
}
