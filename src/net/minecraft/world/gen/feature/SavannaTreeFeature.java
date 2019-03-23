package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SavannaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.ACACIA_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.ACACIA_LEAVES.getDefaultState();

   public SavannaTreeFeature(boolean p_i45463_1_) {
      super(p_i45463_1_);
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = p_208519_3_.nextInt(3) + p_208519_3_.nextInt(3) + 5;
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
            if ((block == Blocks.GRASS_BLOCK || Block.isDirt(block)) && p_208519_4_.getY() < 256 - i - 1) {
               this.setDirtAt(p_208519_2_, p_208519_4_.down());
               EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_208519_3_);
               int k2 = i - p_208519_3_.nextInt(4) - 1;
               int l2 = 3 - p_208519_3_.nextInt(3);
               int i3 = p_208519_4_.getX();
               int j1 = p_208519_4_.getZ();
               int k1 = 0;

               for(int l1 = 0; l1 < i; ++l1) {
                  int i2 = p_208519_4_.getY() + l1;
                  if (l1 >= k2 && l2 > 0) {
                     i3 += enumfacing.getXOffset();
                     j1 += enumfacing.getZOffset();
                     --l2;
                  }

                  BlockPos blockpos = new BlockPos(i3, i2, j1);
                  IBlockState iblockstate = p_208519_2_.getBlockState(blockpos);
                  if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                     this.func_208532_a(p_208519_1_, p_208519_2_, blockpos);
                     k1 = i2;
                  }
               }

               BlockPos blockpos2 = new BlockPos(i3, k1, j1);

               for(int j3 = -3; j3 <= 3; ++j3) {
                  for(int i4 = -3; i4 <= 3; ++i4) {
                     if (Math.abs(j3) != 3 || Math.abs(i4) != 3) {
                        this.placeLeafAt(p_208519_2_, blockpos2.add(j3, 0, i4));
                     }
                  }
               }

               blockpos2 = blockpos2.up();

               for(int k3 = -1; k3 <= 1; ++k3) {
                  for(int j4 = -1; j4 <= 1; ++j4) {
                     this.placeLeafAt(p_208519_2_, blockpos2.add(k3, 0, j4));
                  }
               }

               this.placeLeafAt(p_208519_2_, blockpos2.east(2));
               this.placeLeafAt(p_208519_2_, blockpos2.west(2));
               this.placeLeafAt(p_208519_2_, blockpos2.south(2));
               this.placeLeafAt(p_208519_2_, blockpos2.north(2));
               i3 = p_208519_4_.getX();
               j1 = p_208519_4_.getZ();
               EnumFacing enumfacing1 = EnumFacing.Plane.HORIZONTAL.random(p_208519_3_);
               if (enumfacing1 != enumfacing) {
                  int l3 = k2 - p_208519_3_.nextInt(2) - 1;
                  int k4 = 1 + p_208519_3_.nextInt(3);
                  k1 = 0;

                  for(int l4 = l3; l4 < i && k4 > 0; --k4) {
                     if (l4 >= 1) {
                        int j2 = p_208519_4_.getY() + l4;
                        i3 += enumfacing1.getXOffset();
                        j1 += enumfacing1.getZOffset();
                        BlockPos blockpos1 = new BlockPos(i3, j2, j1);
                        IBlockState iblockstate1 = p_208519_2_.getBlockState(blockpos1);
                        if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES)) {
                           this.func_208532_a(p_208519_1_, p_208519_2_, blockpos1);
                           k1 = j2;
                        }
                     }

                     ++l4;
                  }

                  if (k1 > 0) {
                     BlockPos blockpos3 = new BlockPos(i3, k1, j1);

                     for(int i5 = -2; i5 <= 2; ++i5) {
                        for(int k5 = -2; k5 <= 2; ++k5) {
                           if (Math.abs(i5) != 2 || Math.abs(k5) != 2) {
                              this.placeLeafAt(p_208519_2_, blockpos3.add(i5, 0, k5));
                           }
                        }
                     }

                     blockpos3 = blockpos3.up();

                     for(int j5 = -1; j5 <= 1; ++j5) {
                        for(int l5 = -1; l5 <= 1; ++l5) {
                           this.placeLeafAt(p_208519_2_, blockpos3.add(j5, 0, l5));
                        }
                     }
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

   private void func_208532_a(Set<BlockPos> p_208532_1_, IWorld p_208532_2_, BlockPos p_208532_3_) {
      this.func_208520_a(p_208532_1_, p_208532_2_, p_208532_3_, TRUNK);
   }

   private void placeLeafAt(IWorld p_175924_1_, BlockPos p_175924_2_) {
      IBlockState iblockstate = p_175924_1_.getBlockState(p_175924_2_);
      if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
         this.setBlockState(p_175924_1_, p_175924_2_, LEAF);
      }

   }
}
