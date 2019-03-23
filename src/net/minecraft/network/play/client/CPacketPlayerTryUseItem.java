package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;

public class CPacketPlayerTryUseItem implements Packet<INetHandlerPlayServer> {
   private EnumHand hand;

   public CPacketPlayerTryUseItem() {
   }

   public CPacketPlayerTryUseItem(EnumHand p_i46857_1_) {
      this.hand = p_i46857_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.hand = p_148837_1_.readEnumValue(EnumHand.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.hand);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processTryUseItem(this);
   }

   public EnumHand getHand() {
      return this.hand;
   }
}
