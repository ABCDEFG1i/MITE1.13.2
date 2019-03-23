package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketVehicleMove implements Packet<INetHandlerPlayServer> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public CPacketVehicleMove() {
   }

   public CPacketVehicleMove(Entity p_i46874_1_) {
      this.x = p_i46874_1_.posX;
      this.y = p_i46874_1_.posY;
      this.z = p_i46874_1_.posZ;
      this.yaw = p_i46874_1_.rotationYaw;
      this.pitch = p_i46874_1_.rotationPitch;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeFloat(this.yaw);
      p_148840_1_.writeFloat(this.pitch);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processVehicleMove(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }
}
