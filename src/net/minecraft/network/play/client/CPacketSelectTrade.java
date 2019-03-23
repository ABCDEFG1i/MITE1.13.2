package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketSelectTrade implements Packet<INetHandlerPlayServer> {
   private int field_210354_a;

   public CPacketSelectTrade() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketSelectTrade(int p_i49545_1_) {
      this.field_210354_a = p_i49545_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_210354_a = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_210354_a);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processSelectTrade(this);
   }

   public int func_210353_a() {
      return this.field_210354_a;
   }
}
