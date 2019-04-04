package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.management.PlayerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList extends PlayerList {
   private static final Logger LOGGER = LogManager.getLogger();

   public DedicatedPlayerList(DedicatedServer p_i1503_1_) {
      super(p_i1503_1_);
      this.setViewDistance(p_i1503_1_.getIntProperty("view-distance", 10));
      this.maxPlayers = p_i1503_1_.getIntProperty("max-players", 20);
      this.setWhiteListEnabled(p_i1503_1_.getBooleanProperty("white-list", false));
      if (!p_i1503_1_.isSinglePlayer()) {
         this.getBannedPlayers().setLanServer(true);
         this.getBannedIPs().setLanServer(true);
      }

      this.loadPlayerBanList();
      this.savePlayerBanList();
      this.loadIPBanList();
      this.saveIPBanList();
      this.loadOpsList();
      this.readWhiteList();
      this.saveOpsList();
      if (!this.getWhitelistedPlayers().getSaveFile().exists()) {
         this.saveWhiteList();
      }

   }

   public void setWhiteListEnabled(boolean p_72371_1_) {
      super.setWhiteListEnabled(p_72371_1_);
      this.getServerInstance().setProperty("white-list", p_72371_1_);
      this.getServerInstance().saveProperties();
   }

   public void addOp(GameProfile p_152605_1_) {
      super.addOp(p_152605_1_);
      this.saveOpsList();
   }

   public void removeOp(GameProfile p_152610_1_) {
      super.removeOp(p_152610_1_);
      this.saveOpsList();
   }

   public void reloadWhitelist() {
      this.readWhiteList();
   }

   private void saveIPBanList() {
      try {
         this.getBannedIPs().writeChanges();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to save ip banlist: ", ioexception);
      }

   }

   private void savePlayerBanList() {
      try {
         this.getBannedPlayers().writeChanges();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to save user banlist: ", ioexception);
      }

   }

   private void loadIPBanList() {
      try {
         this.getBannedIPs().readSavedFile();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load ip banlist: ", ioexception);
      }

   }

   private void loadPlayerBanList() {
      try {
         this.getBannedPlayers().readSavedFile();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load user banlist: ", ioexception);
      }

   }

   private void loadOpsList() {
      try {
         this.getOppedPlayers().readSavedFile();
      } catch (Exception exception) {
         LOGGER.warn("Failed to load operators list: ", exception);
      }

   }

   private void saveOpsList() {
      try {
         this.getOppedPlayers().writeChanges();
      } catch (Exception exception) {
         LOGGER.warn("Failed to save operators list: ", exception);
      }

   }

   private void readWhiteList() {
      try {
         this.getWhitelistedPlayers().readSavedFile();
      } catch (Exception exception) {
         LOGGER.warn("Failed to load white-list: ", exception);
      }

   }

   private void saveWhiteList() {
      try {
         this.getWhitelistedPlayers().writeChanges();
      } catch (Exception exception) {
         LOGGER.warn("Failed to save white-list: ", exception);
      }

   }

   public boolean canJoin(GameProfile p_152607_1_) {
      return !this.isWhiteListEnabled() || this.canSendCommands(p_152607_1_) || this.getWhitelistedPlayers().isWhitelisted(p_152607_1_);
   }

   public DedicatedServer getServerInstance() {
      return (DedicatedServer)super.getServerInstance();
   }

   public boolean bypassesPlayerLimit(GameProfile p_183023_1_) {
      return this.getOppedPlayers().bypassesPlayerLimit(p_183023_1_);
   }
}
