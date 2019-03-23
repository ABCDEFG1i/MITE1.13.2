package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerLook implements Packet<INetHandlerPlayClient> {
   private double x;
   private double y;
   private double z;
   private int field_200535_d;
   private EntityAnchorArgument.Type field_201065_e;
   private EntityAnchorArgument.Type field_201066_f;
   private boolean field_200536_e;

   public SPacketPlayerLook() {
   }

   public SPacketPlayerLook(EntityAnchorArgument.Type p_i48589_1_, double p_i48589_2_, double p_i48589_4_, double p_i48589_6_) {
      this.field_201065_e = p_i48589_1_;
      this.x = p_i48589_2_;
      this.y = p_i48589_4_;
      this.z = p_i48589_6_;
   }

   public SPacketPlayerLook(EntityAnchorArgument.Type p_i48590_1_, Entity p_i48590_2_, EntityAnchorArgument.Type p_i48590_3_) {
      this.field_201065_e = p_i48590_1_;
      this.field_200535_d = p_i48590_2_.getEntityId();
      this.field_201066_f = p_i48590_3_;
      Vec3d vec3d = p_i48590_3_.func_201017_a(p_i48590_2_);
      this.x = vec3d.x;
      this.y = vec3d.y;
      this.z = vec3d.z;
      this.field_200536_e = true;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_201065_e = p_148837_1_.readEnumValue(EntityAnchorArgument.Type.class);
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      if (p_148837_1_.readBoolean()) {
         this.field_200536_e = true;
         this.field_200535_d = p_148837_1_.readVarInt();
         this.field_201066_f = p_148837_1_.readEnumValue(EntityAnchorArgument.Type.class);
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.field_201065_e);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeBoolean(this.field_200536_e);
      if (this.field_200536_e) {
         p_148840_1_.writeVarInt(this.field_200535_d);
         p_148840_1_.writeEnumValue(this.field_201066_f);
      }

   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handlePlayerLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityAnchorArgument.Type func_201064_a() {
      return this.field_201065_e;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d func_200531_a(World p_200531_1_) {
      if (this.field_200536_e) {
         Entity entity = p_200531_1_.getEntityByID(this.field_200535_d);
         return entity == null ? new Vec3d(this.x, this.y, this.z) : this.field_201066_f.func_201017_a(entity);
      } else {
         return new Vec3d(this.x, this.y, this.z);
      }
   }
}
