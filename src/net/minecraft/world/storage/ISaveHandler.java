package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface ISaveHandler {
   @Nullable
   WorldInfo loadWorldInfo();

   void checkSessionLock() throws SessionLockException;

   IChunkLoader getChunkLoader(Dimension p_75763_1_);

   void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_);

   void saveWorldInfo(WorldInfo p_75761_1_);

   IPlayerFileData getPlayerNBTManager();

   void flush();

   File getWorldDirectory();

   @Nullable
   File func_212423_a(DimensionType p_212423_1_, String p_212423_2_);

   TemplateManager getStructureTemplateManager();

   DataFixer getFixer();
}
