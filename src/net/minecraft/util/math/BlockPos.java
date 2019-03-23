package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final int Y_SHIFT = 0 + NUM_Z_BITS;
   private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

   public BlockPos(int p_i46030_1_, int p_i46030_2_, int p_i46030_3_) {
      super(p_i46030_1_, p_i46030_2_, p_i46030_3_);
   }

   public BlockPos(double p_i46031_1_, double p_i46031_3_, double p_i46031_5_) {
      super(p_i46031_1_, p_i46031_3_, p_i46031_5_);
   }

   public BlockPos(Entity p_i46032_1_) {
      this(p_i46032_1_.posX, p_i46032_1_.posY, p_i46032_1_.posZ);
   }

   public BlockPos(Vec3d p_i47100_1_) {
      this(p_i47100_1_.x, p_i47100_1_.y, p_i47100_1_.z);
   }

   public BlockPos(Vec3i p_i46034_1_) {
      this(p_i46034_1_.getX(), p_i46034_1_.getY(), p_i46034_1_.getZ());
   }

   public BlockPos add(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
      return p_177963_1_ == 0.0D && p_177963_3_ == 0.0D && p_177963_5_ == 0.0D ? this : new BlockPos((double)this.getX() + p_177963_1_, (double)this.getY() + p_177963_3_, (double)this.getZ() + p_177963_5_);
   }

   public BlockPos add(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
      return p_177982_1_ == 0 && p_177982_2_ == 0 && p_177982_3_ == 0 ? this : new BlockPos(this.getX() + p_177982_1_, this.getY() + p_177982_2_, this.getZ() + p_177982_3_);
   }

   public BlockPos add(Vec3i p_177971_1_) {
      return this.add(p_177971_1_.getX(), p_177971_1_.getY(), p_177971_1_.getZ());
   }

   public BlockPos subtract(Vec3i p_177973_1_) {
      return this.add(-p_177973_1_.getX(), -p_177973_1_.getY(), -p_177973_1_.getZ());
   }

   public BlockPos up() {
      return this.up(1);
   }

   public BlockPos up(int p_177981_1_) {
      return this.offset(EnumFacing.UP, p_177981_1_);
   }

   public BlockPos down() {
      return this.down(1);
   }

   public BlockPos down(int p_177979_1_) {
      return this.offset(EnumFacing.DOWN, p_177979_1_);
   }

   public BlockPos north() {
      return this.north(1);
   }

   public BlockPos north(int p_177964_1_) {
      return this.offset(EnumFacing.NORTH, p_177964_1_);
   }

   public BlockPos south() {
      return this.south(1);
   }

   public BlockPos south(int p_177970_1_) {
      return this.offset(EnumFacing.SOUTH, p_177970_1_);
   }

   public BlockPos west() {
      return this.west(1);
   }

   public BlockPos west(int p_177985_1_) {
      return this.offset(EnumFacing.WEST, p_177985_1_);
   }

   public BlockPos east() {
      return this.east(1);
   }

   public BlockPos east(int p_177965_1_) {
      return this.offset(EnumFacing.EAST, p_177965_1_);
   }

   public BlockPos offset(EnumFacing p_177972_1_) {
      return this.offset(p_177972_1_, 1);
   }

   public BlockPos offset(EnumFacing p_177967_1_, int p_177967_2_) {
      return p_177967_2_ == 0 ? this : new BlockPos(this.getX() + p_177967_1_.getXOffset() * p_177967_2_, this.getY() + p_177967_1_.getYOffset() * p_177967_2_, this.getZ() + p_177967_1_.getZOffset() * p_177967_2_);
   }

   public BlockPos rotate(Rotation p_190942_1_) {
      switch(p_190942_1_) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos crossProduct(Vec3i p_177955_1_) {
      return new BlockPos(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public long toLong() {
      return ((long)this.getX() & X_MASK) << X_SHIFT | ((long)this.getY() & Y_MASK) << Y_SHIFT | ((long)this.getZ() & Z_MASK) << 0;
   }

   public static BlockPos fromLong(long p_177969_0_) {
      int i = (int)(p_177969_0_ << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
      int j = (int)(p_177969_0_ << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
      int k = (int)(p_177969_0_ << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
      return new BlockPos(i, j, k);
   }

   public static Iterable<BlockPos> getAllInBox(BlockPos p_177980_0_, BlockPos p_177980_1_) {
      return getAllInBox(Math.min(p_177980_0_.getX(), p_177980_1_.getX()), Math.min(p_177980_0_.getY(), p_177980_1_.getY()), Math.min(p_177980_0_.getZ(), p_177980_1_.getZ()), Math.max(p_177980_0_.getX(), p_177980_1_.getX()), Math.max(p_177980_0_.getY(), p_177980_1_.getY()), Math.max(p_177980_0_.getZ(), p_177980_1_.getZ()));
   }

   public static Iterable<BlockPos> getAllInBox(int p_191532_0_, int p_191532_1_, int p_191532_2_, int p_191532_3_, int p_191532_4_, int p_191532_5_) {
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private boolean first = true;
            private int lastX;
            private int lastY;
            private int lastZ;

            protected BlockPos computeNext() {
               if (this.first) {
                  this.first = false;
                  this.lastX = p_191532_0_;
                  this.lastY = p_191532_1_;
                  this.lastZ = p_191532_2_;
                  return new BlockPos(p_191532_0_, p_191532_1_, p_191532_2_);
               } else if (this.lastX == p_191532_3_ && this.lastY == p_191532_4_ && this.lastZ == p_191532_5_) {
                  return this.endOfData();
               } else {
                  if (this.lastX < p_191532_3_) {
                     ++this.lastX;
                  } else if (this.lastY < p_191532_4_) {
                     this.lastX = p_191532_0_;
                     ++this.lastY;
                  } else if (this.lastZ < p_191532_5_) {
                     this.lastX = p_191532_0_;
                     this.lastY = p_191532_1_;
                     ++this.lastZ;
                  }

                  return new BlockPos(this.lastX, this.lastY, this.lastZ);
               }
            }
         };
      };
   }

   public BlockPos toImmutable() {
      return this;
   }

   public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos p_177975_0_, BlockPos p_177975_1_) {
      return getAllInBoxMutable(Math.min(p_177975_0_.getX(), p_177975_1_.getX()), Math.min(p_177975_0_.getY(), p_177975_1_.getY()), Math.min(p_177975_0_.getZ(), p_177975_1_.getZ()), Math.max(p_177975_0_.getX(), p_177975_1_.getX()), Math.max(p_177975_0_.getY(), p_177975_1_.getY()), Math.max(p_177975_0_.getZ(), p_177975_1_.getZ()));
   }

   public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(int p_191531_0_, int p_191531_1_, int p_191531_2_, int p_191531_3_, int p_191531_4_, int p_191531_5_) {
      return () -> {
         return new AbstractIterator<BlockPos.MutableBlockPos>() {
            private BlockPos.MutableBlockPos pos;

            protected BlockPos.MutableBlockPos computeNext() {
               if (this.pos == null) {
                  this.pos = new BlockPos.MutableBlockPos(p_191531_0_, p_191531_1_, p_191531_2_);
                  return this.pos;
               } else if (this.pos.x == p_191531_3_ && this.pos.y == p_191531_4_ && this.pos.z == p_191531_5_) {
                  return this.endOfData();
               } else {
                  if (this.pos.x < p_191531_3_) {
                     ++this.pos.x;
                  } else if (this.pos.y < p_191531_4_) {
                     this.pos.x = p_191531_0_;
                     ++this.pos.y;
                  } else if (this.pos.z < p_191531_5_) {
                     this.pos.x = p_191531_0_;
                     this.pos.y = p_191531_1_;
                     ++this.pos.z;
                  }

                  return this.pos;
               }
            }
         };
      };
   }

   public static class MutableBlockPos extends BlockPos {
      protected int x;
      protected int y;
      protected int z;

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(BlockPos p_i46587_1_) {
         this(p_i46587_1_.getX(), p_i46587_1_.getY(), p_i46587_1_.getZ());
      }

      public MutableBlockPos(int p_i46024_1_, int p_i46024_2_, int p_i46024_3_) {
         super(0, 0, 0);
         this.x = p_i46024_1_;
         this.y = p_i46024_2_;
         this.z = p_i46024_3_;
      }

      public BlockPos add(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
         return super.add(p_177963_1_, p_177963_3_, p_177963_5_).toImmutable();
      }

      public BlockPos add(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
         return super.add(p_177982_1_, p_177982_2_, p_177982_3_).toImmutable();
      }

      public BlockPos offset(EnumFacing p_177967_1_, int p_177967_2_) {
         return super.offset(p_177967_1_, p_177967_2_).toImmutable();
      }

      public BlockPos rotate(Rotation p_190942_1_) {
         return super.rotate(p_190942_1_).toImmutable();
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getZ() {
         return this.z;
      }

      public BlockPos.MutableBlockPos setPos(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         this.x = p_181079_1_;
         this.y = p_181079_2_;
         this.z = p_181079_3_;
         return this;
      }

      @OnlyIn(Dist.CLIENT)
      public BlockPos.MutableBlockPos setPos(Entity p_189535_1_) {
         return this.setPos(p_189535_1_.posX, p_189535_1_.posY, p_189535_1_.posZ);
      }

      public BlockPos.MutableBlockPos setPos(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return this.setPos(MathHelper.floor(p_189532_1_), MathHelper.floor(p_189532_3_), MathHelper.floor(p_189532_5_));
      }

      public BlockPos.MutableBlockPos setPos(Vec3i p_189533_1_) {
         return this.setPos(p_189533_1_.getX(), p_189533_1_.getY(), p_189533_1_.getZ());
      }

      public BlockPos.MutableBlockPos move(EnumFacing p_189536_1_) {
         return this.move(p_189536_1_, 1);
      }

      public BlockPos.MutableBlockPos move(EnumFacing p_189534_1_, int p_189534_2_) {
         return this.setPos(this.x + p_189534_1_.getXOffset() * p_189534_2_, this.y + p_189534_1_.getYOffset() * p_189534_2_, this.z + p_189534_1_.getZOffset() * p_189534_2_);
      }

      public BlockPos.MutableBlockPos move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return this.setPos(this.x + p_196234_1_, this.y + p_196234_2_, this.z + p_196234_3_);
      }

      public void setY(int p_185336_1_) {
         this.y = p_185336_1_;
      }

      public BlockPos toImmutable() {
         return new BlockPos(this);
      }
   }

   public static final class PooledMutableBlockPos extends BlockPos.MutableBlockPos implements AutoCloseable {
      private boolean released;
      private static final List<BlockPos.PooledMutableBlockPos> POOL = Lists.newArrayList();

      private PooledMutableBlockPos(int p_i46586_1_, int p_i46586_2_, int p_i46586_3_) {
         super(p_i46586_1_, p_i46586_2_, p_i46586_3_);
      }

      public static BlockPos.PooledMutableBlockPos retain() {
         return retain(0, 0, 0);
      }

      public static BlockPos.PooledMutableBlockPos retain(Entity p_209907_0_) {
         return retain(p_209907_0_.posX, p_209907_0_.posY, p_209907_0_.posZ);
      }

      public static BlockPos.PooledMutableBlockPos retain(double p_185345_0_, double p_185345_2_, double p_185345_4_) {
         return retain(MathHelper.floor(p_185345_0_), MathHelper.floor(p_185345_2_), MathHelper.floor(p_185345_4_));
      }

      public static BlockPos.PooledMutableBlockPos retain(int p_185339_0_, int p_185339_1_, int p_185339_2_) {
         synchronized(POOL) {
            if (!POOL.isEmpty()) {
               BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = POOL.remove(POOL.size() - 1);
               if (blockpos$pooledmutableblockpos != null && blockpos$pooledmutableblockpos.released) {
                  blockpos$pooledmutableblockpos.released = false;
                  blockpos$pooledmutableblockpos.setPos(p_185339_0_, p_185339_1_, p_185339_2_);
                  return blockpos$pooledmutableblockpos;
               }
            }
         }

         return new BlockPos.PooledMutableBlockPos(p_185339_0_, p_185339_1_, p_185339_2_);
      }

      public BlockPos.PooledMutableBlockPos setPos(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(p_181079_1_, p_181079_2_, p_181079_3_);
      }

      @OnlyIn(Dist.CLIENT)
      public BlockPos.PooledMutableBlockPos setPos(Entity p_189535_1_) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(p_189535_1_);
      }

      public BlockPos.PooledMutableBlockPos setPos(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(p_189532_1_, p_189532_3_, p_189532_5_);
      }

      public BlockPos.PooledMutableBlockPos setPos(Vec3i p_189533_1_) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(p_189533_1_);
      }

      public BlockPos.PooledMutableBlockPos move(EnumFacing p_189536_1_) {
         return (BlockPos.PooledMutableBlockPos)super.move(p_189536_1_);
      }

      public BlockPos.PooledMutableBlockPos move(EnumFacing p_189534_1_, int p_189534_2_) {
         return (BlockPos.PooledMutableBlockPos)super.move(p_189534_1_, p_189534_2_);
      }

      public BlockPos.PooledMutableBlockPos move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return (BlockPos.PooledMutableBlockPos)super.move(p_196234_1_, p_196234_2_, p_196234_3_);
      }

      public void close() {
         synchronized(POOL) {
            if (POOL.size() < 100) {
               POOL.add(this);
            }

            this.released = true;
         }
      }
   }
}
