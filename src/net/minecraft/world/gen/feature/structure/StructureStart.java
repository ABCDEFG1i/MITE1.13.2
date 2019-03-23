package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.Biome;

public abstract class StructureStart {
   protected final List<StructurePiece> components = Lists.newArrayList();
   protected MutableBoundingBox boundingBox;
   protected int chunkPosX;
   protected int chunkPosZ;
   private Biome biome;
   private int field_212688_f;

   public StructureStart() {
   }

   public StructureStart(int p_i48653_1_, int p_i48653_2_, Biome p_i48653_3_, SharedSeedRandom p_i48653_4_, long p_i48653_5_) {
      this.chunkPosX = p_i48653_1_;
      this.chunkPosZ = p_i48653_2_;
      this.biome = p_i48653_3_;
      p_i48653_4_.func_202425_c(p_i48653_5_, this.chunkPosX, this.chunkPosZ);
   }

   public MutableBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public List<StructurePiece> getComponents() {
      return this.components;
   }

   public void generateStructure(IWorld p_75068_1_, Random p_75068_2_, MutableBoundingBox p_75068_3_, ChunkPos p_75068_4_) {
      synchronized(this.components) {
         Iterator<StructurePiece> iterator = this.components.iterator();

         while(iterator.hasNext()) {
            StructurePiece structurepiece = iterator.next();
            if (structurepiece.getBoundingBox().intersectsWith(p_75068_3_) && !structurepiece.addComponentParts(p_75068_1_, p_75068_2_, p_75068_3_, p_75068_4_)) {
               iterator.remove();
            }
         }

         this.recalculateStructureSize(p_75068_1_);
      }
   }

   protected void recalculateStructureSize(IBlockReader p_202500_1_) {
      this.boundingBox = MutableBoundingBox.getNewBoundingBox();

      for(StructurePiece structurepiece : this.components) {
         this.boundingBox.expandTo(structurepiece.getBoundingBox());
      }

   }

   public NBTTagCompound writeStructureComponentsToNBT(int p_143021_1_, int p_143021_2_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (this.isSizeableStructure()) {
         nbttagcompound.setString("id", StructureIO.getStructureStartName(this));
         nbttagcompound.setString("biome", IRegistry.field_212624_m.func_177774_c(this.biome).toString());
         nbttagcompound.setInteger("ChunkX", p_143021_1_);
         nbttagcompound.setInteger("ChunkZ", p_143021_2_);
         nbttagcompound.setInteger("references", this.field_212688_f);
         nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
         NBTTagList lvt_4_1_ = new NBTTagList();
         synchronized(this.components) {
            for(StructurePiece structurepiece : this.components) {
               lvt_4_1_.add((INBTBase)structurepiece.createStructureBaseNBT());
            }
         }

         nbttagcompound.setTag("Children", lvt_4_1_);
         this.writeToNBT(nbttagcompound);
         return nbttagcompound;
      } else {
         nbttagcompound.setString("id", "INVALID");
         return nbttagcompound;
      }
   }

   public void writeToNBT(NBTTagCompound p_143022_1_) {
   }

   public void readStructureComponentsFromNBT(IWorld p_143020_1_, NBTTagCompound p_143020_2_) {
      this.chunkPosX = p_143020_2_.getInteger("ChunkX");
      this.chunkPosZ = p_143020_2_.getInteger("ChunkZ");
      this.field_212688_f = p_143020_2_.getInteger("references");
      this.biome = p_143020_2_.hasKey("biome") ? IRegistry.field_212624_m.func_212608_b(new ResourceLocation(p_143020_2_.getString("biome"))) : p_143020_1_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(new BlockPos((this.chunkPosX << 4) + 9, 0, (this.chunkPosZ << 4) + 9), Biomes.PLAINS);
      if (p_143020_2_.hasKey("BB")) {
         this.boundingBox = new MutableBoundingBox(p_143020_2_.getIntArray("BB"));
      }

      NBTTagList nbttaglist = p_143020_2_.getTagList("Children", 10);

      for(int i = 0; i < nbttaglist.size(); ++i) {
         this.components.add(StructureIO.getStructureComponent(nbttaglist.getCompoundTagAt(i), p_143020_1_));
      }

      this.readFromNBT(p_143020_2_);
   }

   public void readFromNBT(NBTTagCompound p_143017_1_) {
   }

   protected void markAvailableHeight(IWorldReaderBase p_75067_1_, Random p_75067_2_, int p_75067_3_) {
      int i = p_75067_1_.getSeaLevel() - p_75067_3_;
      int j = this.boundingBox.getYSize() + 1;
      if (j < i) {
         j += p_75067_2_.nextInt(i - j);
      }

      int k = j - this.boundingBox.maxY;
      this.boundingBox.offset(0, k, 0);

      for(StructurePiece structurepiece : this.components) {
         structurepiece.offset(0, k, 0);
      }

   }

   protected void setRandomHeight(IBlockReader p_75070_1_, Random p_75070_2_, int p_75070_3_, int p_75070_4_) {
      int i = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
      int j;
      if (i > 1) {
         j = p_75070_3_ + p_75070_2_.nextInt(i);
      } else {
         j = p_75070_3_;
      }

      int k = j - this.boundingBox.minY;
      this.boundingBox.offset(0, k, 0);

      for(StructurePiece structurepiece : this.components) {
         structurepiece.offset(0, k, 0);
      }

   }

   public boolean isSizeableStructure() {
      return true;
   }

   public void notifyPostProcessAt(ChunkPos p_175787_1_) {
   }

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }

   public BlockPos getPos() {
      return new BlockPos(this.chunkPosX << 4, 0, this.chunkPosZ << 4);
   }

   public boolean func_212687_g() {
      return this.field_212688_f < this.func_212686_i();
   }

   public void func_212685_h() {
      ++this.field_212688_f;
   }

   protected int func_212686_i() {
      return 1;
   }
}
