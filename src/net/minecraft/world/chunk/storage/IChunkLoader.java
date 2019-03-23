package net.minecraft.world.chunk.storage;

import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.storage.SessionLockException;

public interface IChunkLoader {
   @Nullable
   Chunk loadChunk(IWorld p_199813_1_, int p_199813_2_, int p_199813_3_, Consumer<Chunk> p_199813_4_) throws IOException;

   @Nullable
   ChunkPrimer loadChunkPrimer(IWorld p_202152_1_, int p_202152_2_, int p_202152_3_, Consumer<IChunk> p_202152_4_) throws IOException;

   void saveChunk(World p_75816_1_, IChunk p_75816_2_) throws IOException, SessionLockException;

   void flush();
}
