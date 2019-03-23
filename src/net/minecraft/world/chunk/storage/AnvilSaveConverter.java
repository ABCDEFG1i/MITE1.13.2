package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld {
   private static final Logger LOGGER = LogManager.getLogger();

   public AnvilSaveConverter(Path p_i49567_1_, Path p_i49567_2_, DataFixer p_i49567_3_) {
      super(p_i49567_1_, p_i49567_2_, p_i49567_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Anvil";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      if (!Files.isDirectory(this.savesDirectory)) {
         throw new AnvilConverterException((new TextComponentTranslation("selectWorld.load_folder_access")).getString());
      } else {
         List<WorldSummary> list = Lists.newArrayList();
         File[] afile = this.savesDirectory.toFile().listFiles();

         for(File file1 : afile) {
            if (file1.isDirectory()) {
               String s = file1.getName();
               WorldInfo worldinfo = this.getWorldInfo(s);
               if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133)) {
                  boolean flag = worldinfo.getSaveVersion() != this.getSaveVersion();
                  String s1 = worldinfo.getWorldName();
                  if (StringUtils.isEmpty(s1)) {
                     s1 = s;
                  }

                  long i = 0L;
                  list.add(new WorldSummary(worldinfo, s, s1, 0L, flag));
               }
            }
         }

         return list;
      }
   }

   protected int getSaveVersion() {
      return 19133;
   }

   @OnlyIn(Dist.CLIENT)
   public void flushCache() {
      RegionFileCache.clearRegionFileReferences();
   }

   public ISaveHandler getSaveLoader(String p_197715_1_, @Nullable MinecraftServer p_197715_2_) {
      return new AnvilSaveHandler(this.savesDirectory.toFile(), p_197715_1_, p_197715_2_, this.dataFixer);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isConvertible(String p_207743_1_) {
      WorldInfo worldinfo = this.getWorldInfo(p_207743_1_);
      return worldinfo != null && worldinfo.getSaveVersion() == 19132;
   }

   public boolean isOldMapFormat(String p_75801_1_) {
      WorldInfo worldinfo = this.getWorldInfo(p_75801_1_);
      return worldinfo != null && worldinfo.getSaveVersion() != this.getSaveVersion();
   }

   public boolean convertMapFormat(String p_75805_1_, IProgressUpdate p_75805_2_) {
      p_75805_2_.setLoadingProgress(0);
      List<File> list = Lists.newArrayList();
      List<File> list1 = Lists.newArrayList();
      List<File> list2 = Lists.newArrayList();
      File file1 = new File(this.savesDirectory.toFile(), p_75805_1_);
      File file2 = DimensionType.NETHER.func_212679_a(file1);
      File file3 = DimensionType.THE_END.func_212679_a(file1);
      LOGGER.info("Scanning folders...");
      this.addRegionFilesToCollection(file1, list);
      if (file2.exists()) {
         this.addRegionFilesToCollection(file2, list1);
      }

      if (file3.exists()) {
         this.addRegionFilesToCollection(file3, list2);
      }

      int i = list.size() + list1.size() + list2.size();
      LOGGER.info("Total conversion count is {}", (int)i);
      WorldInfo worldinfo = this.getWorldInfo(p_75805_1_);
      BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;
      BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 = BiomeProviderType.VANILLA_LAYERED;
      BiomeProvider biomeprovider;
      if (worldinfo != null && worldinfo.getTerrainType() == WorldType.FLAT) {
         biomeprovider = biomeprovidertype.create(biomeprovidertype.createSettings().setBiome(Biomes.PLAINS));
      } else {
         biomeprovider = biomeprovidertype1.create(biomeprovidertype1.createSettings().setWorldInfo(worldinfo).setSettings(ChunkGeneratorType.SURFACE.createChunkGenSettings()));
      }

      this.convertFile(new File(file1, "region"), list, biomeprovider, 0, i, p_75805_2_);
      this.convertFile(new File(file2, "region"), list1, biomeprovidertype.create(biomeprovidertype.createSettings().setBiome(Biomes.NETHER)), list.size(), i, p_75805_2_);
      this.convertFile(new File(file3, "region"), list2, biomeprovidertype.create(biomeprovidertype.createSettings().setBiome(Biomes.THE_END)), list.size() + list1.size(), i, p_75805_2_);
      worldinfo.setSaveVersion(19133);
      if (worldinfo.getTerrainType() == WorldType.DEFAULT_1_1) {
         worldinfo.setTerrainType(WorldType.DEFAULT);
      }

      this.createFile(p_75805_1_);
      ISaveHandler isavehandler = this.getSaveLoader(p_75805_1_, (MinecraftServer)null);
      isavehandler.saveWorldInfo(worldinfo);
      return true;
   }

   private void createFile(String p_75809_1_) {
      File file1 = new File(this.savesDirectory.toFile(), p_75809_1_);
      if (!file1.exists()) {
         LOGGER.warn("Unable to create level.dat_mcr backup");
      } else {
         File file2 = new File(file1, "level.dat");
         if (!file2.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
         } else {
            File file3 = new File(file1, "level.dat_mcr");
            if (!file2.renameTo(file3)) {
               LOGGER.warn("Unable to create level.dat_mcr backup");
            }

         }
      }
   }

   private void convertFile(File p_75813_1_, Iterable<File> p_75813_2_, BiomeProvider p_75813_3_, int p_75813_4_, int p_75813_5_, IProgressUpdate p_75813_6_) {
      for(File file1 : p_75813_2_) {
         this.convertChunks(p_75813_1_, file1, p_75813_3_, p_75813_4_, p_75813_5_, p_75813_6_);
         ++p_75813_4_;
         int i = (int)Math.round(100.0D * (double)p_75813_4_ / (double)p_75813_5_);
         p_75813_6_.setLoadingProgress(i);
      }

   }

   private void convertChunks(File p_75811_1_, File p_75811_2_, BiomeProvider p_75811_3_, int p_75811_4_, int p_75811_5_, IProgressUpdate p_75811_6_) {
      try {
         String s = p_75811_2_.getName();
         RegionFile regionfile = new RegionFile(p_75811_2_);
         RegionFile regionfile1 = new RegionFile(new File(p_75811_1_, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

         for(int i = 0; i < 32; ++i) {
            for(int j = 0; j < 32; ++j) {
               if (regionfile.isChunkSaved(i, j) && !regionfile1.isChunkSaved(i, j)) {
                  DataInputStream datainputstream = regionfile.getChunkDataInputStream(i, j);
                  if (datainputstream == null) {
                     LOGGER.warn("Failed to fetch input stream");
                  } else {
                     NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                     datainputstream.close();
                     NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Level");
                     ChunkLoader.AnvilConverterData chunkloader$anvilconverterdata = ChunkLoader.load(nbttagcompound1);
                     NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                     NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                     nbttagcompound2.setTag("Level", nbttagcompound3);
                     ChunkLoader.convertToAnvilFormat(chunkloader$anvilconverterdata, nbttagcompound3, p_75811_3_);
                     DataOutputStream dataoutputstream = regionfile1.getChunkDataOutputStream(i, j);
                     CompressedStreamTools.write(nbttagcompound2, dataoutputstream);
                     dataoutputstream.close();
                  }
               }
            }

            int k = (int)Math.round(100.0D * (double)(p_75811_4_ * 1024) / (double)(p_75811_5_ * 1024));
            int l = (int)Math.round(100.0D * (double)((i + 1) * 32 + p_75811_4_ * 1024) / (double)(p_75811_5_ * 1024));
            if (l > k) {
               p_75811_6_.setLoadingProgress(l);
            }
         }

         regionfile.close();
         regionfile1.close();
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   private void addRegionFilesToCollection(File p_75810_1_, Collection<File> p_75810_2_) {
      File file1 = new File(p_75810_1_, "region");
      File[] afile = file1.listFiles((p_210209_0_, p_210209_1_) -> {
         return p_210209_1_.endsWith(".mcr");
      });
      if (afile != null) {
         Collections.addAll(p_75810_2_, afile);
      }

   }
}
