package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PointyTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();

   public PointyTaigaTreeFeature() {
      super(false);
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = p_208519_3_.nextInt(5) + 7;
      int j = i - p_208519_3_.nextInt(2) - 3;
      int k = i - j;
      int l = 1 + p_208519_3_.nextInt(k + 1);
      if (p_208519_4_.getY() >= 1 && p_208519_4_.getY() + i + 1 <= 256) {
         boolean flag = true;

         for(int i1 = p_208519_4_.getY(); i1 <= p_208519_4_.getY() + 1 + i && flag; ++i1) {
            int j1 = 1;
            if (i1 - p_208519_4_.getY() < j) {
               j1 = 0;
            } else {
               j1 = l;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = p_208519_4_.getX() - j1; k1 <= p_208519_4_.getX() + j1 && flag; ++k1) {
               for(int l1 = p_208519_4_.getZ() - j1; l1 <= p_208519_4_.getZ() + j1 && flag; ++l1) {
                  if (i1 >= 0 && i1 < 256) {
                     if (!this.canGrowInto(p_208519_2_.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1)).getBlock())) {
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
            if ((block == Blocks.GRASS_BLOCK || Block.isDirt(block)) && p_208519_4_.getY() < 256 - i - 1) {
               this.setDirtAt(p_208519_2_, p_208519_4_.down());
               int k2 = 0;

               for(int l2 = p_208519_4_.getY() + i; l2 >= p_208519_4_.getY() + j; --l2) {
                  for(int j3 = p_208519_4_.getX() - k2; j3 <= p_208519_4_.getX() + k2; ++j3) {
                     int k3 = j3 - p_208519_4_.getX();

                     for(int i2 = p_208519_4_.getZ() - k2; i2 <= p_208519_4_.getZ() + k2; ++i2) {
                        int j2 = i2 - p_208519_4_.getZ();
                        if (Math.abs(k3) != k2 || Math.abs(j2) != k2 || k2 <= 0) {
                           BlockPos blockpos = new BlockPos(j3, l2, i2);
                           if (!p_208519_2_.getBlockState(blockpos).isOpaqueCube(p_208519_2_, blockpos)) {
                              this.setBlockState(p_208519_2_, blockpos, LEAF);
                           }
                        }
                     }
                  }

                  if (k2 >= 1 && l2 == p_208519_4_.getY() + j + 1) {
                     --k2;
                  } else if (k2 < l) {
                     ++k2;
                  }
               }

               for(int i3 = 0; i3 < i - 1; ++i3) {
                  IBlockState iblockstate = p_208519_2_.getBlockState(p_208519_4_.up(i3));
                  if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                     this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.up(i3), TRUNK);
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
