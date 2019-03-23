package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.MegaPineTree;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TallTaigaTreeFeature;

public class SpruceTree extends AbstractBigTree {
   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random p_196936_1_) {
      return new TallTaigaTreeFeature(true);
   }

   @Nullable
   protected AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random p_196938_1_) {
      return new MegaPineTree(false, p_196938_1_.nextBoolean());
   }
}
