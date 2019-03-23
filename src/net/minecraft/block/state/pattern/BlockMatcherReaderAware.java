package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockMatcherReaderAware implements IBlockMatcherReaderAware<IBlockState> {
   private final Block block;

   public BlockMatcherReaderAware(Block p_i48707_1_) {
      this.block = p_i48707_1_;
   }

   public static BlockMatcherReaderAware forBlock(Block p_202081_0_) {
      return new BlockMatcherReaderAware(p_202081_0_);
   }

   public boolean test(@Nullable IBlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_) {
      return p_test_1_ != null && p_test_1_.getBlock() == this.block;
   }
}
