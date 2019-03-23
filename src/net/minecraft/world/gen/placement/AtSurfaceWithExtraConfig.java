package net.minecraft.world.gen.placement;

public class AtSurfaceWithExtraConfig implements IPlacementConfig {
   public final int baseCount;
   public final float extraChance;
   public final int extraCount;

   public AtSurfaceWithExtraConfig(int p_i48662_1_, float p_i48662_2_, int p_i48662_3_) {
      this.baseCount = p_i48662_1_;
      this.extraChance = p_i48662_2_;
      this.extraCount = p_i48662_3_;
   }
}
