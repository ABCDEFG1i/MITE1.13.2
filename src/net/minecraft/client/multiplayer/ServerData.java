package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int version = 404;
   public String gameVersion = "1.13.2";
   public boolean pinged;
   public String playerList;
   private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
   private String serverIcon;
   private boolean lanServer;

   public ServerData(String p_i46420_1_, String p_i46420_2_, boolean p_i46420_3_) {
      this.serverName = p_i46420_1_;
      this.serverIP = p_i46420_2_;
      this.lanServer = p_i46420_3_;
   }

   public NBTTagCompound getNBTCompound() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("name", this.serverName);
      nbttagcompound.setString("ip", this.serverIP);
      if (this.serverIcon != null) {
         nbttagcompound.setString("icon", this.serverIcon);
      }

      if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
         nbttagcompound.setBoolean("acceptTextures", true);
      } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
         nbttagcompound.setBoolean("acceptTextures", false);
      }

      return nbttagcompound;
   }

   public ServerData.ServerResourceMode getResourceMode() {
      return this.resourceMode;
   }

   public void setResourceMode(ServerData.ServerResourceMode p_152584_1_) {
      this.resourceMode = p_152584_1_;
   }

   public static ServerData getServerDataFromNBTCompound(NBTTagCompound p_78837_0_) {
      ServerData serverdata = new ServerData(p_78837_0_.getString("name"), p_78837_0_.getString("ip"), false);
      if (p_78837_0_.hasKey("icon", 8)) {
         serverdata.setBase64EncodedIconData(p_78837_0_.getString("icon"));
      }

      if (p_78837_0_.hasKey("acceptTextures", 1)) {
         if (p_78837_0_.getBoolean("acceptTextures")) {
            serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
         } else {
            serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
      }

      return serverdata;
   }

   public String getBase64EncodedIconData() {
      return this.serverIcon;
   }

   public void setBase64EncodedIconData(String p_147407_1_) {
      this.serverIcon = p_147407_1_;
   }

   public boolean isOnLAN() {
      return this.lanServer;
   }

   public void copyFrom(ServerData p_152583_1_) {
      this.serverIP = p_152583_1_.serverIP;
      this.serverName = p_152583_1_.serverName;
      this.setResourceMode(p_152583_1_.getResourceMode());
      this.serverIcon = p_152583_1_.serverIcon;
      this.lanServer = p_152583_1_.lanServer;
   }

   @OnlyIn(Dist.CLIENT)
   public enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final ITextComponent motd;

      ServerResourceMode(String p_i1053_3_) {
         this.motd = new TextComponentTranslation("addServer.resourcePack." + p_i1053_3_);
      }

      public ITextComponent getMotd() {
         return this.motd;
      }
   }
}
