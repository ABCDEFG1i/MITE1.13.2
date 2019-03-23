package net.minecraft.network.play.client;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.WorldServer;

public class CPacketSpectate implements Packet<INetHandlerPlayServer> {
   private UUID id;

   public CPacketSpectate() {
   }

   public CPacketSpectate(UUID p_i46859_1_) {
      this.id = p_i46859_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readUniqueId();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUniqueId(this.id);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.handleSpectate(this);
   }

   @Nullable
   public Entity getEntity(WorldServer p_179727_1_) {
      return p_179727_1_.getEntityFromUuid(this.id);
   }
}
