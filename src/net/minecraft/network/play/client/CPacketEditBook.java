package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketEditBook implements Packet<INetHandlerPlayServer> {
   private ItemStack field_210347_a;
   private boolean field_210348_b;
   private EnumHand field_212645_c;

   public CPacketEditBook() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEditBook(ItemStack p_i49823_1_, boolean p_i49823_2_, EnumHand p_i49823_3_) {
      this.field_210347_a = p_i49823_1_.copy();
      this.field_210348_b = p_i49823_2_;
      this.field_212645_c = p_i49823_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_210347_a = p_148837_1_.readItemStack();
      this.field_210348_b = p_148837_1_.readBoolean();
      this.field_212645_c = p_148837_1_.readEnumValue(EnumHand.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeItemStack(this.field_210347_a);
      p_148840_1_.writeBoolean(this.field_210348_b);
      p_148840_1_.writeEnumValue(this.field_212645_c);
   }

   public void processPacket(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.processEditBook(this);
   }

   public ItemStack func_210346_a() {
      return this.field_210347_a;
   }

   public boolean func_210345_b() {
      return this.field_210348_b;
   }

   public EnumHand func_212644_d() {
      return this.field_212645_c;
   }
}
