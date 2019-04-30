package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class StrongholdStructure extends Structure<StrongholdConfig> {
   private boolean ranBiomeCheck;
   private ChunkPos[] structureCoords;
   private long field_202387_av;

   protected boolean hasStartAt(IChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
      if (this.field_202387_av != p_202372_1_.getSeed()) {
         this.func_202386_c();
      }

      if (!this.ranBiomeCheck) {
         this.func_202385_a(p_202372_1_);
         this.ranBiomeCheck = true;
      }

      for(ChunkPos chunkpos : this.structureCoords) {
         if (p_202372_3_ == chunkpos.x && p_202372_4_ == chunkpos.z) {
            return true;
         }
      }

      return false;
   }

   private void func_202386_c() {
      this.ranBiomeCheck = false;
      this.structureCoords = null;
   }

   protected boolean isEnabledIn(IWorld p_202365_1_) {
      return p_202365_1_.getWorldInfo().isMapFeaturesEnabled();
   }

   protected StructureStart makeStart(IWorld p_202369_1_, IChunkGenerator<?> p_202369_2_, SharedSeedRandom p_202369_3_, int p_202369_4_, int p_202369_5_) {
      Biome biome = p_202369_2_.getBiomeProvider().getBiome(new BlockPos((p_202369_4_ << 4) + 9, 0, (p_202369_5_ << 4) + 9), Biomes.DEFAULT);
      int i = 0;

      StrongholdStructure.Start strongholdstructure$start;
      for(strongholdstructure$start = new StrongholdStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome, i++); strongholdstructure$start.getComponents().isEmpty() || ((StrongholdPieces.Stairs2)strongholdstructure$start.getComponents().get(0)).strongholdPortalRoom == null; strongholdstructure$start = new StrongholdStructure.Start(p_202369_1_, p_202369_3_, p_202369_4_, p_202369_5_, biome, i++)) {
      }

      return strongholdstructure$start;
   }

   protected String getStructureName() {
      return "Stronghold";
   }

   public int getSize() {
      return 8;
   }

   @Override
   public Item getSymbolItem() {
      return Blocks.END_PORTAL_FRAME.asItem();
   }

   @Nullable
   public BlockPos func_211405_a(World p_211405_1_, IChunkGenerator<? extends IChunkGenSettings> p_211405_2_, BlockPos p_211405_3_, int p_211405_4_, boolean p_211405_5_) {
      if (!p_211405_2_.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         if (this.field_202387_av != p_211405_1_.getSeed()) {
            this.func_202386_c();
         }

         if (!this.ranBiomeCheck) {
            this.func_202385_a(p_211405_2_);
            this.ranBiomeCheck = true;
         }

         BlockPos blockpos = null;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(0, 0, 0);
         double d0 = Double.MAX_VALUE;

         for(ChunkPos chunkpos : this.structureCoords) {
            blockpos$mutableblockpos.setPos((chunkpos.x << 4) + 8, 32, (chunkpos.z << 4) + 8);
            double d1 = blockpos$mutableblockpos.distanceSq(p_211405_3_);
            if (blockpos == null) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            } else if (d1 < d0) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            }
         }

         return blockpos;
      }
   }

   private void func_202385_a(IChunkGenerator<?> p_202385_1_) {
      this.field_202387_av = p_202385_1_.getSeed();
      List<Biome> list = Lists.newArrayList();

      for(Biome biome : IRegistry.field_212624_m) {
         if (biome != null && p_202385_1_.hasStructure(biome, Feature.STRONGHOLD)) {
            list.add(biome);
         }
      }

      int i2 = p_202385_1_.getSettings().getStrongholdDistance();
      int j2 = p_202385_1_.getSettings().getStrongholdCount();
      int i = p_202385_1_.getSettings().getStrongholdSpread();
      this.structureCoords = new ChunkPos[j2];
      int j = 0;
      Long2ObjectMap<StructureStart> long2objectmap = p_202385_1_.getStructureReferenceToStartMap(this);
      synchronized(long2objectmap) {
         for(StructureStart structurestart : long2objectmap.values()) {
            if (j < this.structureCoords.length) {
               this.structureCoords[j++] = new ChunkPos(structurestart.getChunkPosX(), structurestart.getChunkPosZ());
            }
         }
      }

      Random random = new Random();
      random.setSeed(p_202385_1_.getSeed());
      double d1 = random.nextDouble() * Math.PI * 2.0D;
      int k = long2objectmap.size();
      if (k < this.structureCoords.length) {
         int l = 0;
         int i1 = 0;

         for(int j1 = 0; j1 < this.structureCoords.length; ++j1) {
            double d0 = (double)(4 * i2 + i2 * i1 * 6) + (random.nextDouble() - 0.5D) * (double)i2 * 2.5D;
            int k1 = (int)Math.round(Math.cos(d1) * d0);
            int l1 = (int)Math.round(Math.sin(d1) * d0);
            BlockPos blockpos = p_202385_1_.getBiomeProvider().findBiomePosition((k1 << 4) + 8, (l1 << 4) + 8, 112, list, random);
            if (blockpos != null) {
               k1 = blockpos.getX() >> 4;
               l1 = blockpos.getZ() >> 4;
            }

            if (j1 >= k) {
               this.structureCoords[j1] = new ChunkPos(k1, l1);
            }

            d1 += (Math.PI * 2D) / (double)i;
            ++l;
            if (l == i) {
               ++i1;
               l = 0;
               i = i + 2 * i / (i1 + 1);
               i = Math.min(i, this.structureCoords.length - j1);
               d1 += random.nextDouble() * Math.PI * 2.0D;
            }
         }
      }

   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(IWorld p_i48716_1_, SharedSeedRandom p_i48716_2_, int p_i48716_3_, int p_i48716_4_, Biome p_i48716_5_, int p_i48716_6_) {
         super(p_i48716_3_, p_i48716_4_, p_i48716_5_, p_i48716_2_, p_i48716_1_.getSeed() + (long)p_i48716_6_);
         StrongholdPieces.prepareStructurePieces();
         StrongholdPieces.Stairs2 strongholdpieces$stairs2 = new StrongholdPieces.Stairs2(0, p_i48716_2_, (p_i48716_3_ << 4) + 2, (p_i48716_4_ << 4) + 2);
         this.components.add(strongholdpieces$stairs2);
         strongholdpieces$stairs2.buildComponent(strongholdpieces$stairs2, this.components, p_i48716_2_);
         List<StructurePiece> list = strongholdpieces$stairs2.pendingChildren;

         while(!list.isEmpty()) {
            int i = p_i48716_2_.nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.buildComponent(strongholdpieces$stairs2, this.components, p_i48716_2_);
         }

         this.recalculateStructureSize(p_i48716_1_);
         this.markAvailableHeight(p_i48716_1_, p_i48716_2_, 10);
      }
   }
}
