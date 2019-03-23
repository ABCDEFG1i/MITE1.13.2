package net.minecraft.client.settings;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreativeSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File dataFile;
   private final DataFixer dataFixer;
   private final HotbarSnapshot[] hotbarSnapshots = new HotbarSnapshot[9];
   private boolean loaded;

   public CreativeSettings(File p_i49702_1_, DataFixer p_i49702_2_) {
      this.dataFile = new File(p_i49702_1_, "hotbar.nbt");
      this.dataFixer = p_i49702_2_;

      for(int i = 0; i < 9; ++i) {
         this.hotbarSnapshots[i] = new HotbarSnapshot();
      }

   }

   private void load() {
      try {
         NBTTagCompound nbttagcompound = CompressedStreamTools.read(this.dataFile);
         if (nbttagcompound == null) {
            return;
         }

         if (!nbttagcompound.hasKey("DataVersion", 99)) {
            nbttagcompound.setInteger("DataVersion", 1343);
         }

         nbttagcompound = NBTUtil.func_210822_a(this.dataFixer, DataFixTypes.HOTBAR, nbttagcompound, nbttagcompound.getInteger("DataVersion"));

         for(int i = 0; i < 9; ++i) {
            this.hotbarSnapshots[i].fromTag(nbttagcompound.getTagList(String.valueOf(i), 10));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to load creative mode options", (Throwable)exception);
      }

   }

   public void save() {
      try {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInteger("DataVersion", 1631);

         for(int i = 0; i < 9; ++i) {
            nbttagcompound.setTag(String.valueOf(i), this.getHotbarSnapshot(i).createTag());
         }

         CompressedStreamTools.write(nbttagcompound, this.dataFile);
      } catch (Exception exception) {
         LOGGER.error("Failed to save creative mode options", (Throwable)exception);
      }

   }

   public HotbarSnapshot getHotbarSnapshot(int p_192563_1_) {
      if (!this.loaded) {
         this.load();
         this.loaded = true;
      }

      return this.hotbarSnapshots[p_192563_1_];
   }
}
