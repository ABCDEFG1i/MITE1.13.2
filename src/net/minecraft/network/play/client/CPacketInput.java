package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketInput implements Packet<INetHandlerPlayServer> {
   private float strafeSpeed;
   private float forwardSpeed;
   private boolean jumping;
   private boolean sneaking;

   public CPacketInput() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketInput(float p_i46868_1_, float p_i46868_2_, boolean p_i46868_3_, boolean p_i46868_4_) {
      this.strafeSpeed = p_i46868_1_;
      this.forwardSpeed = p_i46868_2_;
      this.jumping = p_i46868_3_;
      this.sneaking = p_i46868_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.strafeSpeed = p_148837_1_.readFloat();
      this.forwardSpeed = p_148837_1_.readFloat();
      byte b0 = p_148837_1_.readByte();
      this.jumping = (b0 & 1) > 0;
      this.sneaking = (b0 & 2) > 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.strafeSpeed);
      p_148840_1_.writeFloat(this.forwardSpeed);
      byte b0 = 0;
      if (this.jumping) {
         b0 = (byte)(b0 | 1);
      }

      if (this.sneaking) {
         b0 = (byte)(b0 | 2);
      }

      p_148840_1_.writeByte(b0);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processInput(this);
   }

   public float getStrafeSpeed() {
      return this.strafeSpeed;
   }

   public float getForwardSpeed() {
      return this.forwardSpeed;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }
}
