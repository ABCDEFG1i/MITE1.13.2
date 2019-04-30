package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class SwampHutStructure extends ScatteredStructure<SwampHutConfig> {
   private static final List<Biome.SpawnListEntry> field_202384_d = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.WITCH, 1, 1, 1));

   protected String getStructureName() {
      return "Swamp_Hut";
   }

   public int getSize() {
      return 3;
   }

   @Override
   public Item getSymbolItem() {
      return Blocks.VINE.asItem();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.PLAINS);
      return new SwampHutStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected int getSeedModifier() {
      return 14357620;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202384_d;
   }

   public boolean func_202383_b(IWorld p_202383_1_, BlockPos p_202383_2_) {
      StructureStart structurestart = this.getStart(p_202383_1_, p_202383_2_);
      if (structurestart != NO_STRUCTURE && structurestart instanceof SwampHutStructure.Start && !structurestart.getComponents().isEmpty()) {
         StructurePiece structurepiece = structurestart.getComponents().get(0);
         return structurepiece instanceof SwampHutPiece;
      } else {
         return false;
      }
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48752_1_, SharedSeedRandom p_i48752_2_, int p_i48752_3_, int p_i48752_4_, Biome p_i48752_5_) {
         super(p_i48752_3_, p_i48752_4_, p_i48752_5_, p_i48752_2_, p_i48752_1_.getSeed());
         SwampHutPiece swamphutpiece = new SwampHutPiece(p_i48752_2_, p_i48752_3_ * 16, p_i48752_4_ * 16);
         this.components.add(swamphutpiece);
         this.recalculateStructureSize(p_i48752_1_);
      }
   }
}
