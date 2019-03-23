package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerAbilities implements Packet<INetHandlerPlayClient> {
   private boolean invulnerable;
   private boolean flying;
   private boolean allowFlying;
   private boolean creativeMode;
   private float flySpeed;
   private float walkSpeed;

   public SPacketPlayerAbilities() {
   }

   public SPacketPlayerAbilities(PlayerCapabilities p_i46933_1_) {
      this.setInvulnerable(p_i46933_1_.disableDamage);
      this.setFlying(p_i46933_1_.isFlying);
      this.setAllowFlying(p_i46933_1_.allowFlying);
      this.setCreativeMode(p_i46933_1_.isCreativeMode);
      this.setFlySpeed(p_i46933_1_.getFlySpeed());
      this.setWalkSpeed(p_i46933_1_.getWalkSpeed());
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      byte b0 = p_148837_1_.readByte();
      this.setInvulnerable((b0 & 1) > 0);
      this.setFlying((b0 & 2) > 0);
      this.setAllowFlying((b0 & 4) > 0);
      this.setCreativeMode((b0 & 8) > 0);
      this.setFlySpeed(p_148837_1_.readFloat());
      this.setWalkSpeed(p_148837_1_.readFloat());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      byte b0 = 0;
      if (this.isInvulnerable()) {
         b0 = (byte)(b0 | 1);
      }

      if (this.isFlying()) {
         b0 = (byte)(b0 | 2);
      }

      if (this.isAllowFlying()) {
         b0 = (byte)(b0 | 4);
      }

      if (this.isCreativeMode()) {
         b0 = (byte)(b0 | 8);
      }

      p_148840_1_.writeByte(b0);
      p_148840_1_.writeFloat(this.flySpeed);
      p_148840_1_.writeFloat(this.walkSpeed);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean p_149108_1_) {
      this.invulnerable = p_149108_1_;
   }

   public boolean isFlying() {
      return this.flying;
   }

   public void setFlying(boolean p_149102_1_) {
      this.flying = p_149102_1_;
   }

   public boolean isAllowFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean p_149109_1_) {
      this.allowFlying = p_149109_1_;
   }

   public boolean isCreativeMode() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean p_149111_1_) {
      this.creativeMode = p_149111_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float p_149104_1_) {
      this.flySpeed = p_149104_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setWalkSpeed(float p_149110_1_) {
      this.walkSpeed = p_149110_1_;
   }
}
