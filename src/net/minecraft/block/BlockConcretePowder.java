package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockConcretePowder extends BlockFalling {
   private final IBlockState solidifiedState;

   public BlockConcretePowder(Block p_i48423_1_, Block.Properties p_i48423_2_) {
      super(p_i48423_2_);
      this.solidifiedState = p_i48423_1_.getDefaultState();
   }

   public void onEndFalling(World p_176502_1_, BlockPos p_176502_2_, IBlockState p_176502_3_, IBlockState p_176502_4_) {
      if (func_212566_x(p_176502_4_)) {
         p_176502_1_.setBlockState(p_176502_2_, this.solidifiedState, 3);
      }

   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      return !func_212566_x(iblockreader.getBlockState(blockpos)) && !isTouchingLiquid(iblockreader, blockpos) ? super.getStateForPlacement(p_196258_1_) : this.solidifiedState;
   }

   private static boolean isTouchingLiquid(IBlockReader p_196441_0_, BlockPos p_196441_1_) {
      boolean flag = false;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_196441_1_);

      for(EnumFacing enumfacing : EnumFacing.values()) {
         IBlockState iblockstate = p_196441_0_.getBlockState(blockpos$mutableblockpos);
         if (enumfacing != EnumFacing.DOWN || func_212566_x(iblockstate)) {
            blockpos$mutableblockpos.setPos(p_196441_1_).move(enumfacing);
            iblockstate = p_196441_0_.getBlockState(blockpos$mutableblockpos);
            if (func_212566_x(iblockstate) && !Block.doesSideFillSquare(iblockstate.getCollisionShape(p_196441_0_, p_196441_1_), enumfacing.getOpposite())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private static boolean func_212566_x(IBlockState p_212566_0_) {
      return p_212566_0_.getFluidState().isTagged(FluidTags.WATER);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return isTouchingLiquid(p_196271_4_, p_196271_5_) ? this.solidifiedState : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }
}
