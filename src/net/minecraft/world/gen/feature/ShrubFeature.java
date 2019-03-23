package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ShrubFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private final IBlockState leavesMetadata;
   private final IBlockState woodMetadata;

   public ShrubFeature(IBlockState p_i46450_1_, IBlockState p_i46450_2_) {
      super(false);
      this.woodMetadata = p_i46450_1_;
      this.leavesMetadata = p_i46450_2_;
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      for(IBlockState iblockstate = p_208519_2_.getBlockState(p_208519_4_); (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) && p_208519_4_.getY() > 0; iblockstate = p_208519_2_.getBlockState(p_208519_4_)) {
         p_208519_4_ = p_208519_4_.down();
      }

      Block block = p_208519_2_.getBlockState(p_208519_4_).getBlock();
      if (Block.isDirt(block) || block == Blocks.GRASS_BLOCK) {
         p_208519_4_ = p_208519_4_.up();
         this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_, this.woodMetadata);

         for(int i = p_208519_4_.getY(); i <= p_208519_4_.getY() + 2; ++i) {
            int j = i - p_208519_4_.getY();
            int k = 2 - j;

            for(int l = p_208519_4_.getX() - k; l <= p_208519_4_.getX() + k; ++l) {
               int i1 = l - p_208519_4_.getX();

               for(int j1 = p_208519_4_.getZ() - k; j1 <= p_208519_4_.getZ() + k; ++j1) {
                  int k1 = j1 - p_208519_4_.getZ();
                  if (Math.abs(i1) != k || Math.abs(k1) != k || p_208519_3_.nextInt(2) != 0) {
                     BlockPos blockpos = new BlockPos(l, i, j1);
                     IBlockState iblockstate1 = p_208519_2_.getBlockState(blockpos);
                     if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES)) {
                        this.setBlockState(p_208519_2_, blockpos, this.leavesMetadata);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
