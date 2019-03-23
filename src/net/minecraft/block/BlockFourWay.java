package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockFourWay extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final Map<EnumFacing, BooleanProperty> FACING_TO_PROPERTY_MAP = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199775_0_) -> {
      return p_199775_0_.getKey().getAxis().isHorizontal();
   }).collect(Util.toMapCollector());
   protected final VoxelShape[] field_196410_A;
   protected final VoxelShape[] field_196412_B;

   protected BlockFourWay(float p_i48420_1_, float p_i48420_2_, float p_i48420_3_, float p_i48420_4_, float p_i48420_5_, Block.Properties p_i48420_6_) {
      super(p_i48420_6_);
      this.field_196410_A = this.func_196408_a(p_i48420_1_, p_i48420_2_, p_i48420_5_, 0.0F, p_i48420_5_);
      this.field_196412_B = this.func_196408_a(p_i48420_1_, p_i48420_2_, p_i48420_3_, 0.0F, p_i48420_4_);
   }

   protected VoxelShape[] func_196408_a(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_) {
      float f = 8.0F - p_196408_1_;
      float f1 = 8.0F + p_196408_1_;
      float f2 = 8.0F - p_196408_2_;
      float f3 = 8.0F + p_196408_2_;
      VoxelShape voxelshape = Block.makeCuboidShape((double)f, 0.0D, (double)f, (double)f1, (double)p_196408_3_, (double)f1);
      VoxelShape voxelshape1 = Block.makeCuboidShape((double)f2, (double)p_196408_4_, 0.0D, (double)f3, (double)p_196408_5_, (double)f3);
      VoxelShape voxelshape2 = Block.makeCuboidShape((double)f2, (double)p_196408_4_, (double)f2, (double)f3, (double)p_196408_5_, 16.0D);
      VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, (double)p_196408_4_, (double)f2, (double)f3, (double)p_196408_5_, (double)f3);
      VoxelShape voxelshape4 = Block.makeCuboidShape((double)f2, (double)p_196408_4_, (double)f2, 16.0D, (double)p_196408_5_, (double)f3);
      VoxelShape voxelshape5 = VoxelShapes.func_197872_a(voxelshape1, voxelshape4);
      VoxelShape voxelshape6 = VoxelShapes.func_197872_a(voxelshape2, voxelshape3);
      VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.func_197880_a(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.func_197872_a(voxelshape2, voxelshape1), VoxelShapes.func_197872_a(voxelshape3, voxelshape1), VoxelShapes.func_197872_a(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.func_197872_a(voxelshape2, voxelshape4), VoxelShapes.func_197872_a(voxelshape3, voxelshape4), VoxelShapes.func_197872_a(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.func_197872_a(voxelshape2, voxelshape5), VoxelShapes.func_197872_a(voxelshape3, voxelshape5), VoxelShapes.func_197872_a(voxelshape6, voxelshape5)};

      for(int i = 0; i < 16; ++i) {
         avoxelshape[i] = VoxelShapes.func_197872_a(voxelshape, avoxelshape[i]);
      }

      return avoxelshape;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return this.field_196412_B[this.getIndex(p_196244_1_)];
   }

   public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
      return this.field_196410_A[this.getIndex(p_196268_1_)];
   }

   private static int getMask(EnumFacing p_196407_0_) {
      return 1 << p_196407_0_.getHorizontalIndex();
   }

   protected int getIndex(IBlockState p_196406_1_) {
      int i = 0;
      if (p_196406_1_.get(NORTH)) {
         i |= getMask(EnumFacing.NORTH);
      }

      if (p_196406_1_.get(EAST)) {
         i |= getMask(EnumFacing.EAST);
      }

      if (p_196406_1_.get(SOUTH)) {
         i |= getMask(EnumFacing.SOUTH);
      }

      if (p_196406_1_.get(WEST)) {
         i |= getMask(EnumFacing.WEST);
      }

      return i;
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      if (p_204508_3_.get(WATERLOGGED)) {
         p_204508_1_.setBlockState(p_204508_2_, p_204508_3_.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, IBlockState p_204510_3_, Fluid p_204510_4_) {
      return !p_204510_3_.get(WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, IBlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!p_204509_3_.get(WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
         if (!p_204509_1_.isRemote()) {
            p_204509_1_.setBlockState(p_204509_2_, p_204509_3_.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, p_204509_4_.getFluid(), p_204509_4_.getFluid().getTickRate(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
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
}
