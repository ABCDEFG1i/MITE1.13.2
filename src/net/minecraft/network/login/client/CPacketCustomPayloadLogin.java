package net.minecraft.network.login.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketCustomPayloadLogin implements Packet<INetHandlerLoginServer> {
   private int transaction;
   private PacketBuffer payload;

   public CPacketCustomPayloadLogin() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketCustomPayloadLogin(int p_i49516_1_, @Nullable PacketBuffer p_i49516_2_) {
      this.transaction = p_i49516_1_;
      this.payload = p_i49516_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.transaction = p_148837_1_.readVarInt();
      if (p_148837_1_.readBoolean()) {
         int i = p_148837_1_.readableBytes();
         if (i < 0 || i > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
         }

         this.payload = new PacketBuffer(p_148837_1_.readBytes(i));
      } else {
         this.payload = null;
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transaction);
      if (this.payload != null) {
         p_148840_1_.writeBoolean(true);
         p_148840_1_.writeBytes(this.payload.copy());
      } else {
         p_148840_1_.writeBoolean(false);
      }

   }

   public void processPacket(INetHandlerLoginServer p_148833_1_) {
      p_148833_1_.func_209526_a(this);
   }
}
