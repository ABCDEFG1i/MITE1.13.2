package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class AbstractTree {
   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random p_196936_1_);

   public boolean spawn(IWorld p_196935_1_, BlockPos p_196935_2_, IBlockState p_196935_3_, Random p_196935_4_) {
      AbstractTreeFeature<NoFeatureConfig> abstracttreefeature = this.getTreeFeature(p_196935_4_);
      if (abstracttreefeature == null) {
         return false;
      } else {
         p_196935_1_.setBlockState(p_196935_2_, Blocks.AIR.getDefaultState(), 4);
         if (abstracttreefeature.func_212245_a(p_196935_1_, p_196935_1_.getChunkProvider().getChunkGenerator(), p_196935_4_, p_196935_2_, IFeatureConfig.NO_FEATURE_CONFIG)) {
            return true;
         } else {
            p_196935_1_.setBlockState(p_196935_2_, p_196935_3_, 4);
            return false;
         }
      }
   }
}
