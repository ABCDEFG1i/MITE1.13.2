package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEnableCompression implements Packet<INetHandlerLoginClient> {
   private int compressionThreshold;

   public SPacketEnableCompression() {
   }

   public SPacketEnableCompression(int p_i46854_1_) {
      this.compressionThreshold = p_i46854_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.compressionThreshold = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.compressionThreshold);
   }

   public void processPacket(INetHandlerLoginClient p_148833_1_) {
      p_148833_1_.handleEnableCompression(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
