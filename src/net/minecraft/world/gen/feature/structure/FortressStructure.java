package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class FortressStructure extends Structure<FortressConfig> {
   private static final List<Biome.SpawnListEntry> field_202381_d = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnListEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnListEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnListEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 3, 4, 4));

   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      int i = p_202372_3_ >> 4;
      int j = p_202372_4_ >> 4;
      p_202372_2_.setSeed((long)(i ^ j << 4) ^ p_202372_1_.getSeed());
      p_202372_2_.nextInt();
      if (p_202372_2_.nextInt(3) != 0) {
         return false;
      } else if (p_202372_3_ != (i << 4) + 4 + p_202372_2_.nextInt(8)) {
         return false;
      } else if (p_202372_4_ != (j << 4) + 4 + p_202372_2_.nextInt(8)) {
         return false;
      } else {
         Biome biome = p_202372_1_.getBiomeProvider().getBiome(new BlockPos((p_202372_3_ << 4) + 9, 0, (p_202372_4_ << 4) + 9), Biomes.DEFAULT);
         return p_202372_1_.hasStructure(biome, Feature.FORTRESS);
      }
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
      return new FortressStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected String getStructureName() {
      return "Fortress";
   }

   public int getSize() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202381_d;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48727_1_, SharedSeedRandom p_i48727_2_, int p_i48727_3_, int p_i48727_4_, Biome p_i48727_5_) {
         super(p_i48727_3_, p_i48727_4_, p_i48727_5_, p_i48727_2_, p_i48727_1_.getSeed());
         FortressPieces.Start fortresspieces$start = new FortressPieces.Start(p_i48727_2_, (p_i48727_3_ << 4) + 2, (p_i48727_4_ << 4) + 2);
         this.components.add(fortresspieces$start);
         fortresspieces$start.buildComponent(fortresspieces$start, this.components, p_i48727_2_);
         List<StructurePiece> list = fortresspieces$start.pendingChildren;

         while(!list.isEmpty()) {
            int i = p_i48727_2_.nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.buildComponent(fortresspieces$start, this.components, p_i48727_2_);
         }

         this.recalculateStructureSize(p_i48727_1_);
         this.setRandomHeight(p_i48727_1_, p_i48727_2_, 48, 70);
      }
   }
}
