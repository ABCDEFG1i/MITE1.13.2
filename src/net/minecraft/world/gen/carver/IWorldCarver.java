package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;

public interface IWorldCarver<C extends IFeatureConfig> {
   boolean func_212246_a(IBlockReader p_212246_1_, Random p_212246_2_, int p_212246_3_, int p_212246_4_, C p_212246_5_);

   boolean carve(IWorld p_202522_1_, Random p_202522_2_, int p_202522_3_, int p_202522_4_, int p_202522_5_, int p_202522_6_, BitSet p_202522_7_, C p_202522_8_);
}
