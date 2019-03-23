package net.minecraft.block.state.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockMaterialMatcher implements Predicate<IBlockState> {
   private static final BlockMaterialMatcher AIR_MATCHER = new BlockMaterialMatcher(Material.AIR) {
      public boolean test(@Nullable IBlockState p_test_1_) {
         return p_test_1_ != null && p_test_1_.isAir();
      }
   };
   private final Material material;

   private BlockMaterialMatcher(Material p_i47150_1_) {
      this.material = p_i47150_1_;
   }

   public static BlockMaterialMatcher forMaterial(Material p_189886_0_) {
      return p_189886_0_ == Material.AIR ? AIR_MATCHER : new BlockMaterialMatcher(p_189886_0_);
   }

   public boolean test(@Nullable IBlockState p_test_1_) {
      return p_test_1_ != null && p_test_1_.getMaterial() == this.material;
   }
}
