package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketEnchantItem implements Packet<INetHandlerPlayServer> {
   private int windowId;
   private int button;

   public CPacketEnchantItem() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEnchantItem(int p_i46883_1_, int p_i46883_2_) {
      this.windowId = p_i46883_1_;
      this.button = p_i46883_2_;
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processEnchantItem(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
      this.button = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeByte(this.button);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public int getButton() {
      return this.button;
   }
}
