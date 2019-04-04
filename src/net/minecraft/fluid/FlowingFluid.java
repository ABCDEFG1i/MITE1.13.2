package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
   public static final IntegerProperty LEVEL_1_TO_8 = BlockStateProperties.LEVEL_1_8;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> field_212756_e = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });

   protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
      p_207184_1_.add(FALLING);
   }

   public Vec3d getFlow(IWorldReaderBase p_205564_1_, BlockPos p_205564_2_, IFluidState p_205564_3_) {
      double d0 = 0.0D;
      double d1 = 0.0D;

      Vec3d vec3d1;
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            blockpos$pooledmutableblockpos.setPos(p_205564_2_).move(enumfacing);
            IFluidState ifluidstate = p_205564_1_.getFluidState(blockpos$pooledmutableblockpos);
            if (this.func_212189_g(ifluidstate)) {
               float f = ifluidstate.getHeight();
               float f1 = 0.0F;
               if (f == 0.0F) {
                  if (!p_205564_1_.getBlockState(blockpos$pooledmutableblockpos).getMaterial().blocksMovement()) {
                     IFluidState ifluidstate1 = p_205564_1_.getFluidState(blockpos$pooledmutableblockpos.down());
                     if (this.func_212189_g(ifluidstate1)) {
                        f = ifluidstate1.getHeight();
                        if (f > 0.0F) {
                           f1 = p_205564_3_.getHeight() - (f - 0.8888889F);
                        }
                     }
                  }
               } else if (f > 0.0F) {
                  f1 = p_205564_3_.getHeight() - f;
               }

               if (f1 != 0.0F) {
                  d0 += (double)((float)enumfacing.getXOffset() * f1);
                  d1 += (double)((float)enumfacing.getZOffset() * f1);
               }
            }
         }

         Vec3d vec3d = new Vec3d(d0, 0.0D, d1);
         if (p_205564_3_.get(FALLING)) {
            for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
               blockpos$pooledmutableblockpos.setPos(p_205564_2_).move(enumfacing1);
               if (this.func_205573_a(p_205564_1_, blockpos$pooledmutableblockpos, enumfacing1) || this.func_205573_a(p_205564_1_, blockpos$pooledmutableblockpos.up(), enumfacing1)) {
                  vec3d = vec3d.normalize().add(0.0D, -6.0D, 0.0D);
                  break;
               }
            }
         }

         vec3d1 = vec3d.normalize();
      }

      return vec3d1;
   }

   private boolean func_212189_g(IFluidState p_212189_1_) {
      return p_212189_1_.isEmpty() || p_212189_1_.getFluid().isEquivalentTo(this);
   }

   protected boolean func_205573_a(IBlockReader p_205573_1_, BlockPos p_205573_2_, EnumFacing p_205573_3_) {
      IBlockState iblockstate = p_205573_1_.getBlockState(p_205573_2_);
      Block block = iblockstate.getBlock();
      IFluidState ifluidstate = p_205573_1_.getFluidState(p_205573_2_);
      if (ifluidstate.getFluid().isEquivalentTo(this)) {
         return false;
      } else if (p_205573_3_ == EnumFacing.UP) {
         return true;
      } else if (iblockstate.getMaterial() == Material.ICE) {
         return false;
      } else {
         boolean flag = Block.isExceptBlockForAttachWithPiston(block) || block instanceof BlockStairs;
         return !flag && iblockstate.getBlockFaceShape(p_205573_1_, p_205573_2_, p_205573_3_) == BlockFaceShape.SOLID;
      }
   }

   protected void flowAround(IWorld p_205575_1_, BlockPos p_205575_2_, IFluidState p_205575_3_) {
      if (!p_205575_3_.isEmpty()) {
         IBlockState iblockstate = p_205575_1_.getBlockState(p_205575_2_);
         BlockPos blockpos = p_205575_2_.down();
         IBlockState iblockstate1 = p_205575_1_.getBlockState(blockpos);
         IFluidState ifluidstate = this.calculateCorrectFlowingState(p_205575_1_, blockpos, iblockstate1);
         if (this.canFlow(p_205575_1_, p_205575_2_, iblockstate, EnumFacing.DOWN, blockpos, iblockstate1, p_205575_1_.getFluidState(blockpos), ifluidstate.getFluid())) {
            this.flowInto(p_205575_1_, blockpos, iblockstate1, EnumFacing.DOWN, ifluidstate);
            if (this.func_207936_a(p_205575_1_, p_205575_2_) >= 3) {
               this.func_207937_a(p_205575_1_, p_205575_2_, p_205575_3_, iblockstate);
            }
         } else if (p_205575_3_.isSource() || !this.func_211759_a(p_205575_1_, ifluidstate.getFluid(), p_205575_2_, iblockstate, blockpos, iblockstate1)) {
            this.func_207937_a(p_205575_1_, p_205575_2_, p_205575_3_, iblockstate);
         }

      }
   }

   private void func_207937_a(IWorld p_207937_1_, BlockPos p_207937_2_, IFluidState p_207937_3_, IBlockState p_207937_4_) {
      int i = p_207937_3_.getLevel() - this.getLevelDecreasePerBlock(p_207937_1_);
      if (p_207937_3_.get(FALLING)) {
         i = 7;
      }

      if (i > 0) {
         Map<EnumFacing, IFluidState> map = this.func_205572_b(p_207937_1_, p_207937_2_, p_207937_4_);

         for(Entry<EnumFacing, IFluidState> entry : map.entrySet()) {
            EnumFacing enumfacing = entry.getKey();
            IFluidState ifluidstate = entry.getValue();
            BlockPos blockpos = p_207937_2_.offset(enumfacing);
            IBlockState iblockstate = p_207937_1_.getBlockState(blockpos);
            if (this.canFlow(p_207937_1_, p_207937_2_, p_207937_4_, enumfacing, blockpos, iblockstate, p_207937_1_.getFluidState(blockpos), ifluidstate.getFluid())) {
               this.flowInto(p_207937_1_, blockpos, iblockstate, enumfacing, ifluidstate);
            }
         }

      }
   }

   protected IFluidState calculateCorrectFlowingState(IWorldReaderBase p_205576_1_, BlockPos p_205576_2_, IBlockState p_205576_3_) {
      int i = 0;
      int j = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_205576_2_.offset(enumfacing);
         IBlockState iblockstate = p_205576_1_.getBlockState(blockpos);
         IFluidState ifluidstate = iblockstate.getFluidState();
         if (ifluidstate.getFluid().isEquivalentTo(this) && this.func_212751_a(enumfacing, p_205576_1_, p_205576_2_, p_205576_3_, blockpos, iblockstate)) {
            if (ifluidstate.isSource()) {
               ++j;
            }

            i = Math.max(i, ifluidstate.getLevel());
         }
      }

      if (this.canSourcesMultiply() && j >= 2) {
         IBlockState iblockstate1 = p_205576_1_.getBlockState(p_205576_2_.down());
         IFluidState ifluidstate1 = iblockstate1.getFluidState();
         if (iblockstate1.getMaterial().isSolid() || this.isSameAs(ifluidstate1)) {
            return this.getStillFluidState(false);
         }
      }

      BlockPos blockpos1 = p_205576_2_.up();
      IBlockState iblockstate2 = p_205576_1_.getBlockState(blockpos1);
      IFluidState ifluidstate2 = iblockstate2.getFluidState();
      if (!ifluidstate2.isEmpty() && ifluidstate2.getFluid().isEquivalentTo(this) && this.func_212751_a(EnumFacing.UP, p_205576_1_, p_205576_2_, p_205576_3_, blockpos1, iblockstate2)) {
         return this.getFlowingFluidState(8, true);
      } else {
         int k = i - this.getLevelDecreasePerBlock(p_205576_1_);
         if (k <= 0) {
            return Fluids.EMPTY.getDefaultState();
         } else {
            return this.getFlowingFluidState(k, false);
         }
      }
   }

   private boolean func_212751_a(EnumFacing p_212751_1_, IBlockReader p_212751_2_, BlockPos p_212751_3_, IBlockState p_212751_4_, BlockPos p_212751_5_, IBlockState p_212751_6_) {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap;
      if (!p_212751_4_.getBlock().isVariableOpacity() && !p_212751_6_.getBlock().isVariableOpacity()) {
         object2bytelinkedopenhashmap = field_212756_e.get();
      } else {
         object2bytelinkedopenhashmap = null;
      }

      Block.RenderSideCacheKey block$rendersidecachekey;
      if (object2bytelinkedopenhashmap != null) {
         block$rendersidecachekey = new Block.RenderSideCacheKey(p_212751_4_, p_212751_6_, p_212751_1_);
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         }
      } else {
         block$rendersidecachekey = null;
      }

      VoxelShape voxelshape1 = p_212751_4_.getCollisionShape(p_212751_2_, p_212751_3_);
      VoxelShape voxelshape = p_212751_6_.getCollisionShape(p_212751_2_, p_212751_5_);
      boolean flag = !VoxelShapes.func_204642_b(voxelshape1, voxelshape, p_212751_1_);
      if (object2bytelinkedopenhashmap != null) {
         if (object2bytelinkedopenhashmap.size() == 200) {
            object2bytelinkedopenhashmap.removeLastByte();
         }

         object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
      }

      return flag;
   }

   public abstract Fluid getFlowingFluid();

   public IFluidState getFlowingFluidState(int p_207207_1_, boolean p_207207_2_) {
      return this.getFlowingFluid().getDefaultState().with(LEVEL_1_TO_8, Integer.valueOf(p_207207_1_)).with(FALLING, Boolean.valueOf(p_207207_2_));
   }

   public abstract Fluid getStillFluid();

   public IFluidState getStillFluidState(boolean p_207204_1_) {
      return this.getStillFluid().getDefaultState().with(FALLING, Boolean.valueOf(p_207204_1_));
   }

   protected abstract boolean canSourcesMultiply();

   protected void flowInto(IWorld p_205574_1_, BlockPos p_205574_2_, IBlockState p_205574_3_, EnumFacing p_205574_4_, IFluidState p_205574_5_) {
      if (p_205574_3_.getBlock() instanceof ILiquidContainer) {
         ((ILiquidContainer)p_205574_3_.getBlock()).receiveFluid(p_205574_1_, p_205574_2_, p_205574_3_, p_205574_5_);
      } else {
         if (!p_205574_3_.isAir()) {
            this.beforeReplacingBlock(p_205574_1_, p_205574_2_, p_205574_3_);
         }

         p_205574_1_.setBlockState(p_205574_2_, p_205574_5_.getBlockState(), 3);
      }

   }

   protected abstract void beforeReplacingBlock(IWorld p_205580_1_, BlockPos p_205580_2_, IBlockState p_205580_3_);

   private static short func_212752_a(BlockPos p_212752_0_, BlockPos p_212752_1_) {
      int i = p_212752_1_.getX() - p_212752_0_.getX();
      int j = p_212752_1_.getZ() - p_212752_0_.getZ();
      return (short)((i + 128 & 255) << 8 | j + 128 & 255);
   }

   protected int func_205571_a(IWorldReaderBase p_205571_1_, BlockPos p_205571_2_, int p_205571_3_, EnumFacing p_205571_4_, IBlockState p_205571_5_, BlockPos p_205571_6_, Short2ObjectMap<Pair<IBlockState, IFluidState>> p_205571_7_, Short2BooleanMap p_205571_8_) {
      int i = 1000;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (enumfacing != p_205571_4_) {
            BlockPos blockpos = p_205571_2_.offset(enumfacing);
            short short1 = func_212752_a(p_205571_6_, blockpos);
            Pair<IBlockState, IFluidState> pair = p_205571_7_.computeIfAbsent(short1, (p_212748_2_) -> {
               IBlockState iblockstate1 = p_205571_1_.getBlockState(blockpos);
               return Pair.of(iblockstate1, iblockstate1.getFluidState());
            });
            IBlockState iblockstate = pair.getFirst();
            IFluidState ifluidstate = pair.getSecond();
            if (this.func_211760_a(p_205571_1_, this.getFlowingFluid(), p_205571_2_, p_205571_5_, enumfacing, blockpos, iblockstate, ifluidstate)) {
               boolean flag = p_205571_8_.computeIfAbsent(short1, (p_212749_4_) -> {
                  BlockPos blockpos1 = blockpos.down();
                  IBlockState iblockstate1 = p_205571_1_.getBlockState(blockpos1);
                  return this.func_211759_a(p_205571_1_, this.getFlowingFluid(), blockpos, iblockstate, blockpos1, iblockstate1);
               });
               if (flag) {
                  return p_205571_3_;
               }

               if (p_205571_3_ < this.getSlopeFindDistance(p_205571_1_)) {
                  int j = this.func_205571_a(p_205571_1_, blockpos, p_205571_3_ + 1, enumfacing.getOpposite(), iblockstate, p_205571_6_, p_205571_7_, p_205571_8_);
                  if (j < i) {
                     i = j;
                  }
               }
            }
         }
      }

      return i;
   }

   private boolean func_211759_a(IBlockReader p_211759_1_, Fluid p_211759_2_, BlockPos p_211759_3_, IBlockState p_211759_4_, BlockPos p_211759_5_, IBlockState p_211759_6_) {
      if (!this.func_212751_a(EnumFacing.DOWN, p_211759_1_, p_211759_3_, p_211759_4_, p_211759_5_, p_211759_6_)) {
         return false;
      } else {
         return p_211759_6_.getFluidState().getFluid().isEquivalentTo(this) || this.func_211761_a(p_211759_1_,
                 p_211759_5_, p_211759_6_, p_211759_2_);
      }
   }

   private boolean func_211760_a(IBlockReader p_211760_1_, Fluid p_211760_2_, BlockPos p_211760_3_, IBlockState p_211760_4_, EnumFacing p_211760_5_, BlockPos p_211760_6_, IBlockState p_211760_7_, IFluidState p_211760_8_) {
      return !this.isSameAs(p_211760_8_) && this.func_212751_a(p_211760_5_, p_211760_1_, p_211760_3_, p_211760_4_, p_211760_6_, p_211760_7_) && this.func_211761_a(p_211760_1_, p_211760_6_, p_211760_7_, p_211760_2_);
   }

   private boolean isSameAs(IFluidState p_211758_1_) {
      return p_211758_1_.getFluid().isEquivalentTo(this) && p_211758_1_.isSource();
   }

   protected abstract int getSlopeFindDistance(IWorldReaderBase p_185698_1_);

   private int func_207936_a(IWorldReaderBase p_207936_1_, BlockPos p_207936_2_) {
      int i = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_207936_2_.offset(enumfacing);
         IFluidState ifluidstate = p_207936_1_.getFluidState(blockpos);
         if (this.isSameAs(ifluidstate)) {
            ++i;
         }
      }

      return i;
   }

   protected Map<EnumFacing, IFluidState> func_205572_b(IWorldReaderBase p_205572_1_, BlockPos p_205572_2_, IBlockState p_205572_3_) {
      int i = 1000;
      Map<EnumFacing, IFluidState> map = Maps.newEnumMap(EnumFacing.class);
      Short2ObjectMap<Pair<IBlockState, IFluidState>> short2objectmap = new Short2ObjectOpenHashMap<>();
      Short2BooleanMap short2booleanmap = new Short2BooleanOpenHashMap();

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_205572_2_.offset(enumfacing);
         short short1 = func_212752_a(p_205572_2_, blockpos);
         Pair<IBlockState, IFluidState> pair = short2objectmap.computeIfAbsent(short1, (p_212755_2_) -> {
            IBlockState iblockstate1 = p_205572_1_.getBlockState(blockpos);
            return Pair.of(iblockstate1, iblockstate1.getFluidState());
         });
         IBlockState iblockstate = pair.getFirst();
         IFluidState ifluidstate = pair.getSecond();
         IFluidState ifluidstate1 = this.calculateCorrectFlowingState(p_205572_1_, blockpos, iblockstate);
         if (this.func_211760_a(p_205572_1_, ifluidstate1.getFluid(), p_205572_2_, p_205572_3_, enumfacing, blockpos, iblockstate, ifluidstate)) {
            BlockPos blockpos1 = blockpos.down();
            boolean flag = short2booleanmap.computeIfAbsent(short1, (p_212753_5_) -> {
               IBlockState iblockstate1 = p_205572_1_.getBlockState(blockpos1);
               return this.func_211759_a(p_205572_1_, this.getFlowingFluid(), blockpos, iblockstate, blockpos1, iblockstate1);
            });
            int j;
            if (flag) {
               j = 0;
            } else {
               j = this.func_205571_a(p_205572_1_, blockpos, 1, enumfacing.getOpposite(), iblockstate, p_205572_2_, short2objectmap, short2booleanmap);
            }

            if (j < i) {
               map.clear();
            }

            if (j <= i) {
               map.put(enumfacing, ifluidstate1);
               i = j;
            }
         }
      }

      return map;
   }

   private boolean func_211761_a(IBlockReader p_211761_1_, BlockPos p_211761_2_, IBlockState p_211761_3_, Fluid p_211761_4_) {
      Block block = p_211761_3_.getBlock();
      if (block instanceof ILiquidContainer) {
         return ((ILiquidContainer)block).canContainFluid(p_211761_1_, p_211761_2_, p_211761_3_, p_211761_4_);
      } else if (!(block instanceof BlockDoor) && block != Blocks.SIGN && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN) {
         Material material = p_211761_3_.getMaterial();
         if (material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.OCEAN_PLANT && material != Material.SEA_GRASS) {
            return !material.blocksMovement();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canFlow(IBlockReader p_205570_1_, BlockPos p_205570_2_, IBlockState p_205570_3_, EnumFacing p_205570_4_, BlockPos p_205570_5_, IBlockState p_205570_6_, IFluidState p_205570_7_, Fluid p_205570_8_) {
      return p_205570_7_.canOtherFlowInto(p_205570_8_, p_205570_4_) && this.func_212751_a(p_205570_4_, p_205570_1_, p_205570_2_, p_205570_3_, p_205570_5_, p_205570_6_) && this.func_211761_a(p_205570_1_, p_205570_5_, p_205570_6_, p_205570_8_);
   }

   protected abstract int getLevelDecreasePerBlock(IWorldReaderBase p_204528_1_);

   protected int getTickRate(World p_205578_1_, IFluidState p_205578_2_, IFluidState p_205578_3_) {
      return this.getTickRate(p_205578_1_);
   }

   public void tick(World p_207191_1_, BlockPos p_207191_2_, IFluidState p_207191_3_) {
      if (!p_207191_3_.isSource()) {
         IFluidState ifluidstate = this.calculateCorrectFlowingState(p_207191_1_, p_207191_2_, p_207191_1_.getBlockState(p_207191_2_));
         int i = this.getTickRate(p_207191_1_, p_207191_3_, ifluidstate);
         if (ifluidstate.isEmpty()) {
            p_207191_3_ = ifluidstate;
            p_207191_1_.setBlockState(p_207191_2_, Blocks.AIR.getDefaultState(), 3);
         } else if (!ifluidstate.equals(p_207191_3_)) {
            p_207191_3_ = ifluidstate;
            IBlockState iblockstate = ifluidstate.getBlockState();
            p_207191_1_.setBlockState(p_207191_2_, iblockstate, 2);
            p_207191_1_.getPendingFluidTicks().scheduleTick(p_207191_2_, ifluidstate.getFluid(), i);
            p_207191_1_.notifyNeighborsOfStateChange(p_207191_2_, iblockstate.getBlock());
         }
      }

      this.flowAround(p_207191_1_, p_207191_2_, p_207191_3_);
   }

   protected static int getLevelFromState(IFluidState p_207205_0_) {
      return p_207205_0_.isSource() ? 0 : 8 - Math.min(p_207205_0_.getLevel(), 8) + (p_207205_0_.get(FALLING) ? 8 : 0);
   }

   public float getHeight(IFluidState p_207181_1_) {
      return (float)p_207181_1_.getLevel() / 9.0F;
   }
}
