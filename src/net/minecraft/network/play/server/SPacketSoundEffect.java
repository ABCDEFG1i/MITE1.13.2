package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class SPacketSoundEffect implements Packet<INetHandlerPlayClient> {
   private SoundEvent sound;
   private SoundCategory category;
   private int posX;
   private int posY;
   private int posZ;
   private float soundVolume;
   private float soundPitch;

   public SPacketSoundEffect() {
   }

   public SPacketSoundEffect(SoundEvent p_i46896_1_, SoundCategory p_i46896_2_, double p_i46896_3_, double p_i46896_5_, double p_i46896_7_, float p_i46896_9_, float p_i46896_10_) {
      Validate.notNull(p_i46896_1_, "sound");
      this.sound = p_i46896_1_;
      this.category = p_i46896_2_;
      this.posX = (int)(p_i46896_3_ * 8.0D);
      this.posY = (int)(p_i46896_5_ * 8.0D);
      this.posZ = (int)(p_i46896_7_ * 8.0D);
      this.soundVolume = p_i46896_9_;
      this.soundPitch = p_i46896_10_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.sound = IRegistry.field_212633_v.func_148754_a(p_148837_1_.readVarInt());
      this.category = p_148837_1_.readEnumValue(SoundCategory.class);
      this.posX = p_148837_1_.readInt();
      this.posY = p_148837_1_.readInt();
      this.posZ = p_148837_1_.readInt();
      this.soundVolume = p_148837_1_.readFloat();
      this.soundPitch = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(IRegistry.field_212633_v.func_148757_b(this.sound));
      p_148840_1_.writeEnumValue(this.category);
      p_148840_1_.writeInt(this.posX);
      p_148840_1_.writeInt(this.posY);
      p_148840_1_.writeInt(this.posZ);
      p_148840_1_.writeFloat(this.soundVolume);
      p_148840_1_.writeFloat(this.soundPitch);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundCategory getCategory() {
      return this.category;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return (double)((float)this.posX / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return (double)((float)this.posY / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return (double)((float)this.posZ / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getVolume() {
      return this.soundVolume;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.soundPitch;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSoundEffect(this);
   }
}
