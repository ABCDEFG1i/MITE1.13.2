package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class MegaJungleFeature extends HugeTreesFeature<NoFeatureConfig> {
   public MegaJungleFeature(boolean p_i46448_1_, int p_i46448_2_, int p_i46448_3_, IBlockState p_i46448_4_, IBlockState p_i46448_5_) {
      super(p_i46448_1_, p_i46448_2_, p_i46448_3_, p_i46448_4_, p_i46448_5_);
   }

   public boolean place(Set<BlockPos> p_208519_1_, IWorld p_208519_2_, Random p_208519_3_, BlockPos p_208519_4_) {
      int i = this.getHeight(p_208519_3_);
      if (!this.func_203427_a(p_208519_2_, p_208519_4_, i)) {
         return false;
      } else {
         this.func_202408_c(p_208519_2_, p_208519_4_.up(i), 2);

         for(int j = p_208519_4_.getY() + i - 2 - p_208519_3_.nextInt(4); j > p_208519_4_.getY() + i / 2; j -= 2 + p_208519_3_.nextInt(4)) {
            float f = p_208519_3_.nextFloat() * ((float)Math.PI * 2F);
            int k = p_208519_4_.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
            int l = p_208519_4_.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

            for(int i1 = 0; i1 < 5; ++i1) {
               k = p_208519_4_.getX() + (int)(1.5F + MathHelper.cos(f) * (float)i1);
               l = p_208519_4_.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)i1);
               this.func_208520_a(p_208519_1_, p_208519_2_, new BlockPos(k, j - 3 + i1 / 2, l), this.woodMetadata);
            }

            int j2 = 1 + p_208519_3_.nextInt(2);
            int j1 = j;

            for(int k1 = j - j2; k1 <= j1; ++k1) {
               int l1 = k1 - j1;
               this.growLeavesLayer(p_208519_2_, new BlockPos(k, k1, l), 1 - l1);
            }
         }

         for(int i2 = 0; i2 < i; ++i2) {
            BlockPos blockpos = p_208519_4_.up(i2);
            if (this.canGrowInto(p_208519_2_.getBlockState(blockpos).getBlock())) {
               this.func_208520_a(p_208519_1_, p_208519_2_, blockpos, this.woodMetadata);
               if (i2 > 0) {
                  this.func_202407_a(p_208519_2_, p_208519_3_, blockpos.west(), BlockVine.EAST);
                  this.func_202407_a(p_208519_2_, p_208519_3_, blockpos.north(), BlockVine.SOUTH);
               }
            }

            if (i2 < i - 1) {
               BlockPos blockpos1 = blockpos.east();
               if (this.canGrowInto(p_208519_2_.getBlockState(blockpos1).getBlock())) {
                  this.func_208520_a(p_208519_1_, p_208519_2_, blockpos1, this.woodMetadata);
                  if (i2 > 0) {
                     this.func_202407_a(p_208519_2_, p_208519_3_, blockpos1.east(), BlockVine.WEST);
                     this.func_202407_a(p_208519_2_, p_208519_3_, blockpos1.north(), BlockVine.SOUTH);
                  }
               }

               BlockPos blockpos2 = blockpos.south().east();
               if (this.canGrowInto(p_208519_2_.getBlockState(blockpos2).getBlock())) {
                  this.func_208520_a(p_208519_1_, p_208519_2_, blockpos2, this.woodMetadata);
                  if (i2 > 0) {
                     this.func_202407_a(p_208519_2_, p_208519_3_, blockpos2.east(), BlockVine.WEST);
                     this.func_202407_a(p_208519_2_, p_208519_3_, blockpos2.south(), BlockVine.NORTH);
                  }
               }

               BlockPos blockpos3 = blockpos.south();
               if (this.canGrowInto(p_208519_2_.getBlockState(blockpos3).getBlock())) {
                  this.func_208520_a(p_208519_1_, p_208519_2_, blockpos3, this.woodMetadata);
                  if (i2 > 0) {
                     this.func_202407_a(p_208519_2_, p_208519_3_, blockpos3.west(), BlockVine.EAST);
                     this.func_202407_a(p_208519_2_, p_208519_3_, blockpos3.south(), BlockVine.NORTH);
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_202407_a(IWorld p_202407_1_, Random p_202407_2_, BlockPos p_202407_3_, BooleanProperty p_202407_4_) {
      if (p_202407_2_.nextInt(3) > 0 && p_202407_1_.isAirBlock(p_202407_3_)) {
         this.setBlockState(p_202407_1_, p_202407_3_, Blocks.VINE.getDefaultState().with(p_202407_4_, Boolean.valueOf(true)));
      }

   }

   private void func_202408_c(IWorld p_202408_1_, BlockPos p_202408_2_, int p_202408_3_) {
      int i = 2;

      for(int j = -2; j <= 0; ++j) {
         this.growLeavesLayerStrict(p_202408_1_, p_202408_2_.up(j), p_202408_3_ + 1 - j);
      }

   }
}
