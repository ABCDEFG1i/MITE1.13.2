package net.minecraft.world.gen;

public class GenerationStage {
   public enum Carving {
      AIR,
      LIQUID
   }

   public enum Decoration {
      RAW_GENERATION,
      LOCAL_MODIFICATIONS,
      UNDERGROUND_STRUCTURES,
      SURFACE_STRUCTURES,
      UNDERGROUND_ORES,
      UNDERGROUND_DECORATION,
      VEGETAL_DECORATION,
      TOP_LAYER_MODIFICATION
   }
}
