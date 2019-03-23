package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatisticsManager {
   protected final Object2IntMap<Stat<?>> statsData = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

   public StatisticsManager() {
      this.statsData.defaultReturnValue(0);
   }

   public void func_150871_b(EntityPlayer p_150871_1_, Stat<?> p_150871_2_, int p_150871_3_) {
      this.func_150873_a(p_150871_1_, p_150871_2_, this.func_77444_a(p_150871_2_) + p_150871_3_);
   }

   public void func_150873_a(EntityPlayer p_150873_1_, Stat<?> p_150873_2_, int p_150873_3_) {
      this.statsData.put(p_150873_2_, p_150873_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public <T> int getValue(StatType<T> p_199060_1_, T p_199060_2_) {
      return p_199060_1_.func_199079_a(p_199060_2_) ? this.func_77444_a(p_199060_1_.func_199076_b(p_199060_2_)) : 0;
   }

   public int func_77444_a(Stat<?> p_77444_1_) {
      return this.statsData.getInt(p_77444_1_);
   }
}
