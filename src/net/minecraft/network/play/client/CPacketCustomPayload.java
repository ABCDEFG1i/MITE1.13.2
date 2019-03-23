package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketCustomPayload implements Packet<INetHandlerPlayServer> {
   public static final ResourceLocation BRAND = new ResourceLocation("minecraft:brand");
   private ResourceLocation channel;
   private PacketBuffer data;

   public CPacketCustomPayload() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketCustomPayload(ResourceLocation p_i49549_1_, PacketBuffer p_i49549_2_) {
      this.channel = p_i49549_1_;
      this.data = p_i49549_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.channel = p_148837_1_.readResourceLocation();
      int i = p_148837_1_.readableBytes();
      if (i >= 0 && i <= 32767) {
         this.data = new PacketBuffer(p_148837_1_.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeResourceLocation(this.channel);
      p_148840_1_.writeBytes((ByteBuf)this.data);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processCustomPayload(this);
      if (this.data != null) {
         this.data.release();
      }

   }
}
