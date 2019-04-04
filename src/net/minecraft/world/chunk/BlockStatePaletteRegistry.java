package net.minecraft.world.chunk;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStatePaletteRegistry<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T defaultState;

   public BlockStatePaletteRegistry(ObjectIntIdentityMap<T> p_i48965_1_, T p_i48965_2_) {
      this.registry = p_i48965_1_;
      this.defaultState = p_i48965_2_;
   }

   public int idFor(T p_186041_1_) {
      int i = this.registry.get(p_186041_1_);
      return i == -1 ? 0 : i;
   }

   public T getBlockState(int p_186039_1_) {
      T t = this.registry.getByValue(p_186039_1_);
      return t == null ? this.defaultState : t;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
   }

   public void write(PacketBuffer p_186037_1_) {
   }

   public int getSerializedSize() {
      return PacketBuffer.getVarIntSize(0);
   }

   public void read(NBTTagList p_196968_1_) {
   }
}
