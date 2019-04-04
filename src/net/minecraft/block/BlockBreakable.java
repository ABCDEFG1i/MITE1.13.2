package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBreakable extends Block {
   protected BlockBreakable(Block.Properties p_i48382_1_) {
      super(p_i48382_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState p_200122_1_, IBlockState p_200122_2_, EnumFacing p_200122_3_) {
      return p_200122_2_.getBlock() == this || super.isSideInvisible(p_200122_1_, p_200122_2_, p_200122_3_);
   }
}
