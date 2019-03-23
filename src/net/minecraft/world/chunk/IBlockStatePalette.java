package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockStatePalette<T> {
   int idFor(T p_186041_1_);

   @Nullable
   T getBlockState(int p_186039_1_);

   @OnlyIn(Dist.CLIENT)
   void read(PacketBuffer p_186038_1_);

   void write(PacketBuffer p_186037_1_);

   int getSerializedSize();

   void read(NBTTagList p_196968_1_);
}
