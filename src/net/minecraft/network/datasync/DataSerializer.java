package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;

public interface DataSerializer<T> {
   void write(PacketBuffer p_187160_1_, T p_187160_2_);

   T read(PacketBuffer p_187159_1_);

   DataParameter<T> createKey(int p_187161_1_);

   T copyValue(T p_192717_1_);
}
