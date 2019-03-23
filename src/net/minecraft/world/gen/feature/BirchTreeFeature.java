package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BirchTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState LOG = Blocks.BIRCH_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.BIRCH_LEAVES.getDefaultState();
   private final boolean useExtraRandomHeight;

   public BirchTreeFeature(boolean p_i45449_1_, boolean p_i45449_2_) {
      super(p_i45449_1_);
      this.useExtraRandomHeight = p_i45449_2_;
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = p_208519_3_.nextInt(3) + 5;
      if (this.useExtraRandomHeight) {
         i += p_208519_3_.nextInt(7);
      }

      boolean flag = true;
      if (p_208519_4_.getY() >= 1 && p_208519_4_.getY() + i + 1 <= 256) {
         for(int j = p_208519_4_.getY(); j <= p_208519_4_.getY() + 1 + i; ++j) {
            int k = 1;
            if (j == p_208519_4_.getY()) {
               k = 0;
            }

            if (j >= p_208519_4_.getY() + 1 + i - 2) {
               k = 2;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = p_208519_4_.getX() - k; l <= p_208519_4_.getX() + k && flag; ++l) {
               for(int i1 = p_208519_4_.getZ() - k; i1 <= p_208519_4_.getZ() + k && flag; ++i1) {
                  if (j >= 0 && j < 256) {
                     if (!this.canGrowInto(p_208519_2_.getBlockState(blockpos$mutableblockpos.setPos(l, j, i1)).getBlock())) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            Block block = p_208519_2_.getBlockState(p_208519_4_.down()).getBlock();
            if ((block == Blocks.GRASS_BLOCK || Block.isDirt(block) || block == Blocks.FARMLAND) && p_208519_4_.getY() < 256 - i - 1) {
               this.setDirtAt(p_208519_2_, p_208519_4_.down());

               for(int i2 = p_208519_4_.getY() - 3 + i; i2 <= p_208519_4_.getY() + i; ++i2) {
                  int k2 = i2 - (p_208519_4_.getY() + i);
                  int l2 = 1 - k2 / 2;

                  for(int i3 = p_208519_4_.getX() - l2; i3 <= p_208519_4_.getX() + l2; ++i3) {
                     int j1 = i3 - p_208519_4_.getX();

                     for(int k1 = p_208519_4_.getZ() - l2; k1 <= p_208519_4_.getZ() + l2; ++k1) {
                        int l1 = k1 - p_208519_4_.getZ();
                        if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || p_208519_3_.nextInt(2) != 0 && k2 != 0) {
                           BlockPos blockpos = new BlockPos(i3, i2, k1);
                           IBlockState iblockstate = p_208519_2_.getBlockState(blockpos);
                           if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                              this.setBlockState(p_208519_2_, blockpos, LEAF);
                           }
                        }
                     }
                  }
               }

               for(int j2 = 0; j2 < i; ++j2) {
                  IBlockState iblockstate1 = p_208519_2_.getBlockState(p_208519_4_.up(j2));
                  if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES)) {
                     this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.up(j2), LOG);
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }
}
