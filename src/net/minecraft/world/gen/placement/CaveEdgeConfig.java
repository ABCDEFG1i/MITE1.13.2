package net.minecraft.world.gen.placement;

import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   final GenerationStage.Carving carvingStage;
   final float chance;

   public CaveEdgeConfig(GenerationStage.Carving p_i49000_1_, float p_i49000_2_) {
      this.carvingStage = p_i49000_1_;
      this.chance = p_i49000_2_;
   }
}
