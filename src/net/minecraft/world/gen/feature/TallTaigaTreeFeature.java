package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TallTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();

   public TallTaigaTreeFeature(boolean p_i2025_1_) {
      super(p_i2025_1_);
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = p_208519_3_.nextInt(4) + 6;
      int j = 1 + p_208519_3_.nextInt(2);
      int k = i - j;
      int l = 2 + p_208519_3_.nextInt(2);
      boolean flag = true;
      if (p_208519_4_.getY() >= 1 && p_208519_4_.getY() + i + 1 <= 256) {
         for(int i1 = p_208519_4_.getY(); i1 <= p_208519_4_.getY() + 1 + i && flag; ++i1) {
            int j1;
            if (i1 - p_208519_4_.getY() < j) {
               j1 = 0;
            } else {
               j1 = l;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = p_208519_4_.getX() - j1; k1 <= p_208519_4_.getX() + j1 && flag; ++k1) {
               for(int l1 = p_208519_4_.getZ() - j1; l1 <= p_208519_4_.getZ() + j1 && flag; ++l1) {
                  if (i1 >= 0 && i1 < 256) {
                     IBlockState iblockstate = p_208519_2_.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1));
                     if (!iblockstate.isAir() && !iblockstate.isIn(BlockTags.LEAVES)) {
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
               int i3 = p_208519_3_.nextInt(2);
               int j3 = 1;
               int k3 = 0;

               for(int l3 = 0; l3 <= k; ++l3) {
                  int j4 = p_208519_4_.getY() + i - l3;

                  for(int i2 = p_208519_4_.getX() - i3; i2 <= p_208519_4_.getX() + i3; ++i2) {
                     int j2 = i2 - p_208519_4_.getX();

                     for(int k2 = p_208519_4_.getZ() - i3; k2 <= p_208519_4_.getZ() + i3; ++k2) {
                        int l2 = k2 - p_208519_4_.getZ();
                        if (Math.abs(j2) != i3 || Math.abs(l2) != i3 || i3 <= 0) {
                           BlockPos blockpos = new BlockPos(i2, j4, k2);
                           if (!p_208519_2_.getBlockState(blockpos).isOpaqueCube(p_208519_2_, blockpos)) {
                              this.setBlockState(p_208519_2_, blockpos, LEAF);
                           }
                        }
                     }
                  }

                  if (i3 >= j3) {
                     i3 = k3;
                     k3 = 1;
                     ++j3;
                     if (j3 > l) {
                        j3 = l;
                     }
                  } else {
                     ++i3;
                  }
               }

               int i4 = p_208519_3_.nextInt(3);

               for(int k4 = 0; k4 < i - i4; ++k4) {
                  IBlockState iblockstate1 = p_208519_2_.getBlockState(p_208519_4_.up(k4));
                  if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES)) {
                     this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.up(k4), TRUNK);
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
