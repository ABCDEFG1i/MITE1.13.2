package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Biomes;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.ServerTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.gen.feature.structure.StructureIO;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ChunkPos, NBTTagCompound> chunksToSave = Maps.newHashMap();
   public final File chunkSaveLocation;
   private final DataFixer fixer;
   private LegacyStructureDataUtil field_208031_e;
   private boolean flushing;

   public AnvilChunkLoader(File p_i49571_1_, DataFixer p_i49571_2_) {
      this.chunkSaveLocation = p_i49571_1_;
      this.fixer = p_i49571_2_;
   }

   @Nullable
   private NBTTagCompound func_208030_a(IWorld p_208030_1_, int p_208030_2_, int p_208030_3_) throws IOException {
      return this.func_212146_a(p_208030_1_.getDimension().getType(), p_208030_1_.func_175693_T(), p_208030_2_, p_208030_3_);
   }

   @Nullable
   private NBTTagCompound func_212146_a(DimensionType p_212146_1_, @Nullable WorldSavedDataStorage p_212146_2_, int p_212146_3_, int p_212146_4_) throws IOException {
      NBTTagCompound nbttagcompound = this.chunksToSave.get(new ChunkPos(p_212146_3_, p_212146_4_));
      if (nbttagcompound != null) {
         return nbttagcompound;
      } else {
         DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, p_212146_3_, p_212146_4_);
         if (datainputstream == null) {
            return null;
         } else {
            NBTTagCompound nbttagcompound1 = CompressedStreamTools.read(datainputstream);
            datainputstream.close();
            int i = nbttagcompound1.hasKey("DataVersion", 99) ? nbttagcompound1.getInteger("DataVersion") : -1;
            if (i < 1493) {
               nbttagcompound1 = NBTUtil.update(this.fixer, DataFixTypes.CHUNK, nbttagcompound1, i, 1493);
               if (nbttagcompound1.getCompoundTag("Level").getBoolean("hasLegacyStructureData")) {
                  this.func_212429_a(p_212146_1_, p_212146_2_);
                  nbttagcompound1 = this.field_208031_e.func_212181_a(nbttagcompound1);
               }
            }

            nbttagcompound1 = NBTUtil.func_210822_a(this.fixer, DataFixTypes.CHUNK, nbttagcompound1, Math.max(1493, i));
            if (i < 1631) {
               nbttagcompound1.setInteger("DataVersion", 1631);
               this.addChunkToPending(new ChunkPos(p_212146_3_, p_212146_4_), nbttagcompound1);
            }

            return nbttagcompound1;
         }
      }
   }

   public void func_212429_a(DimensionType p_212429_1_, @Nullable WorldSavedDataStorage p_212429_2_) {
      if (this.field_208031_e == null) {
         this.field_208031_e = LegacyStructureDataUtil.func_212183_a(p_212429_1_, p_212429_2_);
      }

   }

   @Nullable
   public Chunk loadChunk(IWorld p_199813_1_, int p_199813_2_, int p_199813_3_, Consumer<Chunk> p_199813_4_) throws IOException {
      NBTTagCompound nbttagcompound = this.func_208030_a(p_199813_1_, p_199813_2_, p_199813_3_);
      if (nbttagcompound == null) {
         return null;
      } else {
         Chunk chunk = this.checkedReadChunkFromNBT(p_199813_1_, p_199813_2_, p_199813_3_, nbttagcompound);
         if (chunk != null) {
            p_199813_4_.accept(chunk);
            this.readEntitiesFromNBT(nbttagcompound.getCompoundTag("Level"), chunk);
         }

         return chunk;
      }
   }

   @Nullable
   public ChunkPrimer loadChunkPrimer(IWorld p_202152_1_, int p_202152_2_, int p_202152_3_, Consumer<IChunk> p_202152_4_) throws IOException {
      NBTTagCompound nbttagcompound;
      try {
         nbttagcompound = this.func_208030_a(p_202152_1_, p_202152_2_, p_202152_3_);
      } catch (ReportedException reportedexception) {
         if (reportedexception.getCause() instanceof IOException) {
            throw (IOException)reportedexception.getCause();
         }

         throw reportedexception;
      }

      if (nbttagcompound == null) {
         return null;
      } else {
         ChunkPrimer chunkprimer = this.readChunkPrimerFromNBT(p_202152_1_, p_202152_2_, p_202152_3_, nbttagcompound);
         if (chunkprimer != null) {
            p_202152_4_.accept(chunkprimer);
         }

         return chunkprimer;
      }
   }

   @Nullable
   protected Chunk checkedReadChunkFromNBT(IWorld p_75822_1_, int p_75822_2_, int p_75822_3_, NBTTagCompound p_75822_4_) {
      if (p_75822_4_.hasKey("Level", 10) && p_75822_4_.getCompoundTag("Level").hasKey("Status", 8)) {
         ChunkStatus.Type chunkstatus$type = this.readChunkTypeFromNBT(p_75822_4_);
         if (chunkstatus$type != ChunkStatus.Type.LEVELCHUNK) {
            return null;
         } else {
            NBTTagCompound nbttagcompound = p_75822_4_.getCompoundTag("Level");
            if (!nbttagcompound.hasKey("Sections", 9)) {
               LOGGER.error("Chunk file at {},{} is missing block data, skipping", p_75822_2_, p_75822_3_);
               return null;
            } else {
               Chunk chunk = this.readChunkFromNBT(p_75822_1_, nbttagcompound);
               if (!chunk.isAtLocation(p_75822_2_, p_75822_3_)) {
                  LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", p_75822_2_, p_75822_3_, p_75822_2_, p_75822_3_, chunk.x, chunk.z);
                  nbttagcompound.setInteger("xPos", p_75822_2_);
                  nbttagcompound.setInteger("zPos", p_75822_3_);
                  chunk = this.readChunkFromNBT(p_75822_1_, nbttagcompound);
               }

               return chunk;
            }
         }
      } else {
         LOGGER.error("Chunk file at {},{} is missing level data, skipping", p_75822_2_, p_75822_3_);
         return null;
      }
   }

   @Nullable
   protected ChunkPrimer readChunkPrimerFromNBT(IWorld p_202165_1_, int p_202165_2_, int p_202165_3_, NBTTagCompound p_202165_4_) {
      if (p_202165_4_.hasKey("Level", 10) && p_202165_4_.getCompoundTag("Level").hasKey("Status", 8)) {
         ChunkStatus.Type chunkstatus$type = this.readChunkTypeFromNBT(p_202165_4_);
         if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
            return new ChunkPrimerWrapper(this.checkedReadChunkFromNBT(p_202165_1_, p_202165_2_, p_202165_3_, p_202165_4_));
         } else {
            NBTTagCompound nbttagcompound = p_202165_4_.getCompoundTag("Level");
            return this.readChunkPrimerFromNBT(p_202165_1_, nbttagcompound);
         }
      } else {
         LOGGER.error("Chunk file at {},{} is missing level data, skipping", p_202165_2_, p_202165_3_);
         return null;
      }
   }

   public void saveChunk(World p_75816_1_, IChunk p_75816_2_) throws IOException, SessionLockException {
      p_75816_1_.checkSessionLock();

      try {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound.setInteger("DataVersion", 1631);
         ChunkPos chunkpos = p_75816_2_.getPos();
         nbttagcompound.setTag("Level", nbttagcompound1);
         if (p_75816_2_.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
            this.writeChunkToNBT((Chunk)p_75816_2_, p_75816_1_, nbttagcompound1);
         } else {
            NBTTagCompound nbttagcompound2 = this.func_208030_a(p_75816_1_, chunkpos.x, chunkpos.z);
            if (nbttagcompound2 != null && this.readChunkTypeFromNBT(nbttagcompound2) == ChunkStatus.Type.LEVELCHUNK) {
               return;
            }

            this.func_202156_a((ChunkPrimer)p_75816_2_, p_75816_1_, nbttagcompound1);
         }

         this.addChunkToPending(chunkpos, nbttagcompound);
      } catch (Exception exception) {
         LOGGER.error("Failed to save chunk", exception);
      }

   }

   protected void addChunkToPending(ChunkPos p_75824_1_, NBTTagCompound p_75824_2_) {
      this.chunksToSave.put(p_75824_1_, p_75824_2_);
      ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
   }

   public boolean writeNextIO() {
      Iterator<Entry<ChunkPos, NBTTagCompound>> iterator = this.chunksToSave.entrySet().iterator();
      if (!iterator.hasNext()) {
         if (this.flushing) {
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.chunkSaveLocation.getName());
         }

         return false;
      } else {
         Entry<ChunkPos, NBTTagCompound> entry = iterator.next();
         iterator.remove();
         ChunkPos chunkpos = entry.getKey();
         NBTTagCompound nbttagcompound = entry.getValue();
         if (nbttagcompound == null) {
            return true;
         } else {
            try {
               DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, chunkpos.x, chunkpos.z);
               CompressedStreamTools.write(nbttagcompound, dataoutputstream);
               dataoutputstream.close();
               if (this.field_208031_e != null) {
                  this.field_208031_e.func_208216_a(chunkpos.asLong());
               }
            } catch (Exception exception) {
               LOGGER.error("Failed to save chunk", exception);
            }

            return true;
         }
      }
   }

   private ChunkStatus.Type readChunkTypeFromNBT(@Nullable NBTTagCompound p_202161_1_) {
      if (p_202161_1_ != null) {
         ChunkStatus chunkstatus = ChunkStatus.getByName(p_202161_1_.getCompoundTag("Level").getString("Status"));
         if (chunkstatus != null) {
            return chunkstatus.getType();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   public void flush() {
      try {
         this.flushing = true;

         while(this.writeNextIO()) {
         }
      } finally {
         this.flushing = false;
      }

   }

   private void func_202156_a(ChunkPrimer p_202156_1_, World p_202156_2_, NBTTagCompound p_202156_3_) {
      int i = p_202156_1_.getPos().x;
      int j = p_202156_1_.getPos().z;
      p_202156_3_.setInteger("xPos", i);
      p_202156_3_.setInteger("zPos", j);
      p_202156_3_.setLong("LastUpdate", p_202156_2_.getTotalWorldTime());
      p_202156_3_.setLong("InhabitedTime", p_202156_1_.getInhabitedTime());
      p_202156_3_.setString("Status", p_202156_1_.getStatus().getName());
      UpgradeData upgradedata = p_202156_1_.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         p_202156_3_.setTag("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = p_202156_1_.getSections();
      NBTTagList nbttaglist = this.writeChunkSectionsToNBT(p_202156_2_, achunksection);
      p_202156_3_.setTag("Sections", nbttaglist);
      Biome[] abiome = p_202156_1_.getBiomes();
      int[] aint = abiome != null ? new int[abiome.length] : new int[0];
      if (abiome != null) {
         for(int k = 0; k < abiome.length; ++k) {
            aint[k] = IRegistry.field_212624_m.func_148757_b(abiome[k]);
         }
      }

      p_202156_3_.setIntArray("Biomes", aint);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(NBTTagCompound nbttagcompound : p_202156_1_.getEntities()) {
         nbttaglist1.add(nbttagcompound);
      }

      p_202156_3_.setTag("Entities", nbttaglist1);
      NBTTagList nbttaglist2 = new NBTTagList();

      for(BlockPos blockpos : p_202156_1_.getTileEntityPositions()) {
         TileEntity tileentity = p_202156_1_.getTileEntity(blockpos);
         if (tileentity != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            tileentity.writeToNBT(nbttagcompound1);
            nbttaglist2.add(nbttagcompound1);
         } else {
            nbttaglist2.add(p_202156_1_.getDeferredTileEntity(blockpos));
         }
      }

      p_202156_3_.setTag("TileEntities", nbttaglist2);
      p_202156_3_.setTag("Lights", listArrayToTag(p_202156_1_.getPackedLightPositions()));
      p_202156_3_.setTag("PostProcessing", listArrayToTag(p_202156_1_.getPackedPositions()));
      p_202156_3_.setTag("ToBeTicked", p_202156_1_.getBlocksToBeTicked().write());
      p_202156_3_.setTag("LiquidsToBeTicked", p_202156_1_.func_212247_j().write());
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();

      for(Heightmap.Type heightmap$type : p_202156_1_.getHeightMapKeys()) {
         nbttagcompound2.setTag(heightmap$type.getId(), new NBTTagLongArray(p_202156_1_.getHeightmap(heightmap$type).getDataArray()));
      }

      p_202156_3_.setTag("Heightmaps", nbttagcompound2);
      NBTTagCompound nbttagcompound3 = new NBTTagCompound();

      for(GenerationStage.Carving generationstage$carving : GenerationStage.Carving.values()) {
         nbttagcompound3.setByteArray(generationstage$carving.toString(), p_202156_1_.getCarvingMask(generationstage$carving).toByteArray());
      }

      p_202156_3_.setTag("CarvingMasks", nbttagcompound3);
      p_202156_3_.setTag("Structures", this.createStructuresTag(i, j, p_202156_1_.getStructureStarts(), p_202156_1_.getStructureReferences()));
   }

   private void writeChunkToNBT(Chunk p_75820_1_, World p_75820_2_, NBTTagCompound p_75820_3_) {
      p_75820_3_.setInteger("xPos", p_75820_1_.x);
      p_75820_3_.setInteger("zPos", p_75820_1_.z);
      p_75820_3_.setLong("LastUpdate", p_75820_2_.getTotalWorldTime());
      p_75820_3_.setLong("InhabitedTime", p_75820_1_.getInhabitedTime());
      p_75820_3_.setString("Status", p_75820_1_.getStatus().getName());
      UpgradeData upgradedata = p_75820_1_.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         p_75820_3_.setTag("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = p_75820_1_.getSections();
      NBTTagList nbttaglist = this.writeChunkSectionsToNBT(p_75820_2_, achunksection);
      p_75820_3_.setTag("Sections", nbttaglist);
      Biome[] abiome = p_75820_1_.getBiomes();
      int[] aint = new int[abiome.length];

      for(int i = 0; i < abiome.length; ++i) {
         aint[i] = IRegistry.field_212624_m.func_148757_b(abiome[i]);
      }

      p_75820_3_.setIntArray("Biomes", aint);
      p_75820_1_.setHasEntities(false);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(int j = 0; j < p_75820_1_.getEntityLists().length; ++j) {
         for(Entity entity : p_75820_1_.getEntityLists()[j]) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            if (entity.writeToNBTOptional(nbttagcompound)) {
               p_75820_1_.setHasEntities(true);
               nbttaglist1.add(nbttagcompound);
            }
         }
      }

      p_75820_3_.setTag("Entities", nbttaglist1);
      NBTTagList nbttaglist2 = new NBTTagList();

      for(BlockPos blockpos : p_75820_1_.func_203066_o()) {
         TileEntity tileentity = p_75820_1_.getTileEntity(blockpos);
         if (tileentity != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            tileentity.writeToNBT(nbttagcompound1);
            nbttagcompound1.setBoolean("keepPacked", false);
            nbttaglist2.add(nbttagcompound1);
         } else {
            NBTTagCompound nbttagcompound3 = p_75820_1_.getDeferredTileEntity(blockpos);
            if (nbttagcompound3 != null) {
               nbttagcompound3.setBoolean("keepPacked", true);
               nbttaglist2.add(nbttagcompound3);
            }
         }
      }

      p_75820_3_.setTag("TileEntities", nbttaglist2);
      if (p_75820_2_.getPendingBlockTicks() instanceof ServerTickList) {
         p_75820_3_.setTag("TileTicks", ((ServerTickList)p_75820_2_.getPendingBlockTicks()).write(p_75820_1_));
      }

      if (p_75820_2_.getPendingFluidTicks() instanceof ServerTickList) {
         p_75820_3_.setTag("LiquidTicks", ((ServerTickList)p_75820_2_.getPendingFluidTicks()).write(p_75820_1_));
      }

      p_75820_3_.setTag("PostProcessing", listArrayToTag(p_75820_1_.getPackedPositions()));
      if (p_75820_1_.getBlocksToBeTicked() instanceof ChunkPrimerTickList) {
         p_75820_3_.setTag("ToBeTicked", ((ChunkPrimerTickList)p_75820_1_.getBlocksToBeTicked()).write());
      }

      if (p_75820_1_.func_212247_j() instanceof ChunkPrimerTickList) {
         p_75820_3_.setTag("LiquidsToBeTicked", ((ChunkPrimerTickList)p_75820_1_.func_212247_j()).write());
      }

      NBTTagCompound nbttagcompound2 = new NBTTagCompound();

      for(Heightmap.Type heightmap$type : p_75820_1_.getHeightmaps()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            nbttagcompound2.setTag(heightmap$type.getId(), new NBTTagLongArray(p_75820_1_.getHeightmap(heightmap$type).getDataArray()));
         }
      }

      p_75820_3_.setTag("Heightmaps", nbttagcompound2);
      p_75820_3_.setTag("Structures", this.createStructuresTag(p_75820_1_.x, p_75820_1_.z, p_75820_1_.getStructureStarts(), p_75820_1_.getStructureReferences()));
   }

   private Chunk readChunkFromNBT(IWorld p_75823_1_, NBTTagCompound p_75823_2_) {
      int i = p_75823_2_.getInteger("xPos");
      int j = p_75823_2_.getInteger("zPos");
      Biome[] abiome = new Biome[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (p_75823_2_.hasKey("Biomes", 11)) {
         int[] aint = p_75823_2_.getIntArray("Biomes");

         for(int k = 0; k < aint.length; ++k) {
            abiome[k] = IRegistry.field_212624_m.func_148754_a(aint[k]);
            if (abiome[k] == null) {
               abiome[k] = p_75823_1_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
            }
         }
      } else {
         for(int i1 = 0; i1 < abiome.length; ++i1) {
            abiome[i1] = p_75823_1_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((i1 & 15) + (i << 4), 0, (i1 >> 4 & 15) + (j << 4)), Biomes.PLAINS);
         }
      }

      UpgradeData upgradedata = p_75823_2_.hasKey("UpgradeData", 10) ? new UpgradeData(p_75823_2_.getCompoundTag("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimerTickList<Block> chunkprimerticklist1 = new ChunkPrimerTickList<>((p_205531_0_) -> {
         return p_205531_0_.getDefaultState().isAir();
      }, IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, new ChunkPos(i, j));
      ChunkPrimerTickList<Fluid> chunkprimerticklist = new ChunkPrimerTickList<>((p_206242_0_) -> {
         return p_206242_0_ == Fluids.EMPTY;
      }, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, new ChunkPos(i, j));
      long l = p_75823_2_.getLong("InhabitedTime");
      Chunk chunk = new Chunk(p_75823_1_.getWorld(), i, j, abiome, upgradedata, chunkprimerticklist1, chunkprimerticklist, l);
      chunk.setStatus(p_75823_2_.getString("Status"));
      NBTTagList nbttaglist = p_75823_2_.getTagList("Sections", 10);
      chunk.setSections(this.readSectionsFromNBT(p_75823_1_, nbttaglist));
      NBTTagCompound nbttagcompound = p_75823_2_.getCompoundTag("Heightmaps");

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         if (heightmap$type.getUsage() == Heightmap.Usage.LIVE_WORLD) {
            String s = heightmap$type.getId();
            if (nbttagcompound.hasKey(s, 12)) {
               chunk.setHeightmap(heightmap$type, nbttagcompound.readLongArray(s));
            } else {
               chunk.getHeightmap(heightmap$type).generate();
            }
         }
      }

      NBTTagCompound nbttagcompound1 = p_75823_2_.getCompoundTag("Structures");
      chunk.setStructureStarts(this.readStructureStartsFromNBT(p_75823_1_, nbttagcompound1));
      chunk.setStructureReferences(this.readStructureReferencesFromNBT(nbttagcompound1));
      NBTTagList nbttaglist1 = p_75823_2_.getTagList("PostProcessing", 9);

      for(int j1 = 0; j1 < nbttaglist1.size(); ++j1) {
         NBTTagList nbttaglist2 = nbttaglist1.getTagListAt(j1);

         for(int k1 = 0; k1 < nbttaglist2.size(); ++k1) {
            chunk.addPackedPos(nbttaglist2.getShortAt(k1), j1);
         }
      }

      chunkprimerticklist1.readToBeTickedListFromNBT(p_75823_2_.getTagList("ToBeTicked", 9));
      chunkprimerticklist.readToBeTickedListFromNBT(p_75823_2_.getTagList("LiquidsToBeTicked", 9));
      if (p_75823_2_.getBoolean("shouldSave")) {
         chunk.setModified(true);
      }

      return chunk;
   }

   private void readEntitiesFromNBT(NBTTagCompound p_199814_1_, Chunk p_199814_2_) {
      NBTTagList nbttaglist = p_199814_1_.getTagList("Entities", 10);
      World world = p_199814_2_.getWorld();

      for(int i = 0; i < nbttaglist.size(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         readChunkEntity(nbttagcompound, world, p_199814_2_);
         p_199814_2_.setHasEntities(true);
      }

      NBTTagList nbttaglist1 = p_199814_1_.getTagList("TileEntities", 10);

      for(int j = 0; j < nbttaglist1.size(); ++j) {
         NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(j);
         boolean flag = nbttagcompound1.getBoolean("keepPacked");
         if (flag) {
            p_199814_2_.addTileEntity(nbttagcompound1);
         } else {
            TileEntity tileentity = TileEntity.create(nbttagcompound1);
            if (tileentity != null) {
               p_199814_2_.addTileEntity(tileentity);
            }
         }
      }

      if (p_199814_1_.hasKey("TileTicks", 9) && world.getPendingBlockTicks() instanceof ServerTickList) {
         ((ServerTickList)world.getPendingBlockTicks()).read(p_199814_1_.getTagList("TileTicks", 10));
      }

      if (p_199814_1_.hasKey("LiquidTicks", 9) && world.getPendingFluidTicks() instanceof ServerTickList) {
         ((ServerTickList)world.getPendingFluidTicks()).read(p_199814_1_.getTagList("LiquidTicks", 10));
      }

   }

   private ChunkPrimer readChunkPrimerFromNBT(IWorld p_202155_1_, NBTTagCompound p_202155_2_) {
      int i = p_202155_2_.getInteger("xPos");
      int j = p_202155_2_.getInteger("zPos");
      Biome[] abiome = new Biome[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (p_202155_2_.hasKey("Biomes", 11)) {
         int[] aint = p_202155_2_.getIntArray("Biomes");

         for(int k = 0; k < aint.length; ++k) {
            abiome[k] = IRegistry.field_212624_m.func_148754_a(aint[k]);
            if (abiome[k] == null) {
               abiome[k] = p_202155_1_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
            }
         }
      } else {
         for(int l1 = 0; l1 < abiome.length; ++l1) {
            abiome[l1] = p_202155_1_.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos((l1 & 15) + (i << 4), 0, (l1 >> 4 & 15) + (j << 4)), Biomes.PLAINS);
         }
      }

      UpgradeData upgradedata = p_202155_2_.hasKey("UpgradeData", 10) ? new UpgradeData(p_202155_2_.getCompoundTag("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimer chunkprimer = new ChunkPrimer(i, j, upgradedata);
      chunkprimer.setBiomes(abiome);
      chunkprimer.setInhabitedTime(p_202155_2_.getLong("InhabitedTime"));
      chunkprimer.setStatus(p_202155_2_.getString("Status"));
      NBTTagList nbttaglist = p_202155_2_.getTagList("Sections", 10);
      chunkprimer.setChunkSections(this.readSectionsFromNBT(p_202155_1_, nbttaglist));
      NBTTagList nbttaglist1 = p_202155_2_.getTagList("Entities", 10);

      for(int l = 0; l < nbttaglist1.size(); ++l) {
         chunkprimer.addEntity(nbttaglist1.getCompoundTagAt(l));
      }

      NBTTagList nbttaglist3 = p_202155_2_.getTagList("TileEntities", 10);

      for(int i1 = 0; i1 < nbttaglist3.size(); ++i1) {
         NBTTagCompound nbttagcompound = nbttaglist3.getCompoundTagAt(i1);
         chunkprimer.addTileEntity(nbttagcompound);
      }

      NBTTagList nbttaglist4 = p_202155_2_.getTagList("Lights", 9);

      for(int i2 = 0; i2 < nbttaglist4.size(); ++i2) {
         NBTTagList nbttaglist2 = nbttaglist4.getTagListAt(i2);

         for(int j1 = 0; j1 < nbttaglist2.size(); ++j1) {
            chunkprimer.addLightValue(nbttaglist2.getShortAt(j1), i2);
         }
      }

      NBTTagList nbttaglist5 = p_202155_2_.getTagList("PostProcessing", 9);

      for(int j2 = 0; j2 < nbttaglist5.size(); ++j2) {
         NBTTagList nbttaglist6 = nbttaglist5.getTagListAt(j2);

         for(int k1 = 0; k1 < nbttaglist6.size(); ++k1) {
            chunkprimer.func_201636_b(nbttaglist6.getShortAt(k1), j2);
         }
      }

      chunkprimer.getBlocksToBeTicked().readToBeTickedListFromNBT(p_202155_2_.getTagList("ToBeTicked", 9));
      chunkprimer.func_212247_j().readToBeTickedListFromNBT(p_202155_2_.getTagList("LiquidsToBeTicked", 9));
      NBTTagCompound nbttagcompound1 = p_202155_2_.getCompoundTag("Heightmaps");

      for(String s1 : nbttagcompound1.getKeySet()) {
         chunkprimer.setHeightMap(Heightmap.Type.func_203501_a(s1), nbttagcompound1.readLongArray(s1));
      }

      NBTTagCompound nbttagcompound2 = p_202155_2_.getCompoundTag("Structures");
      chunkprimer.setStructureStarts(this.readStructureStartsFromNBT(p_202155_1_, nbttagcompound2));
      chunkprimer.setStructureReferences(this.readStructureReferencesFromNBT(nbttagcompound2));
      NBTTagCompound nbttagcompound3 = p_202155_2_.getCompoundTag("CarvingMasks");

      for(String s : nbttagcompound3.getKeySet()) {
         GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(s);
         chunkprimer.setCarvingMask(generationstage$carving, BitSet.valueOf(nbttagcompound3.getByteArray(s)));
      }

      return chunkprimer;
   }

   private NBTTagList writeChunkSectionsToNBT(World p_202159_1_, ChunkSection[] p_202159_2_) {
      NBTTagList nbttaglist = new NBTTagList();
      boolean flag = p_202159_1_.dimension.hasSkyLight();

      for(ChunkSection chunksection : p_202159_2_) {
         if (chunksection != Chunk.EMPTY_SECTION) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Y", (byte)(chunksection.getYLocation() >> 4 & 255));
            chunksection.getData().writeChunkPalette(nbttagcompound, "Palette", "BlockStates");
            nbttagcompound.setByteArray("BlockLight", chunksection.getBlockLight().getData());
            if (flag) {
               nbttagcompound.setByteArray("SkyLight", chunksection.getSkyLight().getData());
            } else {
               nbttagcompound.setByteArray("SkyLight", new byte[chunksection.getBlockLight().getData().length]);
            }

            nbttaglist.add(nbttagcompound);
         }
      }

      return nbttaglist;
   }

   private ChunkSection[] readSectionsFromNBT(IWorldReaderBase p_202158_1_, NBTTagList p_202158_2_) {
      int i = 16;
      ChunkSection[] achunksection = new ChunkSection[16];
      boolean flag = p_202158_1_.getDimension().hasSkyLight();

      for(int j = 0; j < p_202158_2_.size(); ++j) {
         NBTTagCompound nbttagcompound = p_202158_2_.getCompoundTagAt(j);
         int k = nbttagcompound.getByte("Y");
         ChunkSection chunksection = new ChunkSection(k << 4, flag);
         chunksection.getData().readBlockStates(nbttagcompound, "Palette", "BlockStates");
         chunksection.setBlockLight(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));
         if (flag) {
            chunksection.setSkyLight(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
         }

         chunksection.recalculateRefCounts();
         achunksection[k] = chunksection;
      }

      return achunksection;
   }

   private NBTTagCompound createStructuresTag(int p_202160_1_, int p_202160_2_, Map<String, StructureStart> p_202160_3_, Map<String, LongSet> p_202160_4_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();

      for(Entry<String, StructureStart> entry : p_202160_3_.entrySet()) {
         nbttagcompound1.setTag(entry.getKey(), entry.getValue().writeStructureComponentsToNBT(p_202160_1_, p_202160_2_));
      }

      nbttagcompound.setTag("Starts", nbttagcompound1);
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();

      for(Entry<String, LongSet> entry1 : p_202160_4_.entrySet()) {
         nbttagcompound2.setTag(entry1.getKey(), new NBTTagLongArray(entry1.getValue()));
      }

      nbttagcompound.setTag("References", nbttagcompound2);
      return nbttagcompound;
   }

   private Map<String, StructureStart> readStructureStartsFromNBT(IWorld p_202162_1_, NBTTagCompound p_202162_2_) {
      Map<String, StructureStart> map = Maps.newHashMap();
      NBTTagCompound nbttagcompound = p_202162_2_.getCompoundTag("Starts");

      for(String s : nbttagcompound.getKeySet()) {
         map.put(s, StructureIO.func_202602_a(nbttagcompound.getCompoundTag(s), p_202162_1_));
      }

      return map;
   }

   private Map<String, LongSet> readStructureReferencesFromNBT(NBTTagCompound p_202167_1_) {
      Map<String, LongSet> map = Maps.newHashMap();
      NBTTagCompound nbttagcompound = p_202167_1_.getCompoundTag("References");

      for(String s : nbttagcompound.getKeySet()) {
         map.put(s, new LongOpenHashSet(nbttagcompound.readLongArray(s)));
      }

      return map;
   }

   public static NBTTagList listArrayToTag(ShortList[] p_202163_0_) {
      NBTTagList nbttaglist = new NBTTagList();

      for(ShortList shortlist : p_202163_0_) {
         NBTTagList nbttaglist1 = new NBTTagList();
         if (shortlist != null) {
            for(Short oshort : shortlist) {
               nbttaglist1.add(new NBTTagShort(oshort));
            }
         }

         nbttaglist.add(nbttaglist1);
      }

      return nbttaglist;
   }

   @Nullable
   private static Entity createEntityFromNBT(NBTTagCompound p_206240_0_, World p_206240_1_, Function<Entity, Entity> p_206240_2_) {
      Entity entity = createEntityFromNBT(p_206240_0_, p_206240_1_);
      if (entity == null) {
         return null;
      } else {
         entity = p_206240_2_.apply(entity);
         if (entity != null && p_206240_0_.hasKey("Passengers", 9)) {
            NBTTagList nbttaglist = p_206240_0_.getTagList("Passengers", 10);

            for(int i = 0; i < nbttaglist.size(); ++i) {
               Entity entity1 = createEntityFromNBT(nbttaglist.getCompoundTagAt(i), p_206240_1_, p_206240_2_);
               if (entity1 != null) {
                  entity1.startRiding(entity, true);
               }
            }
         }

         return entity;
      }
   }

   @Nullable
   public static Entity readChunkEntity(NBTTagCompound p_186050_0_, World p_186050_1_, Chunk p_186050_2_) {
      return createEntityFromNBT(p_186050_0_, p_186050_1_, (p_206241_1_) -> {
         p_186050_2_.addEntity(p_206241_1_);
         return p_206241_1_;
      });
   }

   @Nullable
   public static Entity readWorldEntityPos(NBTTagCompound p_186054_0_, World p_186054_1_, double p_186054_2_, double p_186054_4_, double p_186054_6_, boolean p_186054_8_) {
      return createEntityFromNBT(p_186054_0_, p_186054_1_, (p_206238_8_) -> {
         p_206238_8_.setLocationAndAngles(p_186054_2_, p_186054_4_, p_186054_6_, p_206238_8_.rotationYaw, p_206238_8_.rotationPitch);
         return p_186054_8_ && !p_186054_1_.spawnEntity(p_206238_8_) ? null : p_206238_8_;
      });
   }

   @Nullable
   public static Entity readWorldEntity(NBTTagCompound p_186051_0_, World p_186051_1_, boolean p_186051_2_) {
      return createEntityFromNBT(p_186051_0_, p_186051_1_, (p_206239_2_) -> {
         return p_186051_2_ && !p_186051_1_.spawnEntity(p_206239_2_) ? null : p_206239_2_;
      });
   }

   @Nullable
   protected static Entity createEntityFromNBT(NBTTagCompound p_186053_0_, World p_186053_1_) {
      try {
         return EntityType.create(p_186053_0_, p_186053_1_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.warn("Exception loading entity: ", runtimeexception);
         return null;
      }
   }

   public static void spawnEntity(Entity p_186052_0_, IWorld p_186052_1_) {
      if (p_186052_1_.spawnEntity(p_186052_0_) && p_186052_0_.isBeingRidden()) {
         for(Entity entity : p_186052_0_.getPassengers()) {
            spawnEntity(entity, p_186052_1_);
         }
      }

   }

   public boolean func_212147_a(ChunkPos p_212147_1_, DimensionType p_212147_2_, WorldSavedDataStorage p_212147_3_) {
      boolean flag = false;

      try {
         this.func_212146_a(p_212147_2_, p_212147_3_, p_212147_1_.x, p_212147_1_.z);

         while(this.writeNextIO()) {
            flag = true;
         }
      } catch (IOException var6) {
      }

      return flag;
   }
}
