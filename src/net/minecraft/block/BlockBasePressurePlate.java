package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape UNPRESSED_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AxisAlignedBB PRESSURE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected BlockBasePressurePlate(Block.Properties p_i48445_1_) {
      super(p_i48445_1_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return this.getRedstoneStrength(p_196244_1_) > 0 ? PRESSED_AABB : UNPRESSED_AABB;
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 20;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == EnumFacing.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      return iblockstate.isTopSolid() || iblockstate.getBlock() instanceof BlockFence;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         int i = this.getRedstoneStrength(p_196267_1_);
         if (i > 0) {
            this.updateState(p_196267_2_, p_196267_3_, p_196267_1_, i);
         }

      }
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote) {
         int i = this.getRedstoneStrength(p_196262_1_);
         if (i == 0) {
            this.updateState(p_196262_2_, p_196262_3_, p_196262_1_, i);
         }

      }
   }

   protected void updateState(World p_180666_1_, BlockPos p_180666_2_, IBlockState p_180666_3_, int p_180666_4_) {
      int i = this.computeRedstoneStrength(p_180666_1_, p_180666_2_);
      boolean flag = p_180666_4_ > 0;
      boolean flag1 = i > 0;
      if (p_180666_4_ != i) {
         p_180666_3_ = this.setRedstoneStrength(p_180666_3_, i);
         p_180666_1_.setBlockState(p_180666_2_, p_180666_3_, 2);
         this.updateNeighbors(p_180666_1_, p_180666_2_);
         p_180666_1_.markBlockRangeForRenderUpdate(p_180666_2_, p_180666_2_);
      }

      if (!flag1 && flag) {
         this.playClickOffSound(p_180666_1_, p_180666_2_);
      } else if (flag1 && !flag) {
         this.playClickOnSound(p_180666_1_, p_180666_2_);
      }

      if (flag1) {
         p_180666_1_.getPendingBlockTicks().scheduleTick(new BlockPos(p_180666_2_), this, this.tickRate(p_180666_1_));
      }

   }

   protected abstract void playClickOnSound(IWorld p_185507_1_, BlockPos p_185507_2_);

   protected abstract void playClickOffSound(IWorld p_185508_1_, BlockPos p_185508_2_);

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (this.getRedstoneStrength(p_196243_1_) > 0) {
            this.updateNeighbors(p_196243_2_, p_196243_3_);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   protected void updateNeighbors(World p_176578_1_, BlockPos p_176578_2_) {
      p_176578_1_.notifyNeighborsOfStateChange(p_176578_2_, this);
      p_176578_1_.notifyNeighborsOfStateChange(p_176578_2_.down(), this);
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return this.getRedstoneStrength(p_180656_1_);
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_4_ == EnumFacing.UP ? this.getRedstoneStrength(p_176211_1_) : 0;
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.DESTROY;
   }

   protected abstract int computeRedstoneStrength(World p_180669_1_, BlockPos p_180669_2_);

   protected abstract int getRedstoneStrength(IBlockState p_176576_1_);

   protected abstract IBlockState setRedstoneStrength(IBlockState p_176575_1_, int p_176575_2_);

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
