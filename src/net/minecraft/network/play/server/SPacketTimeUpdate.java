package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketTimeUpdate implements Packet<INetHandlerPlayClient> {
   private long totalWorldTime;
   private long worldTime;

   public SPacketTimeUpdate() {
   }

   public SPacketTimeUpdate(long p_i46902_1_, long p_i46902_3_, boolean p_i46902_5_) {
      this.totalWorldTime = p_i46902_1_;
      this.worldTime = p_i46902_3_;
      if (!p_i46902_5_) {
         this.worldTime = -this.worldTime;
         if (this.worldTime == 0L) {
            this.worldTime = -1L;
         }
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.totalWorldTime = p_148837_1_.readLong();
      this.worldTime = p_148837_1_.readLong();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.totalWorldTime);
      p_148840_1_.writeLong(this.worldTime);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleTimeUpdate(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getTotalWorldTime() {
      return this.totalWorldTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getWorldTime() {
      return this.worldTime;
   }
}
