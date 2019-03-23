package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class MegaPineTree extends HugeTreesFeature<NoFeatureConfig> {
   private static final IBlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final IBlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();
   private static final IBlockState PODZOL = Blocks.PODZOL.getDefaultState();
   private final boolean useBaseHeight;

   public MegaPineTree(boolean p_i45457_1_, boolean p_i45457_2_) {
      super(p_i45457_1_, 13, 15, TRUNK, LEAF);
      this.useBaseHeight = p_i45457_2_;
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = this.getHeight(p_208519_3_);
      if (!this.func_203427_a(p_208519_2_, p_208519_4_, i)) {
         return false;
      } else {
         this.createCrown(p_208519_2_, p_208519_4_.getX(), p_208519_4_.getZ(), p_208519_4_.getY() + i, 0, p_208519_3_);

         for(int j = 0; j < i; ++j) {
            IBlockState iblockstate = p_208519_2_.getBlockState(p_208519_4_.up(j));
            if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
               this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.up(j), this.woodMetadata);
            }

            if (j < i - 1) {
               iblockstate = p_208519_2_.getBlockState(p_208519_4_.add(1, j, 0));
               if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.add(1, j, 0), this.woodMetadata);
               }

               iblockstate = p_208519_2_.getBlockState(p_208519_4_.add(1, j, 1));
               if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.add(1, j, 1), this.woodMetadata);
               }

               iblockstate = p_208519_2_.getBlockState(p_208519_4_.add(0, j, 1));
               if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.func_208520_a(p_208519_1_, p_208519_2_, p_208519_4_.add(0, j, 1), this.woodMetadata);
               }
            }
         }

         this.generateSaplings(p_208519_2_, p_208519_3_, p_208519_4_);
         return true;
      }
   }

   private void createCrown(IWorld p_150541_1_, int p_150541_2_, int p_150541_3_, int p_150541_4_, int p_150541_5_, Random p_150541_6_) {
      int i = p_150541_6_.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
      int j = 0;

      for(int k = p_150541_4_ - i; k <= p_150541_4_; ++k) {
         int l = p_150541_4_ - k;
         int i1 = p_150541_5_ + MathHelper.floor((float)l / (float)i * 3.5F);
         this.growLeavesLayerStrict(p_150541_1_, new BlockPos(p_150541_2_, k, p_150541_3_), i1 + (l > 0 && i1 == j && (k & 1) == 0 ? 1 : 0));
         j = i1;
      }

   }

   public void generateSaplings(IWorld p_180711_1_, Random p_180711_2_, BlockPos p_180711_3_) {
      this.placePodzolCircle(p_180711_1_, p_180711_3_.west().north());
      this.placePodzolCircle(p_180711_1_, p_180711_3_.east(2).north());
      this.placePodzolCircle(p_180711_1_, p_180711_3_.west().south(2));
      this.placePodzolCircle(p_180711_1_, p_180711_3_.east(2).south(2));

      for(int i = 0; i < 5; ++i) {
         int j = p_180711_2_.nextInt(64);
         int k = j % 8;
         int l = j / 8;
         if (k == 0 || k == 7 || l == 0 || l == 7) {
            this.placePodzolCircle(p_180711_1_, p_180711_3_.add(-3 + k, 0, -3 + l));
         }
      }

   }

   private void placePodzolCircle(IWorld p_175933_1_, BlockPos p_175933_2_) {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.placePodzolAt(p_175933_1_, p_175933_2_.add(i, 0, j));
            }
         }
      }

   }

   private void placePodzolAt(IWorld p_175934_1_, BlockPos p_175934_2_) {
      for(int i = 2; i >= -3; --i) {
         BlockPos blockpos = p_175934_2_.up(i);
         IBlockState iblockstate = p_175934_1_.getBlockState(blockpos);
         Block block = iblockstate.getBlock();
         if (block == Blocks.GRASS_BLOCK || Block.isDirt(block)) {
            this.setBlockState(p_175934_1_, blockpos, PODZOL);
            break;
         }

         if (!iblockstate.isAir() && i < 0) {
            break;
         }
      }

   }
}
