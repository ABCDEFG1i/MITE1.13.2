package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUpdateTileEntity implements Packet<INetHandlerPlayClient> {
   private BlockPos blockPos;
   private int tileEntityType;
   private NBTTagCompound nbt;

   public SPacketUpdateTileEntity() {
   }

   public SPacketUpdateTileEntity(BlockPos p_i46967_1_, int p_i46967_2_, NBTTagCompound p_i46967_3_) {
      this.blockPos = p_i46967_1_;
      this.tileEntityType = p_i46967_2_;
      this.nbt = p_i46967_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.blockPos = p_148837_1_.readBlockPos();
      this.tileEntityType = p_148837_1_.readUnsignedByte();
      this.nbt = p_148837_1_.readCompoundTag();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.blockPos);
      p_148840_1_.writeByte((byte)this.tileEntityType);
      p_148840_1_.writeCompoundTag(this.nbt);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleUpdateTileEntity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.blockPos;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTileEntityType() {
      return this.tileEntityType;
   }

   @OnlyIn(Dist.CLIENT)
   public NBTTagCompound getNbtCompound() {
      return this.nbt;
   }
}
