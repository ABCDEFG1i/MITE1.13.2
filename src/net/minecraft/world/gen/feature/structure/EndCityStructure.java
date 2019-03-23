package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class EndCityStructure extends Structure<EndCityConfig> {
   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.getSettings().getEndCityDistance();
      int j = p_211744_1_.getSettings().getEndCitySeparation();
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setSeed(p_211744_1_.getSeed(), k1, l1, 10387313);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      l1 = l1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      return new ChunkPos(k1, l1);
   }

   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      ChunkPos chunkpos = this.getStartPositionForPosition(p_202372_1_, p_202372_2_, p_202372_3_, p_202372_4_, 0, 0);
      if (p_202372_3_ == chunkpos.x && p_202372_4_ == chunkpos.z) {
         Biome biome = p_202372_1_.getBiomeProvider().getBiome(new BlockPos((p_202372_3_ << 4) + 9, 0, (p_202372_4_ << 4) + 9), Biomes.DEFAULT);
         if (!p_202372_1_.hasStructure(biome, Feature.END_CITY)) {
            return false;
         } else {
            int i = getYPosForStructure(p_202372_3_, p_202372_4_, p_202372_1_);
            return i >= 60;
         }
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
      return new EndCityStructure.Start(p_202369_1_, p_202369_2_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected String getStructureName() {
      return "EndCity";
   }

   public int getSize() {
      return 9;
   }

   private static int getYPosForStructure(int p_191070_0_, int p_191070_1_, IChunkGenerator<?> p_191070_2_) {
      Random random = new Random((long)(p_191070_0_ + p_191070_1_ * 10387313));
      Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
      ChunkPrimer chunkprimer = new ChunkPrimer(new ChunkPos(p_191070_0_, p_191070_1_), UpgradeData.EMPTY);
      p_191070_2_.makeBase(chunkprimer);
      int i = 5;
      int j = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
         i = -5;
      } else if (rotation == Rotation.CLOCKWISE_180) {
         i = -5;
         j = -5;
      } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
         j = -5;
      }

      int k = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7);
      int l = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7 + j);
      int i1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7);
      int j1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7 + j);
      return Math.min(Math.min(k, l), Math.min(i1, j1));
   }

   public static class Start extends StructureStart {
      private boolean isSizeable;

      public Start() {
      }

      public Start(IWorld p_i48770_1_, IChunkGenerator<?> p_i48770_2_, SharedSeedRandom p_i48770_3_, int p_i48770_4_, int p_i48770_5_, Biome p_i48770_6_) {
         super(p_i48770_4_, p_i48770_5_, p_i48770_6_, p_i48770_3_, p_i48770_1_.getSeed());
         Rotation rotation = Rotation.values()[p_i48770_3_.nextInt(Rotation.values().length)];
         int i = EndCityStructure.getYPosForStructure(p_i48770_4_, p_i48770_5_, p_i48770_2_);
         if (i < 60) {
            this.isSizeable = false;
         } else {
            BlockPos blockpos = new BlockPos(p_i48770_4_ * 16 + 8, i, p_i48770_5_ * 16 + 8);
            EndCityPieces.startHouseTower(p_i48770_1_.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, p_i48770_3_);
            this.recalculateStructureSize(p_i48770_1_);
            this.isSizeable = true;
         }
      }

      public boolean isSizeableStructure() {
         return this.isSizeable;
      }
   }
}
