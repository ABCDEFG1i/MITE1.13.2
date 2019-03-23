package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketChangeGameState implements Packet<INetHandlerPlayClient> {
   public static final String[] MESSAGE_NAMES = new String[]{"block.minecraft.bed.not_valid"};
   private int state;
   private float value;

   public SPacketChangeGameState() {
   }

   public SPacketChangeGameState(int p_i46943_1_, float p_i46943_2_) {
      this.state = p_i46943_1_;
      this.value = p_i46943_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.state = p_148837_1_.readUnsignedByte();
      this.value = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.state);
      p_148840_1_.writeFloat(this.value);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleChangeGameState(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getGameState() {
      return this.state;
   }

   @OnlyIn(Dist.CLIENT)
   public float getValue() {
      return this.value;
   }
}
