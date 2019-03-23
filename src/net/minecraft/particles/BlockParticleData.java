package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<BlockParticleData> DESERIALIZER = new IParticleData.IDeserializer<BlockParticleData>() {
      public BlockParticleData deserialize(ParticleType<BlockParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException {
         p_197544_2_.expect(' ');
         return new BlockParticleData(p_197544_1_, (new BlockStateParser(p_197544_2_, false)).parse(false).func_197249_b());
      }

      public BlockParticleData read(ParticleType<BlockParticleData> p_197543_1_, PacketBuffer p_197543_2_) {
         return new BlockParticleData(p_197543_1_, Block.BLOCK_STATE_IDS.getByValue(p_197543_2_.readVarInt()));
      }
   };
   private final ParticleType<BlockParticleData> particleType;
   private final IBlockState blockState;

   public BlockParticleData(ParticleType<BlockParticleData> p_i47953_1_, IBlockState p_i47953_2_) {
      this.particleType = p_i47953_1_;
      this.blockState = p_i47953_2_;
   }

   public void write(PacketBuffer p_197553_1_) {
      p_197553_1_.writeVarInt(Block.BLOCK_STATE_IDS.get(this.blockState));
   }

   public String getParameters() {
      return this.getType().getId() + " " + BlockStateParser.toString(this.blockState, (NBTTagCompound)null);
   }

   public ParticleType<BlockParticleData> getType() {
      return this.particleType;
   }

   @OnlyIn(Dist.CLIENT)
   public IBlockState func_197584_c() {
      return this.blockState;
   }
}
