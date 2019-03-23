package net.minecraft.world.gen.feature.template;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class IntegrityProcessor implements ITemplateProcessor {
   private final float chance;
   private final Random random;

   public IntegrityProcessor(BlockPos p_i47148_1_, PlacementSettings p_i47148_2_) {
      this.chance = p_i47148_2_.getIntegrity();
      this.random = p_i47148_2_.getRandom(p_i47148_1_);
   }

   @Nullable
   public Template.BlockInfo processBlock(IBlockReader p_189943_1_, BlockPos p_189943_2_, Template.BlockInfo p_189943_3_) {
      return !(this.chance >= 1.0F) && !(this.random.nextFloat() <= this.chance) ? null : p_189943_3_;
   }
}
