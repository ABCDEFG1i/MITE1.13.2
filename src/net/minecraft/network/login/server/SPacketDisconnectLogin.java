package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketDisconnectLogin implements Packet<INetHandlerLoginClient> {
   private ITextComponent reason;

   public SPacketDisconnectLogin() {
   }

   public SPacketDisconnectLogin(ITextComponent p_i46853_1_) {
      this.reason = p_i46853_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.reason = ITextComponent.Serializer.fromJsonLenient(p_148837_1_.readString(32767));
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeTextComponent(this.reason);
   }

   public void processPacket(INetHandlerLoginClient p_148833_1_) {
      p_148833_1_.handleDisconnect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getReason() {
      return this.reason;
   }
}
