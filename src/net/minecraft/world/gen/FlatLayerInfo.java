package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.registry.IRegistry;

public class FlatLayerInfo {
   private final IBlockState layerMaterial;
   private final int layerCount;
   private int layerMinimumY;

   public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_) {
      this.layerCount = p_i45467_1_;
      this.layerMaterial = p_i45467_2_.getDefaultState();
   }

   public int getLayerCount() {
      return this.layerCount;
   }

   public IBlockState getLayerMaterial() {
      return this.layerMaterial;
   }

   public int getMinY() {
      return this.layerMinimumY;
   }

   public void setMinY(int p_82660_1_) {
      this.layerMinimumY = p_82660_1_;
   }

   public String toString() {
      return (this.layerCount > 1 ? this.layerCount + "*" : "") + IRegistry.field_212618_g.func_177774_c(this.layerMaterial.getBlock());
   }
}
