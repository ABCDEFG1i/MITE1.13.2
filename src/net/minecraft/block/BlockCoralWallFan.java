package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockCoralWallFan extends BlockCoralWallFanDead {
   private final Block deadBlock;

   protected BlockCoralWallFan(Block p_i49774_1_, Block.Properties p_i49774_2_) {
      super(p_i49774_2_);
      this.deadBlock = p_i49774_1_;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      this.func_212558_a(p_196259_1_, p_196259_2_, p_196259_3_);
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!func_212557_b_(p_196267_1_, p_196267_2_, p_196267_3_)) {
         p_196267_2_.setBlockState(p_196267_3_, this.deadBlock.getDefaultState().with(field_212560_b, Boolean.valueOf(false)).with(FACING, p_196267_1_.get(FACING)), 2);
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (p_196271_1_.get(field_212560_b)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         }

         this.func_212558_a(p_196271_1_, p_196271_4_, p_196271_5_);
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }
}
