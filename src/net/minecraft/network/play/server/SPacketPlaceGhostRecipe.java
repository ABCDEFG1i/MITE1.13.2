package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient> {
   private int field_194314_a;
   private ResourceLocation recipe;

   public SPacketPlaceGhostRecipe() {
   }

   public SPacketPlaceGhostRecipe(int p_i47615_1_, IRecipe p_i47615_2_) {
      this.field_194314_a = p_i47615_1_;
      this.recipe = p_i47615_2_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_199615_a() {
      return this.recipe;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_194313_b() {
      return this.field_194314_a;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_194314_a = p_148837_1_.readByte();
      this.recipe = p_148837_1_.readResourceLocation();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.field_194314_a);
      p_148840_1_.writeResourceLocation(this.recipe);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handlePlaceGhostRecipe(this);
   }
}
