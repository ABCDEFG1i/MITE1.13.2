package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketConfirmTeleport implements Packet<INetHandlerPlayServer> {
   private int telportId;

   public CPacketConfirmTeleport() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketConfirmTeleport(int p_i46889_1_) {
      this.telportId = p_i46889_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.telportId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.telportId);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processConfirmTeleport(this);
   }

   public int getTeleportId() {
      return this.telportId;
   }
}
