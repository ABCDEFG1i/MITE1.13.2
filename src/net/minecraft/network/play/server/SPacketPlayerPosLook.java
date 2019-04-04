package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerPosLook implements Packet<INetHandlerPlayClient> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;
   private Set<SPacketPlayerPosLook.EnumFlags> flags;
   private int teleportId;

   public SPacketPlayerPosLook() {
   }

   public SPacketPlayerPosLook(double p_i46928_1_, double p_i46928_3_, double p_i46928_5_, float p_i46928_7_, float p_i46928_8_, Set<SPacketPlayerPosLook.EnumFlags> p_i46928_9_, int p_i46928_10_) {
      this.x = p_i46928_1_;
      this.y = p_i46928_3_;
      this.z = p_i46928_5_;
      this.yaw = p_i46928_7_;
      this.pitch = p_i46928_8_;
      this.flags = p_i46928_9_;
      this.teleportId = p_i46928_10_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
      this.flags = SPacketPlayerPosLook.EnumFlags.unpack(p_148837_1_.readUnsignedByte());
      this.teleportId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeFloat(this.yaw);
      p_148840_1_.writeFloat(this.pitch);
      p_148840_1_.writeByte(SPacketPlayerPosLook.EnumFlags.pack(this.flags));
      p_148840_1_.writeVarInt(this.teleportId);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handlePlayerPosLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public float getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTeleportId() {
      return this.teleportId;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<SPacketPlayerPosLook.EnumFlags> getFlags() {
      return this.flags;
   }

   public enum EnumFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private final int bit;

      EnumFlags(int p_i46690_3_) {
         this.bit = p_i46690_3_;
      }

      private int getMask() {
         return 1 << this.bit;
      }

      private boolean isSet(int p_187043_1_) {
         return (p_187043_1_ & this.getMask()) == this.getMask();
      }

      public static Set<SPacketPlayerPosLook.EnumFlags> unpack(int p_187044_0_) {
         Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);

         for(SPacketPlayerPosLook.EnumFlags spacketplayerposlook$enumflags : values()) {
            if (spacketplayerposlook$enumflags.isSet(p_187044_0_)) {
               set.add(spacketplayerposlook$enumflags);
            }
         }

         return set;
      }

      public static int pack(Set<SPacketPlayerPosLook.EnumFlags> p_187040_0_) {
         int i = 0;

         for(SPacketPlayerPosLook.EnumFlags spacketplayerposlook$enumflags : p_187040_0_) {
            i |= spacketplayerposlook$enumflags.getMask();
         }

         return i;
      }
   }
}
