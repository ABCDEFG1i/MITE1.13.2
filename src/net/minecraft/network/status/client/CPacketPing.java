package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPing implements Packet<INetHandlerStatusServer> {
   private long clientTime;

   public CPacketPing() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPing(long p_i46842_1_) {
      this.clientTime = p_i46842_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.clientTime = p_148837_1_.readLong();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.clientTime);
   }

   public void processPacket(INetHandlerStatusServer p_148833_1_) {
      p_148833_1_.processPing(this);
   }

   public long getClientTime() {
      return this.clientTime;
   }
}
