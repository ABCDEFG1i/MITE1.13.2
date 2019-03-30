package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockDirtSnowy extends Block {
   public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

   protected BlockDirtSnowy(Block.Properties p_i48327_1_) {
      super(p_i48327_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(SNOWY, Boolean.valueOf(false)));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ != EnumFacing.UP) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         Block block = p_196271_3_.getBlock();
         return p_196271_1_.with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Block block = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().up()).getBlock();
      return this.getDefaultState().with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(SNOWY);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Blocks.DIRT;
   }
}
