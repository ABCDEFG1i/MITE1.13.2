package net.minecraft.client.network;

import net.minecraft.network.NetHandlerLoginServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public NetHandlerHandshakeMemory(MinecraftServer p_i45287_1_, NetworkManager p_i45287_2_) {
      this.server = p_i45287_1_;
      this.networkManager = p_i45287_2_;
   }

   public void processHandshake(CPacketHandshake p_147383_1_) {
      this.networkManager.setConnectionState(p_147383_1_.getRequestedState());
      this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }
}
