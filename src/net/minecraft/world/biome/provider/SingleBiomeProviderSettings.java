package net.minecraft.world.biome.provider;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class SingleBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome biome = Biomes.PLAINS;

   public SingleBiomeProviderSettings setBiome(Biome p_205436_1_) {
      this.biome = p_205436_1_;
      return this;
   }

   public Biome getBiome() {
      return this.biome;
   }
}
