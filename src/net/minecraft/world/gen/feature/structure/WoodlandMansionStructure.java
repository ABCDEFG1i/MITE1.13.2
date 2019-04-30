package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class WoodlandMansionStructure extends Structure<WoodlandMansionConfig> {
   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.getSettings().getMansionDistance();
      int j = p_211744_1_.getSettings().getMansionSeparation();
      int k = p_211744_3_ + i * p_211744_5_;
      int l = p_211744_4_ + i * p_211744_6_;
      int i1 = k < 0 ? k - i + 1 : k;
      int j1 = l < 0 ? l - i + 1 : l;
      int k1 = i1 / i;
      int l1 = j1 / i;
      ((SharedSeedRandom)p_211744_2_).setSeed(p_211744_1_.getSeed(), k1, l1, 10387319);
      k1 = k1 * i;
      l1 = l1 * i;
      k1 = k1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      l1 = l1 + (p_211744_2_.nextInt(i - j) + p_211744_2_.nextInt(i - j)) / 2;
      return new ChunkPos(k1, l1);
   }

   @Override
   public StructureRequirements getRequirements() {
      return StructureRequirements.MANSION;
   }

   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      ChunkPos chunkpos = this.getStartPositionForPosition(p_202372_1_, p_202372_2_, p_202372_3_, p_202372_4_, 0, 0);
      if (p_202372_3_ == chunkpos.x && p_202372_4_ == chunkpos.z) {
         for(Biome biome : p_202372_1_.getBiomeProvider().getBiomesInSquare(p_202372_3_ * 16 + 9, p_202372_4_ * 16 + 9, 32)) {
            if (!p_202372_1_.hasStructure(biome, Feature.WOODLAND_MANSION)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
      return new WoodlandMansionStructure.Start(p_202369_1_, p_202369_2_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected String getStructureName() {
      return "Mansion";
   }

   public int getSize() {
      return 8;
   }

   @Override
   public Item getSymbolItem() {
      return Blocks.DARK_OAK_LOG.asItem();
   }

   public static class Start extends StructureStart {
      private boolean isValid;

      public Start() {
      }

      public Start(IWorld p_i48767_1_, IChunkGenerator<?> p_i48767_2_, SharedSeedRandom p_i48767_3_, int p_i48767_4_, int p_i48767_5_, Biome p_i48767_6_) {
         super(p_i48767_4_, p_i48767_5_, p_i48767_6_, p_i48767_3_, p_i48767_1_.getSeed());
         Rotation rotation = Rotation.values()[p_i48767_3_.nextInt(Rotation.values().length)];
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

         ChunkPrimer chunkprimer = new ChunkPrimer(new ChunkPos(p_i48767_4_, p_i48767_5_), UpgradeData.EMPTY);
         p_i48767_2_.makeBase(chunkprimer);
         int k = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7);
         int l = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7 + j);
         int i1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7);
         int j1 = chunkprimer.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7 + j);
         int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));
         if (k1 < 60) {
            this.isValid = false;
         } else {
            BlockPos blockpos = new BlockPos(p_i48767_4_ * 16 + 8, k1 + 1, p_i48767_5_ * 16 + 8);
            List<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(p_i48767_1_.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, p_i48767_3_);
            this.components.addAll(list);
            this.recalculateStructureSize(p_i48767_1_);
            this.isValid = true;
         }
      }

      public void generateStructure(IWorld p_75068_1_, Random p_75068_2_, MutableBoundingBox p_75068_3_, ChunkPos p_75068_4_) {
         super.generateStructure(p_75068_1_, p_75068_2_, p_75068_3_, p_75068_4_);
         int i = this.boundingBox.minY;

         for(int j = p_75068_3_.minX; j <= p_75068_3_.maxX; ++j) {
            for(int k = p_75068_3_.minZ; k <= p_75068_3_.maxZ; ++k) {
               BlockPos blockpos = new BlockPos(j, i, k);
               if (!p_75068_1_.isAirBlock(blockpos) && this.boundingBox.isVecInside(blockpos)) {
                  boolean flag = false;

                  for(StructurePiece structurepiece : this.components) {
                     if (structurepiece.getBoundingBox().isVecInside(blockpos)) {
                        flag = true;
                        break;
                     }
                  }

                  if (flag) {
                     for(int l = i - 1; l > 1; --l) {
                        BlockPos blockpos1 = new BlockPos(j, l, k);
                        if (!p_75068_1_.isAirBlock(blockpos1) && !p_75068_1_.getBlockState(blockpos1).getMaterial().isLiquid()) {
                           break;
                        }

                        p_75068_1_.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

      }

      public boolean isSizeableStructure() {
         return this.isValid;
      }
   }
}
