package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketResourcePackStatus implements Packet<INetHandlerPlayServer> {
   private CPacketResourcePackStatus.Action action;

   public CPacketResourcePackStatus() {
   }

   public CPacketResourcePackStatus(CPacketResourcePackStatus.Action p_i47156_1_) {
      this.action = p_i47156_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnumValue(CPacketResourcePackStatus.Action.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.handleResourcePackStatus(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}
