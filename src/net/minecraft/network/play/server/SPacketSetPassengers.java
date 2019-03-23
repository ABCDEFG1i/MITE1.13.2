package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSetPassengers implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private int[] passengerIds;

   public SPacketSetPassengers() {
   }

   public SPacketSetPassengers(Entity p_i46909_1_) {
      this.entityId = p_i46909_1_.getEntityId();
      List<Entity> list = p_i46909_1_.getPassengers();
      this.passengerIds = new int[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         this.passengerIds[i] = list.get(i).getEntityId();
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.passengerIds = p_148837_1_.readVarIntArray();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeVarIntArray(this.passengerIds);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSetPassengers(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int[] getPassengerIds() {
      return this.passengerIds;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }
}
