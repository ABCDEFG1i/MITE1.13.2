package net.minecraft.world.gen.feature.template;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ITemplateProcessor {
   @Nullable
   Template.BlockInfo processBlock(IBlockReader p_189943_1_, BlockPos p_189943_2_, Template.BlockInfo p_189943_3_);
}
