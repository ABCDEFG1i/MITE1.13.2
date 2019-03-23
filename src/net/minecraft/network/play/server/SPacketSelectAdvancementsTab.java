package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSelectAdvancementsTab implements Packet<INetHandlerPlayClient> {
   @Nullable
   private ResourceLocation tab;

   public SPacketSelectAdvancementsTab() {
   }

   public SPacketSelectAdvancementsTab(@Nullable ResourceLocation p_i47596_1_) {
      this.tab = p_i47596_1_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleSelectAdvancementsTab(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      if (p_148837_1_.readBoolean()) {
         this.tab = p_148837_1_.readResourceLocation();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.tab != null);
      if (this.tab != null) {
         p_148840_1_.writeResourceLocation(this.tab);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTab() {
      return this.tab;
   }
}
