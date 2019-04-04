package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPlayerDigging implements Packet<INetHandlerPlayServer> {
   private BlockPos position;
   private EnumFacing facing;
   private CPacketPlayerDigging.Action action;

   public CPacketPlayerDigging() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPlayerDigging(CPacketPlayerDigging.Action p_i46871_1_, BlockPos p_i46871_2_, EnumFacing p_i46871_3_) {
      this.action = p_i46871_1_;
      this.position = p_i46871_2_;
      this.facing = p_i46871_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnumValue(CPacketPlayerDigging.Action.class);
      this.position = p_148837_1_.readBlockPos();
      this.facing = EnumFacing.byIndex(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
      p_148840_1_.writeBlockPos(this.position);
      p_148840_1_.writeByte(this.facing.getIndex());
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processPlayerDigging(this);
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public EnumFacing getFacing() {
      return this.facing;
   }

   public CPacketPlayerDigging.Action getAction() {
      return this.action;
   }

   public enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_HELD_ITEMS
   }
}
