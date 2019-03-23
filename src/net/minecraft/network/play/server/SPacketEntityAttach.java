package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityAttach implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private int vehicleEntityId;

   public SPacketEntityAttach() {
   }

   public SPacketEntityAttach(Entity p_i46916_1_, @Nullable Entity p_i46916_2_) {
      this.entityId = p_i46916_1_.getEntityId();
      this.vehicleEntityId = p_i46916_2_ != null ? p_i46916_2_.getEntityId() : -1;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readInt();
      this.vehicleEntityId = p_148837_1_.readInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.entityId);
      p_148840_1_.writeInt(this.vehicleEntityId);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityAttach(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVehicleEntityId() {
      return this.vehicleEntityId;
   }
}
