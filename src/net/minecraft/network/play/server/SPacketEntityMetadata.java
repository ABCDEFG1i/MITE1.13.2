package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityMetadata implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

   public SPacketEntityMetadata() {
   }

   public SPacketEntityMetadata(int p_i46917_1_, EntityDataManager p_i46917_2_, boolean p_i46917_3_) {
      this.entityId = p_i46917_1_;
      if (p_i46917_3_) {
         this.dataManagerEntries = p_i46917_2_.getAll();
         p_i46917_2_.setClean();
      } else {
         this.dataManagerEntries = p_i46917_2_.getDirty();
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.dataManagerEntries = EntityDataManager.readEntries(p_148837_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      EntityDataManager.writeEntries(this.dataManagerEntries, p_148840_1_);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityMetadata(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
      return this.dataManagerEntries;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }
}
