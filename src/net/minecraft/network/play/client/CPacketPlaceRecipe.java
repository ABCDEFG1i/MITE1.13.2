package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPlaceRecipe implements Packet<INetHandlerPlayServer> {
   private int field_194320_a;
   private ResourceLocation field_194321_b;
   private boolean field_194322_c;

   public CPacketPlaceRecipe() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPlaceRecipe(int p_i47614_1_, IRecipe p_i47614_2_, boolean p_i47614_3_) {
      this.field_194320_a = p_i47614_1_;
      this.field_194321_b = p_i47614_2_.getId();
      this.field_194322_c = p_i47614_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_194320_a = p_148837_1_.readByte();
      this.field_194321_b = p_148837_1_.readResourceLocation();
      this.field_194322_c = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.field_194320_a);
      p_148840_1_.writeResourceLocation(this.field_194321_b);
      p_148840_1_.writeBoolean(this.field_194322_c);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processPlaceRecipe(this);
   }

   public int func_194318_a() {
      return this.field_194320_a;
   }

   public ResourceLocation func_199618_b() {
      return this.field_194321_b;
   }

   public boolean func_194319_c() {
      return this.field_194322_c;
   }
}
