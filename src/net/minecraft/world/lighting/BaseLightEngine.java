package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.IWorldWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseLightEngine implements ILightEngine {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EnumFacing[] DIRECTIONS = EnumFacing.values();
   private final IntPriorityQueue lightQueue = new IntArrayFIFOQueue(786);

   public int getLightAt(IWorldReaderBase p_202666_1_, BlockPos p_202666_2_) {
      return p_202666_1_.getLightFor(this.getLightType(), p_202666_2_);
   }

   public void setLight(IWorldWriter p_202667_1_, BlockPos p_202667_2_, int p_202667_3_) {
      p_202667_1_.setLightFor(this.getLightType(), p_202667_2_, p_202667_3_);
   }

   protected int getOpacity(IBlockReader p_202665_1_, BlockPos p_202665_2_) {
      return p_202665_1_.getBlockState(p_202665_2_).getOpacity(p_202665_1_, p_202665_2_);
   }

   protected int getLightAt(IBlockReader p_202670_1_, BlockPos p_202670_2_) {
      return p_202670_1_.getBlockState(p_202670_2_).getLightValue();
   }

   private int packLightChange(@Nullable EnumFacing p_202662_1_, int p_202662_2_, int p_202662_3_, int p_202662_4_, int p_202662_5_) {
      int i = 7;
      if (p_202662_1_ != null) {
         i = p_202662_1_.ordinal();
      }

      return i << 24 | p_202662_2_ << 18 | p_202662_3_ << 10 | p_202662_4_ << 4 | p_202662_5_ << 0;
   }

   private int unpackXPos(int p_202660_1_) {
      return p_202660_1_ >> 18 & 63;
   }

   private int unpackYPos(int p_202668_1_) {
      return p_202668_1_ >> 10 & 255;
   }

   private int unpackZPos(int p_202658_1_) {
      return p_202658_1_ >> 4 & 63;
   }

   private int unpackLight(int p_202663_1_) {
      return p_202663_1_ >> 0 & 15;
   }

   @Nullable
   private EnumFacing func_202661_e(int p_202661_1_) {
      int i = p_202661_1_ >> 24 & 7;
      return i == 7 ? null : EnumFacing.values()[p_202661_1_ >> 24 & 7];
   }

   protected void func_202664_a(IWorld p_202664_1_, ChunkPos p_202664_2_) {
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         while(!this.lightQueue.isEmpty()) {
            int i = this.lightQueue.dequeueInt();
            int j = this.unpackLight(i);
            int k = this.unpackXPos(i) - 16;
            int l = this.unpackYPos(i);
            int i1 = this.unpackZPos(i) - 16;
            EnumFacing enumfacing = this.func_202661_e(i);

            for(EnumFacing enumfacing1 : DIRECTIONS) {
               if (enumfacing1 != enumfacing) {
                  int j1 = k + enumfacing1.getXOffset();
                  int k1 = l + enumfacing1.getYOffset();
                  int l1 = i1 + enumfacing1.getZOffset();
                  if (k1 <= 255 && k1 >= 0) {
                     blockpos$pooledmutableblockpos.setPos(j1 + p_202664_2_.getXStart(), k1, l1 + p_202664_2_.getZStart());
                     int i2 = this.getOpacity(p_202664_1_, blockpos$pooledmutableblockpos);
                     int j2 = j - Math.max(i2, 1);
                     if (j2 > 0 && j2 > this.getLightAt(p_202664_1_, blockpos$pooledmutableblockpos)) {
                        this.setLight(p_202664_1_, blockpos$pooledmutableblockpos, j2);
                        this.enqueueLightChange(p_202664_2_, blockpos$pooledmutableblockpos, j2);
                     }
                  }
               }
            }
         }
      }

   }

   protected void enqueueLightChange(ChunkPos p_202669_1_, int p_202669_2_, int p_202669_3_, int p_202669_4_, int p_202669_5_) {
      int i = p_202669_2_ - p_202669_1_.getXStart() + 16;
      int j = p_202669_4_ - p_202669_1_.getZStart() + 16;
      this.lightQueue.enqueue(this.packLightChange(null, i, p_202669_3_, j, p_202669_5_));
   }

   protected void enqueueLightChange(ChunkPos p_202659_1_, BlockPos p_202659_2_, int p_202659_3_) {
      this.enqueueLightChange(p_202659_1_, p_202659_2_.getX(), p_202659_2_.getY(), p_202659_2_.getZ(), p_202659_3_);
   }
}
