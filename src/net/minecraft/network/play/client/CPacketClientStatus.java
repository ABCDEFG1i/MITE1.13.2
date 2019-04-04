package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketClientStatus implements Packet<INetHandlerPlayServer> {
   private CPacketClientStatus.State status;

   public CPacketClientStatus() {
   }

   public CPacketClientStatus(CPacketClientStatus.State p_i46886_1_) {
      this.status = p_i46886_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.status = p_148837_1_.readEnumValue(CPacketClientStatus.State.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.status);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processClientStatus(this);
   }

   public CPacketClientStatus.State getStatus() {
      return this.status;
   }

   public enum State {
      PERFORM_RESPAWN,
      REQUEST_STATS
   }
}
