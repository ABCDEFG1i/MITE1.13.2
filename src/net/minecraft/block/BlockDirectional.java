package net.minecraft.block;

import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public abstract class BlockDirectional extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   protected BlockDirectional(Block.Properties p_i48415_1_) {
      super(p_i48415_1_);
   }
}
