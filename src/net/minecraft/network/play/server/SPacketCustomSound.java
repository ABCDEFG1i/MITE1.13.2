package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCustomSound implements Packet<INetHandlerPlayClient> {
   private ResourceLocation soundName;
   private SoundCategory category;
   private int x;
   private int y = Integer.MAX_VALUE;
   private int z;
   private float volume;
   private float pitch;

   public SPacketCustomSound() {
   }

   public SPacketCustomSound(ResourceLocation p_i47939_1_, SoundCategory p_i47939_2_, Vec3d p_i47939_3_, float p_i47939_4_, float p_i47939_5_) {
      this.soundName = p_i47939_1_;
      this.category = p_i47939_2_;
      this.x = (int)(p_i47939_3_.x * 8.0D);
      this.y = (int)(p_i47939_3_.y * 8.0D);
      this.z = (int)(p_i47939_3_.z * 8.0D);
      this.volume = p_i47939_4_;
      this.pitch = p_i47939_5_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.soundName = p_148837_1_.readResourceLocation();
      this.category = p_148837_1_.readEnumValue(SoundCategory.class);
      this.x = p_148837_1_.readInt();
      this.y = p_148837_1_.readInt();
      this.z = p_148837_1_.readInt();
      this.volume = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeResourceLocation(this.soundName);
      p_148840_1_.writeEnumValue(this.category);
      p_148840_1_.writeInt(this.x);
      p_148840_1_.writeInt(this.y);
      p_148840_1_.writeInt(this.z);
      p_148840_1_.writeFloat(this.volume);
      p_148840_1_.writeFloat(this.pitch);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_197698_a() {
      return this.soundName;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundCategory getCategory() {
      return this.category;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return (double)((float)this.x / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return (double)((float)this.y / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return (double)((float)this.z / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getVolume() {
      return this.volume;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.pitch;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleCustomSound(this);
   }
}
