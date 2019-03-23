package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityVelocity implements Packet<INetHandlerPlayClient> {
   private int entityID;
   private int motionX;
   private int motionY;
   private int motionZ;

   public SPacketEntityVelocity() {
   }

   public SPacketEntityVelocity(Entity p_i46914_1_) {
      this(p_i46914_1_.getEntityId(), p_i46914_1_.motionX, p_i46914_1_.motionY, p_i46914_1_.motionZ);
   }

   public SPacketEntityVelocity(int p_i46915_1_, double p_i46915_2_, double p_i46915_4_, double p_i46915_6_) {
      this.entityID = p_i46915_1_;
      double d0 = 3.9D;
      if (p_i46915_2_ < -3.9D) {
         p_i46915_2_ = -3.9D;
      }

      if (p_i46915_4_ < -3.9D) {
         p_i46915_4_ = -3.9D;
      }

      if (p_i46915_6_ < -3.9D) {
         p_i46915_6_ = -3.9D;
      }

      if (p_i46915_2_ > 3.9D) {
         p_i46915_2_ = 3.9D;
      }

      if (p_i46915_4_ > 3.9D) {
         p_i46915_4_ = 3.9D;
      }

      if (p_i46915_6_ > 3.9D) {
         p_i46915_6_ = 3.9D;
      }

      this.motionX = (int)(p_i46915_2_ * 8000.0D);
      this.motionY = (int)(p_i46915_4_ * 8000.0D);
      this.motionZ = (int)(p_i46915_6_ * 8000.0D);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.motionX = p_148837_1_.readShort();
      this.motionY = p_148837_1_.readShort();
      this.motionZ = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeShort(this.motionX);
      p_148840_1_.writeShort(this.motionY);
      p_148840_1_.writeShort(this.motionZ);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityVelocity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionX() {
      return this.motionX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionY() {
      return this.motionY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionZ() {
      return this.motionZ;
   }
}
