package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.CanopyTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class DarkOakTree extends AbstractBigTree {
   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random p_196936_1_) {
      return null;
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random p_196938_1_) {
      return new CanopyTreeFeature(true);
   }
}
