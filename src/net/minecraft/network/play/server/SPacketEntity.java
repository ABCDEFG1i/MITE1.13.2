package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntity implements Packet<INetHandlerPlayClient> {
   protected int entityId;
   protected int posX;
   protected int posY;
   protected int posZ;
   protected byte yaw;
   protected byte pitch;
   protected boolean onGround;
   protected boolean rotating;

   public SPacketEntity() {
   }

   public SPacketEntity(int p_i46936_1_) {
      this.entityId = p_i46936_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityMovement(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_149065_1_) {
      return p_149065_1_.getEntityByID(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public int getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isRotating() {
      return this.rotating;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getOnGround() {
      return this.onGround;
   }

   public static class Look extends SPacketEntity {
      public Look() {
         this.rotating = true;
      }

      public Look(int p_i47081_1_, byte p_i47081_2_, byte p_i47081_3_, boolean p_i47081_4_) {
         super(p_i47081_1_);
         this.yaw = p_i47081_2_;
         this.pitch = p_i47081_3_;
         this.rotating = true;
         this.onGround = p_i47081_4_;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         super.readPacketData(p_148837_1_);
         this.yaw = p_148837_1_.readByte();
         this.pitch = p_148837_1_.readByte();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         super.writePacketData(p_148840_1_);
         p_148840_1_.writeByte(this.yaw);
         p_148840_1_.writeByte(this.pitch);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }

   public static class Move extends SPacketEntity {
      public Move() {
         this.rotating = true;
      }

      public Move(int p_i47082_1_, long p_i47082_2_, long p_i47082_4_, long p_i47082_6_, byte p_i47082_8_, byte p_i47082_9_, boolean p_i47082_10_) {
         super(p_i47082_1_);
         this.posX = (int)p_i47082_2_;
         this.posY = (int)p_i47082_4_;
         this.posZ = (int)p_i47082_6_;
         this.yaw = p_i47082_8_;
         this.pitch = p_i47082_9_;
         this.onGround = p_i47082_10_;
         this.rotating = true;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         super.readPacketData(p_148837_1_);
         this.posX = p_148837_1_.readShort();
         this.posY = p_148837_1_.readShort();
         this.posZ = p_148837_1_.readShort();
         this.yaw = p_148837_1_.readByte();
         this.pitch = p_148837_1_.readByte();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         super.writePacketData(p_148840_1_);
         p_148840_1_.writeShort(this.posX);
         p_148840_1_.writeShort(this.posY);
         p_148840_1_.writeShort(this.posZ);
         p_148840_1_.writeByte(this.yaw);
         p_148840_1_.writeByte(this.pitch);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }

   public static class RelMove extends SPacketEntity {
      public RelMove() {
      }

      public RelMove(int p_i47083_1_, long p_i47083_2_, long p_i47083_4_, long p_i47083_6_, boolean p_i47083_8_) {
         super(p_i47083_1_);
         this.posX = (int)p_i47083_2_;
         this.posY = (int)p_i47083_4_;
         this.posZ = (int)p_i47083_6_;
         this.onGround = p_i47083_8_;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         super.readPacketData(p_148837_1_);
         this.posX = p_148837_1_.readShort();
         this.posY = p_148837_1_.readShort();
         this.posZ = p_148837_1_.readShort();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         super.writePacketData(p_148840_1_);
         p_148840_1_.writeShort(this.posX);
         p_148840_1_.writeShort(this.posY);
         p_148840_1_.writeShort(this.posZ);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }
}
