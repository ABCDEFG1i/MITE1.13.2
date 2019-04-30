package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class OceanMonumentStructure extends Structure<OceanMonumentConfig> {
   private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.GUARDIAN, 1, 2, 4));

   protected ChunkPos getStartPositionForPosition(IChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int i = p_211744_1_.getSettings().getOceanMonumentSpacing();
      int j = p_211744_1_.getSettings().getOceanMonumentSeparation();
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
         for(Biome biome : p_202372_1_.getBiomeProvider().getBiomesInSquare(p_202372_3_ * 16 + 9, p_202372_4_ * 16 + 9, 16)) {
            if (!p_202372_1_.hasStructure(biome, Feature.OCEAN_MONUMENT)) {
               return false;
            }
         }

         for(Biome biome1 : p_202372_1_.getBiomeProvider().getBiomesInSquare(p_202372_3_ * 16 + 9, p_202372_4_ * 16 + 9, 29)) {
            if (biome1.getBiomeCategory() != Biome.Category.OCEAN && biome1.getBiomeCategory() != Biome.Category.RIVER) {
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
      return new OceanMonumentStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome);
   }

   protected String getStructureName() {
      return "Monument";
   }

   public int getSize() {
      return 8;
   }

   @Override
   public StructureRequirements getRequirements() {
      return StructureRequirements.MONUMENT;
   }

   @Override
   public Item getSymbolItem() {
      return Blocks.PRISMARINE.asItem();
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return MONUMENT_ENEMIES;
   }

   public static class Start extends StructureStart {
      private final Set<ChunkPos> processed = Sets.newHashSet();
      private boolean wasCreated;

      public Start() {
      }

      public Start(IWorld p_i48754_1_, SharedSeedRandom p_i48754_2_, int p_i48754_3_, int p_i48754_4_, Biome p_i48754_5_) {
         super(p_i48754_3_, p_i48754_4_, p_i48754_5_, p_i48754_2_, p_i48754_1_.getSeed());
         this.create(p_i48754_1_, p_i48754_2_, p_i48754_3_, p_i48754_4_);
      }

      private void create(IBlockReader p_175789_1_, Random p_175789_2_, int p_175789_3_, int p_175789_4_) {
         int i = p_175789_3_ * 16 - 29;
         int j = p_175789_4_ * 16 - 29;
         EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_175789_2_);
         this.components.add(new OceanMonumentPieces.MonumentBuilding(p_175789_2_, i, j, enumfacing));
         this.recalculateStructureSize(p_175789_1_);
         this.wasCreated = true;
      }

      public void generateStructure(IWorld p_75068_1_, Random p_75068_2_, MutableBoundingBox p_75068_3_, ChunkPos p_75068_4_) {
         if (!this.wasCreated) {
            this.components.clear();
            this.create(p_75068_1_, p_75068_2_, this.getChunkPosX(), this.getChunkPosZ());
         }

         super.generateStructure(p_75068_1_, p_75068_2_, p_75068_3_, p_75068_4_);
      }

      public void notifyPostProcessAt(ChunkPos p_175787_1_) {
         super.notifyPostProcessAt(p_175787_1_);
         this.processed.add(p_175787_1_);
      }

      public void writeToNBT(NBTTagCompound p_143022_1_) {
         super.writeToNBT(p_143022_1_);
         NBTTagList nbttaglist = new NBTTagList();

         for(ChunkPos chunkpos : this.processed) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("X", chunkpos.x);
            nbttagcompound.setInteger("Z", chunkpos.z);
            nbttaglist.add(nbttagcompound);
         }

         p_143022_1_.setTag("Processed", nbttaglist);
      }

      public void readFromNBT(NBTTagCompound p_143017_1_) {
         super.readFromNBT(p_143017_1_);
         if (p_143017_1_.hasKey("Processed", 9)) {
            NBTTagList nbttaglist = p_143017_1_.getTagList("Processed", 10);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
               this.processed.add(new ChunkPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Z")));
            }
         }

      }
   }
}
