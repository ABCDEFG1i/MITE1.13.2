package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class BasicParticleType extends ParticleType<BasicParticleType> implements IParticleData {
   private static final IParticleData.IDeserializer<BasicParticleType> DESERIALIZER = new IParticleData.IDeserializer<BasicParticleType>() {
      public BasicParticleType deserialize(ParticleType<BasicParticleType> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         return (BasicParticleType)p_197544_1_;
      }

      public BasicParticleType read(ParticleType<BasicParticleType> p_197543_1_, PacketBuffer p_197543_2_) {
         return (BasicParticleType)p_197543_1_;
      }
   };

   protected BasicParticleType(ResourceLocation p_i49347_1_, boolean p_i49347_2_) {
      super(p_i49347_1_, p_i49347_2_, DESERIALIZER);
   }

   public ParticleType<BasicParticleType> getType() {
      return this;
   }

   public void write(PacketBuffer p_197553_1_) {
   }

   public String getParameters() {
      return this.getId().toString();
   }
}
