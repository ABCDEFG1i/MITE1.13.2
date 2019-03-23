package net.minecraft.util;

import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public interface ITaskType<K, T extends ITaskType<K, T>> {
   @Nullable
   T getPreviousTaskType();

   void acceptInRange(K p_201492_1_, BiConsumer<K, T> p_201492_2_);
}
