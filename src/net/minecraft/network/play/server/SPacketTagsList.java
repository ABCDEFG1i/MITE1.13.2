package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketTagsList implements Packet<INetHandlerPlayClient> {
   private NetworkTagManager tags;

   public SPacketTagsList() {
   }

   public SPacketTagsList(NetworkTagManager p_i48211_1_) {
      this.tags = p_i48211_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.tags = NetworkTagManager.read(p_148837_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      this.tags.write(p_148840_1_);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleTags(this);
   }

   @OnlyIn(Dist.CLIENT)
   public NetworkTagManager getTags() {
      return this.tags;
   }
}
