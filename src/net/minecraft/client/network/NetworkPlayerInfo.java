package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NetworkPlayerInfo {
   private final GameProfile gameProfile;
   private final Map<Type, ResourceLocation> playerTextures = Maps.newEnumMap(Type.class);
   private GameType gameType;
   private int responseTime;
   private boolean playerTexturesLoaded;
   private String skinType;
   private ITextComponent displayName;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private long renderVisibilityId;

   public NetworkPlayerInfo(GameProfile p_i46294_1_) {
      this.gameProfile = p_i46294_1_;
   }

   public NetworkPlayerInfo(SPacketPlayerListItem.AddPlayerData p_i46583_1_) {
      this.gameProfile = p_i46583_1_.getProfile();
      this.gameType = p_i46583_1_.getGameMode();
      this.responseTime = p_i46583_1_.getPing();
      this.displayName = p_i46583_1_.getDisplayName();
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   protected void setGameType(GameType p_178839_1_) {
      this.gameType = p_178839_1_;
   }

   public int getResponseTime() {
      return this.responseTime;
   }

   protected void setResponseTime(int p_178838_1_) {
      this.responseTime = p_178838_1_;
   }

   public boolean hasLocationSkin() {
      return this.getLocationSkin() != null;
   }

   public String getSkinType() {
      return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
   }

   public ResourceLocation getLocationSkin() {
      this.loadPlayerTextures();
      return MoreObjects.firstNonNull(this.playerTextures.get(Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
   }

   @Nullable
   public ResourceLocation getLocationCape() {
      this.loadPlayerTextures();
      return this.playerTextures.get(Type.CAPE);
   }

   @Nullable
   public ResourceLocation getLocationElytra() {
      this.loadPlayerTextures();
      return this.playerTextures.get(Type.ELYTRA);
   }

   @Nullable
   public ScorePlayerTeam getPlayerTeam() {
      return Minecraft.getInstance().world.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
   }

   protected void loadPlayerTextures() {
      synchronized(this) {
         if (!this.playerTexturesLoaded) {
            this.playerTexturesLoaded = true;
            Minecraft.getInstance().getSkinManager().loadProfileTextures(this.gameProfile, (p_210250_1_, p_210250_2_, p_210250_3_) -> {
               switch(p_210250_1_) {
               case SKIN:
                  this.playerTextures.put(Type.SKIN, p_210250_2_);
                  this.skinType = p_210250_3_.getMetadata("model");
                  if (this.skinType == null) {
                     this.skinType = "default";
                  }
                  break;
               case CAPE:
                  this.playerTextures.put(Type.CAPE, p_210250_2_);
                  break;
               case ELYTRA:
                  this.playerTextures.put(Type.ELYTRA, p_210250_2_);
               }

            }, true);
         }

      }
   }

   public void setDisplayName(@Nullable ITextComponent p_178859_1_) {
      this.displayName = p_178859_1_;
   }

   @Nullable
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public int getLastHealth() {
      return this.lastHealth;
   }

   public void setLastHealth(int p_178836_1_) {
      this.lastHealth = p_178836_1_;
   }

   public int getDisplayHealth() {
      return this.displayHealth;
   }

   public void setDisplayHealth(int p_178857_1_) {
      this.displayHealth = p_178857_1_;
   }

   public long getLastHealthTime() {
      return this.lastHealthTime;
   }

   public void setLastHealthTime(long p_178846_1_) {
      this.lastHealthTime = p_178846_1_;
   }

   public long getHealthBlinkTime() {
      return this.healthBlinkTime;
   }

   public void setHealthBlinkTime(long p_178844_1_) {
      this.healthBlinkTime = p_178844_1_;
   }

   public long getRenderVisibilityId() {
      return this.renderVisibilityId;
   }

   public void setRenderVisibilityId(long p_178843_1_) {
      this.renderVisibilityId = p_178843_1_;
   }
}
