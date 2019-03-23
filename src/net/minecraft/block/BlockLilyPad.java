package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
   protected static final VoxelShape LILY_PAD_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   protected BlockLilyPad(Block.Properties p_i48297_1_) {
      super(p_i48297_1_);
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      super.onEntityCollision(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
      if (p_196262_4_ instanceof EntityBoat) {
         p_196262_2_.destroyBlock(new BlockPos(p_196262_3_), true);
      }

   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return LILY_PAD_AABB;
   }

   protected boolean isValidGround(IBlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      IFluidState ifluidstate = p_200014_2_.getFluidState(p_200014_3_);
      return ifluidstate.getFluid() == Fluids.WATER || p_200014_1_.getMaterial() == Material.ICE;
   }
}
