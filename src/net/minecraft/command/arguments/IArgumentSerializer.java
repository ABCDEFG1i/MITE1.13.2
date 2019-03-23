package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketBuffer;

public interface IArgumentSerializer<T extends ArgumentType<?>> {
   void write(T p_197072_1_, PacketBuffer p_197072_2_);

   T read(PacketBuffer p_197071_1_);

   void func_212244_a(T p_212244_1_, JsonObject p_212244_2_);
}
