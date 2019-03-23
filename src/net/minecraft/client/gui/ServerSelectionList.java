package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerSelectionList extends GuiListExtended<ServerSelectionList.Entry> {
   private final GuiMultiplayer owner;
   private final List<ServerListEntryNormal> serverListInternet = Lists.newArrayList();
   private final ServerSelectionList.Entry lanScanEntry = new ServerListEntryLanScan();
   private final List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
   private int selectedSlotIndex = -1;

   private void func_195094_h() {
      this.clearEntries();
      this.serverListInternet.forEach(this::addEntry);
      this.addEntry(this.lanScanEntry);
      this.serverListLan.forEach(this::addEntry);
   }

   public ServerSelectionList(GuiMultiplayer p_i45049_1_, Minecraft p_i45049_2_, int p_i45049_3_, int p_i45049_4_, int p_i45049_5_, int p_i45049_6_, int p_i45049_7_) {
      super(p_i45049_2_, p_i45049_3_, p_i45049_4_, p_i45049_5_, p_i45049_6_, p_i45049_7_);
      this.owner = p_i45049_1_;
   }

   public void setSelectedSlotIndex(int p_148192_1_) {
      this.selectedSlotIndex = p_148192_1_;
   }

   protected boolean isSelected(int p_148131_1_) {
      return p_148131_1_ == this.selectedSlotIndex;
   }

   public int getSelected() {
      return this.selectedSlotIndex;
   }

   public void updateOnlineServers(ServerList p_148195_1_) {
      this.serverListInternet.clear();

      for(int i = 0; i < p_148195_1_.countServers(); ++i) {
         this.serverListInternet.add(new ServerListEntryNormal(this.owner, p_148195_1_.getServerData(i)));
      }

      this.func_195094_h();
   }

   public void updateNetworkServers(List<LanServerInfo> p_148194_1_) {
      this.serverListLan.clear();

      for(LanServerInfo lanserverinfo : p_148194_1_) {
         this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanserverinfo));
      }

      this.func_195094_h();
   }

   protected int getScrollBarX() {
      return super.getScrollBarX() + 30;
   }

   public int getListWidth() {
      return super.getListWidth() + 85;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends GuiListExtended.IGuiListEntry<ServerSelectionList.Entry> {
   }
}
