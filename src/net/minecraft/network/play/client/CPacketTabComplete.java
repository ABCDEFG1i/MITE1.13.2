package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketTabComplete implements Packet<INetHandlerPlayServer> {
   private int field_197710_a;
   private String field_197711_b;

   public CPacketTabComplete() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketTabComplete(int p_i47928_1_, String p_i47928_2_) {
      this.field_197710_a = p_i47928_1_;
      this.field_197711_b = p_i47928_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_197710_a = p_148837_1_.readVarInt();
      this.field_197711_b = p_148837_1_.readString(32500);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_197710_a);
      p_148840_1_.func_211400_a(this.field_197711_b, 32500);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processTabComplete(this);
   }

   public int func_197709_a() {
      return this.field_197710_a;
   }

   public String func_197707_b() {
      return this.field_197711_b;
   }
}
