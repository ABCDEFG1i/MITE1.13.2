package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class TreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState DEFAULT_TRUNK = Blocks.OAK_LOG.getDefaultState();
   private static final IBlockState DEFAULT_LEAF = Blocks.OAK_LEAVES.getDefaultState();
   protected final int minTreeHeight;
   private final boolean vinesGrow;
   private final IBlockState metaWood;
   private final IBlockState metaLeaves;

   public TreeFeature(boolean p_i2027_1_) {
      this(p_i2027_1_, 4, DEFAULT_TRUNK, DEFAULT_LEAF, false);
   }

   public TreeFeature(boolean p_i46446_1_, int p_i46446_2_, IBlockState p_i46446_3_, IBlockState p_i46446_4_, boolean p_i46446_5_) {
      super(p_i46446_1_);
      this.minTreeHeight = p_i46446_2_;
      this.metaWood = p_i46446_3_;
      this.metaLeaves = p_i46446_4_;
      this.vinesGrow = p_i46446_5_;
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = this.func_208534_a(p_208519_3_);
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
               int k2 = 3;
               int l2 = 0;

               for(int i3 = p_208519_4_.getY() - 3 + i; i3 <= p_208519_4_.getY() + i; ++i3) {
                  int i4 = i3 - (p_208519_4_.getY() + i);
                  int j1 = 1 - i4 / 2;

                  for(int k1 = p_208519_4_.getX() - j1; k1 <= p_208519_4_.getX() + j1; ++k1) {
                     int l1 = k1 - p_208519_4_.getX();

                     for(int i2 = p_208519_4_.getZ() - j1; i2 <= p_208519_4_.getZ() + j1; ++i2) {
                        int j2 = i2 - p_208519_4_.getZ();
                        if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || p_208519_3_.nextInt(2) != 0 && i4 != 0) {
                           BlockPos blockpos = new BlockPos(k1, i3, i2);
                           IBlockState iblockstate = p_208519_2_.getBlockState(blockpos);
                           Material material = iblockstate.getMaterial();
                           if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES) || material == Material.VINE) {
                              this.setBlockState(p_208519_2_, blockpos, this.metaLeaves);
                           }
                        }
                     }
                  }
               }

               for(int j3 = 0; j3 < i; ++j3) {
                  IBlockState iblockstate1 = p_208519_2_.getBlockState(p_208519_4_.up(j3));
                  Material material1 = iblockstate1.getMaterial();
                  if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES) || material1 == Material.VINE) {
                     this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.up(j3), this.metaWood);
                     if (this.vinesGrow && j3 > 0) {
                        if (p_208519_3_.nextInt(3) > 0 && p_208519_2_.isAirBlock(p_208519_4_.add(-1, j3, 0))) {
                           this.addVine(p_208519_2_, p_208519_4_.add(-1, j3, 0), BlockVine.EAST);
                        }

                        if (p_208519_3_.nextInt(3) > 0 && p_208519_2_.isAirBlock(p_208519_4_.add(1, j3, 0))) {
                           this.addVine(p_208519_2_, p_208519_4_.add(1, j3, 0), BlockVine.WEST);
                        }

                        if (p_208519_3_.nextInt(3) > 0 && p_208519_2_.isAirBlock(p_208519_4_.add(0, j3, -1))) {
                           this.addVine(p_208519_2_, p_208519_4_.add(0, j3, -1), BlockVine.SOUTH);
                        }

                        if (p_208519_3_.nextInt(3) > 0 && p_208519_2_.isAirBlock(p_208519_4_.add(0, j3, 1))) {
                           this.addVine(p_208519_2_, p_208519_4_.add(0, j3, 1), BlockVine.NORTH);
                        }
                     }
                  }
               }

               if (this.vinesGrow) {
                  for(int k3 = p_208519_4_.getY() - 3 + i; k3 <= p_208519_4_.getY() + i; ++k3) {
                     int j4 = k3 - (p_208519_4_.getY() + i);
                     int k4 = 2 - j4 / 2;
                     BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

                     for(int l4 = p_208519_4_.getX() - k4; l4 <= p_208519_4_.getX() + k4; ++l4) {
                        for(int i5 = p_208519_4_.getZ() - k4; i5 <= p_208519_4_.getZ() + k4; ++i5) {
                           blockpos$mutableblockpos1.setPos(l4, k3, i5);
                           if (p_208519_2_.getBlockState(blockpos$mutableblockpos1).isIn(BlockTags.LEAVES)) {
                              BlockPos blockpos1 = blockpos$mutableblockpos1.west();
                              BlockPos blockpos2 = blockpos$mutableblockpos1.east();
                              BlockPos blockpos3 = blockpos$mutableblockpos1.north();
                              BlockPos blockpos4 = blockpos$mutableblockpos1.south();
                              if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos1).isAir()) {
                                 this.addHangingVine(p_208519_2_, blockpos1, BlockVine.EAST);
                              }

                              if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos2).isAir()) {
                                 this.addHangingVine(p_208519_2_, blockpos2, BlockVine.WEST);
                              }

                              if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos3).isAir()) {
                                 this.addHangingVine(p_208519_2_, blockpos3, BlockVine.SOUTH);
                              }

                              if (p_208519_3_.nextInt(4) == 0 && p_208519_2_.getBlockState(blockpos4).isAir()) {
                                 this.addHangingVine(p_208519_2_, blockpos4, BlockVine.NORTH);
                              }
                           }
                        }
                     }
                  }

                  if (p_208519_3_.nextInt(5) == 0 && i > 5) {
                     for(int l3 = 0; l3 < 2; ++l3) {
                        for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                           if (p_208519_3_.nextInt(4 - l3) == 0) {
                              EnumFacing enumfacing1 = enumfacing.getOpposite();
                              this.placeCocoa(p_208519_2_, p_208519_3_.nextInt(3), p_208519_4_.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing);
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

   protected int func_208534_a(Random p_208534_1_) {
      return this.minTreeHeight + p_208534_1_.nextInt(3);
   }

   private void placeCocoa(IWorld p_181652_1_, int p_181652_2_, BlockPos p_181652_3_, EnumFacing p_181652_4_) {
      this.setBlockState(p_181652_1_, p_181652_3_, Blocks.COCOA.getDefaultState().with(BlockCocoa.AGE, Integer.valueOf(p_181652_2_)).with(BlockCocoa.HORIZONTAL_FACING, p_181652_4_));
   }

   private void addVine(IWorld p_181651_1_, BlockPos p_181651_2_, BooleanProperty p_181651_3_) {
      this.setBlockState(p_181651_1_, p_181651_2_, Blocks.VINE.getDefaultState().with(p_181651_3_, Boolean.valueOf(true)));
   }

   private void addHangingVine(IWorld p_181650_1_, BlockPos p_181650_2_, BooleanProperty p_181650_3_) {
      this.addVine(p_181650_1_, p_181650_2_, p_181650_3_);
      int i = 4;

      for(BlockPos blockpos = p_181650_2_.down(); p_181650_1_.getBlockState(blockpos).isAir() && i > 0; --i) {
         this.addVine(p_181650_1_, blockpos, p_181650_3_);
         blockpos = blockpos.down();
      }

   }
}
