package net.minecraft.world;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeInt;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePartBitSet;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;

public interface IWorldReaderBase extends IBlockReader {
   boolean isAirBlock(BlockPos p_175623_1_);

   Biome getBiome(BlockPos p_180494_1_);

   int getLightFor(EnumLightType p_175642_1_, BlockPos p_175642_2_);

   default boolean canBlockSeeSky(BlockPos p_175710_1_) {
      if (p_175710_1_.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(p_175710_1_);
      } else {
         BlockPos blockpos = new BlockPos(p_175710_1_.getX(), this.getSeaLevel(), p_175710_1_.getZ());
         if (!this.canSeeSky(blockpos)) {
            return false;
         } else {
            for(BlockPos blockpos1 = blockpos.down(); blockpos1.getY() > p_175710_1_.getY(); blockpos1 = blockpos1.down()) {
               IBlockState iblockstate = this.getBlockState(blockpos1);
               if (iblockstate.getOpacity(this, blockpos1) > 0 && !iblockstate.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   int getLightSubtracted(BlockPos p_201669_1_, int p_201669_2_);

   boolean isChunkLoaded(int p_175680_1_, int p_175680_2_, boolean p_175680_3_);

   boolean canSeeSky(BlockPos p_175678_1_);

   default BlockPos getHeight(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
      return new BlockPos(p_205770_2_.getX(), this.getHeight(p_205770_1_, p_205770_2_.getX(), p_205770_2_.getZ()), p_205770_2_.getZ());
   }

   int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_);

   default float getBrightness(BlockPos p_205052_1_) {
      return this.getDimension().getLightBrightnessTable()[this.getLight(p_205052_1_)];
   }

   @Nullable
   default EntityPlayer getClosestPlayerToEntity(Entity p_72890_1_, double p_72890_2_) {
      return this.getClosestPlayer(p_72890_1_.posX, p_72890_1_.posY, p_72890_1_.posZ, p_72890_2_, false);
   }

   @Nullable
   default EntityPlayer getNearestPlayerNotCreative(Entity p_184136_1_, double p_184136_2_) {
      return this.getClosestPlayer(p_184136_1_.posX, p_184136_1_.posY, p_184136_1_.posZ, p_184136_2_, true);
   }

   @Nullable
   default EntityPlayer getClosestPlayer(double p_184137_1_, double p_184137_3_, double p_184137_5_, double p_184137_7_, boolean p_184137_9_) {
      Predicate<Entity> predicate = p_184137_9_ ? EntitySelectors.CAN_AI_TARGET : EntitySelectors.NOT_SPECTATING;
      return this.getClosestPlayer(p_184137_1_, p_184137_3_, p_184137_5_, p_184137_7_, predicate);
   }

   @Nullable
   EntityPlayer getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_);

   int getSkylightSubtracted();

   WorldBorder getWorldBorder();

   boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_);

   int getStrongPower(BlockPos p_175627_1_, EnumFacing p_175627_2_);

   boolean isRemote();

   int getSeaLevel();

   default boolean checkNoEntityCollision(IBlockState p_195584_1_, BlockPos p_195584_2_) {
      VoxelShape voxelshape = p_195584_1_.getCollisionShape(this, p_195584_2_);
      return voxelshape.isEmpty() || this.checkNoEntityCollision((Entity)null, voxelshape.withOffset((double)p_195584_2_.getX(), (double)p_195584_2_.getY(), (double)p_195584_2_.getZ()));
   }

   default boolean checkNoEntityCollision(@Nullable Entity p_195587_1_, AxisAlignedBB p_195587_2_) {
      return this.checkNoEntityCollision(p_195587_1_, VoxelShapes.func_197881_a(p_195587_2_));
   }

   default Stream<VoxelShape> func_212391_a(VoxelShape p_212391_1_, VoxelShape p_212391_2_, boolean p_212391_3_) {
      int i = MathHelper.floor(p_212391_1_.getStart(EnumFacing.Axis.X)) - 1;
      int j = MathHelper.ceil(p_212391_1_.getEnd(EnumFacing.Axis.X)) + 1;
      int k = MathHelper.floor(p_212391_1_.getStart(EnumFacing.Axis.Y)) - 1;
      int l = MathHelper.ceil(p_212391_1_.getEnd(EnumFacing.Axis.Y)) + 1;
      int i1 = MathHelper.floor(p_212391_1_.getStart(EnumFacing.Axis.Z)) - 1;
      int j1 = MathHelper.ceil(p_212391_1_.getEnd(EnumFacing.Axis.Z)) + 1;
      WorldBorder worldborder = this.getWorldBorder();
      boolean flag = worldborder.minX() < (double)i && (double)j < worldborder.maxX() && worldborder.minZ() < (double)i1 && (double)j1 < worldborder.maxZ();
      VoxelShapePart voxelshapepart = new VoxelShapePartBitSet(j - i, l - k, j1 - i1);
      Predicate<VoxelShape> predicate = (p_212393_1_) -> {
         return !p_212393_1_.isEmpty() && VoxelShapes.func_197879_c(p_212391_1_, p_212393_1_, IBooleanFunction.AND);
      };
      Stream<VoxelShape> stream = StreamSupport.stream(BlockPos.MutableBlockPos.getAllInBoxMutable(i, k, i1, j - 1, l - 1, j1 - 1).spliterator(), false).map((p_212390_12_) -> {
         int k1 = p_212390_12_.getX();
         int l1 = p_212390_12_.getY();
         int i2 = p_212390_12_.getZ();
         boolean flag1 = k1 == i || k1 == j - 1;
         boolean flag2 = l1 == k || l1 == l - 1;
         boolean flag3 = i2 == i1 || i2 == j1 - 1;
         if ((!flag1 || !flag2) && (!flag2 || !flag3) && (!flag3 || !flag1) && this.isBlockLoaded(p_212390_12_)) {
            VoxelShape voxelshape;
            if (p_212391_3_ && !flag && !worldborder.contains(p_212390_12_)) {
               voxelshape = VoxelShapes.func_197868_b();
            } else {
               voxelshape = this.getBlockState(p_212390_12_).getCollisionShape(this, p_212390_12_);
            }

            VoxelShape voxelshape1 = p_212391_2_.withOffset((double)(-k1), (double)(-l1), (double)(-i2));
            if (VoxelShapes.func_197879_c(voxelshape1, voxelshape, IBooleanFunction.AND)) {
               return VoxelShapes.func_197880_a();
            } else if (voxelshape == VoxelShapes.func_197868_b()) {
               voxelshapepart.func_199625_a(k1 - i, l1 - k, i2 - i1, true, true);
               return VoxelShapes.func_197880_a();
            } else {
               return voxelshape.withOffset((double)k1, (double)l1, (double)i2);
            }
         } else {
            return VoxelShapes.func_197880_a();
         }
      }).filter(predicate);
      return Stream.concat(stream, Stream.generate(() -> {
         return new VoxelShapeInt(voxelshapepart, i, k, i1);
      }).limit(1L).filter(predicate));
   }

   default Stream<VoxelShape> func_199406_a(@Nullable Entity p_199406_1_, AxisAlignedBB p_199406_2_, double p_199406_3_, double p_199406_5_, double p_199406_7_) {
      return this.func_212389_a(p_199406_1_, p_199406_2_, Collections.emptySet(), p_199406_3_, p_199406_5_, p_199406_7_);
   }

   default Stream<VoxelShape> func_212389_a(@Nullable Entity p_212389_1_, AxisAlignedBB p_212389_2_, Set<Entity> p_212389_3_, double p_212389_4_, double p_212389_6_, double p_212389_8_) {
      double d0 = 1.0E-7D;
      VoxelShape voxelshape = VoxelShapes.func_197881_a(p_212389_2_);
      VoxelShape voxelshape1 = VoxelShapes.func_197881_a(p_212389_2_.offset(p_212389_4_ > 0.0D ? -1.0E-7D : 1.0E-7D, p_212389_6_ > 0.0D ? -1.0E-7D : 1.0E-7D, p_212389_8_ > 0.0D ? -1.0E-7D : 1.0E-7D));
      VoxelShape voxelshape2 = VoxelShapes.func_197882_b(VoxelShapes.func_197881_a(p_212389_2_.expand(p_212389_4_, p_212389_6_, p_212389_8_).grow(1.0E-7D)), voxelshape1, IBooleanFunction.ONLY_FIRST);
      return this.func_212392_a(p_212389_1_, voxelshape2, voxelshape, p_212389_3_);
   }

   default Stream<VoxelShape> func_212388_b(@Nullable Entity p_212388_1_, AxisAlignedBB p_212388_2_) {
      return this.func_212392_a(p_212388_1_, VoxelShapes.func_197881_a(p_212388_2_), VoxelShapes.func_197880_a(), Collections.emptySet());
   }

   default Stream<VoxelShape> func_212392_a(@Nullable Entity p_212392_1_, VoxelShape p_212392_2_, VoxelShape p_212392_3_, Set<Entity> p_212392_4_) {
      boolean flag = p_212392_1_ != null && p_212392_1_.isOutsideBorder();
      boolean flag1 = p_212392_1_ != null && this.isInsideWorldBorder(p_212392_1_);
      if (p_212392_1_ != null && flag == flag1) {
         p_212392_1_.setOutsideBorder(!flag1);
      }

      return this.func_212391_a(p_212392_2_, p_212392_3_, flag1);
   }

   default boolean isInsideWorldBorder(Entity p_191503_1_) {
      WorldBorder worldborder = this.getWorldBorder();
      double d0 = worldborder.minX();
      double d1 = worldborder.minZ();
      double d2 = worldborder.maxX();
      double d3 = worldborder.maxZ();
      if (p_191503_1_.isOutsideBorder()) {
         ++d0;
         ++d1;
         --d2;
         --d3;
      } else {
         --d0;
         --d1;
         ++d2;
         ++d3;
      }

      return p_191503_1_.posX > d0 && p_191503_1_.posX < d2 && p_191503_1_.posZ > d1 && p_191503_1_.posZ < d3;
   }

   default boolean isCollisionBoxesEmpty(@Nullable Entity p_211156_1_, AxisAlignedBB p_211156_2_, Set<Entity> p_211156_3_) {
      return this.func_212392_a(p_211156_1_, VoxelShapes.func_197881_a(p_211156_2_), VoxelShapes.func_197880_a(), p_211156_3_).allMatch(VoxelShape::isEmpty);
   }

   default boolean isCollisionBoxesEmpty(@Nullable Entity p_195586_1_, AxisAlignedBB p_195586_2_) {
      return this.isCollisionBoxesEmpty(p_195586_1_, p_195586_2_, Collections.emptySet());
   }

   default boolean hasWater(BlockPos p_201671_1_) {
      return this.getFluidState(p_201671_1_).isTagged(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AxisAlignedBB p_72953_1_) {
      int i = MathHelper.floor(p_72953_1_.minX);
      int j = MathHelper.ceil(p_72953_1_.maxX);
      int k = MathHelper.floor(p_72953_1_.minY);
      int l = MathHelper.ceil(p_72953_1_.maxY);
      int i1 = MathHelper.floor(p_72953_1_.minZ);
      int j1 = MathHelper.ceil(p_72953_1_.maxZ);

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                  if (!iblockstate.getFluidState().isEmpty()) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }
         }

         return false;
      }
   }

   default int getLight(BlockPos p_201696_1_) {
      return this.getNeighborAwareLightSubtracted(p_201696_1_, this.getSkylightSubtracted());
   }

   default int getNeighborAwareLightSubtracted(BlockPos p_205049_1_, int p_205049_2_) {
      if (p_205049_1_.getX() >= -30000000 && p_205049_1_.getZ() >= -30000000 && p_205049_1_.getX() < 30000000 && p_205049_1_.getZ() < 30000000) {
         if (this.getBlockState(p_205049_1_).useNeighborBrightness(this, p_205049_1_)) {
            int i = this.getLightSubtracted(p_205049_1_.up(), p_205049_2_);
            int j = this.getLightSubtracted(p_205049_1_.east(), p_205049_2_);
            int k = this.getLightSubtracted(p_205049_1_.west(), p_205049_2_);
            int l = this.getLightSubtracted(p_205049_1_.south(), p_205049_2_);
            int i1 = this.getLightSubtracted(p_205049_1_.north(), p_205049_2_);
            if (j > i) {
               i = j;
            }

            if (k > i) {
               i = k;
            }

            if (l > i) {
               i = l;
            }

            if (i1 > i) {
               i = i1;
            }

            return i;
         } else {
            return this.getLightSubtracted(p_205049_1_, p_205049_2_);
         }
      } else {
         return 15;
      }
   }

   default boolean isBlockLoaded(BlockPos p_175667_1_) {
      return this.isBlockLoaded(p_175667_1_, true);
   }

   default boolean isBlockLoaded(BlockPos p_175668_1_, boolean p_175668_2_) {
      return this.isChunkLoaded(p_175668_1_.getX() >> 4, p_175668_1_.getZ() >> 4, p_175668_2_);
   }

   default boolean isAreaLoaded(BlockPos p_205050_1_, int p_205050_2_) {
      return this.isAreaLoaded(p_205050_1_, p_205050_2_, true);
   }

   default boolean isAreaLoaded(BlockPos p_175648_1_, int p_175648_2_, boolean p_175648_3_) {
      return this.isAreaLoaded(p_175648_1_.getX() - p_175648_2_, p_175648_1_.getY() - p_175648_2_, p_175648_1_.getZ() - p_175648_2_, p_175648_1_.getX() + p_175648_2_, p_175648_1_.getY() + p_175648_2_, p_175648_1_.getZ() + p_175648_2_, p_175648_3_);
   }

   default boolean isAreaLoaded(BlockPos p_175707_1_, BlockPos p_175707_2_) {
      return this.isAreaLoaded(p_175707_1_, p_175707_2_, true);
   }

   default boolean isAreaLoaded(BlockPos p_175706_1_, BlockPos p_175706_2_, boolean p_175706_3_) {
      return this.isAreaLoaded(p_175706_1_.getX(), p_175706_1_.getY(), p_175706_1_.getZ(), p_175706_2_.getX(), p_175706_2_.getY(), p_175706_2_.getZ(), p_175706_3_);
   }

   default boolean isAreaLoaded(MutableBoundingBox p_175711_1_) {
      return this.isAreaLoaded(p_175711_1_, true);
   }

   default boolean isAreaLoaded(MutableBoundingBox p_175639_1_, boolean p_175639_2_) {
      return this.isAreaLoaded(p_175639_1_.minX, p_175639_1_.minY, p_175639_1_.minZ, p_175639_1_.maxX, p_175639_1_.maxY, p_175639_1_.maxZ, p_175639_2_);
   }

   default boolean isAreaLoaded(int p_175663_1_, int p_175663_2_, int p_175663_3_, int p_175663_4_, int p_175663_5_, int p_175663_6_, boolean p_175663_7_) {
      if (p_175663_5_ >= 0 && p_175663_2_ < 256) {
         p_175663_1_ = p_175663_1_ >> 4;
         p_175663_3_ = p_175663_3_ >> 4;
         p_175663_4_ = p_175663_4_ >> 4;
         p_175663_6_ = p_175663_6_ >> 4;

         for(int i = p_175663_1_; i <= p_175663_4_; ++i) {
            for(int j = p_175663_3_; j <= p_175663_6_; ++j) {
               if (!this.isChunkLoaded(i, j, p_175663_7_)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   Dimension getDimension();
}
