package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;

public interface IParticleData {
   ParticleType<?> getType();

   void write(PacketBuffer p_197553_1_);

   String getParameters();

   public interface IDeserializer<T extends IParticleData> {
      T deserialize(ParticleType<T> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException;

      T read(ParticleType<T> p_197543_1_, PacketBuffer p_197543_2_);
   }
}
