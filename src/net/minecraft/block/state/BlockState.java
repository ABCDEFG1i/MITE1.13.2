package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.state.AbstractStateHolder;
import net.minecraft.state.IProperty;

public class BlockState extends AbstractStateHolder<Block, IBlockState> implements IBlockState {
   public BlockState(Block p_i49006_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i49006_2_) {
      super(p_i49006_1_, p_i49006_2_);
   }

   public Block getBlock() {
      return this.object;
   }
}
