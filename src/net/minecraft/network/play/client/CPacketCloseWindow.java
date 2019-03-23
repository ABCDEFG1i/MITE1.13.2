package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketCloseWindow implements Packet<INetHandlerPlayServer> {
   private int windowId;

   public CPacketCloseWindow() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketCloseWindow(int p_i46881_1_) {
      this.windowId = p_i46881_1_;
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processCloseWindow(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
   }
}
