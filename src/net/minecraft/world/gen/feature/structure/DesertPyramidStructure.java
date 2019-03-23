package net.minecraft.world.gen.feature.structure;

import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class DesertPyramidStructure extends ScatteredStructure<DesertPyramidConfig> {
   protected String getStructureName() {
      return "Desert_Pyramid";
   }

   public int getSize() {
      return 3;
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.PLAINS);
      return new DesertPyramidStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected int getSeedModifier() {
      return 14357617;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48628_1_, SharedSeedRandom p_i48628_2_, int p_i48628_3_, int p_i48628_4_, Biome p_i48628_5_) {
         super(p_i48628_3_, p_i48628_4_, p_i48628_5_, p_i48628_2_, p_i48628_1_.getSeed());
         DesertPyramidPiece desertpyramidpiece = new DesertPyramidPiece(p_i48628_2_, p_i48628_3_ * 16, p_i48628_4_ * 16);
         this.components.add(desertpyramidpiece);
         this.recalculateStructureSize(p_i48628_1_);
      }
   }
}
