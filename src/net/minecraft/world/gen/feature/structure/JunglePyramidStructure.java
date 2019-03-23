package net.minecraft.world.gen.feature.structure;

import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class JunglePyramidStructure extends ScatteredStructure<JunglePyramidConfig> {
   protected String getStructureName() {
      return "Jungle_Pyramid";
   }

   public int getSize() {
      return 3;
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.PLAINS);
      return new JunglePyramidStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected int getSeedModifier() {
      return 14357619;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48733_1_, SharedSeedRandom p_i48733_2_, int p_i48733_3_, int p_i48733_4_, Biome p_i48733_5_) {
         super(p_i48733_3_, p_i48733_4_, p_i48733_5_, p_i48733_2_, p_i48733_1_.getSeed());
         JunglePyramidPiece junglepyramidpiece = new JunglePyramidPiece(p_i48733_2_, p_i48733_3_ * 16, p_i48733_4_ * 16);
         this.components.add(junglepyramidpiece);
         this.recalculateStructureSize(p_i48733_1_);
      }
   }
}
