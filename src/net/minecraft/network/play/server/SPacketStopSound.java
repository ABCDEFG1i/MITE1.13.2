package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketStopSound implements Packet<INetHandlerPlayClient> {
   private ResourceLocation field_197705_a;
   private SoundCategory category;

   public SPacketStopSound() {
   }

   public SPacketStopSound(@Nullable ResourceLocation p_i47929_1_, @Nullable SoundCategory p_i47929_2_) {
      this.field_197705_a = p_i47929_1_;
      this.category = p_i47929_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      int i = p_148837_1_.readByte();
      if ((i & 1) > 0) {
         this.category = p_148837_1_.readEnumValue(SoundCategory.class);
      }

      if ((i & 2) > 0) {
         this.field_197705_a = p_148837_1_.readResourceLocation();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      if (this.category != null) {
         if (this.field_197705_a != null) {
            p_148840_1_.writeByte(3);
            p_148840_1_.writeEnumValue(this.category);
            p_148840_1_.writeResourceLocation(this.field_197705_a);
         } else {
            p_148840_1_.writeByte(1);
            p_148840_1_.writeEnumValue(this.category);
         }
      } else if (this.field_197705_a != null) {
         p_148840_1_.writeByte(2);
         p_148840_1_.writeResourceLocation(this.field_197705_a);
      } else {
         p_148840_1_.writeByte(0);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_197703_a() {
      return this.field_197705_a;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public SoundCategory getCategory() {
      return this.category;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleStopSound(this);
   }
}
