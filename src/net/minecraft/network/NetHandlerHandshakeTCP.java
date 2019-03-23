package net.minecraft.network;

import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {
   private final MinecraftServer field_147387_a;
   private final NetworkManager field_147386_b;

   public NetHandlerHandshakeTCP(MinecraftServer p_i45295_1_, NetworkManager p_i45295_2_) {
      this.field_147387_a = p_i45295_1_;
      this.field_147386_b = p_i45295_2_;
   }

   public void processHandshake(CPacketHandshake p_147383_1_) {
      switch(p_147383_1_.getRequestedState()) {
      case LOGIN:
         this.field_147386_b.setConnectionState(EnumConnectionState.LOGIN);
         if (p_147383_1_.getProtocolVersion() > 404) {
            ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.outdated_server", "1.13.2");
            this.field_147386_b.sendPacket(new SPacketDisconnectLogin(itextcomponent));
            this.field_147386_b.closeChannel(itextcomponent);
         } else if (p_147383_1_.getProtocolVersion() < 404) {
            ITextComponent itextcomponent1 = new TextComponentTranslation("multiplayer.disconnect.outdated_client", "1.13.2");
            this.field_147386_b.sendPacket(new SPacketDisconnectLogin(itextcomponent1));
            this.field_147386_b.closeChannel(itextcomponent1);
         } else {
            this.field_147386_b.setNetHandler(new NetHandlerLoginServer(this.field_147387_a, this.field_147386_b));
         }
         break;
      case STATUS:
         this.field_147386_b.setConnectionState(EnumConnectionState.STATUS);
         this.field_147386_b.setNetHandler(new NetHandlerStatusServer(this.field_147387_a, this.field_147386_b));
         break;
      default:
         throw new UnsupportedOperationException("Invalid intention " + p_147383_1_.getRequestedState());
      }

   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }
}
