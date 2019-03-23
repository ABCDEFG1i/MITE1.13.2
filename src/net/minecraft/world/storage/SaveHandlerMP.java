package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaveHandlerMP implements ISaveHandler {
   public WorldInfo loadWorldInfo() {
      return null;
   }

   public void checkSessionLock() throws SessionLockException {
   }

   public IChunkLoader getChunkLoader(Dimension p_75763_1_) {
      return null;
   }

   public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_) {
   }

   public void saveWorldInfo(WorldInfo p_75761_1_) {
   }

   public IPlayerFileData getPlayerNBTManager() {
      return null;
   }

   public void flush() {
   }

   @Nullable
   public File func_212423_a(DimensionType p_212423_1_, String p_212423_2_) {
      return null;
   }

   public File getWorldDirectory() {
      return null;
   }

   public TemplateManager getStructureTemplateManager() {
      return null;
   }

   public DataFixer getFixer() {
      return null;
   }
}
