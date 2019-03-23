package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class CanopyTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
   private static final IBlockState DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState();

   public CanopyTreeFeature(boolean p_i45461_1_) {
      super(p_i45461_1_);
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = p_208519_3_.nextInt(3) + p_208519_3_.nextInt(2) + 6;
      int j = p_208519_4_.getX();
      int k = p_208519_4_.getY();
      int l = p_208519_4_.getZ();
      if (k >= 1 && k + i + 1 < 256) {
         BlockPos blockpos = p_208519_4_.down();
         Block block = p_208519_2_.getBlockState(blockpos).getBlock();
         if (block != Blocks.GRASS_BLOCK && !Block.isDirt(block)) {
            return false;
         } else if (!this.placeTreeOfHeight(p_208519_2_, p_208519_4_, i)) {
            return false;
         } else {
            this.setDirtAt(p_208519_2_, blockpos);
            this.setDirtAt(p_208519_2_, blockpos.east());
            this.setDirtAt(p_208519_2_, blockpos.south());
            this.setDirtAt(p_208519_2_, blockpos.south().east());
            EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_208519_3_);
            int i1 = i - p_208519_3_.nextInt(4);
            int j1 = 2 - p_208519_3_.nextInt(3);
            int k1 = j;
            int l1 = l;
            int i2 = k + i - 1;

            for(int j2 = 0; j2 < i; ++j2) {
               if (j2 >= i1 && j1 > 0) {
                  k1 += enumfacing.getXOffset();
                  l1 += enumfacing.getZOffset();
                  --j1;
               }

               int k2 = k + j2;
               BlockPos blockpos1 = new BlockPos(k1, k2, l1);
               IBlockState iblockstate = p_208519_2_.getBlockState(blockpos1);
               if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208533_a(p_208519_1_, p_208519_2_, blockpos1);
                  this.func_208533_a(p_208519_1_, p_208519_2_, blockpos1.east());
                  this.func_208533_a(p_208519_1_, p_208519_2_, blockpos1.south());
                  this.func_208533_a(p_208519_1_, p_208519_2_, blockpos1.east().south());
               }
            }

            for(int i3 = -2; i3 <= 0; ++i3) {
               for(int l3 = -2; l3 <= 0; ++l3) {
                  int k4 = -1;
                  this.func_202414_a(p_208519_2_, k1 + i3, i2 + k4, l1 + l3);
                  this.func_202414_a(p_208519_2_, 1 + k1 - i3, i2 + k4, l1 + l3);
                  this.func_202414_a(p_208519_2_, k1 + i3, i2 + k4, 1 + l1 - l3);
                  this.func_202414_a(p_208519_2_, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
                  if ((i3 > -2 || l3 > -1) && (i3 != -1 || l3 != -2)) {
                     k4 = 1;
                     this.func_202414_a(p_208519_2_, k1 + i3, i2 + k4, l1 + l3);
                     this.func_202414_a(p_208519_2_, 1 + k1 - i3, i2 + k4, l1 + l3);
                     this.func_202414_a(p_208519_2_, k1 + i3, i2 + k4, 1 + l1 - l3);
                     this.func_202414_a(p_208519_2_, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
                  }
               }
            }

            if (p_208519_3_.nextBoolean()) {
               this.func_202414_a(p_208519_2_, k1, i2 + 2, l1);
               this.func_202414_a(p_208519_2_, k1 + 1, i2 + 2, l1);
               this.func_202414_a(p_208519_2_, k1 + 1, i2 + 2, l1 + 1);
               this.func_202414_a(p_208519_2_, k1, i2 + 2, l1 + 1);
            }

            for(int j3 = -3; j3 <= 4; ++j3) {
               for(int i4 = -3; i4 <= 4; ++i4) {
                  if ((j3 != -3 || i4 != -3) && (j3 != -3 || i4 != 4) && (j3 != 4 || i4 != -3) && (j3 != 4 || i4 != 4) && (Math.abs(j3) < 3 || Math.abs(i4) < 3)) {
                     this.func_202414_a(p_208519_2_, k1 + j3, i2, l1 + i4);
                  }
               }
            }

            for(int k3 = -1; k3 <= 2; ++k3) {
               for(int j4 = -1; j4 <= 2; ++j4) {
                  if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && p_208519_3_.nextInt(3) <= 0) {
                     int l4 = p_208519_3_.nextInt(3) + 2;

                     for(int i5 = 0; i5 < l4; ++i5) {
                        this.func_208533_a(p_208519_1_, p_208519_2_, new BlockPos(j + k3, i2 - i5 - 1, l + j4));
                     }

                     for(int j5 = -1; j5 <= 1; ++j5) {
                        for(int l2 = -1; l2 <= 1; ++l2) {
                           this.func_202414_a(p_208519_2_, k1 + k3 + j5, i2, l1 + j4 + l2);
                        }
                     }

                     for(int k5 = -2; k5 <= 2; ++k5) {
                        for(int l5 = -2; l5 <= 2; ++l5) {
                           if (Math.abs(k5) != 2 || Math.abs(l5) != 2) {
                              this.func_202414_a(p_208519_2_, k1 + k3 + k5, i2 - 1, l1 + j4 + l5);
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean placeTreeOfHeight(IBlockReader p_181638_1_, BlockPos p_181638_2_, int p_181638_3_) {
      int i = p_181638_2_.getX();
      int j = p_181638_2_.getY();
      int k = p_181638_2_.getZ();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l = 0; l <= p_181638_3_ + 1; ++l) {
         int i1 = 1;
         if (l == 0) {
            i1 = 0;
         }

         if (l >= p_181638_3_ - 1) {
            i1 = 2;
         }

         for(int j1 = -i1; j1 <= i1; ++j1) {
            for(int k1 = -i1; k1 <= i1; ++k1) {
               if (!this.canGrowInto(p_181638_1_.getBlockState(blockpos$mutableblockpos.setPos(i + j1, j + l, k + k1)).getBlock())) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void func_208533_a(Set<BlockPos> p_208533_1_, IWorld p_208533_2_, BlockPos p_208533_3_) {
      if (this.canGrowInto(p_208533_2_.getBlockState(p_208533_3_).getBlock())) {
         this.func_208520_a(p_208533_1_, p_208533_2_, p_208533_3_, DARK_OAK_LOG);
      }

   }

   private void func_202414_a(IWorld p_202414_1_, int p_202414_2_, int p_202414_3_, int p_202414_4_) {
      BlockPos blockpos = new BlockPos(p_202414_2_, p_202414_3_, p_202414_4_);
      if (p_202414_1_.getBlockState(blockpos).isAir()) {
         this.setBlockState(p_202414_1_, blockpos, DARK_OAK_LEAVES);
      }

   }
}
