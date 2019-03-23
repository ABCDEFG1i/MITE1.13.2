package net.minecraft.world.chunk.storage;

import net.minecraft.init.Biomes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
   public static ChunkLoader.AnvilConverterData load(NBTTagCompound p_76691_0_) {
      int i = p_76691_0_.getInteger("xPos");
      int j = p_76691_0_.getInteger("zPos");
      ChunkLoader.AnvilConverterData chunkloader$anvilconverterdata = new ChunkLoader.AnvilConverterData(i, j);
      chunkloader$anvilconverterdata.blocks = p_76691_0_.getByteArray("Blocks");
      chunkloader$anvilconverterdata.data = new NibbleArrayReader(p_76691_0_.getByteArray("Data"), 7);
      chunkloader$anvilconverterdata.skyLight = new NibbleArrayReader(p_76691_0_.getByteArray("SkyLight"), 7);
      chunkloader$anvilconverterdata.blockLight = new NibbleArrayReader(p_76691_0_.getByteArray("BlockLight"), 7);
      chunkloader$anvilconverterdata.heightmap = p_76691_0_.getByteArray("HeightMap");
      chunkloader$anvilconverterdata.terrainPopulated = p_76691_0_.getBoolean("TerrainPopulated");
      chunkloader$anvilconverterdata.entities = p_76691_0_.getTagList("Entities", 10);
      chunkloader$anvilconverterdata.tileEntities = p_76691_0_.getTagList("TileEntities", 10);
      chunkloader$anvilconverterdata.tileTicks = p_76691_0_.getTagList("TileTicks", 10);

      try {
         chunkloader$anvilconverterdata.lastUpdated = p_76691_0_.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         chunkloader$anvilconverterdata.lastUpdated = (long)p_76691_0_.getInteger("LastUpdate");
      }

      return chunkloader$anvilconverterdata;
   }

   public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData p_76690_0_, NBTTagCompound p_76690_1_, BiomeProvider p_76690_2_) {
      p_76690_1_.setInteger("xPos", p_76690_0_.x);
      p_76690_1_.setInteger("zPos", p_76690_0_.z);
      p_76690_1_.setLong("LastUpdate", p_76690_0_.lastUpdated);
      int[] aint = new int[p_76690_0_.heightmap.length];

      for(int i = 0; i < p_76690_0_.heightmap.length; ++i) {
         aint[i] = p_76690_0_.heightmap[i];
      }

      p_76690_1_.setIntArray("HeightMap", aint);
      p_76690_1_.setBoolean("TerrainPopulated", p_76690_0_.terrainPopulated);
      NBTTagList nbttaglist = new NBTTagList();

      for(int j = 0; j < 8; ++j) {
         boolean flag = true;

         for(int k = 0; k < 16 && flag; ++k) {
            for(int l = 0; l < 16 && flag; ++l) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  int j1 = k << 11 | i1 << 7 | l + (j << 4);
                  int k1 = p_76690_0_.blocks[j1];
                  if (k1 != 0) {
                     flag = false;
                     break;
                  }
               }
            }
         }

         if (!flag) {
            byte[] abyte1 = new byte[4096];
            NibbleArray nibblearray = new NibbleArray();
            NibbleArray nibblearray1 = new NibbleArray();
            NibbleArray nibblearray2 = new NibbleArray();

            for(int j3 = 0; j3 < 16; ++j3) {
               for(int l1 = 0; l1 < 16; ++l1) {
                  for(int i2 = 0; i2 < 16; ++i2) {
                     int j2 = j3 << 11 | i2 << 7 | l1 + (j << 4);
                     int k2 = p_76690_0_.blocks[j2];
                     abyte1[l1 << 8 | i2 << 4 | j3] = (byte)(k2 & 255);
                     nibblearray.set(j3, l1, i2, p_76690_0_.data.get(j3, l1 + (j << 4), i2));
                     nibblearray1.set(j3, l1, i2, p_76690_0_.skyLight.get(j3, l1 + (j << 4), i2));
                     nibblearray2.set(j3, l1, i2, p_76690_0_.blockLight.get(j3, l1 + (j << 4), i2));
                  }
               }
            }

            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Y", (byte)(j & 255));
            nbttagcompound.setByteArray("Blocks", abyte1);
            nbttagcompound.setByteArray("Data", nibblearray.getData());
            nbttagcompound.setByteArray("SkyLight", nibblearray1.getData());
            nbttagcompound.setByteArray("BlockLight", nibblearray2.getData());
            nbttaglist.add((INBTBase)nbttagcompound);
         }
      }

      p_76690_1_.setTag("Sections", nbttaglist);
      byte[] abyte = new byte[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l2 = 0; l2 < 16; ++l2) {
         for(int i3 = 0; i3 < 16; ++i3) {
            blockpos$mutableblockpos.setPos(p_76690_0_.x << 4 | l2, 0, p_76690_0_.z << 4 | i3);
            abyte[i3 << 4 | l2] = (byte)(IRegistry.field_212624_m.func_148757_b(p_76690_2_.getBiome(blockpos$mutableblockpos, Biomes.DEFAULT)) & 255);
         }
      }

      p_76690_1_.setByteArray("Biomes", abyte);
      p_76690_1_.setTag("Entities", p_76690_0_.entities);
      p_76690_1_.setTag("TileEntities", p_76690_0_.tileEntities);
      if (p_76690_0_.tileTicks != null) {
         p_76690_1_.setTag("TileTicks", p_76690_0_.tileTicks);
      }

      p_76690_1_.setBoolean("convertedFromAlphaFormat", true);
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public NBTTagList entities;
      public NBTTagList tileEntities;
      public NBTTagList tileTicks;
      public final int x;
      public final int z;

      public AnvilConverterData(int p_i1999_1_, int p_i1999_2_) {
         this.x = p_i1999_1_;
         this.z = p_i1999_2_;
      }
   }
}
