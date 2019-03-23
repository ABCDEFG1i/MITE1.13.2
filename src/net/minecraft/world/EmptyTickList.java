package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyTickList<T> implements ITickList<T> {
   private static final EmptyTickList INSTANCE = new EmptyTickList();

   public static <T> EmptyTickList<T> get() {
      return INSTANCE;
   }

   public boolean isTickScheduled(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205360_1_, T p_205360_2_, int p_205360_3_) {
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
   }

   public boolean isTickPending(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }
}
