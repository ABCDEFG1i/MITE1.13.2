package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketExplosion implements Packet<INetHandlerPlayClient> {
   private double posX;
   private double posY;
   private double posZ;
   private float strength;
   private List<BlockPos> affectedBlockPositions;
   private float motionX;
   private float motionY;
   private float motionZ;

   public SPacketExplosion() {
   }

   public SPacketExplosion(double p_i47099_1_, double p_i47099_3_, double p_i47099_5_, float p_i47099_7_, List<BlockPos> p_i47099_8_, Vec3d p_i47099_9_) {
      this.posX = p_i47099_1_;
      this.posY = p_i47099_3_;
      this.posZ = p_i47099_5_;
      this.strength = p_i47099_7_;
      this.affectedBlockPositions = Lists.newArrayList(p_i47099_8_);
      if (p_i47099_9_ != null) {
         this.motionX = (float)p_i47099_9_.x;
         this.motionY = (float)p_i47099_9_.y;
         this.motionZ = (float)p_i47099_9_.z;
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.posX = (double)p_148837_1_.readFloat();
      this.posY = (double)p_148837_1_.readFloat();
      this.posZ = (double)p_148837_1_.readFloat();
      this.strength = p_148837_1_.readFloat();
      int i = p_148837_1_.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(i);
      int j = (int)this.posX;
      int k = (int)this.posY;
      int l = (int)this.posZ;

      for(int i1 = 0; i1 < i; ++i1) {
         int j1 = p_148837_1_.readByte() + j;
         int k1 = p_148837_1_.readByte() + k;
         int l1 = p_148837_1_.readByte() + l;
         this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
      }

      this.motionX = p_148837_1_.readFloat();
      this.motionY = p_148837_1_.readFloat();
      this.motionZ = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat((float)this.posX);
      p_148840_1_.writeFloat((float)this.posY);
      p_148840_1_.writeFloat((float)this.posZ);
      p_148840_1_.writeFloat(this.strength);
      p_148840_1_.writeInt(this.affectedBlockPositions.size());
      int i = (int)this.posX;
      int j = (int)this.posY;
      int k = (int)this.posZ;

      for(BlockPos blockpos : this.affectedBlockPositions) {
         int l = blockpos.getX() - i;
         int i1 = blockpos.getY() - j;
         int j1 = blockpos.getZ() - k;
         p_148840_1_.writeByte(l);
         p_148840_1_.writeByte(i1);
         p_148840_1_.writeByte(j1);
      }

      p_148840_1_.writeFloat(this.motionX);
      p_148840_1_.writeFloat(this.motionY);
      p_148840_1_.writeFloat(this.motionZ);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleExplosion(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionX() {
      return this.motionX;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionY() {
      return this.motionY;
   }

   @OnlyIn(Dist.CLIENT)
   public float getMotionZ() {
      return this.motionZ;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public float getStrength() {
      return this.strength;
   }

   @OnlyIn(Dist.CLIENT)
   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
