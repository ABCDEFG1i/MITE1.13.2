package net.minecraft.world.storage;

import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldSummary implements Comparable<WorldSummary> {
   private final String fileName;
   private final String displayName;
   private final long lastTimePlayed;
   private final long sizeOnDisk;
   private final boolean requiresConversion;
   private final GameType gameType;
   private final boolean hardcore;
   private final boolean cheatsEnabled;
   private final String versionName;
   private final int versionId;
   private final boolean versionSnapshot;
   private final WorldType terrainType;

   public WorldSummary(WorldInfo p_i46646_1_, String p_i46646_2_, String p_i46646_3_, long p_i46646_4_, boolean p_i46646_6_) {
      this.fileName = p_i46646_2_;
      this.displayName = p_i46646_3_;
      this.lastTimePlayed = p_i46646_1_.getLastTimePlayed();
      this.sizeOnDisk = p_i46646_4_;
      this.gameType = p_i46646_1_.getGameType();
      this.requiresConversion = p_i46646_6_;
      this.hardcore = p_i46646_1_.isHardcoreModeEnabled();
      this.cheatsEnabled = p_i46646_1_.areCommandsAllowed();
      this.versionName = p_i46646_1_.getVersionName();
      this.versionId = p_i46646_1_.getVersionId();
      this.versionSnapshot = p_i46646_1_.isVersionSnapshot();
      this.terrainType = p_i46646_1_.getTerrainType();
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public boolean requiresConversion() {
      return this.requiresConversion;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int compareTo(WorldSummary p_compareTo_1_) {
      if (this.lastTimePlayed < p_compareTo_1_.lastTimePlayed) {
         return 1;
      } else {
         return this.lastTimePlayed > p_compareTo_1_.lastTimePlayed ? -1 : this.fileName.compareTo(p_compareTo_1_.fileName);
      }
   }

   public GameType getEnumGameType() {
      return this.gameType;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   public boolean getCheatsEnabled() {
      return this.cheatsEnabled;
   }

   public ITextComponent func_200538_i() {
      return StringUtils.isNullOrEmpty(this.versionName) ? new TextComponentTranslation("selectWorld.versionUnknown") : new TextComponentString(this.versionName);
   }

   public boolean markVersionInList() {
      return this.askToOpenWorld() || this.func_197731_n() || this.func_202842_n();
   }

   public boolean askToOpenWorld() {
      return this.versionId > 413495445;
   }

   public boolean func_202842_n() {
      return this.terrainType == WorldType.CUSTOMIZED && this.versionId < 1466;
   }

   public boolean func_197731_n() {
      return this.versionId < 413495445;
   }
}
