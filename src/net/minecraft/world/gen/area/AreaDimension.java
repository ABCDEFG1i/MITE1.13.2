package net.minecraft.world.gen.area;

public final class AreaDimension {
   private final int startX;
   private final int startZ;
   private final int xSize;
   private final int zSize;

   public AreaDimension(int p_i48650_1_, int p_i48650_2_, int p_i48650_3_, int p_i48650_4_) {
      this.startX = p_i48650_1_;
      this.startZ = p_i48650_2_;
      this.xSize = p_i48650_3_;
      this.zSize = p_i48650_4_;
   }

   public int getStartX() {
      return this.startX;
   }

   public int getStartZ() {
      return this.startZ;
   }

   public int getXSize() {
      return this.xSize;
   }

   public int getZSize() {
      return this.zSize;
   }
}
