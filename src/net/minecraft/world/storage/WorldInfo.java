package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldInfo {
   private String versionName;
   private int versionId;
   private boolean versionSnapshot;
   public static final EnumDifficulty DEFAULT_DIFFICULTY = EnumDifficulty.NORMAL;
   private long randomSeed;
   private WorldType terrainType = WorldType.DEFAULT;
   private NBTTagCompound generatorOptions = new NBTTagCompound();
   @Nullable
   private String legacyCustomOptions;
   private int spawnX;
   private int spawnY;
   private int spawnZ;
   private long totalTime;
   private long worldTime;
   private long lastTimePlayed;
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixer;
   private final int field_209227_p;
   private boolean field_209228_q;
   private NBTTagCompound playerTag;
   private int dimension;
   private String levelName;
   private int saveVersion;
   private int cleanWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private GameType gameType;
   private boolean mapFeaturesEnabled;
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private EnumDifficulty difficulty;
   private boolean difficultyLocked;
   private double borderCenterX;
   private double borderCenterZ;
   private double borderSize = 6.0E7D;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone = 5.0D;
   private double borderDamagePerBlock = 0.2D;
   private int borderWarningDistance = 5;
   private int borderWarningTime = 15;
   private final Set<String> disabledDataPacks = Sets.newHashSet();
   private final Set<String> enabledDataPacks = Sets.newLinkedHashSet();
   private final Map<DimensionType, NBTTagCompound> dimensionData = Maps.newIdentityHashMap();
   private NBTTagCompound customBossEvents;
   private final GameRules gameRules = new GameRules();

   protected WorldInfo() {
      this.fixer = null;
      this.field_209227_p = 1631;
      this.func_212242_b(new NBTTagCompound());
   }

   public WorldInfo(NBTTagCompound p_i49564_1_, DataFixer p_i49564_2_, int p_i49564_3_, @Nullable NBTTagCompound p_i49564_4_) {
      this.fixer = p_i49564_2_;
      if (p_i49564_1_.hasKey("Version", 10)) {
         NBTTagCompound nbttagcompound = p_i49564_1_.getCompoundTag("Version");
         this.versionName = nbttagcompound.getString("Name");
         this.versionId = nbttagcompound.getInteger("Id");
         this.versionSnapshot = nbttagcompound.getBoolean("Snapshot");
      }

      this.randomSeed = p_i49564_1_.getLong("RandomSeed");
      if (p_i49564_1_.hasKey("generatorName", 8)) {
         String s1 = p_i49564_1_.getString("generatorName");
         this.terrainType = WorldType.byName(s1);
         if (this.terrainType == null) {
            this.terrainType = WorldType.DEFAULT;
         } else if (this.terrainType == WorldType.CUSTOMIZED) {
            this.legacyCustomOptions = p_i49564_1_.getString("generatorOptions");
         } else if (this.terrainType.isVersioned()) {
            int i = 0;
            if (p_i49564_1_.hasKey("generatorVersion", 99)) {
               i = p_i49564_1_.getInteger("generatorVersion");
            }

            this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
         }

         this.func_212242_b(p_i49564_1_.getCompoundTag("generatorOptions"));
      }

      this.gameType = GameType.getByID(p_i49564_1_.getInteger("GameType"));
      if (p_i49564_1_.hasKey("legacy_custom_options", 8)) {
         this.legacyCustomOptions = p_i49564_1_.getString("legacy_custom_options");
      }

      if (p_i49564_1_.hasKey("MapFeatures", 99)) {
         this.mapFeaturesEnabled = p_i49564_1_.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = p_i49564_1_.getInteger("SpawnX");
      this.spawnY = p_i49564_1_.getInteger("SpawnY");
      this.spawnZ = p_i49564_1_.getInteger("SpawnZ");
      this.totalTime = p_i49564_1_.getLong("Time");
      if (p_i49564_1_.hasKey("DayTime", 99)) {
         this.worldTime = p_i49564_1_.getLong("DayTime");
      } else {
         this.worldTime = this.totalTime;
      }

      this.lastTimePlayed = p_i49564_1_.getLong("LastPlayed");
      this.sizeOnDisk = p_i49564_1_.getLong("SizeOnDisk");
      this.levelName = p_i49564_1_.getString("LevelName");
      this.saveVersion = p_i49564_1_.getInteger("version");
      this.cleanWeatherTime = p_i49564_1_.getInteger("clearWeatherTime");
      this.rainTime = p_i49564_1_.getInteger("rainTime");
      this.raining = p_i49564_1_.getBoolean("raining");
      this.thunderTime = p_i49564_1_.getInteger("thunderTime");
      this.thundering = p_i49564_1_.getBoolean("thundering");
      this.hardcore = p_i49564_1_.getBoolean("hardcore");
      if (p_i49564_1_.hasKey("initialized", 99)) {
         this.initialized = p_i49564_1_.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (p_i49564_1_.hasKey("allowCommands", 99)) {
         this.allowCommands = p_i49564_1_.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.field_209227_p = p_i49564_3_;
      if (p_i49564_4_ != null) {
         this.playerTag = p_i49564_4_;
      }

      if (p_i49564_1_.hasKey("GameRules", 10)) {
         this.gameRules.readFromNBT(p_i49564_1_.getCompoundTag("GameRules"));
      }

      if (p_i49564_1_.hasKey("Difficulty", 99)) {
         this.difficulty = EnumDifficulty.byId(p_i49564_1_.getByte("Difficulty"));
      }

      if (p_i49564_1_.hasKey("DifficultyLocked", 1)) {
         this.difficultyLocked = p_i49564_1_.getBoolean("DifficultyLocked");
      }

      if (p_i49564_1_.hasKey("BorderCenterX", 99)) {
         this.borderCenterX = p_i49564_1_.getDouble("BorderCenterX");
      }

      if (p_i49564_1_.hasKey("BorderCenterZ", 99)) {
         this.borderCenterZ = p_i49564_1_.getDouble("BorderCenterZ");
      }

      if (p_i49564_1_.hasKey("BorderSize", 99)) {
         this.borderSize = p_i49564_1_.getDouble("BorderSize");
      }

      if (p_i49564_1_.hasKey("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = p_i49564_1_.getLong("BorderSizeLerpTime");
      }

      if (p_i49564_1_.hasKey("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = p_i49564_1_.getDouble("BorderSizeLerpTarget");
      }

      if (p_i49564_1_.hasKey("BorderSafeZone", 99)) {
         this.borderSafeZone = p_i49564_1_.getDouble("BorderSafeZone");
      }

      if (p_i49564_1_.hasKey("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = p_i49564_1_.getDouble("BorderDamagePerBlock");
      }

      if (p_i49564_1_.hasKey("BorderWarningBlocks", 99)) {
         this.borderWarningDistance = p_i49564_1_.getInteger("BorderWarningBlocks");
      }

      if (p_i49564_1_.hasKey("BorderWarningTime", 99)) {
         this.borderWarningTime = p_i49564_1_.getInteger("BorderWarningTime");
      }

      if (p_i49564_1_.hasKey("DimensionData", 10)) {
         NBTTagCompound nbttagcompound1 = p_i49564_1_.getCompoundTag("DimensionData");

         for(String s : nbttagcompound1.getKeySet()) {
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(s)), nbttagcompound1.getCompoundTag(s));
         }
      }

      if (p_i49564_1_.hasKey("DataPacks", 10)) {
         NBTTagCompound nbttagcompound2 = p_i49564_1_.getCompoundTag("DataPacks");
         NBTTagList nbttaglist = nbttagcompound2.getTagList("Disabled", 8);

         for(int k = 0; k < nbttaglist.size(); ++k) {
            this.disabledDataPacks.add(nbttaglist.getStringTagAt(k));
         }

         NBTTagList nbttaglist1 = nbttagcompound2.getTagList("Enabled", 8);

         for(int j = 0; j < nbttaglist1.size(); ++j) {
            this.enabledDataPacks.add(nbttaglist1.getStringTagAt(j));
         }
      }

      if (p_i49564_1_.hasKey("CustomBossEvents", 10)) {
         this.customBossEvents = p_i49564_1_.getCompoundTag("CustomBossEvents");
      }

   }

   public WorldInfo(WorldSettings p_i2158_1_, String p_i2158_2_) {
      this.fixer = null;
      this.field_209227_p = 1631;
      this.populateFromWorldSettings(p_i2158_1_);
      this.levelName = p_i2158_2_;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void populateFromWorldSettings(WorldSettings p_176127_1_) {
      this.randomSeed = p_176127_1_.getSeed();
      this.gameType = p_176127_1_.getGameType();
      this.mapFeaturesEnabled = p_176127_1_.isMapFeaturesEnabled();
      this.hardcore = p_176127_1_.getHardcoreEnabled();
      this.terrainType = p_176127_1_.getTerrainType();
      this.func_212242_b((NBTTagCompound)Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.INSTANCE, p_176127_1_.func_205391_j()));
      this.allowCommands = p_176127_1_.areCommandsAllowed();
   }

   public NBTTagCompound cloneNBTCompound(@Nullable NBTTagCompound p_76082_1_) {
      this.func_209225_Q();
      if (p_76082_1_ == null) {
         p_76082_1_ = this.playerTag;
      }

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.updateTagCompound(nbttagcompound, p_76082_1_);
      return nbttagcompound;
   }

   private void updateTagCompound(NBTTagCompound p_76064_1_, NBTTagCompound p_76064_2_) {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("Name", "1.13.2");
      nbttagcompound.setInteger("Id", 1631);
      nbttagcompound.setBoolean("Snapshot", false);
      p_76064_1_.setTag("Version", nbttagcompound);
      p_76064_1_.setInteger("DataVersion", 1631);
      p_76064_1_.setLong("RandomSeed", this.randomSeed);
      p_76064_1_.setString("generatorName", this.terrainType.func_211889_b());
      p_76064_1_.setInteger("generatorVersion", this.terrainType.getVersion());
      if (!this.generatorOptions.isEmpty()) {
         p_76064_1_.setTag("generatorOptions", this.generatorOptions);
      }

      if (this.legacyCustomOptions != null) {
         p_76064_1_.setString("legacy_custom_options", this.legacyCustomOptions);
      }

      p_76064_1_.setInteger("GameType", this.gameType.getID());
      p_76064_1_.setBoolean("MapFeatures", this.mapFeaturesEnabled);
      p_76064_1_.setInteger("SpawnX", this.spawnX);
      p_76064_1_.setInteger("SpawnY", this.spawnY);
      p_76064_1_.setInteger("SpawnZ", this.spawnZ);
      p_76064_1_.setLong("Time", this.totalTime);
      p_76064_1_.setLong("DayTime", this.worldTime);
      p_76064_1_.setLong("SizeOnDisk", this.sizeOnDisk);
      p_76064_1_.setLong("LastPlayed", Util.millisecondsSinceEpoch());
      p_76064_1_.setString("LevelName", this.levelName);
      p_76064_1_.setInteger("version", this.saveVersion);
      p_76064_1_.setInteger("clearWeatherTime", this.cleanWeatherTime);
      p_76064_1_.setInteger("rainTime", this.rainTime);
      p_76064_1_.setBoolean("raining", this.raining);
      p_76064_1_.setInteger("thunderTime", this.thunderTime);
      p_76064_1_.setBoolean("thundering", this.thundering);
      p_76064_1_.setBoolean("hardcore", this.hardcore);
      p_76064_1_.setBoolean("allowCommands", this.allowCommands);
      p_76064_1_.setBoolean("initialized", this.initialized);
      p_76064_1_.setDouble("BorderCenterX", this.borderCenterX);
      p_76064_1_.setDouble("BorderCenterZ", this.borderCenterZ);
      p_76064_1_.setDouble("BorderSize", this.borderSize);
      p_76064_1_.setLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      p_76064_1_.setDouble("BorderSafeZone", this.borderSafeZone);
      p_76064_1_.setDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      p_76064_1_.setDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      p_76064_1_.setDouble("BorderWarningBlocks", (double)this.borderWarningDistance);
      p_76064_1_.setDouble("BorderWarningTime", (double)this.borderWarningTime);
      if (this.difficulty != null) {
         p_76064_1_.setByte("Difficulty", (byte)this.difficulty.getId());
      }

      p_76064_1_.setBoolean("DifficultyLocked", this.difficultyLocked);
      p_76064_1_.setTag("GameRules", this.gameRules.writeToNBT());
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();

      for(Entry<DimensionType, NBTTagCompound> entry : this.dimensionData.entrySet()) {
         nbttagcompound1.setTag(String.valueOf(entry.getKey().getId()), entry.getValue());
      }

      p_76064_1_.setTag("DimensionData", nbttagcompound1);
      if (p_76064_2_ != null) {
         p_76064_1_.setTag("Player", p_76064_2_);
      }

      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();

      for(String s : this.enabledDataPacks) {
         nbttaglist.add((INBTBase)(new NBTTagString(s)));
      }

      nbttagcompound2.setTag("Enabled", nbttaglist);
      NBTTagList nbttaglist1 = new NBTTagList();

      for(String s1 : this.disabledDataPacks) {
         nbttaglist1.add((INBTBase)(new NBTTagString(s1)));
      }

      nbttagcompound2.setTag("Disabled", nbttaglist1);
      p_76064_1_.setTag("DataPacks", nbttagcompound2);
      if (this.customBossEvents != null) {
         p_76064_1_.setTag("CustomBossEvents", this.customBossEvents);
      }

   }

   public long getSeed() {
      return this.randomSeed;
   }

   public int getSpawnX() {
      return this.spawnX;
   }

   public int getSpawnY() {
      return this.spawnY;
   }

   public int getSpawnZ() {
      return this.spawnZ;
   }

   public long getWorldTotalTime() {
      return this.totalTime;
   }

   public long getWorldTime() {
      return this.worldTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   private void func_209225_Q() {
      if (!this.field_209228_q && this.playerTag != null) {
         if (this.field_209227_p < 1631) {
            if (this.fixer == null) {
               throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }

            this.playerTag = NBTUtil.func_210822_a(this.fixer, DataFixTypes.PLAYER, this.playerTag, this.field_209227_p);
         }

         this.dimension = this.playerTag.getInteger("Dimension");
         this.field_209228_q = true;
      }
   }

   public NBTTagCompound getPlayerNBTTagCompound() {
      this.func_209225_Q();
      return this.playerTag;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDimension() {
      this.func_209225_Q();
      return this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnX(int p_76058_1_) {
      this.spawnX = p_76058_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnY(int p_76056_1_) {
      this.spawnY = p_76056_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSpawnZ(int p_76087_1_) {
      this.spawnZ = p_76087_1_;
   }

   public void setWorldTotalTime(long p_82572_1_) {
      this.totalTime = p_82572_1_;
   }

   public void setWorldTime(long p_76068_1_) {
      this.worldTime = p_76068_1_;
   }

   public void setSpawn(BlockPos p_176143_1_) {
      this.spawnX = p_176143_1_.getX();
      this.spawnY = p_176143_1_.getY();
      this.spawnZ = p_176143_1_.getZ();
   }

   public String getWorldName() {
      return this.levelName;
   }

   public void setWorldName(String p_76062_1_) {
      this.levelName = p_76062_1_;
   }

   public int getSaveVersion() {
      return this.saveVersion;
   }

   public void setSaveVersion(int p_76078_1_) {
      this.saveVersion = p_76078_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getCleanWeatherTime() {
      return this.cleanWeatherTime;
   }

   public void setCleanWeatherTime(int p_176142_1_) {
      this.cleanWeatherTime = p_176142_1_;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean p_76069_1_) {
      this.thundering = p_76069_1_;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int p_76090_1_) {
      this.thunderTime = p_76090_1_;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean p_76084_1_) {
      this.raining = p_76084_1_;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int p_76080_1_) {
      this.rainTime = p_76080_1_;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public void setMapFeaturesEnabled(boolean p_176128_1_) {
      this.mapFeaturesEnabled = p_176128_1_;
   }

   public void setGameType(GameType p_76060_1_) {
      this.gameType = p_76060_1_;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   public void setHardcore(boolean p_176119_1_) {
      this.hardcore = p_176119_1_;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   public void setTerrainType(WorldType p_76085_1_) {
      this.terrainType = p_76085_1_;
   }

   public NBTTagCompound getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void func_212242_b(NBTTagCompound p_212242_1_) {
      this.generatorOptions = p_212242_1_;
   }

   public boolean areCommandsAllowed() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean p_176121_1_) {
      this.allowCommands = p_176121_1_;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setServerInitialized(boolean p_76091_1_) {
      this.initialized = p_76091_1_;
   }

   public GameRules getGameRulesInstance() {
      return this.gameRules;
   }

   public double getBorderCenterX() {
      return this.borderCenterX;
   }

   public double getBorderCenterZ() {
      return this.borderCenterZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   public void setBorderSize(double p_176145_1_) {
      this.borderSize = p_176145_1_;
   }

   public long getBorderLerpTime() {
      return this.borderSizeLerpTime;
   }

   public void setBorderLerpTime(long p_176135_1_) {
      this.borderSizeLerpTime = p_176135_1_;
   }

   public double getBorderLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   public void setBorderLerpTarget(double p_176118_1_) {
      this.borderSizeLerpTarget = p_176118_1_;
   }

   public void getBorderCenterZ(double p_176141_1_) {
      this.borderCenterZ = p_176141_1_;
   }

   public void getBorderCenterX(double p_176124_1_) {
      this.borderCenterX = p_176124_1_;
   }

   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   public void setBorderSafeZone(double p_176129_1_) {
      this.borderSafeZone = p_176129_1_;
   }

   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   public void setBorderDamagePerBlock(double p_176125_1_) {
      this.borderDamagePerBlock = p_176125_1_;
   }

   public int getBorderWarningDistance() {
      return this.borderWarningDistance;
   }

   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   public void setBorderWarningDistance(int p_176122_1_) {
      this.borderWarningDistance = p_176122_1_;
   }

   public void setBorderWarningTime(int p_176136_1_) {
      this.borderWarningTime = p_176136_1_;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(EnumDifficulty p_176144_1_) {
      this.difficulty = p_176144_1_;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean p_180783_1_) {
      this.difficultyLocked = p_180783_1_;
   }

   public void addToCrashReport(CrashReportCategory p_85118_1_) {
      p_85118_1_.addDetail("Level seed", () -> {
         return String.valueOf(this.getSeed());
      });
      p_85118_1_.addDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.terrainType.getId(), this.terrainType.func_211888_a(), this.terrainType.getVersion(), this.mapFeaturesEnabled);
      });
      p_85118_1_.addDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      p_85118_1_.addDetail("Level spawn location", () -> {
         return CrashReportCategory.getCoordinateInfo(this.spawnX, this.spawnY, this.spawnZ);
      });
      p_85118_1_.addDetail("Level time", () -> {
         return String.format("%d game time, %d day time", this.totalTime, this.worldTime);
      });
      p_85118_1_.addDetail("Level dimension", () -> {
         return String.valueOf(this.dimension);
      });
      p_85118_1_.addDetail("Level storage version", () -> {
         String s = "Unknown?";

         try {
            switch(this.saveVersion) {
            case 19132:
               s = "McRegion";
               break;
            case 19133:
               s = "Anvil";
            }
         } catch (Throwable var3) {
            ;
         }

         return String.format("0x%05X - %s", this.saveVersion, s);
      });
      p_85118_1_.addDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
      });
      p_85118_1_.addDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameType.getName(), this.gameType.getID(), this.hardcore, this.allowCommands);
      });
   }

   public NBTTagCompound getDimensionData(DimensionType p_186347_1_) {
      NBTTagCompound nbttagcompound = this.dimensionData.get(p_186347_1_);
      return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
   }

   public void setDimensionData(DimensionType p_186345_1_, NBTTagCompound p_186345_2_) {
      this.dimensionData.put(p_186345_1_, p_186345_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getVersionId() {
      return this.versionId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVersionSnapshot() {
      return this.versionSnapshot;
   }

   @OnlyIn(Dist.CLIENT)
   public String getVersionName() {
      return this.versionName;
   }

   public Set<String> getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set<String> getEnabledDataPacks() {
      return this.enabledDataPacks;
   }

   @Nullable
   public NBTTagCompound getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable NBTTagCompound p_201356_1_) {
      this.customBossEvents = p_201356_1_;
   }
}
