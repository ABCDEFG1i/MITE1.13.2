package net.minecraft.world.gen.feature;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class MinableConfig implements IFeatureConfig {
   public static final Predicate<IBlockState> IS_ROCK = (p_210462_0_) -> {
      if (p_210462_0_ == null) {
         return false;
      } else {
         Block block = p_210462_0_.getBlock();
         return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
      }
   };
   public final Predicate<IBlockState> canReplace;
   public final int size;
   public final IBlockState state;

   public MinableConfig(Predicate<IBlockState> p_i48673_1_, IBlockState p_i48673_2_, int p_i48673_3_) {
      this.size = p_i48673_3_;
      this.state = p_i48673_2_;
      this.canReplace = p_i48673_1_;
   }
}
