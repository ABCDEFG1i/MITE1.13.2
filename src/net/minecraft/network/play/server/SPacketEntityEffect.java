package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityEffect implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private byte effectId;
   private byte amplifier;
   private int duration;
   private byte flags;

   public SPacketEntityEffect() {
   }

   public SPacketEntityEffect(int p_i46891_1_, PotionEffect p_i46891_2_) {
      this.entityId = p_i46891_1_;
      this.effectId = (byte)(Potion.getIdFromPotion(p_i46891_2_.getPotion()) & 255);
      this.amplifier = (byte)(p_i46891_2_.getAmplifier() & 255);
      if (p_i46891_2_.getDuration() > 32767) {
         this.duration = 32767;
      } else {
         this.duration = p_i46891_2_.getDuration();
      }

      this.flags = 0;
      if (p_i46891_2_.isAmbient()) {
         this.flags = (byte)(this.flags | 1);
      }

      if (p_i46891_2_.doesShowParticles()) {
         this.flags = (byte)(this.flags | 2);
      }

      if (p_i46891_2_.func_205348_f()) {
         this.flags = (byte)(this.flags | 4);
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.effectId = p_148837_1_.readByte();
      this.amplifier = p_148837_1_.readByte();
      this.duration = p_148837_1_.readVarInt();
      this.flags = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(this.effectId);
      p_148840_1_.writeByte(this.amplifier);
      p_148840_1_.writeVarInt(this.duration);
      p_148840_1_.writeByte(this.flags);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isMaxDuration() {
      return this.duration == 32767;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleEntityEffect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getEffectId() {
      return this.effectId;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getAmplifier() {
      return this.amplifier;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDuration() {
      return this.duration;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean doesShowParticles() {
      return (this.flags & 2) == 2;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getIsAmbient() {
      return (this.flags & 1) == 1;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_205527_h() {
      return (this.flags & 4) == 4;
   }
}
