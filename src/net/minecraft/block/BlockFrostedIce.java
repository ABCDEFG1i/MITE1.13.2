package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFrostedIce extends BlockIce {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

   public BlockFrostedIce(Block.Properties p_i48394_1_) {
      super(p_i48394_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if ((p_196267_4_.nextInt(3) == 0 || this.func_196456_a(p_196267_2_, p_196267_3_, 4)) && p_196267_2_.getLight(p_196267_3_) > 11 - p_196267_1_.get(AGE) - p_196267_1_.getOpacity(p_196267_2_, p_196267_3_) && this.func_196455_e(p_196267_1_, p_196267_2_, p_196267_3_)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(EnumFacing enumfacing : EnumFacing.values()) {
               blockpos$pooledmutableblockpos.setPos(p_196267_3_).move(enumfacing);
               IBlockState iblockstate = p_196267_2_.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate.getBlock() == this && !this.func_196455_e(iblockstate, p_196267_2_, blockpos$pooledmutableblockpos)) {
                  p_196267_2_.getPendingBlockTicks().scheduleTick(blockpos$pooledmutableblockpos, this, MathHelper.nextInt(p_196267_4_, 20, 40));
               }
            }
         }

      } else {
         p_196267_2_.getPendingBlockTicks().scheduleTick(p_196267_3_, this, MathHelper.nextInt(p_196267_4_, 20, 40));
      }
   }

   private boolean func_196455_e(IBlockState p_196455_1_, World p_196455_2_, BlockPos p_196455_3_) {
      int i = p_196455_1_.get(AGE);
      if (i < 3) {
         p_196455_2_.setBlockState(p_196455_3_, p_196455_1_.with(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.turnIntoWater(p_196455_1_, p_196455_2_, p_196455_3_);
         return true;
      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (p_189540_4_ == this && this.func_196456_a(p_189540_2_, p_189540_3_, 2)) {
         this.turnIntoWater(p_189540_1_, p_189540_2_, p_189540_3_);
      }

      super.neighborChanged(p_189540_1_, p_189540_2_, p_189540_3_, p_189540_4_, p_189540_5_);
   }

   private boolean func_196456_a(IBlockReader p_196456_1_, BlockPos p_196456_2_, int p_196456_3_) {
      int i = 0;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            blockpos$pooledmutableblockpos.setPos(p_196456_2_).move(enumfacing);
            if (p_196456_1_.getBlockState(blockpos$pooledmutableblockpos).getBlock() == this) {
               ++i;
               if (i >= p_196456_3_) {
                  boolean flag = false;
                  return flag;
               }
            }
         }

         return true;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, IBlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }
}
