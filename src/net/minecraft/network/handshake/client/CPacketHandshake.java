package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketHandshake implements Packet<INetHandlerHandshakeServer> {
   private int protocolVersion;
   private String ip;
   private int port;
   private EnumConnectionState requestedState;

   public CPacketHandshake() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketHandshake(String p_i47613_1_, int p_i47613_2_, EnumConnectionState p_i47613_3_) {
      this.protocolVersion = 404;
      this.ip = p_i47613_1_;
      this.port = p_i47613_2_;
      this.requestedState = p_i47613_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.protocolVersion = p_148837_1_.readVarInt();
      this.ip = p_148837_1_.readString(255);
      this.port = p_148837_1_.readUnsignedShort();
      this.requestedState = EnumConnectionState.getById(p_148837_1_.readVarInt());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.protocolVersion);
      p_148840_1_.writeString(this.ip);
      p_148840_1_.writeShort(this.port);
      p_148840_1_.writeVarInt(this.requestedState.getId());
   }

   public void processPacket(INetHandlerHandshakeServer p_148833_1_) {
      p_148833_1_.processHandshake(this);
   }

   public EnumConnectionState getRequestedState() {
      return this.requestedState;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }
}
