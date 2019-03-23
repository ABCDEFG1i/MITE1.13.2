package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateCommandMinecart implements Packet<INetHandlerPlayServer> {
   private int field_210374_a;
   private String command;
   private boolean field_210376_c;

   public CPacketUpdateCommandMinecart() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateCommandMinecart(int p_i49542_1_, String p_i49542_2_, boolean p_i49542_3_) {
      this.field_210374_a = p_i49542_1_;
      this.command = p_i49542_2_;
      this.field_210376_c = p_i49542_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_210374_a = p_148837_1_.readVarInt();
      this.command = p_148837_1_.readString(32767);
      this.field_210376_c = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_210374_a);
      p_148840_1_.writeString(this.command);
      p_148840_1_.writeBoolean(this.field_210376_c);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processUpdateCommandMinecart(this);
   }

   @Nullable
   public CommandBlockBaseLogic getCommandBlock(World p_210371_1_) {
      Entity entity = p_210371_1_.getEntityByID(this.field_210374_a);
      return entity instanceof EntityMinecartCommandBlock ? ((EntityMinecartCommandBlock)entity).getCommandBlockLogic() : null;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean func_210373_b() {
      return this.field_210376_c;
   }
}
