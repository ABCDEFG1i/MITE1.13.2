package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCamera implements Packet<INetHandlerPlayClient> {
   public int entityId;

   public SPacketCamera() {
   }

   public SPacketCamera(Entity p_i46920_1_) {
      this.entityId = p_i46920_1_.getEntityId();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleCamera(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_179780_1_) {
      return p_179780_1_.getEntityByID(this.entityId);
   }
}
