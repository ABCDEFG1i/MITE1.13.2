package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.init.Particles;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketParticles implements Packet<INetHandlerPlayClient> {
   private float xCoord;
   private float yCoord;
   private float zCoord;
   private float xOffset;
   private float yOffset;
   private float zOffset;
   private float particleSpeed;
   private int particleCount;
   private boolean longDistance;
   private IParticleData field_197700_j;

   public SPacketParticles() {
   }

   public <T extends IParticleData> SPacketParticles(T p_i47932_1_, boolean p_i47932_2_, float p_i47932_3_, float p_i47932_4_, float p_i47932_5_, float p_i47932_6_, float p_i47932_7_, float p_i47932_8_, float p_i47932_9_, int p_i47932_10_) {
      this.field_197700_j = p_i47932_1_;
      this.longDistance = p_i47932_2_;
      this.xCoord = p_i47932_3_;
      this.yCoord = p_i47932_4_;
      this.zCoord = p_i47932_5_;
      this.xOffset = p_i47932_6_;
      this.yOffset = p_i47932_7_;
      this.zOffset = p_i47932_8_;
      this.particleSpeed = p_i47932_9_;
      this.particleCount = p_i47932_10_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      ParticleType<?> particletype = IRegistry.field_212632_u.func_148754_a(p_148837_1_.readInt());
      if (particletype == null) {
         particletype = Particles.BARRIER;
      }

      this.longDistance = p_148837_1_.readBoolean();
      this.xCoord = p_148837_1_.readFloat();
      this.yCoord = p_148837_1_.readFloat();
      this.zCoord = p_148837_1_.readFloat();
      this.xOffset = p_148837_1_.readFloat();
      this.yOffset = p_148837_1_.readFloat();
      this.zOffset = p_148837_1_.readFloat();
      this.particleSpeed = p_148837_1_.readFloat();
      this.particleCount = p_148837_1_.readInt();
      this.field_197700_j = this.func_199855_a(p_148837_1_, particletype);
   }

   private <T extends IParticleData> T func_199855_a(PacketBuffer p_199855_1_, ParticleType<T> p_199855_2_) {
      return p_199855_2_.getDeserializer().read(p_199855_2_, p_199855_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(IRegistry.field_212632_u.func_148757_b(this.field_197700_j.getType()));
      p_148840_1_.writeBoolean(this.longDistance);
      p_148840_1_.writeFloat(this.xCoord);
      p_148840_1_.writeFloat(this.yCoord);
      p_148840_1_.writeFloat(this.zCoord);
      p_148840_1_.writeFloat(this.xOffset);
      p_148840_1_.writeFloat(this.yOffset);
      p_148840_1_.writeFloat(this.zOffset);
      p_148840_1_.writeFloat(this.particleSpeed);
      p_148840_1_.writeInt(this.particleCount);
      this.field_197700_j.write(p_148840_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLongDistance() {
      return this.longDistance;
   }

   @OnlyIn(Dist.CLIENT)
   public double getXCoordinate() {
      return (double)this.xCoord;
   }

   @OnlyIn(Dist.CLIENT)
   public double getYCoordinate() {
      return (double)this.yCoord;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZCoordinate() {
      return (double)this.zCoord;
   }

   @OnlyIn(Dist.CLIENT)
   public float getXOffset() {
      return this.xOffset;
   }

   @OnlyIn(Dist.CLIENT)
   public float getYOffset() {
      return this.yOffset;
   }

   @OnlyIn(Dist.CLIENT)
   public float getZOffset() {
      return this.zOffset;
   }

   @OnlyIn(Dist.CLIENT)
   public float getParticleSpeed() {
      return this.particleSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public int getParticleCount() {
      return this.particleCount;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData func_197699_j() {
      return this.field_197700_j;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleParticles(this);
   }
}
