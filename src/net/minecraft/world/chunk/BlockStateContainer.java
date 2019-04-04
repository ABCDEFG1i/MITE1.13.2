package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStateContainer<T> implements IBlockStatePaletteResizer<T> {
   private final IBlockStatePalette<T> field_205521_b;
   private final IBlockStatePaletteResizer<T> field_205522_c = (p_205517_0_, p_205517_1_) -> {
      return 0;
   };
   private final ObjectIntIdentityMap<T> registry;
   private final Function<NBTTagCompound, T> deserializer;
   private final Function<T, NBTTagCompound> serializer;
   private final T defaultState;
   protected BitArray storage;
   private IBlockStatePalette<T> palette;
   private int bits;
   private final ReentrantLock lock = new ReentrantLock();

   private void lock() {
      if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
         String s = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((p_210458_0_) -> {
            return p_210458_0_.getName() + ": \n\tat " + Arrays.stream(p_210458_0_.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
         }).collect(Collectors.joining("\n"));
         CrashReport crashreport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Thread dumps");
         crashreportcategory.addCrashSection("Thread dumps", s);
         throw new ReportedException(crashreport);
      } else {
         this.lock.lock();
      }
   }

   private void unlock() {
      this.lock.unlock();
   }

   public BlockStateContainer(IBlockStatePalette<T> p_i48961_1_, ObjectIntIdentityMap<T> p_i48961_2_, Function<NBTTagCompound, T> p_i48961_3_, Function<T, NBTTagCompound> p_i48961_4_, T p_i48961_5_) {
      this.field_205521_b = p_i48961_1_;
      this.registry = p_i48961_2_;
      this.deserializer = p_i48961_3_;
      this.serializer = p_i48961_4_;
      this.defaultState = p_i48961_5_;
      this.setBits(4);
   }

   private static int getIndex(int p_186011_0_, int p_186011_1_, int p_186011_2_) {
      return p_186011_1_ << 8 | p_186011_2_ << 4 | p_186011_0_;
   }

   private void setBits(int p_186012_1_) {
      if (p_186012_1_ != this.bits) {
         this.bits = p_186012_1_;
         if (this.bits <= 4) {
            this.bits = 4;
            this.palette = new BlockStatePaletteLinear<>(this.registry, this.bits, this, this.deserializer);
         } else if (this.bits < 9) {
            this.palette = new BlockStatePaletteHashMap<>(this.registry, this.bits, this, this.deserializer, this.serializer);
         } else {
            this.palette = this.field_205521_b;
            this.bits = MathHelper.log2DeBruijn(this.registry.size());
         }

         this.palette.idFor(this.defaultState);
         this.storage = new BitArray(this.bits, 4096);
      }
   }

   public int onResize(int p_onResize_1_, T p_onResize_2_) {
      this.lock();
      BitArray bitarray = this.storage;
      IBlockStatePalette<T> iblockstatepalette = this.palette;
      this.setBits(p_onResize_1_);

      for(int i = 0; i < bitarray.size(); ++i) {
         T t = iblockstatepalette.getBlockState(bitarray.getAt(i));
         if (t != null) {
            this.set(i, t);
         }
      }

      int j = this.palette.idFor(p_onResize_2_);
      this.unlock();
      return j;
   }

   public void set(int p_186013_1_, int p_186013_2_, int p_186013_3_, T p_186013_4_) {
      this.lock();
      this.set(getIndex(p_186013_1_, p_186013_2_, p_186013_3_), p_186013_4_);
      this.unlock();
   }

   protected void set(int p_186014_1_, T p_186014_2_) {
      int i = this.palette.idFor(p_186014_2_);
      this.storage.setAt(p_186014_1_, i);
   }

   public T get(int p_186016_1_, int p_186016_2_, int p_186016_3_) {
      return this.get(getIndex(p_186016_1_, p_186016_2_, p_186016_3_));
   }

   protected T get(int p_186015_1_) {
      T t = this.palette.getBlockState(this.storage.getAt(p_186015_1_));
      return t == null ? this.defaultState : t;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186010_1_) {
      this.lock();
      int i = p_186010_1_.readByte();
      if (this.bits != i) {
         this.setBits(i);
      }

      this.palette.read(p_186010_1_);
      p_186010_1_.readLongArray(this.storage.getBackingLongArray());
      this.unlock();
   }

   public void write(PacketBuffer p_186009_1_) {
      this.lock();
      p_186009_1_.writeByte(this.bits);
      this.palette.write(p_186009_1_);
      p_186009_1_.writeLongArray(this.storage.getBackingLongArray());
      this.unlock();
   }

   public void readBlockStates(NBTTagCompound p_196964_1_, String p_196964_2_, String p_196964_3_) {
      this.lock();
      NBTTagList nbttaglist = p_196964_1_.getTagList(p_196964_2_, 10);
      int i = Math.max(4, MathHelper.log2DeBruijn(nbttaglist.size()));
      if (i != this.bits) {
         this.setBits(i);
      }

      this.palette.read(nbttaglist);
      long[] along = p_196964_1_.readLongArray(p_196964_3_);
      int j = along.length * 64 / 4096;
      if (this.palette == this.field_205521_b) {
         IBlockStatePalette<T> iblockstatepalette = new BlockStatePaletteHashMap<>(this.registry, i, this.field_205522_c, this.deserializer, this.serializer);
         iblockstatepalette.read(nbttaglist);
         BitArray bitarray = new BitArray(i, 4096, along);

         for(int k = 0; k < 4096; ++k) {
            this.storage.setAt(k, this.field_205521_b.idFor(iblockstatepalette.getBlockState(bitarray.getAt(k))));
         }
      } else if (j == this.bits) {
         System.arraycopy(along, 0, this.storage.getBackingLongArray(), 0, along.length);
      } else {
         BitArray bitarray1 = new BitArray(j, 4096, along);

         for(int l = 0; l < 4096; ++l) {
            this.storage.setAt(l, bitarray1.getAt(l));
         }
      }

      this.unlock();
   }

   public void writeChunkPalette(NBTTagCompound p_196963_1_, String p_196963_2_, String p_196963_3_) {
      this.lock();
      BlockStatePaletteHashMap<T> blockstatepalettehashmap = new BlockStatePaletteHashMap<>(this.registry, this.bits, this.field_205522_c, this.deserializer, this.serializer);
      blockstatepalettehashmap.idFor(this.defaultState);
      int[] aint = new int[4096];

      for(int i = 0; i < 4096; ++i) {
         aint[i] = blockstatepalettehashmap.idFor(this.get(i));
      }

      NBTTagList nbttaglist = new NBTTagList();
      blockstatepalettehashmap.writePaletteToList(nbttaglist);
      p_196963_1_.setTag(p_196963_2_, nbttaglist);
      int j = Math.max(4, MathHelper.log2DeBruijn(nbttaglist.size()));
      BitArray bitarray = new BitArray(j, 4096);

      for(int k = 0; k < aint.length; ++k) {
         bitarray.setAt(k, aint[k]);
      }

      p_196963_1_.setLongArray(p_196963_3_, bitarray.getBackingLongArray());
      this.unlock();
   }

   public int getSerializedSize() {
      return 1 + this.palette.getSerializedSize() + PacketBuffer.getVarIntSize(this.storage.size()) + this.storage.getBackingLongArray().length * 8;
   }
}
