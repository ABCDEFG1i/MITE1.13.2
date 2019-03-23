package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneWire extends Block {
   public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
   public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
   public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
   public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   public static final Map<EnumFacing, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, NORTH, EnumFacing.EAST, EAST, EnumFacing.SOUTH, SOUTH, EnumFacing.WEST, WEST));
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   private boolean canProvidePower = true;
   private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

   public BlockRedstoneWire(Block.Properties p_i48344_1_) {
      super(p_i48344_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE).with(POWER, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPES[getAABBIndex(p_196244_1_)];
   }

   private static int getAABBIndex(IBlockState p_185699_0_) {
      int i = 0;
      boolean flag = p_185699_0_.get(NORTH) != RedstoneSide.NONE;
      boolean flag1 = p_185699_0_.get(EAST) != RedstoneSide.NONE;
      boolean flag2 = p_185699_0_.get(SOUTH) != RedstoneSide.NONE;
      boolean flag3 = p_185699_0_.get(WEST) != RedstoneSide.NONE;
      if (flag || flag2 && !flag && !flag1 && !flag3) {
         i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
      }

      if (flag1 || flag3 && !flag && !flag1 && !flag2) {
         i |= 1 << EnumFacing.EAST.getHorizontalIndex();
      }

      if (flag2 || flag && !flag1 && !flag2 && !flag3) {
         i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
      }

      if (flag3 || flag1 && !flag && !flag2 && !flag3) {
         i |= 1 << EnumFacing.WEST.getHorizontalIndex();
      }

      return i;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, EnumFacing.WEST)).with(EAST, this.getSide(iblockreader, blockpos, EnumFacing.EAST)).with(NORTH, this.getSide(iblockreader, blockpos, EnumFacing.NORTH)).with(SOUTH, this.getSide(iblockreader, blockpos, EnumFacing.SOUTH));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == EnumFacing.DOWN) {
         return p_196271_1_;
      } else {
         return p_196271_2_ == EnumFacing.UP ? p_196271_1_.with(WEST, this.getSide(p_196271_4_, p_196271_5_, EnumFacing.WEST)).with(EAST, this.getSide(p_196271_4_, p_196271_5_, EnumFacing.EAST)).with(NORTH, this.getSide(p_196271_4_, p_196271_5_, EnumFacing.NORTH)).with(SOUTH, this.getSide(p_196271_4_, p_196271_5_, EnumFacing.SOUTH)) : p_196271_1_.with(FACING_PROPERTY_MAP.get(p_196271_2_), this.getSide(p_196271_4_, p_196271_5_, p_196271_2_));
      }
   }

   public void updateDiagonalNeighbors(IBlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_) {
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = p_196248_1_.get(FACING_PROPERTY_MAP.get(enumfacing));
            if (redstoneside != RedstoneSide.NONE && p_196248_2_.getBlockState(blockpos$pooledmutableblockpos.setPos(p_196248_3_).move(enumfacing)).getBlock() != this) {
               blockpos$pooledmutableblockpos.move(EnumFacing.DOWN);
               IBlockState iblockstate = p_196248_2_.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos = blockpos$pooledmutableblockpos.offset(enumfacing.getOpposite());
                  IBlockState iblockstate1 = iblockstate.updatePostPlacement(enumfacing.getOpposite(), p_196248_2_.getBlockState(blockpos), p_196248_2_, blockpos$pooledmutableblockpos, blockpos);
                  replaceBlock(iblockstate, iblockstate1, p_196248_2_, blockpos$pooledmutableblockpos, p_196248_4_);
               }

               blockpos$pooledmutableblockpos.setPos(p_196248_3_).move(enumfacing).move(EnumFacing.UP);
               IBlockState iblockstate3 = p_196248_2_.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate3.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos1 = blockpos$pooledmutableblockpos.offset(enumfacing.getOpposite());
                  IBlockState iblockstate2 = iblockstate3.updatePostPlacement(enumfacing.getOpposite(), p_196248_2_.getBlockState(blockpos1), p_196248_2_, blockpos$pooledmutableblockpos, blockpos1);
                  replaceBlock(iblockstate3, iblockstate2, p_196248_2_, blockpos$pooledmutableblockpos, p_196248_4_);
               }
            }
         }
      }

   }

   private RedstoneSide getSide(IBlockReader p_208074_1_, BlockPos p_208074_2_, EnumFacing p_208074_3_) {
      BlockPos blockpos = p_208074_2_.offset(p_208074_3_);
      IBlockState iblockstate = p_208074_1_.getBlockState(p_208074_2_.offset(p_208074_3_));
      IBlockState iblockstate1 = p_208074_1_.getBlockState(p_208074_2_.up());
      if (!iblockstate1.isNormalCube()) {
         boolean flag = p_208074_1_.getBlockState(blockpos).isTopSolid() || p_208074_1_.getBlockState(blockpos).getBlock() == Blocks.GLOWSTONE;
         if (flag && canConnectUpwardsTo(p_208074_1_.getBlockState(blockpos.up()))) {
            if (iblockstate.isBlockNormalCube()) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !canConnectTo(p_208074_1_.getBlockState(blockpos), p_208074_3_) && (iblockstate.isNormalCube() || !canConnectUpwardsTo(p_208074_1_.getBlockState(blockpos.down()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      return iblockstate.isTopSolid() || iblockstate.getBlock() == Blocks.GLOWSTONE;
   }

   private IBlockState updateSurroundingRedstone(World p_176338_1_, BlockPos p_176338_2_, IBlockState p_176338_3_) {
      p_176338_3_ = this.func_212568_b(p_176338_1_, p_176338_2_, p_176338_3_);
      List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for(BlockPos blockpos : list) {
         p_176338_1_.notifyNeighborsOfStateChange(blockpos, this);
      }

      return p_176338_3_;
   }

   private IBlockState func_212568_b(World p_212568_1_, BlockPos p_212568_2_, IBlockState p_212568_3_) {
      IBlockState iblockstate = p_212568_3_;
      int i = p_212568_3_.get(POWER);
      int j = 0;
      j = this.func_212567_a(j, p_212568_3_);
      this.canProvidePower = false;
      int k = p_212568_1_.getRedstonePowerFromNeighbors(p_212568_2_);
      this.canProvidePower = true;
      if (k > 0 && k > j - 1) {
         j = k;
      }

      int l = 0;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_212568_2_.offset(enumfacing);
         boolean flag = blockpos.getX() != p_212568_2_.getX() || blockpos.getZ() != p_212568_2_.getZ();
         IBlockState iblockstate1 = p_212568_1_.getBlockState(blockpos);
         if (flag) {
            l = this.func_212567_a(l, iblockstate1);
         }

         if (iblockstate1.isNormalCube() && !p_212568_1_.getBlockState(p_212568_2_.up()).isNormalCube()) {
            if (flag && p_212568_2_.getY() >= p_212568_2_.getY()) {
               l = this.func_212567_a(l, p_212568_1_.getBlockState(blockpos.up()));
            }
         } else if (!iblockstate1.isNormalCube() && flag && p_212568_2_.getY() <= p_212568_2_.getY()) {
            l = this.func_212567_a(l, p_212568_1_.getBlockState(blockpos.down()));
         }
      }

      if (l > j) {
         j = l - 1;
      } else if (j > 0) {
         --j;
      } else {
         j = 0;
      }

      if (k > j - 1) {
         j = k;
      }

      if (i != j) {
         p_212568_3_ = p_212568_3_.with(POWER, Integer.valueOf(j));
         if (p_212568_1_.getBlockState(p_212568_2_) == iblockstate) {
            p_212568_1_.setBlockState(p_212568_2_, p_212568_3_, 2);
         }

         this.blocksNeedingUpdate.add(p_212568_2_);

         for(EnumFacing enumfacing1 : EnumFacing.values()) {
            this.blocksNeedingUpdate.add(p_212568_2_.offset(enumfacing1));
         }
      }

      return p_212568_3_;
   }

   private void notifyWireNeighborsOfStateChange(World p_176344_1_, BlockPos p_176344_2_) {
      if (p_176344_1_.getBlockState(p_176344_2_).getBlock() == this) {
         p_176344_1_.notifyNeighborsOfStateChange(p_176344_2_, this);

         for(EnumFacing enumfacing : EnumFacing.values()) {
            p_176344_1_.notifyNeighborsOfStateChange(p_176344_2_.offset(enumfacing), this);
         }

      }
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock() && !p_196259_2_.isRemote) {
         this.updateSurroundingRedstone(p_196259_2_, p_196259_3_, p_196259_1_);

         for(EnumFacing enumfacing : EnumFacing.Plane.VERTICAL) {
            p_196259_2_.notifyNeighborsOfStateChange(p_196259_3_.offset(enumfacing), this);
         }

         for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(p_196259_2_, p_196259_3_.offset(enumfacing1));
         }

         for(EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = p_196259_3_.offset(enumfacing2);
            if (p_196259_2_.getBlockState(blockpos).isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(p_196259_2_, blockpos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(p_196259_2_, blockpos.down());
            }
         }

      }
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (!p_196243_2_.isRemote) {
            for(EnumFacing enumfacing : EnumFacing.values()) {
               p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.offset(enumfacing), this);
            }

            this.updateSurroundingRedstone(p_196243_2_, p_196243_3_, p_196243_1_);

            for(EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
               this.notifyWireNeighborsOfStateChange(p_196243_2_, p_196243_3_.offset(enumfacing1));
            }

            for(EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
               BlockPos blockpos = p_196243_3_.offset(enumfacing2);
               if (p_196243_2_.getBlockState(blockpos).isNormalCube()) {
                  this.notifyWireNeighborsOfStateChange(p_196243_2_, blockpos.up());
               } else {
                  this.notifyWireNeighborsOfStateChange(p_196243_2_, blockpos.down());
               }
            }

         }
      }
   }

   private int func_212567_a(int p_212567_1_, IBlockState p_212567_2_) {
      if (p_212567_2_.getBlock() != this) {
         return p_212567_1_;
      } else {
         int i = p_212567_2_.get(POWER);
         return i > p_212567_1_ ? i : p_212567_1_;
      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         if (p_189540_1_.isValidPosition(p_189540_2_, p_189540_3_)) {
            this.updateSurroundingRedstone(p_189540_2_, p_189540_3_, p_189540_1_);
         } else {
            p_189540_1_.dropBlockAsItem(p_189540_2_, p_189540_3_, 0);
            p_189540_2_.removeBlock(p_189540_3_);
         }

      }
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return !this.canProvidePower ? 0 : p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      if (!this.canProvidePower) {
         return 0;
      } else {
         int i = p_180656_1_.get(POWER);
         if (i == 0) {
            return 0;
         } else if (p_180656_4_ == EnumFacing.UP) {
            return i;
         } else {
            EnumSet<EnumFacing> enumset = EnumSet.noneOf(EnumFacing.class);

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               if (this.isPowerSourceAt(p_180656_2_, p_180656_3_, enumfacing)) {
                  enumset.add(enumfacing);
               }
            }

            if (p_180656_4_.getAxis().isHorizontal() && enumset.isEmpty()) {
               return i;
            } else if (enumset.contains(p_180656_4_) && !enumset.contains(p_180656_4_.rotateYCCW()) && !enumset.contains(p_180656_4_.rotateY())) {
               return i;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean isPowerSourceAt(IBlockReader p_176339_1_, BlockPos p_176339_2_, EnumFacing p_176339_3_) {
      BlockPos blockpos = p_176339_2_.offset(p_176339_3_);
      IBlockState iblockstate = p_176339_1_.getBlockState(blockpos);
      boolean flag = iblockstate.isNormalCube();
      boolean flag1 = p_176339_1_.getBlockState(p_176339_2_.up()).isNormalCube();
      if (!flag1 && flag && canConnectUpwardsTo(p_176339_1_, blockpos.up())) {
         return true;
      } else if (canConnectTo(iblockstate, p_176339_3_)) {
         return true;
      } else if (iblockstate.getBlock() == Blocks.REPEATER && iblockstate.get(BlockRedstoneDiode.POWERED) && iblockstate.get(BlockRedstoneDiode.HORIZONTAL_FACING) == p_176339_3_) {
         return true;
      } else {
         return !flag && canConnectUpwardsTo(p_176339_1_, blockpos.down());
      }
   }

   protected static boolean canConnectUpwardsTo(IBlockReader p_176340_0_, BlockPos p_176340_1_) {
      return canConnectUpwardsTo(p_176340_0_.getBlockState(p_176340_1_));
   }

   protected static boolean canConnectUpwardsTo(IBlockState p_176346_0_) {
      return canConnectTo(p_176346_0_, (EnumFacing)null);
   }

   protected static boolean canConnectTo(IBlockState p_176343_0_, @Nullable EnumFacing p_176343_1_) {
      Block block = p_176343_0_.getBlock();
      if (block == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (p_176343_0_.getBlock() == Blocks.REPEATER) {
         EnumFacing enumfacing = p_176343_0_.get(BlockRedstoneRepeater.HORIZONTAL_FACING);
         return enumfacing == p_176343_1_ || enumfacing.getOpposite() == p_176343_1_;
      } else if (Blocks.OBSERVER == p_176343_0_.getBlock()) {
         return p_176343_1_ == p_176343_0_.get(BlockObserver.FACING);
      } else {
         return p_176343_0_.canProvidePower() && p_176343_1_ != null;
      }
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return this.canProvidePower;
   }

   @OnlyIn(Dist.CLIENT)
   public static int colorMultiplier(int p_176337_0_) {
      float f = (float)p_176337_0_ / 15.0F;
      float f1 = f * 0.6F + 0.4F;
      if (p_176337_0_ == 0) {
         f1 = 0.3F;
      }

      float f2 = f * f * 0.7F - 0.5F;
      float f3 = f * f * 0.6F - 0.7F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
      return -16777216 | i << 16 | j << 8 | k;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      int i = p_180655_1_.get(POWER);
      if (i != 0) {
         double d0 = (double)p_180655_3_.getX() + 0.5D + ((double)p_180655_4_.nextFloat() - 0.5D) * 0.2D;
         double d1 = (double)((float)p_180655_3_.getY() + 0.0625F);
         double d2 = (double)p_180655_3_.getZ() + 0.5D + ((double)p_180655_4_.nextFloat() - 0.5D) * 0.2D;
         float f = (float)i / 15.0F;
         float f1 = f * 0.6F + 0.4F;
         float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         p_180655_2_.spawnParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return p_185499_1_.with(NORTH, p_185499_1_.get(SOUTH)).with(EAST, p_185499_1_.get(WEST)).with(SOUTH, p_185499_1_.get(NORTH)).with(WEST, p_185499_1_.get(EAST));
      case COUNTERCLOCKWISE_90:
         return p_185499_1_.with(NORTH, p_185499_1_.get(EAST)).with(EAST, p_185499_1_.get(SOUTH)).with(SOUTH, p_185499_1_.get(WEST)).with(WEST, p_185499_1_.get(NORTH));
      case CLOCKWISE_90:
         return p_185499_1_.with(NORTH, p_185499_1_.get(WEST)).with(EAST, p_185499_1_.get(NORTH)).with(SOUTH, p_185499_1_.get(EAST)).with(WEST, p_185499_1_.get(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return p_185471_1_.with(NORTH, p_185471_1_.get(SOUTH)).with(SOUTH, p_185471_1_.get(NORTH));
      case FRONT_BACK:
         return p_185471_1_.with(EAST, p_185471_1_.get(WEST)).with(WEST, p_185471_1_.get(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
