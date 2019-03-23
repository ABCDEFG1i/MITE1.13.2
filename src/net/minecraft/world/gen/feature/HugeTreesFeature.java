package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public abstract class HugeTreesFeature<T extends IFeatureConfig> extends AbstractTreeFeature<T> {
   protected final int baseHeight;
   protected final IBlockState woodMetadata;
   protected final IBlockState leavesMetadata;
   protected int extraRandomHeight;

   public HugeTreesFeature(boolean p_i46447_1_, int p_i46447_2_, int p_i46447_3_, IBlockState p_i46447_4_, IBlockState p_i46447_5_) {
      super(p_i46447_1_);
      this.baseHeight = p_i46447_2_;
      this.extraRandomHeight = p_i46447_3_;
      this.woodMetadata = p_i46447_4_;
      this.leavesMetadata = p_i46447_5_;
   }

   protected int getHeight(Random p_150533_1_) {
      int i = p_150533_1_.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         i += p_150533_1_.nextInt(this.extraRandomHeight);
      }

      return i;
   }

   private boolean isSpaceAt(IBlockReader p_175926_1_, BlockPos p_175926_2_, int p_175926_3_) {
      boolean flag = true;
      if (p_175926_2_.getY() >= 1 && p_175926_2_.getY() + p_175926_3_ + 1 <= 256) {
         for(int i = 0; i <= 1 + p_175926_3_; ++i) {
            int j = 2;
            if (i == 0) {
               j = 1;
            } else if (i >= 1 + p_175926_3_ - 2) {
               j = 2;
            }

            for(int k = -j; k <= j && flag; ++k) {
               for(int l = -j; l <= j && flag; ++l) {
                  if (p_175926_2_.getY() + i < 0 || p_175926_2_.getY() + i >= 256 || !this.canGrowInto(p_175926_1_.getBlockState(p_175926_2_.add(k, i, l)).getBlock())) {
                     flag = false;
                  }
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   private boolean func_202405_b(IWorld p_202405_1_, BlockPos p_202405_2_) {
      BlockPos blockpos = p_202405_2_.down();
      Block block = p_202405_1_.getBlockState(blockpos).getBlock();
      if ((block == Blocks.GRASS_BLOCK || Block.isDirt(block)) && p_202405_2_.getY() >= 2) {
         this.setDirtAt(p_202405_1_, blockpos);
         this.setDirtAt(p_202405_1_, blockpos.east());
         this.setDirtAt(p_202405_1_, blockpos.south());
         this.setDirtAt(p_202405_1_, blockpos.south().east());
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_203427_a(IWorld p_203427_1_, BlockPos p_203427_2_, int p_203427_3_) {
      return this.isSpaceAt(p_203427_1_, p_203427_2_, p_203427_3_) && this.func_202405_b(p_203427_1_, p_203427_2_);
   }

   protected void growLeavesLayerStrict(IWorld p_175925_1_, BlockPos p_175925_2_, int p_175925_3_) {
      int i = p_175925_3_ * p_175925_3_;

      for(int j = -p_175925_3_; j <= p_175925_3_ + 1; ++j) {
         for(int k = -p_175925_3_; k <= p_175925_3_ + 1; ++k) {
            int l = Math.min(Math.abs(j), Math.abs(j - 1));
            int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
            if (l + i1 < 7 && l * l + i1 * i1 <= i) {
               BlockPos blockpos = p_175925_2_.add(j, 0, k);
               IBlockState iblockstate = p_175925_1_.getBlockState(blockpos);
               if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.setBlockState(p_175925_1_, blockpos, this.leavesMetadata);
               }
            }
         }
      }

   }

   protected void growLeavesLayer(IWorld p_175928_1_, BlockPos p_175928_2_, int p_175928_3_) {
      int i = p_175928_3_ * p_175928_3_;

      for(int j = -p_175928_3_; j <= p_175928_3_; ++j) {
         for(int k = -p_175928_3_; k <= p_175928_3_; ++k) {
            if (j * j + k * k <= i) {
               BlockPos blockpos = p_175928_2_.add(j, 0, k);
               IBlockState iblockstate = p_175928_1_.getBlockState(blockpos);
               if (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) {
                  this.setBlockState(p_175928_1_, blockpos, this.leavesMetadata);
               }
            }
         }
      }

   }
}
