package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class BuriedTreasureStructure extends Structure<BuriedTreasureConfig> {
   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      Biome biome = p_202372_1_.getBiomeProvider().getBiome(new BlockPos((p_202372_3_ << 4) + 9, 0, (p_202372_4_ << 4) + 9),
              null);
      if (p_202372_1_.hasStructure(biome, Feature.BURIED_TREASURE)) {
         ((SharedSeedRandom)p_202372_2_).setSeed(p_202372_1_.getSeed(), p_202372_3_, p_202372_4_, 10387320);
         BuriedTreasureConfig buriedtreasureconfig = (BuriedTreasureConfig)p_202372_1_.getStructureConfig(biome, Feature.BURIED_TREASURE);
         return p_202372_2_.nextFloat() < buriedtreasureconfig.chance;
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9),
              null);
      return new BuriedTreasureStructure.Start(p_202369_1_, p_202369_2_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected String getStructureName() {
      return "Buried_Treasure";
   }

   public int getSize() {
      return 1;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48890_1_, IChunkGenerator<?> p_i48890_2_, SharedSeedRandom p_i48890_3_, int p_i48890_4_, int p_i48890_5_, Biome p_i48890_6_) {
         super(p_i48890_4_, p_i48890_5_, p_i48890_6_, p_i48890_3_, p_i48890_1_.getSeed());
         int i = p_i48890_4_ * 16;
         int j = p_i48890_5_ * 16;
         BlockPos blockpos = new BlockPos(i + 9, 90, j + 9);
         this.components.add(new BuriedTreasurePieces.Piece(blockpos));
         this.recalculateStructureSize(p_i48890_1_);
      }

      public BlockPos getPos() {
         return new BlockPos((this.chunkPosX << 4) + 9, 0, (this.chunkPosZ << 4) + 9);
      }
   }
}
