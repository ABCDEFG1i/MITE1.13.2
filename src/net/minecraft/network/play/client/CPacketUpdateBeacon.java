package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateBeacon implements Packet<INetHandlerPlayServer> {
   private int field_210357_a;
   private int field_210358_b;

   public CPacketUpdateBeacon() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateBeacon(int p_i49544_1_, int p_i49544_2_) {
      this.field_210357_a = p_i49544_1_;
      this.field_210358_b = p_i49544_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_210357_a = p_148837_1_.readVarInt();
      this.field_210358_b = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_210357_a);
      p_148840_1_.writeVarInt(this.field_210358_b);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processUpdateBeacon(this);
   }

   public int func_210355_a() {
      return this.field_210357_a;
   }

   public int func_210356_b() {
      return this.field_210358_b;
   }
}
