package net.minecraft.network.play.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketStatistics implements Packet<INetHandlerPlayClient> {
   private Object2IntMap<Stat<?>> statisticMap;

   public SPacketStatistics() {
   }

   public SPacketStatistics(Object2IntMap<Stat<?>> p_i47942_1_) {
      this.statisticMap = p_i47942_1_;
   }

   public void processPacket(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.handleStatistics(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      int i = p_148837_1_.readVarInt();
      this.statisticMap = new Object2IntOpenHashMap<>(i);

      for(int j = 0; j < i; ++j) {
         this.func_197684_a(IRegistry.field_212634_w.func_148754_a(p_148837_1_.readVarInt()), p_148837_1_);
      }

   }

   private <T> void func_197684_a(StatType<T> p_197684_1_, PacketBuffer p_197684_2_) {
      int i = p_197684_2_.readVarInt();
      int j = p_197684_2_.readVarInt();
      this.statisticMap.put(p_197684_1_.func_199076_b(p_197684_1_.func_199080_a().func_148754_a(i)), j);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.statisticMap.size());

      for(Entry<Stat<?>> entry : this.statisticMap.object2IntEntrySet()) {
         Stat<?> stat = entry.getKey();
         p_148840_1_.writeVarInt(IRegistry.field_212634_w.func_148757_b(stat.func_197921_a()));
         p_148840_1_.writeVarInt(this.func_197683_a(stat));
         p_148840_1_.writeVarInt(entry.getIntValue());
      }

   }

   private <T> int func_197683_a(Stat<T> p_197683_1_) {
      return p_197683_1_.func_197921_a().func_199080_a().func_148757_b(p_197683_1_.func_197920_b());
   }

   @OnlyIn(Dist.CLIENT)
   public Map<Stat<?>, Integer> getStatisticMap() {
      return this.statisticMap;
   }
}
