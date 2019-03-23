package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTUtil;

public class ChunkSection {
   private static final IBlockStatePalette<IBlockState> field_205512_a = new BlockStatePaletteRegistry<>(Block.BLOCK_STATE_IDS, Blocks.AIR.getDefaultState());
   private final int yBase;
   private int blockRefCount;
   private int tickRefCount;
   private int fluidRefCount;
   private final BlockStateContainer<IBlockState> data;
   private NibbleArray blockLight;
   private NibbleArray skyLight;

   public ChunkSection(int p_i1997_1_, boolean p_i1997_2_) {
      this.yBase = p_i1997_1_;
      this.data = new BlockStateContainer<>(field_205512_a, Block.BLOCK_STATE_IDS, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.getDefaultState());
      this.blockLight = new NibbleArray();
      if (p_i1997_2_) {
         this.skyLight = new NibbleArray();
      }

   }

   public IBlockState get(int p_177485_1_, int p_177485_2_, int p_177485_3_) {
      return this.data.get(p_177485_1_, p_177485_2_, p_177485_3_);
   }

   public IFluidState func_206914_b(int p_206914_1_, int p_206914_2_, int p_206914_3_) {
      return this.data.get(p_206914_1_, p_206914_2_, p_206914_3_).getFluidState();
   }

   public void set(int p_177484_1_, int p_177484_2_, int p_177484_3_, IBlockState p_177484_4_) {
      IBlockState iblockstate = this.get(p_177484_1_, p_177484_2_, p_177484_3_);
      IFluidState ifluidstate = this.func_206914_b(p_177484_1_, p_177484_2_, p_177484_3_);
      IFluidState ifluidstate1 = p_177484_4_.getFluidState();
      if (!iblockstate.isAir()) {
         --this.blockRefCount;
         if (iblockstate.needsRandomTick()) {
            --this.tickRefCount;
         }
      }

      if (!ifluidstate.isEmpty()) {
         --this.fluidRefCount;
      }

      if (!p_177484_4_.isAir()) {
         ++this.blockRefCount;
         if (p_177484_4_.needsRandomTick()) {
            ++this.tickRefCount;
         }
      }

      if (!ifluidstate1.isEmpty()) {
         --this.fluidRefCount;
      }

      this.data.set(p_177484_1_, p_177484_2_, p_177484_3_, p_177484_4_);
   }

   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public boolean func_206915_b() {
      return this.needsRandomTick() || this.func_206917_d();
   }

   public boolean needsRandomTick() {
      return this.tickRefCount > 0;
   }

   public boolean func_206917_d() {
      return this.fluidRefCount > 0;
   }

   public int getYLocation() {
      return this.yBase;
   }

   public void setSkyLight(int p_76657_1_, int p_76657_2_, int p_76657_3_, int p_76657_4_) {
      this.skyLight.set(p_76657_1_, p_76657_2_, p_76657_3_, p_76657_4_);
   }

   public int getSkyLight(int p_76670_1_, int p_76670_2_, int p_76670_3_) {
      return this.skyLight.get(p_76670_1_, p_76670_2_, p_76670_3_);
   }

   public void setBlockLight(int p_76677_1_, int p_76677_2_, int p_76677_3_, int p_76677_4_) {
      this.blockLight.set(p_76677_1_, p_76677_2_, p_76677_3_, p_76677_4_);
   }

   public int getBlockLight(int p_76674_1_, int p_76674_2_, int p_76674_3_) {
      return this.blockLight.get(p_76674_1_, p_76674_2_, p_76674_3_);
   }

   public void recalculateRefCounts() {
      this.blockRefCount = 0;
      this.tickRefCount = 0;
      this.fluidRefCount = 0;

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
               IBlockState iblockstate = this.get(i, j, k);
               IFluidState ifluidstate = this.func_206914_b(i, j, k);
               if (!iblockstate.isAir()) {
                  ++this.blockRefCount;
                  if (iblockstate.needsRandomTick()) {
                     ++this.tickRefCount;
                  }
               }

               if (!ifluidstate.isEmpty()) {
                  ++this.blockRefCount;
                  if (ifluidstate.getTickRandomly()) {
                     ++this.fluidRefCount;
                  }
               }
            }
         }
      }

   }

   public BlockStateContainer<IBlockState> getData() {
      return this.data;
   }

   public NibbleArray getBlockLight() {
      return this.blockLight;
   }

   public NibbleArray getSkyLight() {
      return this.skyLight;
   }

   public void setBlockLight(NibbleArray p_76659_1_) {
      this.blockLight = p_76659_1_;
   }

   public void setSkyLight(NibbleArray p_76666_1_) {
      this.skyLight = p_76666_1_;
   }
}
