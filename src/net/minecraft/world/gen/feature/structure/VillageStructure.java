package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class VillageStructure extends Structure<VillageConfig> {
   public String getStructureName() {
      return "Village";
   }

   public int getSize() {
      return 8;
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.getSettings().getVillageDistance();
      int j = p_211744_1_.getSettings().getVillageSeparation();
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setSeed(p_211744_1_.getSeed(), k1, l1, 10387312);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + p_211744_2_.nextInt(i - j);
      l1 = l1 + p_211744_2_.nextInt(i - j);
      return new ChunkPos(k1, l1);
   }

   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      ChunkPos chunkpos = this.getStartPositionForPosition(p_202372_1_, p_202372_2_, p_202372_3_, p_202372_4_, 0, 0);
      if (p_202372_3_ == chunkpos.x && p_202372_4_ == chunkpos.z) {
         Biome biome = p_202372_1_.getBiomeProvider().getBiome(new BlockPos((p_202372_3_ << 4) + 9, 0, (p_202372_4_ << 4) + 9), Biomes.DEFAULT);
         return p_202372_1_.hasStructure(biome, Feature.VILLAGE);
      } else {
         return false;
      }
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
      return new VillageStructure.Start(p_202369_1_, p_202369_2_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   public static class Start extends StructureStart {
      private boolean hasMoreThanTwoComponents;

      public Start() {
      }

      public Start(IWorld p_i48753_1_, IChunkGenerator<?> p_i48753_2_, SharedSeedRandom p_i48753_3_, int p_i48753_4_, int p_i48753_5_, Biome p_i48753_6_) {
         super(p_i48753_4_, p_i48753_5_, p_i48753_6_, p_i48753_3_, p_i48753_1_.getSeed());
         VillageConfig villageconfig = (VillageConfig)p_i48753_2_.getStructureConfig(p_i48753_6_, Feature.VILLAGE);
         List<VillagePieces.PieceWeight> list = VillagePieces.getStructureVillageWeightedPieceList(p_i48753_3_, villageconfig.field_202461_a);
         VillagePieces.Start villagepieces$start = new VillagePieces.Start(0, p_i48753_3_, (p_i48753_4_ << 4) + 2, (p_i48753_5_ << 4) + 2, list, villageconfig);
         this.components.add(villagepieces$start);
         villagepieces$start.buildComponent(villagepieces$start, this.components, p_i48753_3_);
         List<StructurePiece> list1 = villagepieces$start.pendingRoads;
         List<StructurePiece> list2 = villagepieces$start.pendingHouses;

         while(!list1.isEmpty() || !list2.isEmpty()) {
            if (list1.isEmpty()) {
               int i = p_i48753_3_.nextInt(list2.size());
               StructurePiece structurepiece = list2.remove(i);
               structurepiece.buildComponent(villagepieces$start, this.components, p_i48753_3_);
            } else {
               int j = p_i48753_3_.nextInt(list1.size());
               StructurePiece structurepiece2 = list1.remove(j);
               structurepiece2.buildComponent(villagepieces$start, this.components, p_i48753_3_);
            }
         }

         this.recalculateStructureSize(p_i48753_1_);
         int k = 0;

         for(StructurePiece structurepiece1 : this.components) {
            if (!(structurepiece1 instanceof VillagePieces.Road)) {
               ++k;
            }
         }

         this.hasMoreThanTwoComponents = k > 2;
      }

      public boolean isSizeableStructure() {
         return this.hasMoreThanTwoComponents;
      }

      public void writeToNBT(NBTTagCompound p_143022_1_) {
         super.writeToNBT(p_143022_1_);
         p_143022_1_.setBoolean("Valid", this.hasMoreThanTwoComponents);
      }

      public void readFromNBT(NBTTagCompound p_143017_1_) {
         super.readFromNBT(p_143017_1_);
         this.hasMoreThanTwoComponents = p_143017_1_.getBoolean("Valid");
      }
   }
}
