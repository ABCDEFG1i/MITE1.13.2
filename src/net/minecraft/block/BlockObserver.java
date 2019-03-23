package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockObserver extends BlockDirectional {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public BlockObserver(Block.Properties p_i48358_1_) {
      super(p_i48358_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.SOUTH).with(POWERED, Boolean.valueOf(false)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED);
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (p_196267_1_.get(POWERED)) {
         p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(POWERED, Boolean.valueOf(false)), 2);
      } else {
         p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(POWERED, Boolean.valueOf(true)), 2);
         p_196267_2_.getPendingBlockTicks().scheduleTick(p_196267_3_, this, 2);
      }

      this.updateNeighborsInFront(p_196267_2_, p_196267_3_, p_196267_1_);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(FACING) == p_196271_2_ && !p_196271_1_.get(POWERED)) {
         this.startSignal(p_196271_4_, p_196271_5_);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   private void startSignal(IWorld p_203420_1_, BlockPos p_203420_2_) {
      if (!p_203420_1_.isRemote() && !p_203420_1_.getPendingBlockTicks().isTickScheduled(p_203420_2_, this)) {
         p_203420_1_.getPendingBlockTicks().scheduleTick(p_203420_2_, this, 2);
      }

   }

   protected void updateNeighborsInFront(World p_190961_1_, BlockPos p_190961_2_, IBlockState p_190961_3_) {
      EnumFacing enumfacing = p_190961_3_.get(FACING);
      BlockPos blockpos = p_190961_2_.offset(enumfacing.getOpposite());
      p_190961_1_.neighborChanged(blockpos, this, p_190961_2_);
      p_190961_1_.notifyNeighborsOfStateExcept(blockpos, this, enumfacing);
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(POWERED) && p_180656_1_.get(FACING) == p_180656_4_ ? 15 : 0;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_1_.getBlock() != p_196259_4_.getBlock()) {
         if (!p_196259_2_.isRemote() && p_196259_1_.get(POWERED) && !p_196259_2_.getPendingBlockTicks().isTickScheduled(p_196259_3_, this)) {
            IBlockState iblockstate = p_196259_1_.with(POWERED, Boolean.valueOf(false));
            p_196259_2_.setBlockState(p_196259_3_, iblockstate, 18);
            this.updateNeighborsInFront(p_196259_2_, p_196259_3_, iblockstate);
         }

      }
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (!p_196243_2_.isRemote && p_196243_1_.get(POWERED) && p_196243_2_.getPendingBlockTicks().isTickScheduled(p_196243_3_, this)) {
            this.updateNeighborsInFront(p_196243_2_, p_196243_3_, p_196243_1_.with(POWERED, Boolean.valueOf(false)));
         }

      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.func_196010_d().getOpposite().getOpposite());
   }
}
