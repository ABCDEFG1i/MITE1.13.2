package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class ScatteredStructure<C extends IFeatureConfig> extends Structure<C> {
   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = this.getBiomeFeatureDistance(p_211744_1_);
      int j = this.func_211745_b(p_211744_1_);
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setSeed(p_211744_1_.getSeed(), k1, l1, this.getSeedModifier());
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + p_211744_2_.nextInt(i - j);
      l1 = l1 + p_211744_2_.nextInt(i - j);
      return new ChunkPos(k1, l1);
   }

   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      ChunkPos chunkpos = this.getStartPositionForPosition(p_202372_1_, p_202372_2_, p_202372_3_, p_202372_4_, 0, 0);
      if (p_202372_3_ == chunkpos.x && p_202372_4_ == chunkpos.z) {
         Biome biome = p_202372_1_.getBiomeProvider().getBiome(new BlockPos(p_202372_3_ * 16 + 9, 0, p_202372_4_ * 16 + 9),
                 null);
          return p_202372_1_.hasStructure(biome, this);
      }

      return false;
   }

   protected int getBiomeFeatureDistance(IChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.getSettings().getBiomeFeatureDistance();
   }

   protected int func_211745_b(IChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.getSettings().func_211731_i();
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected abstract StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_);

   protected abstract int getSeedModifier();
}
