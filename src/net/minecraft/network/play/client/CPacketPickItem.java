package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPickItem implements Packet<INetHandlerPlayServer> {
   private int field_210350_a;

   public CPacketPickItem() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPickItem(int p_i49547_1_) {
      this.field_210350_a = p_i49547_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_210350_a = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_210350_a);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processPickItem(this);
   }

   public int func_210349_a() {
      return this.field_210350_a;
   }
}
