package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class MineshaftStructure extends Structure<MineshaftConfig> {
   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      ((SharedSeedRandom)p_202372_2_).func_202425_c(p_202372_1_.getSeed(), p_202372_3_, p_202372_4_);
      Biome biome = p_202372_1_.getBiomeProvider().getBiome(new BlockPos((p_202372_3_ << 4) + 9, 0, (p_202372_4_ << 4) + 9), Biomes.DEFAULT);
      if (p_202372_1_.hasStructure(biome, Feature.MINESHAFT)) {
         MineshaftConfig mineshaftconfig = (MineshaftConfig)p_202372_1_.getStructureConfig(biome, Feature.MINESHAFT);
         double d0 = mineshaftconfig.field_202439_a;
         return p_202372_2_.nextDouble() < d0;
      } else {
         return false;
      }
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
      return new MineshaftStructure.Start(p_202369_1_, p_202369_2_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected String getStructureName() {
      return "Mineshaft";
   }

   public int getSize() {
      return 8;
   }

   @Override
   public Item getSymbolItem() {
      return Items.CHEST_MINECART;
   }

   public static class Start extends StructureStart {
      private MineshaftStructure.Type field_202507_c;

      public Start() {
      }

      public Start(IWorld p_i48759_1_, IChunkGenerator<?> p_i48759_2_, SharedSeedRandom p_i48759_3_, int p_i48759_4_, int p_i48759_5_, Biome p_i48759_6_) {
         super(p_i48759_4_, p_i48759_5_, p_i48759_6_, p_i48759_3_, p_i48759_1_.getSeed());
         MineshaftConfig mineshaftconfig = (MineshaftConfig)p_i48759_2_.getStructureConfig(p_i48759_6_, Feature.MINESHAFT);
         this.field_202507_c = mineshaftconfig.type;
         MineshaftPieces.Room mineshaftpieces$room = new MineshaftPieces.Room(0, p_i48759_3_, (p_i48759_4_ << 4) + 2, (p_i48759_5_ << 4) + 2, this.field_202507_c);
         this.components.add(mineshaftpieces$room);
         mineshaftpieces$room.buildComponent(mineshaftpieces$room, this.components, p_i48759_3_);
         this.recalculateStructureSize(p_i48759_1_);
         if (mineshaftconfig.type == MineshaftStructure.Type.MESA) {
            int i = -5;
            int j = p_i48759_1_.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getYSize() / 2 - -5;
            this.boundingBox.offset(0, j, 0);

            for(StructurePiece structurepiece : this.components) {
               structurepiece.offset(0, j, 0);
            }
         } else {
            this.markAvailableHeight(p_i48759_1_, p_i48759_3_, 10);
         }

      }
   }

   public enum Type {
      NORMAL,
      MESA;

      public static MineshaftStructure.Type byId(int p_189910_0_) {
         return p_189910_0_ >= 0 && p_189910_0_ < values().length ? values()[p_189910_0_] : NORMAL;
      }
   }
}
