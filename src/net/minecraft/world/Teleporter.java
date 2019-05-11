package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

public class Teleporter {
   private static final BlockPortal BLOCK_NETHER_PORTAL = (BlockPortal)Blocks.NETHER_PORTAL;
   protected final WorldServer world;
   protected final Random random;
   protected final Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache = new Long2ObjectOpenHashMap<>(4096);

   public Teleporter(WorldServer p_i1963_1_) {
      this.world = p_i1963_1_;
      this.random = new Random(p_i1963_1_.getSeed());
   }

   public void placeInPortal(Entity p_180266_1_, float p_180266_2_) {
      if (this.world.dimension.getType() != DimensionType.THE_END) {
         if (!this.placeInExistingPortal(p_180266_1_, p_180266_2_)) {
            this.makeNetherPortal(p_180266_1_);
            this.placeInExistingPortal(p_180266_1_, p_180266_2_);
         }
      } else {
         int i = MathHelper.floor(p_180266_1_.posX);
         int j = MathHelper.floor(p_180266_1_.posY) - 1;
         int k = MathHelper.floor(p_180266_1_.posZ);
         int l = 1;
         int i1 = 0;

         for(int j1 = -2; j1 <= 2; ++j1) {
            for(int k1 = -2; k1 <= 2; ++k1) {
               for(int l1 = -1; l1 < 3; ++l1) {
                  int i2 = i + k1;
                  int j2 = j + l1;
                  int k2 = k - j1;
                  boolean flag = l1 < 0;
                  this.world.setBlockState(new BlockPos(i2, j2, k2), flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
               }
            }
         }

         p_180266_1_.setLocationAndAngles((double)i, (double)j, (double)k, p_180266_1_.rotationYaw, 0.0F);
         p_180266_1_.motionX = 0.0D;
         p_180266_1_.motionY = 0.0D;
         p_180266_1_.motionZ = 0.0D;
      }
   }

   public boolean placeInExistingPortal(Entity p_180620_1_, float p_180620_2_) {
      double d0 = -1.0D;
      int j = MathHelper.floor(p_180620_1_.posX);
      int k = MathHelper.floor(p_180620_1_.posZ);
      boolean flag = true;
      BlockPos blockpos = BlockPos.ORIGIN;
      long l = ChunkPos.asLong(j, k);
      if (this.destinationCoordinateCache.containsKey(l)) {
         Teleporter.PortalPosition teleporter$portalposition = this.destinationCoordinateCache.get(l);
         d0 = 0.0D;
         blockpos = teleporter$portalposition;
         teleporter$portalposition.lastUpdateTime = this.world.getTotalWorldTime();
         flag = false;
      } else {
         BlockPos blockpos3 = new BlockPos(p_180620_1_);

         for(int i1 = -128; i1 <= 128; ++i1) {
            BlockPos blockpos2;
            for(int j1 = -128; j1 <= 128; ++j1) {
               for(BlockPos blockpos1 = blockpos3.add(i1, this.world.getActualHeight() - 1 - blockpos3.getY(), j1); blockpos1.getY() >= 0; blockpos1 = blockpos2) {
                  blockpos2 = blockpos1.down();
                  if (this.world.getBlockState(blockpos1).getBlock() == BLOCK_NETHER_PORTAL) {
                     for(blockpos2 = blockpos1.down(); this.world.getBlockState(blockpos2).getBlock() == BLOCK_NETHER_PORTAL; blockpos2 = blockpos2.down()) {
                        blockpos1 = blockpos2;
                     }

                     double d1 = blockpos1.distanceSq(blockpos3);
                     if (d0 < 0.0D || d1 < d0) {
                        d0 = d1;
                        blockpos = blockpos1;
                     }
                  }
               }
            }
         }
      }

      if (d0 >= 0.0D) {
         if (flag) {
            this.destinationCoordinateCache.put(l, new Teleporter.PortalPosition(blockpos, this.world.getTotalWorldTime()));
         }

         double d5 = (double)blockpos.getX() + 0.5D;
         double d7 = (double)blockpos.getZ() + 0.5D;
         BlockPattern.PatternHelper blockpattern$patternhelper = BLOCK_NETHER_PORTAL.createPatternHelper(this.world, blockpos);
         boolean flag1 = blockpattern$patternhelper.getForwards().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
         double d2 = blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X ? (double)blockpattern$patternhelper.getFrontTopLeft().getZ() : (double)blockpattern$patternhelper.getFrontTopLeft().getX();
         double d6 = (double)(blockpattern$patternhelper.getFrontTopLeft().getY() + 1) - p_180620_1_.getLastPortalVec().y * (double)blockpattern$patternhelper.getHeight();
         if (flag1) {
            ++d2;
         }

         if (blockpattern$patternhelper.getForwards().getAxis() == EnumFacing.Axis.X) {
            d7 = d2 + (1.0D - p_180620_1_.getLastPortalVec().x) * (double)blockpattern$patternhelper.getWidth() * (double)blockpattern$patternhelper.getForwards().rotateY().getAxisDirection().getOffset();
         } else {
            d5 = d2 + (1.0D - p_180620_1_.getLastPortalVec().x) * (double)blockpattern$patternhelper.getWidth() * (double)blockpattern$patternhelper.getForwards().rotateY().getAxisDirection().getOffset();
         }

         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         if (blockpattern$patternhelper.getForwards().getOpposite() == p_180620_1_.getTeleportDirection()) {
            f = 1.0F;
            f1 = 1.0F;
         } else if (blockpattern$patternhelper.getForwards().getOpposite() == p_180620_1_.getTeleportDirection().getOpposite()) {
            f = -1.0F;
            f1 = -1.0F;
         } else if (blockpattern$patternhelper.getForwards().getOpposite() == p_180620_1_.getTeleportDirection().rotateY()) {
            f2 = 1.0F;
            f3 = -1.0F;
         } else {
            f2 = -1.0F;
            f3 = 1.0F;
         }

         double d3 = p_180620_1_.motionX;
         double d4 = p_180620_1_.motionZ;
         p_180620_1_.motionX = d3 * (double)f + d4 * (double)f3;
         p_180620_1_.motionZ = d3 * (double)f2 + d4 * (double)f1;
         p_180620_1_.rotationYaw = p_180620_2_ - (float)(p_180620_1_.getTeleportDirection().getOpposite().getHorizontalIndex() * 90) + (float)(blockpattern$patternhelper.getForwards().getHorizontalIndex() * 90);
         if (p_180620_1_ instanceof EntityPlayerMP) {
            ((EntityPlayerMP)p_180620_1_).connection.setPlayerLocation(d5, d6, d7, p_180620_1_.rotationYaw, p_180620_1_.rotationPitch);
            ((EntityPlayerMP)p_180620_1_).connection.captureCurrentPosition();
         } else {
            p_180620_1_.setLocationAndAngles(d5, d6, d7, p_180620_1_.rotationYaw, p_180620_1_.rotationPitch);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean makeUnderworldPortal(){
      return true;
   }

   public boolean makeNetherPortal(Entity p_85188_1_) {
      int i = 16;
      double d0 = -1.0D;
      int j = MathHelper.floor(p_85188_1_.posX);
      int k = MathHelper.floor(p_85188_1_.posY);
      int l = MathHelper.floor(p_85188_1_.posZ);
      int i1 = j;
      int j1 = k;
      int k1 = l;
      int l1 = 0;
      int i2 = this.random.nextInt(4);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j2 = j - 16; j2 <= j + 16; ++j2) {
         double d1 = (double)j2 + 0.5D - p_85188_1_.posX;

         for(int l2 = l - 16; l2 <= l + 16; ++l2) {
            double d2 = (double)l2 + 0.5D - p_85188_1_.posZ;

            label276:
            for(int j3 = this.world.getActualHeight() - 1; j3 >= 0; --j3) {
               if (this.world.isAirBlock(blockpos$mutableblockpos.setPos(j2, j3, l2))) {
                  while(j3 > 0 && this.world.isAirBlock(blockpos$mutableblockpos.setPos(j2, j3 - 1, l2))) {
                     --j3;
                  }

                  for(int k3 = i2; k3 < i2 + 4; ++k3) {
                     int l3 = k3 % 2;
                     int i4 = 1 - l3;
                     if (k3 % 4 >= 2) {
                        l3 = -l3;
                        i4 = -i4;
                     }

                     for(int j4 = 0; j4 < 3; ++j4) {
                        for(int k4 = 0; k4 < 4; ++k4) {
                           for(int l4 = -1; l4 < 4; ++l4) {
                              int i5 = j2 + (k4 - 1) * l3 + j4 * i4;
                              int j5 = j3 + l4;
                              int k5 = l2 + (k4 - 1) * i4 - j4 * l3;
                              blockpos$mutableblockpos.setPos(i5, j5, k5);
                              if (l4 < 0 && !this.world.getBlockState(blockpos$mutableblockpos).getMaterial().isSolid() || l4 >= 0 && !this.world.isAirBlock(blockpos$mutableblockpos)) {
                                 continue label276;
                              }
                           }
                        }
                     }

                     double d5 = (double)j3 + 0.5D - p_85188_1_.posY;
                     double d7 = d1 * d1 + d5 * d5 + d2 * d2;
                     if (d0 < 0.0D || d7 < d0) {
                        d0 = d7;
                        i1 = j2;
                        j1 = j3;
                        k1 = l2;
                        l1 = k3 % 4;
                     }
                  }
               }
            }
         }
      }

      if (d0 < 0.0D) {
         for(int l5 = j - 16; l5 <= j + 16; ++l5) {
            double d3 = (double)l5 + 0.5D - p_85188_1_.posX;

            for(int j6 = l - 16; j6 <= l + 16; ++j6) {
               double d4 = (double)j6 + 0.5D - p_85188_1_.posZ;

               label214:
               for(int i7 = this.world.getActualHeight() - 1; i7 >= 0; --i7) {
                  if (this.world.isAirBlock(blockpos$mutableblockpos.setPos(l5, i7, j6))) {
                     while(i7 > 0 && this.world.isAirBlock(blockpos$mutableblockpos.setPos(l5, i7 - 1, j6))) {
                        --i7;
                     }

                     for(int l7 = i2; l7 < i2 + 2; ++l7) {
                        int l8 = l7 % 2;
                        int k9 = 1 - l8;

                        for(int i10 = 0; i10 < 4; ++i10) {
                           for(int k10 = -1; k10 < 4; ++k10) {
                              int i11 = l5 + (i10 - 1) * l8;
                              int j11 = i7 + k10;
                              int k11 = j6 + (i10 - 1) * k9;
                              blockpos$mutableblockpos.setPos(i11, j11, k11);
                              if (k10 < 0 && !this.world.getBlockState(blockpos$mutableblockpos).getMaterial().isSolid() || k10 >= 0 && !this.world.isAirBlock(blockpos$mutableblockpos)) {
                                 continue label214;
                              }
                           }
                        }

                        double d6 = (double)i7 + 0.5D - p_85188_1_.posY;
                        double d8 = d3 * d3 + d6 * d6 + d4 * d4;
                        if (d0 < 0.0D || d8 < d0) {
                           d0 = d8;
                           i1 = l5;
                           j1 = i7;
                           k1 = j6;
                           l1 = l7 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int i6 = i1;
      int k2 = j1;
      int k6 = k1;
      int l6 = l1 % 2;
      int i3 = 1 - l6;
      if (l1 % 4 >= 2) {
         l6 = -l6;
         i3 = -i3;
      }

      if (d0 < 0.0D) {
         j1 = MathHelper.clamp(j1, 70, this.world.getActualHeight() - 10);
         k2 = j1;

         for(int j7 = -1; j7 <= 1; ++j7) {
            for(int i8 = 1; i8 < 3; ++i8) {
               for(int i9 = -1; i9 < 3; ++i9) {
                  int l9 = i6 + (i8 - 1) * l6 + j7 * i3;
                  int j10 = k2 + i9;
                  int l10 = k6 + (i8 - 1) * i3 - j7 * l6;
                  boolean flag = i9 < 0;
                  blockpos$mutableblockpos.setPos(l9, j10, l10);
                  this.world.setBlockState(blockpos$mutableblockpos, flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
               }
            }
         }
      }

      for(int k7 = -1; k7 < 3; ++k7) {
         for(int j8 = -1; j8 < 4; ++j8) {
            if (k7 == -1 || k7 == 2 || j8 == -1 || j8 == 3) {
               blockpos$mutableblockpos.setPos(i6 + k7 * l6, k2 + j8, k6 + k7 * i3);
               this.world.setBlockState(blockpos$mutableblockpos, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
         }
      }

      IBlockState block_nether_portal = BLOCK_NETHER_PORTAL.getDefaultState().with(BlockPortal.AXIS, l6 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);

      for(int k8 = 0; k8 < 2; ++k8) {
         for(int j9 = 0; j9 < 3; ++j9) {
            blockpos$mutableblockpos.setPos(i6 + k8 * l6, k2 + j9, k6 + k8 * i3);
            this.world.setBlockState(blockpos$mutableblockpos, block_nether_portal, 18);
         }
      }

      return true;
   }

   public void removeStalePortalLocations(long p_85189_1_) {
      if (p_85189_1_ % 100L == 0L) {
         long i = p_85189_1_ - 300L;
         ObjectIterator<Teleporter.PortalPosition> objectiterator = this.destinationCoordinateCache.values().iterator();

         while(objectiterator.hasNext()) {
            Teleporter.PortalPosition teleporter$portalposition = objectiterator.next();
            if (teleporter$portalposition == null || teleporter$portalposition.lastUpdateTime < i) {
               objectiterator.remove();
            }
         }
      }

   }

   public class PortalPosition extends BlockPos {
      public long lastUpdateTime;

      public PortalPosition(BlockPos p_i45747_2_, long p_i45747_3_) {
         super(p_i45747_2_.getX(), p_i45747_2_.getY(), p_i45747_2_.getZ());
         this.lastUpdateTime = p_i45747_3_;
      }
   }
}
