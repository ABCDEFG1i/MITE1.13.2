package net.minecraft.util;

public enum BlockRenderLayer {
   SOLID("Solid"),
   CUTOUT_MIPPED("Mipped Cutout"),
   CUTOUT("Cutout"),
   TRANSLUCENT("Translucent");

   private final String layerName;

   BlockRenderLayer(String p_i45755_3_) {
      this.layerName = p_i45755_3_;
   }

   public String toString() {
      return this.layerName;
   }
}
