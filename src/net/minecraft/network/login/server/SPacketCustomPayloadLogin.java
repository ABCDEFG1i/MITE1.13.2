package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCustomPayloadLogin implements Packet<INetHandlerLoginClient> {
   private int transaction;
   private ResourceLocation channel;
   private PacketBuffer payload;

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.transaction = p_148837_1_.readVarInt();
      this.channel = p_148837_1_.readResourceLocation();
      int i = p_148837_1_.readableBytes();
      if (i >= 0 && i <= 1048576) {
         this.payload = new PacketBuffer(p_148837_1_.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transaction);
      p_148840_1_.writeResourceLocation(this.channel);
      p_148840_1_.writeBytes(this.payload.copy());
   }

   public void processPacket(INetHandlerLoginClient p_148833_1_) {
      p_148833_1_.func_209521_a(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransaction() {
      return this.transaction;
   }
}
