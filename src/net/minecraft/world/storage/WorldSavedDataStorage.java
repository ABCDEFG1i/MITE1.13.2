package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.dimension.DimensionType;

public class WorldSavedDataStorage {
   private final Map<DimensionType, DimensionSavedDataManager> field_212427_a;
   @Nullable
   private final ISaveHandler saveHandler;

   public WorldSavedDataStorage(@Nullable ISaveHandler p_i2162_1_) {
      this.saveHandler = p_i2162_1_;
      Builder<DimensionType, DimensionSavedDataManager> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
         DimensionSavedDataManager dimensionsaveddatamanager = new DimensionSavedDataManager(dimensiontype, p_i2162_1_);
         builder.put(dimensiontype, dimensionsaveddatamanager);
         dimensionsaveddatamanager.func_75746_b();
      }

      this.field_212427_a = builder.build();
   }

   @Nullable
   public <T extends WorldSavedData> T func_212426_a(DimensionType p_212426_1_, Function<String, T> p_212426_2_, String p_212426_3_) {
      return this.field_212427_a.get(p_212426_1_).func_201067_a(p_212426_2_, p_212426_3_);
   }

   public void func_212424_a(DimensionType p_212424_1_, String p_212424_2_, WorldSavedData p_212424_3_) {
      this.field_212427_a.get(p_212424_1_).func_75745_a(p_212424_2_, p_212424_3_);
   }

   public void saveAllData() {
      this.field_212427_a.values().forEach(DimensionSavedDataManager::func_212775_b);
   }

   public int func_212425_a(DimensionType p_212425_1_, String p_212425_2_) {
      return this.field_212427_a.get(p_212425_1_).func_75743_a(p_212425_2_);
   }

   public NBTTagCompound func_208028_a(String p_208028_1_, int p_208028_2_) throws IOException {
      return DimensionSavedDataManager.func_212774_a(this.saveHandler, DimensionType.OVERWORLD, p_208028_1_, p_208028_2_);
   }
}
