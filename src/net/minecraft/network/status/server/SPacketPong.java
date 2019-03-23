package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class SPacketPong implements Packet<INetHandlerStatusClient> {
   private long clientTime;

   public SPacketPong() {
   }

   public SPacketPong(long p_i46850_1_) {
      this.clientTime = p_i46850_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.clientTime = p_148837_1_.readLong();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.clientTime);
   }

   public void processPacket(INetHandlerStatusClient p_148833_1_) {
      p_148833_1_.handlePong(this);
   }
}
