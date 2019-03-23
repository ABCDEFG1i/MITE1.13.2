package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketNBTQueryResponse implements Packet<INetHandlerPlayClient> {
   private int field_211714_a;
   @Nullable
   private NBTTagCompound tag;

   public SPacketNBTQueryResponse() {
   }

   public SPacketNBTQueryResponse(int p_i49757_1_, @Nullable NBTTagCompound p_i49757_2_) {
      this.field_211714_a = p_i49757_1_;
      this.tag = p_i49757_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_211714_a = p_148837_1_.readVarInt();
      this.tag = p_148837_1_.readCompoundTag();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_211714_a);
      p_148840_1_.writeCompoundTag(this.tag);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleNBTQueryResponse(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_211713_b() {
      return this.field_211714_a;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public NBTTagCompound getTag() {
      return this.tag;
   }

   public boolean shouldSkipErrors() {
      return true;
   }
}
