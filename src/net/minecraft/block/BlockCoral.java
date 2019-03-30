package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCoral extends Block {
   private final Block deadBlock;

   public BlockCoral(Block p_i48893_1_, Block.Properties p_i48893_2_) {
      super(p_i48893_2_);
      this.deadBlock = p_i48893_1_;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!this.canLive(p_196267_2_, p_196267_3_)) {
         p_196267_2_.setBlockState(p_196267_3_, this.deadBlock.getDefaultState(), 2);
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!this.canLive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 60 + p_196271_4_.getRandom().nextInt(40));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected boolean canLive(IBlockReader p_203943_1_, BlockPos p_203943_2_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         IFluidState ifluidstate = p_203943_1_.getFluidState(p_203943_2_.offset(enumfacing));
         if (ifluidstate.isTagged(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      if (!this.canLive(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
         p_196258_1_.getWorld().getPendingBlockTicks().scheduleTick(p_196258_1_.getPos(), this, 60 + p_196258_1_.getWorld().getRandom().nextInt(40));
      }

      return this.getDefaultState();
   }

   protected boolean canSilkHarvest() {
      return true;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return this.deadBlock;
   }
}
