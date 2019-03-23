package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class AbstractBigTree extends AbstractTree {
   public boolean spawn(IWorld p_196935_1_, BlockPos p_196935_2_, IBlockState p_196935_3_, Random p_196935_4_) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (canBigTreeSpawnAt(p_196935_3_, p_196935_1_, p_196935_2_, i, j)) {
               return this.spawnBigTree(p_196935_1_, p_196935_2_, p_196935_3_, p_196935_4_, i, j);
            }
         }
      }

      return super.spawn(p_196935_1_, p_196935_2_, p_196935_3_, p_196935_4_);
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random p_196938_1_);

   public boolean spawnBigTree(IWorld p_196939_1_, BlockPos p_196939_2_, IBlockState p_196939_3_, Random p_196939_4_, int p_196939_5_, int p_196939_6_) {
      AbstractTreeFeature<NoFeatureConfig> abstracttreefeature = this.getBigTreeFeature(p_196939_4_);
      if (abstracttreefeature == null) {
         return false;
      } else {
         IBlockState iblockstate = Blocks.AIR.getDefaultState();
         p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_, 0, p_196939_6_), iblockstate, 4);
         p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_ + 1, 0, p_196939_6_), iblockstate, 4);
         p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_, 0, p_196939_6_ + 1), iblockstate, 4);
         p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_ + 1, 0, p_196939_6_ + 1), iblockstate, 4);
         if (abstracttreefeature.func_212245_a(p_196939_1_, p_196939_1_.getChunkProvider().getChunkGenerator(), p_196939_4_, p_196939_2_.add(p_196939_5_, 0, p_196939_6_), IFeatureConfig.NO_FEATURE_CONFIG)) {
            return true;
         } else {
            p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_, 0, p_196939_6_), p_196939_3_, 4);
            p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_ + 1, 0, p_196939_6_), p_196939_3_, 4);
            p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_, 0, p_196939_6_ + 1), p_196939_3_, 4);
            p_196939_1_.setBlockState(p_196939_2_.add(p_196939_5_ + 1, 0, p_196939_6_ + 1), p_196939_3_, 4);
            return false;
         }
      }
   }

   public static boolean canBigTreeSpawnAt(IBlockState p_196937_0_, IBlockReader p_196937_1_, BlockPos p_196937_2_, int p_196937_3_, int p_196937_4_) {
      Block block = p_196937_0_.getBlock();
      return block == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_, 0, p_196937_4_)).getBlock() && block == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_ + 1, 0, p_196937_4_)).getBlock() && block == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_, 0, p_196937_4_ + 1)).getBlock() && block == p_196937_1_.getBlockState(p_196937_2_.add(p_196937_3_ + 1, 0, p_196937_4_ + 1)).getBlock();
   }
}
