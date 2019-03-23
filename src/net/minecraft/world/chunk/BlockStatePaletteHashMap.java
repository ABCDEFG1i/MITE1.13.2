package net.minecraft.world.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStatePaletteHashMap<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final IntIdentityHashBiMap<T> statePaletteMap;
   private final IBlockStatePaletteResizer<T> paletteResizer;
   private final Function<NBTTagCompound, T> deserializer;
   private final Function<T, NBTTagCompound> serializer;
   private final int bits;

   public BlockStatePaletteHashMap(ObjectIntIdentityMap<T> p_i48964_1_, int p_i48964_2_, IBlockStatePaletteResizer<T> p_i48964_3_, Function<NBTTagCompound, T> p_i48964_4_, Function<T, NBTTagCompound> p_i48964_5_) {
      this.registry = p_i48964_1_;
      this.bits = p_i48964_2_;
      this.paletteResizer = p_i48964_3_;
      this.deserializer = p_i48964_4_;
      this.serializer = p_i48964_5_;
      this.statePaletteMap = new IntIdentityHashBiMap<>(1 << p_i48964_2_);
   }

   public int idFor(T p_186041_1_) {
      int i = this.statePaletteMap.getId(p_186041_1_);
      if (i == -1) {
         i = this.statePaletteMap.add(p_186041_1_);
         if (i >= 1 << this.bits) {
            i = this.paletteResizer.onResize(this.bits + 1, p_186041_1_);
         }
      }

      return i;
   }

   @Nullable
   public T getBlockState(int p_186039_1_) {
      return this.statePaletteMap.get(p_186039_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
      this.statePaletteMap.clear();
      int i = p_186038_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.statePaletteMap.add(this.registry.getByValue(p_186038_1_.readVarInt()));
      }

   }

   public void write(PacketBuffer p_186037_1_) {
      int i = this.getPaletteSize();
      p_186037_1_.writeVarInt(i);

      for(int j = 0; j < i; ++j) {
         p_186037_1_.writeVarInt(this.registry.get(this.statePaletteMap.get(j)));
      }

   }

   public int getSerializedSize() {
      int i = PacketBuffer.getVarIntSize(this.getPaletteSize());

      for(int j = 0; j < this.getPaletteSize(); ++j) {
         i += PacketBuffer.getVarIntSize(this.registry.get(this.statePaletteMap.get(j)));
      }

      return i;
   }

   public int getPaletteSize() {
      return this.statePaletteMap.size();
   }

   public void read(NBTTagList p_196968_1_) {
      this.statePaletteMap.clear();

      for(int i = 0; i < p_196968_1_.size(); ++i) {
         this.statePaletteMap.add(this.deserializer.apply(p_196968_1_.getCompoundTagAt(i)));
      }

   }

   public void writePaletteToList(NBTTagList p_196969_1_) {
      for(int i = 0; i < this.getPaletteSize(); ++i) {
         p_196969_1_.add(this.serializer.apply(this.statePaletteMap.get(i)));
      }

   }
}
