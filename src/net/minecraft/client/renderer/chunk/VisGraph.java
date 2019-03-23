package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisGraph {
   private static final int DX = (int)Math.pow(16.0D, 0.0D);
   private static final int DZ = (int)Math.pow(16.0D, 1.0D);
   private static final int DY = (int)Math.pow(16.0D, 2.0D);
   private static final EnumFacing[] DIRECTIONS = EnumFacing.values();
   private final BitSet bitSet = new BitSet(4096);
   private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], (p_209264_0_) -> {
      int i = 0;
      int j = 15;
      int k = 0;

      for(int l = 0; l < 16; ++l) {
         for(int i1 = 0; i1 < 16; ++i1) {
            for(int j1 = 0; j1 < 16; ++j1) {
               if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                  p_209264_0_[k++] = getIndex(l, i1, j1);
               }
            }
         }
      }

   });
   private int empty = 4096;

   public void setOpaqueCube(BlockPos p_178606_1_) {
      this.bitSet.set(getIndex(p_178606_1_), true);
      --this.empty;
   }

   private static int getIndex(BlockPos p_178608_0_) {
      return getIndex(p_178608_0_.getX() & 15, p_178608_0_.getY() & 15, p_178608_0_.getZ() & 15);
   }

   private static int getIndex(int p_178605_0_, int p_178605_1_, int p_178605_2_) {
      return p_178605_0_ << 0 | p_178605_1_ << 8 | p_178605_2_ << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility setvisibility = new SetVisibility();
      if (4096 - this.empty < 256) {
         setvisibility.setAllVisible(true);
      } else if (this.empty == 0) {
         setvisibility.setAllVisible(false);
      } else {
         for(int i : INDEX_OF_EDGES) {
            if (!this.bitSet.get(i)) {
               setvisibility.setManyVisible(this.floodFill(i));
            }
         }
      }

      return setvisibility;
   }

   public Set<EnumFacing> getVisibleFacings(BlockPos p_178609_1_) {
      return this.floodFill(getIndex(p_178609_1_));
   }

   private Set<EnumFacing> floodFill(int p_178604_1_) {
      Set<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
      IntPriorityQueue intpriorityqueue = new IntArrayFIFOQueue();
      intpriorityqueue.enqueue(p_178604_1_);
      this.bitSet.set(p_178604_1_, true);

      while(!intpriorityqueue.isEmpty()) {
         int i = intpriorityqueue.dequeueInt();
         this.addEdges(i, set);

         for(EnumFacing enumfacing : DIRECTIONS) {
            int j = this.getNeighborIndexAtFace(i, enumfacing);
            if (j >= 0 && !this.bitSet.get(j)) {
               this.bitSet.set(j, true);
               intpriorityqueue.enqueue(j);
            }
         }
      }

      return set;
   }

   private void addEdges(int p_178610_1_, Set<EnumFacing> p_178610_2_) {
      int i = p_178610_1_ >> 0 & 15;
      if (i == 0) {
         p_178610_2_.add(EnumFacing.WEST);
      } else if (i == 15) {
         p_178610_2_.add(EnumFacing.EAST);
      }

      int j = p_178610_1_ >> 8 & 15;
      if (j == 0) {
         p_178610_2_.add(EnumFacing.DOWN);
      } else if (j == 15) {
         p_178610_2_.add(EnumFacing.UP);
      }

      int k = p_178610_1_ >> 4 & 15;
      if (k == 0) {
         p_178610_2_.add(EnumFacing.NORTH);
      } else if (k == 15) {
         p_178610_2_.add(EnumFacing.SOUTH);
      }

   }

   private int getNeighborIndexAtFace(int p_178603_1_, EnumFacing p_178603_2_) {
      switch(p_178603_2_) {
      case DOWN:
         if ((p_178603_1_ >> 8 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - DY;
      case UP:
         if ((p_178603_1_ >> 8 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + DY;
      case NORTH:
         if ((p_178603_1_ >> 4 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - DZ;
      case SOUTH:
         if ((p_178603_1_ >> 4 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + DZ;
      case WEST:
         if ((p_178603_1_ >> 0 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - DX;
      case EAST:
         if ((p_178603_1_ >> 0 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + DX;
      default:
         return -1;
      }
   }
}
