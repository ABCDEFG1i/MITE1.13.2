package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockHorizontalFace extends BlockHorizontal {
   public static final EnumProperty<AttachFace> FACE = BlockStateProperties.FACE;

   protected BlockHorizontalFace(Block.Properties p_i48402_1_) {
      super(p_i48402_1_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      EnumFacing enumfacing = func_196365_i(p_196260_1_).getOpposite();
      BlockPos blockpos = p_196260_3_.offset(enumfacing);
      IBlockState iblockstate = p_196260_2_.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (isExceptionBlockForAttaching(block)) {
         return false;
      } else {
         boolean flag = iblockstate.getBlockFaceShape(p_196260_2_, blockpos, enumfacing.getOpposite()) == BlockFaceShape.SOLID;
         if (enumfacing == EnumFacing.UP) {
            return block == Blocks.HOPPER || flag;
         } else {
            return !isExceptBlockForAttachWithPiston(block) && flag;
         }
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      for(EnumFacing enumfacing : p_196258_1_.func_196009_e()) {
         IBlockState iblockstate;
         if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            iblockstate = this.getDefaultState().with(FACE, enumfacing == EnumFacing.UP ? AttachFace.CEILING : AttachFace.FLOOR).with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing());
         } else {
            iblockstate = this.getDefaultState().with(FACE, AttachFace.WALL).with(HORIZONTAL_FACING, enumfacing.getOpposite());
         }

         if (iblockstate.isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
            return iblockstate;
         }
      }

      return null;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return func_196365_i(p_196271_1_).getOpposite() == p_196271_2_ && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected static EnumFacing func_196365_i(IBlockState p_196365_0_) {
      switch(p_196365_0_.get(FACE)) {
      case CEILING:
         return EnumFacing.DOWN;
      case FLOOR:
         return EnumFacing.UP;
      default:
         return p_196365_0_.get(HORIZONTAL_FACING);
      }
   }
}
