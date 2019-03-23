package net.minecraft.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class WorldSettings {
   private final long seed;
   private final GameType gameType;
   private final boolean mapFeaturesEnabled;
   private final boolean hardcoreEnabled;
   private final WorldType terrainType;
   private boolean commandsAllowed;
   private boolean bonusChestEnabled;
   private JsonElement generatorOptions = new JsonObject();

   public WorldSettings(long p_i1957_1_, GameType p_i1957_3_, boolean p_i1957_4_, boolean p_i1957_5_, WorldType p_i1957_6_) {
      this.seed = p_i1957_1_;
      this.gameType = p_i1957_3_;
      this.mapFeaturesEnabled = p_i1957_4_;
      this.hardcoreEnabled = p_i1957_5_;
      this.terrainType = p_i1957_6_;
   }

   public WorldSettings(WorldInfo p_i1958_1_) {
      this(p_i1958_1_.getSeed(), p_i1958_1_.getGameType(), p_i1958_1_.isMapFeaturesEnabled(), p_i1958_1_.isHardcoreModeEnabled(), p_i1958_1_.getTerrainType());
   }

   public WorldSettings enableBonusChest() {
      this.bonusChestEnabled = true;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldSettings enableCommands() {
      this.commandsAllowed = true;
      return this;
   }

   public WorldSettings func_205390_a(JsonElement p_205390_1_) {
      this.generatorOptions = p_205390_1_;
      return this;
   }

   public boolean isBonusChestEnabled() {
      return this.bonusChestEnabled;
   }

   public long getSeed() {
      return this.seed;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean getHardcoreEnabled() {
      return this.hardcoreEnabled;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   public boolean areCommandsAllowed() {
      return this.commandsAllowed;
   }

   public static GameType getGameTypeById(int p_77161_0_) {
      return GameType.getByID(p_77161_0_);
   }

   public JsonElement func_205391_j() {
      return this.generatorOptions;
   }
}
