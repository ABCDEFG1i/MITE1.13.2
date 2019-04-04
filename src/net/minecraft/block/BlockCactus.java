package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   protected static final VoxelShape field_196400_b = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape field_196401_c = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   protected BlockCactus(Block.Properties p_i48435_1_) {
      super(p_i48435_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_1_.isValidPosition(p_196267_2_, p_196267_3_)) {
         p_196267_2_.destroyBlock(p_196267_3_, true);
      } else {
         BlockPos blockpos = p_196267_3_.up();
         if (p_196267_2_.isAirBlock(blockpos)) {
            int i;
            for(i = 1; p_196267_2_.getBlockState(p_196267_3_.down(i)).getBlock() == this; ++i) {
            }

            if (i < 3) {
               int j = p_196267_1_.get(AGE);
               if (j == 15) {
                  p_196267_2_.setBlockState(blockpos, this.getDefaultState());
                  IBlockState iblockstate = p_196267_1_.with(AGE, Integer.valueOf(0));
                  p_196267_2_.setBlockState(p_196267_3_, iblockstate, 4);
                  iblockstate.neighborChanged(p_196267_2_, blockpos, this, p_196267_3_);
               } else {
                  p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(AGE, Integer.valueOf(j + 1)), 4);
               }

            }
         }
      }
   }

   public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
      return field_196400_b;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return field_196401_c;
   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return true;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.offset(enumfacing));
         Material material = iblockstate.getMaterial();
         if (material.isSolid() || p_196260_2_.getFluidState(p_196260_3_.offset(enumfacing)).isTagged(FluidTags.LAVA)) {
            return false;
         }
      }

      Block block = p_196260_2_.getBlockState(p_196260_3_.down()).getBlock();
      return (block == Blocks.CACTUS || block == Blocks.SAND || block == Blocks.RED_SAND) && !p_196260_2_.getBlockState(p_196260_3_.up()).getMaterial().isLiquid();
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      p_196262_4_.attackEntityFrom(DamageSource.CACTUS, 1.0F);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
