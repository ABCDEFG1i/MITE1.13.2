package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketNBTQueryEntity implements Packet<INetHandlerPlayServer> {
   private int transactionId;
   private int entityId;

   public CPacketNBTQueryEntity() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketNBTQueryEntity(int p_i49755_1_, int p_i49755_2_) {
      this.transactionId = p_i49755_1_;
      this.entityId = p_i49755_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      this.entityId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      p_148840_1_.writeVarInt(this.entityId);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processNBTQueryEntity(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
