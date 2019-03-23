package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUseBed implements Packet<INetHandlerPlayClient> {
   private int playerID;
   private BlockPos bedPos;

   public SPacketUseBed() {
   }

   public SPacketUseBed(EntityPlayer p_i46927_1_, BlockPos p_i46927_2_) {
      this.playerID = p_i46927_1_.getEntityId();
      this.bedPos = p_i46927_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.playerID = p_148837_1_.readVarInt();
      this.bedPos = p_148837_1_.readBlockPos();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.playerID);
      p_148840_1_.writeBlockPos(this.bedPos);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleUseBed(this);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityPlayer getPlayer(World p_149091_1_) {
      return (EntityPlayer)p_149091_1_.getEntityByID(this.playerID);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getBedPosition() {
      return this.bedPos;
   }
}
