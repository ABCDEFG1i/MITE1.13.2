package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateCommandBlock implements Packet<INetHandlerPlayServer> {
   private BlockPos pos;
   private String command;
   private boolean field_210367_c;
   private boolean conditional;
   private boolean field_210369_e;
   private TileEntityCommandBlock.Mode mode;

   public CPacketUpdateCommandBlock() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateCommandBlock(BlockPos p_i49543_1_, String p_i49543_2_, TileEntityCommandBlock.Mode p_i49543_3_, boolean p_i49543_4_, boolean p_i49543_5_, boolean p_i49543_6_) {
      this.pos = p_i49543_1_;
      this.command = p_i49543_2_;
      this.field_210367_c = p_i49543_4_;
      this.conditional = p_i49543_5_;
      this.field_210369_e = p_i49543_6_;
      this.mode = p_i49543_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.command = p_148837_1_.readString(32767);
      this.mode = p_148837_1_.readEnumValue(TileEntityCommandBlock.Mode.class);
      int i = p_148837_1_.readByte();
      this.field_210367_c = (i & 1) != 0;
      this.conditional = (i & 2) != 0;
      this.field_210369_e = (i & 4) != 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeString(this.command);
      p_148840_1_.writeEnumValue(this.mode);
      int i = 0;
      if (this.field_210367_c) {
         i |= 1;
      }

      if (this.conditional) {
         i |= 2;
      }

      if (this.field_210369_e) {
         i |= 4;
      }

      p_148840_1_.writeByte(i);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processUpdateCommandBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean func_210363_c() {
      return this.field_210367_c;
   }

   public boolean isConditional() {
      return this.conditional;
   }

   public boolean func_210362_e() {
      return this.field_210369_e;
   }

   public TileEntityCommandBlock.Mode getMode() {
      return this.mode;
   }
}
