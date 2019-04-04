package net.minecraft.world.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStatePaletteLinear<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T[] states;
   private final IBlockStatePaletteResizer<T> resizeHandler;
   private final Function<NBTTagCompound, T> deserializer;
   private final int bits;
   private int arraySize;

   public BlockStatePaletteLinear(ObjectIntIdentityMap<T> p_i48962_1_, int p_i48962_2_, IBlockStatePaletteResizer<T> p_i48962_3_, Function<NBTTagCompound, T> p_i48962_4_) {
      this.registry = p_i48962_1_;
      this.states = (T[])(new Object[1 << p_i48962_2_]);
      this.bits = p_i48962_2_;
      this.resizeHandler = p_i48962_3_;
      this.deserializer = p_i48962_4_;
   }

   public int idFor(T p_186041_1_) {
      for(int i = 0; i < this.arraySize; ++i) {
         if (this.states[i] == p_186041_1_) {
            return i;
         }
      }

      int j = this.arraySize;
      if (j < this.states.length) {
         this.states[j] = p_186041_1_;
         ++this.arraySize;
         return j;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, p_186041_1_);
      }
   }

   @Nullable
   public T getBlockState(int p_186039_1_) {
      return p_186039_1_ >= 0 && p_186039_1_ < this.arraySize ? this.states[p_186039_1_] : null;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
      this.arraySize = p_186038_1_.readVarInt();

      for(int i = 0; i < this.arraySize; ++i) {
         this.states[i] = this.registry.getByValue(p_186038_1_.readVarInt());
      }

   }

   public void write(PacketBuffer p_186037_1_) {
      p_186037_1_.writeVarInt(this.arraySize);

      for(int i = 0; i < this.arraySize; ++i) {
         p_186037_1_.writeVarInt(this.registry.get(this.states[i]));
      }

   }

   public int getSerializedSize() {
      int i = PacketBuffer.getVarIntSize(this.func_202137_b());

      for(int j = 0; j < this.func_202137_b(); ++j) {
         i += PacketBuffer.getVarIntSize(this.registry.get(this.states[j]));
      }

      return i;
   }

   public int func_202137_b() {
      return this.arraySize;
   }

   public void read(NBTTagList p_196968_1_) {
      for(int i = 0; i < p_196968_1_.size(); ++i) {
         this.states[i] = this.deserializer.apply(p_196968_1_.getCompoundTagAt(i));
      }

      this.arraySize = p_196968_1_.size();
   }
}
