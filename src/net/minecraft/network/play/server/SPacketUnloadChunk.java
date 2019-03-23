package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUnloadChunk implements Packet<INetHandlerPlayClient> {
   private int x;
   private int z;

   public SPacketUnloadChunk() {
   }

   public SPacketUnloadChunk(int p_i46944_1_, int p_i46944_2_) {
      this.x = p_i46944_1_;
      this.z = p_i46944_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readInt();
      this.z = p_148837_1_.readInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.x);
      p_148840_1_.writeInt(this.z);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.processChunkUnload(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZ() {
      return this.z;
   }
}
