package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUpdateRecipes implements Packet<INetHandlerPlayClient> {
   private List<IRecipe> field_199617_a;

   public SPacketUpdateRecipes() {
   }

   public SPacketUpdateRecipes(Collection<IRecipe> p_i48176_1_) {
      this.field_199617_a = Lists.newArrayList(p_i48176_1_);
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.func_199525_a(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_199617_a = Lists.newArrayList();
      int i = p_148837_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.field_199617_a.add(RecipeSerializers.read(p_148837_1_));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_199617_a.size());

      for(IRecipe irecipe : this.field_199617_a) {
         RecipeSerializers.write(irecipe, p_148840_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<IRecipe> func_199616_a() {
      return this.field_199617_a;
   }
}
