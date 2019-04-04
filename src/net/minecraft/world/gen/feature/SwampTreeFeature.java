package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SwampTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.OAK_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.OAK_LEAVES.getDefaultState();

   public SwampTreeFeature() {
      super(false);
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i;
      for(i = p_208519_3_.nextInt(4) + 5; p_208519_2_.getFluidState(p_208519_4_.down()).isTagged(FluidTags.WATER); p_208519_4_ = p_208519_4_.down()) {
      }

      boolean flag = true;
      if (p_208519_4_.getY() >= 1 && p_208519_4_.getY() + i + 1 <= 256) {
         for(int j = p_208519_4_.getY(); j <= p_208519_4_.getY() + 1 + i; ++j) {
            int k = 1;
            if (j == p_208519_4_.getY()) {
               k = 0;
            }

            if (j >= p_208519_4_.getY() + 1 + i - 2) {
               k = 3;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int l = p_208519_4_.getX() - k; l <= p_208519_4_.getX() + k && flag; ++l) {
               for(int i1 = p_208519_4_.getZ() - k; i1 <= p_208519_4_.getZ() + k && flag; ++i1) {
                  if (j >= 0 && j < 256) {
                     IBlockState iblockstate = p_208519_2_.getBlockState(blockpos$mutableblockpos.setPos(l, j, i1));
                     Block block = iblockstate.getBlock();
                     if (!iblockstate.isAir() && !iblockstate.isIn(BlockTags.LEAVES)) {
                        if (block == Blocks.WATER) {
                           if (j > p_208519_4_.getY()) {
                              flag = false;
                           }
                        } else {
                           flag = false;
                        }
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
            Block block1 = p_208519_2_.getBlockState(p_208519_4_.down()).getBlock();
            if ((block1 == Blocks.GRASS_BLOCK || Block.isDirt(block1)) && p_208519_4_.getY() < 256 - i - 1) {
               this.setDirtAt(p_208519_2_, p_208519_4_.down());

               for(int k1 = p_208519_4_.getY() - 3 + i; k1 <= p_208519_4_.getY() + i; ++k1) {
                  int j2 = k1 - (p_208519_4_.getY() + i);
                  int l2 = 2 - j2 / 2;

                  for(int j3 = p_208519_4_.getX() - l2; j3 <= p_208519_4_.getX() + l2; ++j3) {
                     int k3 = j3 - p_208519_4_.getX();

                     for(int i4 = p_208519_4_.getZ() - l2; i4 <= p_208519_4_.getZ() + l2; ++i4) {
                        int j1 = i4 - p_208519_4_.getZ();
                        if (Math.abs(k3) != l2 || Math.abs(j1) != l2 || p_208519_3_.nextInt(2) != 0 && j2 != 0) {
                           BlockPos blockpos = new BlockPos(j3, k1, i4);
                           if (!p_208519_2_.getBlockState(blockpos).isOpaqueCube(p_208519_2_, blockpos)) {
                              this.setBlockState(p_208519_2_, blockpos, LEAF);
                           }
                        }
                     }
                  }
               }

               for(int l1 = 0; l1 < i; ++l1) {
                  IBlockState iblockstate1 = p_208519_2_.getBlockState(p_208519_4_.up(l1));
                  Block block2 = iblockstate1.getBlock();
                  if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES) || block2 == Blocks.WATER) {
                     this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.up(l1), TRUNK);
                  }
               }

               for(int i2 = p_208519_4_.getY() - 3 + i; i2 <= p_208519_4_.getY() + i; ++i2) {
                  int k2 = i2 - (p_208519_4_.getY() + i);
                  int i3 = 2 - k2 / 2;
                  BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                  for(int l3 = p_208519_4_.getX() - i3; l3 <= p_208519_4_.getX() + i3; ++l3) {
                     for(int j4 = p_208519_4_.getZ() - i3; j4 <= p_208519_4_.getZ() + i3; ++j4) {
                        blockpos$mutableblockpos1.setPos(l3, i2, j4);
                        if (p_208519_2_.getBlockState(blockpos$mutableblockpos1).isIn(BlockTags.LEAVES)) {
                           BlockPos blockpos3 = blockpos$mutableblockpos1.west();
                           BlockPos blockpos4 = blockpos$mutableblockpos1.east();
                           BlockPos blockpos1 = blockpos$mutableblockpos1.north();
                           BlockPos blockpos2 = blockpos$mutableblockpos1.south();
                           if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos3).isAir()) {
                              this.addVine(p_208519_2_, blockpos3, BlockVine.EAST);
                           }

                           if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos4).isAir()) {
                              this.addVine(p_208519_2_, blockpos4, BlockVine.WEST);
                           }

                           if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos1).isAir()) {
                              this.addVine(p_208519_2_, blockpos1, BlockVine.SOUTH);
                           }

                           if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos2).isAir()) {
                              this.addVine(p_208519_2_, blockpos2, BlockVine.NORTH);
                           }
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

   private void addVine(IWorld p_181647_1_, BlockPos p_181647_2_, BooleanProperty p_181647_3_) {
      IBlockState iblockstate = Blocks.VINE.getDefaultState().with(p_181647_3_, Boolean.valueOf(true));
      this.setBlockState(p_181647_1_, p_181647_2_, iblockstate);
      int i = 4;

      for(BlockPos blockpos = p_181647_2_.down(); p_181647_1_.getBlockState(blockpos).isAir() && i > 0; --i) {
         this.setBlockState(p_181647_1_, blockpos, iblockstate);
         blockpos = blockpos.down();
      }

   }
}
