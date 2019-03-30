package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.Random;

public class BlockChorusPlant extends BlockSixWay {
   protected BlockChorusPlant(Block.Properties p_i48428_1_) {
      super(0.3125F, p_i48428_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)).with(DOWN, Boolean.valueOf(false)));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.makeConnections(p_196258_1_.getWorld(), p_196258_1_.getPos());
   }

   public IBlockState makeConnections(IBlockReader p_196497_1_, BlockPos p_196497_2_) {
      Block block = p_196497_1_.getBlockState(p_196497_2_.down()).getBlock();
      Block block1 = p_196497_1_.getBlockState(p_196497_2_.up()).getBlock();
      Block block2 = p_196497_1_.getBlockState(p_196497_2_.north()).getBlock();
      Block block3 = p_196497_1_.getBlockState(p_196497_2_.east()).getBlock();
      Block block4 = p_196497_1_.getBlockState(p_196497_2_.south()).getBlock();
      Block block5 = p_196497_1_.getBlockState(p_196497_2_.west()).getBlock();
      return this.getDefaultState().with(DOWN, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)).with(UP, Boolean.valueOf(block1 == this || block1 == Blocks.CHORUS_FLOWER)).with(NORTH, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER)).with(EAST, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER)).with(SOUTH, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER)).with(WEST, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         Block block = p_196271_3_.getBlock();
         boolean flag = block == this || block == Blocks.CHORUS_FLOWER || p_196271_2_ == EnumFacing.DOWN && block == Blocks.END_STONE;
         return p_196271_1_.with(FACING_TO_PROPERTY_MAP.get(p_196271_2_), Boolean.valueOf(flag));
      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_1_.isValidPosition(p_196267_2_, p_196267_3_)) {
         p_196267_2_.destroyBlock(p_196267_3_, true);
      }

   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.CHORUS_FRUIT;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return p_196264_2_.nextInt(2);
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      boolean flag = !p_196260_2_.getBlockState(p_196260_3_.up()).isAir() && !iblockstate.isAir();

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = p_196260_3_.offset(enumfacing);
         Block block = p_196260_2_.getBlockState(blockpos).getBlock();
         if (block == this) {
            if (flag) {
               return false;
            }

            Block block1 = p_196260_2_.getBlockState(blockpos.down()).getBlock();
            if (block1 == this || block1 == Blocks.END_STONE) {
               return true;
            }
         }
      }

      Block block2 = iblockstate.getBlock();
      return block2 == this || block2 == Blocks.END_STONE;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
