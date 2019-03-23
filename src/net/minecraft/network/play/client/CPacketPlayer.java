package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPlayer implements Packet<INetHandlerPlayServer> {
   protected double x;
   protected double y;
   protected double z;
   protected float yaw;
   protected float pitch;
   protected boolean onGround;
   protected boolean moving;
   protected boolean rotating;

   public CPacketPlayer() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPlayer(boolean p_i46875_1_) {
      this.onGround = p_i46875_1_;
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processPlayer(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.onGround = p_148837_1_.readUnsignedByte() != 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.onGround ? 1 : 0);
   }

   public double getX(double p_186997_1_) {
      return this.moving ? this.x : p_186997_1_;
   }

   public double getY(double p_186996_1_) {
      return this.moving ? this.y : p_186996_1_;
   }

   public double getZ(double p_187000_1_) {
      return this.moving ? this.z : p_187000_1_;
   }

   public float getYaw(float p_186999_1_) {
      return this.rotating ? this.yaw : p_186999_1_;
   }

   public float getPitch(float p_186998_1_) {
      return this.rotating ? this.pitch : p_186998_1_;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class Position extends CPacketPlayer {
      public Position() {
         this.moving = true;
      }

      @OnlyIn(Dist.CLIENT)
      public Position(double p_i46867_1_, double p_i46867_3_, double p_i46867_5_, boolean p_i46867_7_) {
         this.x = p_i46867_1_;
         this.y = p_i46867_3_;
         this.z = p_i46867_5_;
         this.onGround = p_i46867_7_;
         this.moving = true;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         this.x = p_148837_1_.readDouble();
         this.y = p_148837_1_.readDouble();
         this.z = p_148837_1_.readDouble();
         super.readPacketData(p_148837_1_);
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         p_148840_1_.writeDouble(this.x);
         p_148840_1_.writeDouble(this.y);
         p_148840_1_.writeDouble(this.z);
         super.writePacketData(p_148840_1_);
      }
   }

   public static class PositionRotation extends CPacketPlayer {
      public PositionRotation() {
         this.moving = true;
         this.rotating = true;
      }

      @OnlyIn(Dist.CLIENT)
      public PositionRotation(double p_i46865_1_, double p_i46865_3_, double p_i46865_5_, float p_i46865_7_, float p_i46865_8_, boolean p_i46865_9_) {
         this.x = p_i46865_1_;
         this.y = p_i46865_3_;
         this.z = p_i46865_5_;
         this.yaw = p_i46865_7_;
         this.pitch = p_i46865_8_;
         this.onGround = p_i46865_9_;
         this.rotating = true;
         this.moving = true;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         this.x = p_148837_1_.readDouble();
         this.y = p_148837_1_.readDouble();
         this.z = p_148837_1_.readDouble();
         this.yaw = p_148837_1_.readFloat();
         this.pitch = p_148837_1_.readFloat();
         super.readPacketData(p_148837_1_);
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         p_148840_1_.writeDouble(this.x);
         p_148840_1_.writeDouble(this.y);
         p_148840_1_.writeDouble(this.z);
         p_148840_1_.writeFloat(this.yaw);
         p_148840_1_.writeFloat(this.pitch);
         super.writePacketData(p_148840_1_);
      }
   }

   public static class Rotation extends CPacketPlayer {
      public Rotation() {
         this.rotating = true;
      }

      @OnlyIn(Dist.CLIENT)
      public Rotation(float p_i46863_1_, float p_i46863_2_, boolean p_i46863_3_) {
         this.yaw = p_i46863_1_;
         this.pitch = p_i46863_2_;
         this.onGround = p_i46863_3_;
         this.rotating = true;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         this.yaw = p_148837_1_.readFloat();
         this.pitch = p_148837_1_.readFloat();
         super.readPacketData(p_148837_1_);
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         p_148840_1_.writeFloat(this.yaw);
         p_148840_1_.writeFloat(this.pitch);
         super.writePacketData(p_148840_1_);
      }
   }
}
