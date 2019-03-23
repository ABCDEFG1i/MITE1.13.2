package net.minecraft.world.biome.provider;

import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.storage.WorldInfo;

public class OverworldBiomeProviderSettings implements IBiomeProviderSettings {
   private WorldInfo field_205443_a;
   private OverworldGenSettings field_205444_b;

   public OverworldBiomeProviderSettings setWorldInfo(WorldInfo p_205439_1_) {
      this.field_205443_a = p_205439_1_;
      return this;
   }

   public OverworldBiomeProviderSettings setSettings(OverworldGenSettings p_205441_1_) {
      this.field_205444_b = p_205441_1_;
      return this;
   }

   public WorldInfo getWorldInfo() {
      return this.field_205443_a;
   }

   public OverworldGenSettings getSettings() {
      return this.field_205444_b;
   }
}
