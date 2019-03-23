package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketDestroyEntities implements Packet<INetHandlerPlayClient> {
   private int[] entityIDs;

   public SPacketDestroyEntities() {
   }

   public SPacketDestroyEntities(int... p_i46926_1_) {
      this.entityIDs = p_i46926_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityIDs = new int[p_148837_1_.readVarInt()];

      for(int i = 0; i < this.entityIDs.length; ++i) {
         this.entityIDs[i] = p_148837_1_.readVarInt();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityIDs.length);

      for(int i : this.entityIDs) {
         p_148840_1_.writeVarInt(i);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleDestroyEntities(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int[] getEntityIDs() {
      return this.entityIDs;
   }
}
