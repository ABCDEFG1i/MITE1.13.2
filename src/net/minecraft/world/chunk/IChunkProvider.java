package net.minecraft.world.chunk;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.world.gen.IChunkGenerator;

public interface IChunkProvider extends AutoCloseable {
   @Nullable
   Chunk func_186025_d(int p_186025_1_, int p_186025_2_, boolean p_186025_3_, boolean p_186025_4_);

   @Nullable
   default IChunk func_201713_d(int p_201713_1_, int p_201713_2_, boolean p_201713_3_) {
      Chunk chunk = this.func_186025_d(p_201713_1_, p_201713_2_, true, false);
      if (chunk == null && p_201713_3_) {
         throw new UnsupportedOperationException("Could not create an empty chunk");
      } else {
         return chunk;
      }
   }

   boolean func_73156_b(BooleanSupplier p_73156_1_);

   String makeString();

   IChunkGenerator<?> getChunkGenerator();

   default void close() {
   }
}
