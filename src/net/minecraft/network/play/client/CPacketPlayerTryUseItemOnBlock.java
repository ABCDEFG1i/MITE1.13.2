package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPlayerTryUseItemOnBlock implements Packet<INetHandlerPlayServer> {
   private BlockPos position;
   private EnumFacing placedBlockDirection;
   private EnumHand hand;
   private float facingX;
   private float facingY;
   private float facingZ;

   public CPacketPlayerTryUseItemOnBlock() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPlayerTryUseItemOnBlock(BlockPos p_i46858_1_, EnumFacing p_i46858_2_, EnumHand p_i46858_3_, float p_i46858_4_, float p_i46858_5_, float p_i46858_6_) {
      this.position = p_i46858_1_;
      this.placedBlockDirection = p_i46858_2_;
      this.hand = p_i46858_3_;
      this.facingX = p_i46858_4_;
      this.facingY = p_i46858_5_;
      this.facingZ = p_i46858_6_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.position = p_148837_1_.readBlockPos();
      this.placedBlockDirection = p_148837_1_.readEnumValue(EnumFacing.class);
      this.hand = p_148837_1_.readEnumValue(EnumHand.class);
      this.facingX = p_148837_1_.readFloat();
      this.facingY = p_148837_1_.readFloat();
      this.facingZ = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.position);
      p_148840_1_.writeEnumValue(this.placedBlockDirection);
      p_148840_1_.writeEnumValue(this.hand);
      p_148840_1_.writeFloat(this.facingX);
      p_148840_1_.writeFloat(this.facingY);
      p_148840_1_.writeFloat(this.facingZ);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processTryUseItemOnBlock(this);
   }

   public BlockPos getPos() {
      return this.position;
   }

   public EnumFacing getDirection() {
      return this.placedBlockDirection;
   }

   public EnumHand getHand() {
      return this.hand;
   }

   public float getFacingX() {
      return this.facingX;
   }

   public float getFacingY() {
      return this.facingY;
   }

   public float getFacingZ() {
      return this.facingZ;
   }
}
