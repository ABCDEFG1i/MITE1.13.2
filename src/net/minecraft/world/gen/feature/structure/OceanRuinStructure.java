package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStructure extends ScatteredStructure<OceanRuinConfig> {
   public String getStructureName() {
      return "Ocean_Ruin";
   }

   public int getSize() {
      return 3;
   }

   protected int getBiomeFeatureDistance(IChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.getSettings().func_204026_h();
   }

   protected int func_211745_b(IChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.getSettings().func_211727_m();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), (Biome)null);
      return new OceanRuinStructure.Start(p_202369_1_, p_202369_2_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected int getSeedModifier() {
      return 14357621;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48872_1_, IChunkGenerator<?> p_i48872_2_, SharedSeedRandom p_i48872_3_, int p_i48872_4_, int p_i48872_5_, Biome p_i48872_6_) {
         super(p_i48872_4_, p_i48872_5_, p_i48872_6_, p_i48872_3_, p_i48872_1_.getSeed());
         OceanRuinConfig oceanruinconfig = (OceanRuinConfig)p_i48872_2_.getStructureConfig(p_i48872_6_, Feature.OCEAN_RUIN);
         int i = p_i48872_4_ * 16;
         int j = p_i48872_5_ * 16;
         BlockPos blockpos = new BlockPos(i, 90, j);
         Rotation rotation = Rotation.values()[p_i48872_3_.nextInt(Rotation.values().length)];
         TemplateManager templatemanager = p_i48872_1_.getSaveHandler().getStructureTemplateManager();
         OceanRuinPieces.func_204041_a(templatemanager, blockpos, rotation, this.components, p_i48872_3_, oceanruinconfig);
         this.recalculateStructureSize(p_i48872_1_);
      }
   }

   public static enum Type {
      WARM,
      COLD;
   }
}
