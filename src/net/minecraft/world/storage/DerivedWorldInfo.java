package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo delegate;

   public DerivedWorldInfo(WorldInfo p_i2145_1_) {
      this.delegate = p_i2145_1_;
   }

   public NBTTagCompound cloneNBTCompound(@Nullable NBTTagCompound p_76082_1_) {
      return this.delegate.cloneNBTCompound(p_76082_1_);
   }

   public long getSeed() {
      return this.delegate.getSeed();
   }

   public int getSpawnX() {
      return this.delegate.getSpawnX();
   }

   public int getSpawnY() {
      return this.delegate.getSpawnY();
   }

   public int getSpawnZ() {
      return this.delegate.getSpawnZ();
   }

   public long getWorldTotalTime() {
      return this.delegate.getWorldTotalTime();
   }

   public long getWorldTime() {
      return this.delegate.getWorldTime();
   }

   @OnlyIn(Dist.CLIENT)
   public long getSizeOnDisk() {
      return this.delegate.getSizeOnDisk();
   }

   public NBTTagCompound getPlayerNBTTagCompound() {
      return this.delegate.getPlayerNBTTagCompound();
   }

   @OnlyIn(Dist.CLIENT)
   public int getDimension() {
      return this.delegate.getDimension();
   }

   public String getWorldName() {
      return this.delegate.getWorldName();
   }

   public int getSaveVersion() {
      return this.delegate.getSaveVersion();
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.delegate.getLastTimePlayed();
   }

   public boolean isThundering() {
      return this.delegate.isThundering();
   }

   public int getThunderTime() {
      return this.delegate.getThunderTime();
   }

   public boolean isRaining() {
      return this.delegate.isRaining();
   }

   public int getRainTime() {
      return this.delegate.getRainTime();
   }

   public GameType getGameType() {
      return this.delegate.getGameType();
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int p_76058_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int p_76056_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int p_76087_1_) {
   }

   public void setWorldTotalTime(long p_82572_1_) {
   }

   public void setWorldTime(long p_76068_1_) {
   }

   public void setSpawn(BlockPos p_176143_1_) {
   }

   public void setWorldName(String p_76062_1_) {
   }

   public void setSaveVersion(int p_76078_1_) {
   }

   public void setThundering(boolean p_76069_1_) {
   }

   public void setThunderTime(int p_76090_1_) {
   }

   public void setRaining(boolean p_76084_1_) {
   }

   public void setRainTime(int p_76080_1_) {
   }

   public boolean isMapFeaturesEnabled() {
      return this.delegate.isMapFeaturesEnabled();
   }

   public boolean isHardcoreModeEnabled() {
      return this.delegate.isHardcoreModeEnabled();
   }

   public WorldType getTerrainType() {
      return this.delegate.getTerrainType();
   }

   public void setTerrainType(WorldType p_76085_1_) {
   }

   public boolean areCommandsAllowed() {
      return this.delegate.areCommandsAllowed();
   }

   public void setAllowCommands(boolean p_176121_1_) {
   }

   public boolean isInitialized() {
      return this.delegate.isInitialized();
   }

   public void setServerInitialized(boolean p_76091_1_) {
   }

   public GameRules getGameRulesInstance() {
      return this.delegate.getGameRulesInstance();
   }

   public EnumDifficulty getDifficulty() {
      return this.delegate.getDifficulty();
   }

   public void setDifficulty(EnumDifficulty p_176144_1_) {
   }

   public boolean isDifficultyLocked() {
      return this.delegate.isDifficultyLocked();
   }

   public void setDifficultyLocked(boolean p_180783_1_) {
   }

   public void setDimensionData(DimensionType p_186345_1_, NBTTagCompound p_186345_2_) {
      this.delegate.setDimensionData(p_186345_1_, p_186345_2_);
   }

   public NBTTagCompound getDimensionData(DimensionType p_186347_1_) {
      return this.delegate.getDimensionData(p_186347_1_);
   }
}
