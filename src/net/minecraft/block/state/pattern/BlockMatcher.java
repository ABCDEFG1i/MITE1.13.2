package net.minecraft.block.state.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockMatcher implements Predicate<IBlockState> {
   private final Block block;

   public BlockMatcher(Block p_i45654_1_) {
      this.block = p_i45654_1_;
   }

   public static BlockMatcher forBlock(Block p_177642_0_) {
      return new BlockMatcher(p_177642_0_);
   }

   public boolean test(@Nullable IBlockState p_test_1_) {
      return p_test_1_ != null && p_test_1_.getBlock() == this.block;
   }
}
