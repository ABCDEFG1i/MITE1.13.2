package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File p_i49568_1_, String p_i49568_2_, @Nullable MinecraftServer p_i49568_3_, DataFixer p_i49568_4_) {
      super(p_i49568_1_, p_i49568_2_, p_i49568_3_, p_i49568_4_);
   }

   public IChunkLoader getChunkLoader(Dimension p_75763_1_) {
      File file1 = p_75763_1_.getType().func_212679_a(this.getWorldDirectory());
      file1.mkdirs();
      return new AnvilChunkLoader(file1, this.dataFixer);
   }

   public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, @Nullable NBTTagCompound p_75755_2_) {
      p_75755_1_.setSaveVersion(19133);
      super.saveWorldInfoWithPlayer(p_75755_1_, p_75755_2_);
   }

   public void flush() {
      try {
         ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
      } catch (InterruptedException interruptedexception) {
         interruptedexception.printStackTrace();
      }

      RegionFileCache.clearRegionFileReferences();
   }
}
