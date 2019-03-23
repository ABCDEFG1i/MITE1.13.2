package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSeaPickle extends BlockBush implements IGrowable, IBucketPickupHandler, ILiquidContainer {
   public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES_1_4;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape field_204904_c = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
   protected static final VoxelShape field_204905_t = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
   protected static final VoxelShape field_204906_u = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
   protected static final VoxelShape field_204907_v = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

   protected BlockSeaPickle(Block.Properties p_i48924_1_) {
      super(p_i48924_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(PICKLES, Integer.valueOf(1)).with(WATERLOGGED, Boolean.valueOf(true)));
   }

   public int getLightValue(IBlockState p_149750_1_) {
      return this.isInBadEnvironment(p_149750_1_) ? 0 : super.getLightValue(p_149750_1_) + 3 * p_149750_1_.get(PICKLES);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      if (iblockstate.getBlock() == this) {
         return iblockstate.with(PICKLES, Integer.valueOf(Math.min(4, iblockstate.get(PICKLES) + 1)));
      } else {
         IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
         boolean flag = ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
         return super.getStateForPlacement(p_196258_1_).with(WATERLOGGED, Boolean.valueOf(flag));
      }
   }

   private boolean isInBadEnvironment(IBlockState p_204901_1_) {
      return !p_204901_1_.get(WATERLOGGED);
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return !p_200014_1_.getCollisionShape(p_200014_2_, p_200014_3_).func_212434_a(EnumFacing.UP).isEmpty();
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      return this.isValidGround(p_196260_2_.getBlockState(blockpos), p_196260_2_, blockpos);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (p_196271_1_.get(WATERLOGGED)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         }

         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_2_.getItem().getItem() == this.asItem() && p_196253_1_.get(PICKLES) < 4 ? true : super.isReplaceable(p_196253_1_, p_196253_2_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch(p_196244_1_.get(PICKLES)) {
      case 1:
      default:
         return field_204904_c;
      case 2:
         return field_204905_t;
      case 3:
         return field_204906_u;
      case 4:
         return field_204907_v;
      }
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

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(PICKLES, WATERLOGGED);
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return p_196264_1_.get(PICKLES);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, IBlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, IBlockState p_180670_4_) {
      return true;
   }

   public void grow(World p_176474_1_, Random p_176474_2_, BlockPos p_176474_3_, IBlockState p_176474_4_) {
      if (!this.isInBadEnvironment(p_176474_4_) && p_176474_1_.getBlockState(p_176474_3_.down()).isIn(BlockTags.CORAL_BLOCKS)) {
         int i = 5;
         int j = 1;
         int k = 2;
         int l = 0;
         int i1 = p_176474_3_.getX() - 2;
         int j1 = 0;

         for(int k1 = 0; k1 < 5; ++k1) {
            for(int l1 = 0; l1 < j; ++l1) {
               int i2 = 2 + p_176474_3_.getY() - 1;

               for(int j2 = i2 - 2; j2 < i2; ++j2) {
                  BlockPos blockpos = new BlockPos(i1 + k1, j2, p_176474_3_.getZ() - j1 + l1);
                  if (blockpos != p_176474_3_ && p_176474_2_.nextInt(6) == 0 && p_176474_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
                     IBlockState iblockstate = p_176474_1_.getBlockState(blockpos.down());
                     if (iblockstate.isIn(BlockTags.CORAL_BLOCKS)) {
                        p_176474_1_.setBlockState(blockpos, Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, Integer.valueOf(p_176474_2_.nextInt(4) + 1)), 3);
                     }
                  }
               }
            }

            if (l < 2) {
               j += 2;
               ++j1;
            } else {
               j -= 2;
               --j1;
            }

            ++l;
         }

         p_176474_1_.setBlockState(p_176474_3_, p_176474_4_.with(PICKLES, Integer.valueOf(4)), 2);
      }

   }
}
