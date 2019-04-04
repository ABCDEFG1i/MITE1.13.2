package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final List<ServerData> servers = Lists.newArrayList();

   public ServerList(Minecraft p_i1194_1_) {
      this.mc = p_i1194_1_;
      this.loadServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.gameDir, "servers.dat"));
         if (nbttagcompound == null) {
            return;
         }

         NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
         }
      } catch (Exception exception) {
         LOGGER.error("Couldn't load server list", exception);
      }

   }

   public void saveServerList() {
      try {
         NBTTagList nbttaglist = new NBTTagList();

         for(ServerData serverdata : this.servers) {
            nbttaglist.add(serverdata.getNBTCompound());
         }

         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setTag("servers", nbttaglist);
         CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.gameDir, "servers.dat"));
      } catch (Exception exception) {
         LOGGER.error("Couldn't save server list", exception);
      }

   }

   public ServerData getServerData(int p_78850_1_) {
      return this.servers.get(p_78850_1_);
   }

   public void removeServerData(int p_78851_1_) {
      this.servers.remove(p_78851_1_);
   }

   public void addServerData(ServerData p_78849_1_) {
      this.servers.add(p_78849_1_);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int p_78857_1_, int p_78857_2_) {
      ServerData serverdata = this.getServerData(p_78857_1_);
      this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
      this.servers.set(p_78857_2_, serverdata);
      this.saveServerList();
   }

   public void set(int p_147413_1_, ServerData p_147413_2_) {
      this.servers.set(p_147413_1_, p_147413_2_);
   }

   public static void saveSingleServer(ServerData p_147414_0_) {
      ServerList serverlist = new ServerList(Minecraft.getInstance());
      serverlist.loadServerList();

      for(int i = 0; i < serverlist.countServers(); ++i) {
         ServerData serverdata = serverlist.getServerData(i);
         if (serverdata.serverName.equals(p_147414_0_.serverName) && serverdata.serverIP.equals(p_147414_0_.serverIP)) {
            serverlist.set(i, p_147414_0_);
            break;
         }
      }

      serverlist.saveServerList();
   }
}
