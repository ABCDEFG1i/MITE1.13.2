package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEndRod extends BlockDirectional {
   protected static final VoxelShape END_ROD_VERTICAL_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape END_ROD_NS_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape END_ROD_EW_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected BlockEndRod(Block.Properties p_i48404_1_) {
      super(p_i48404_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.UP));
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.with(FACING, p_185471_2_.mirror(p_185471_1_.get(FACING)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch(p_196244_1_.get(FACING).getAxis()) {
      case X:
      default:
         return END_ROD_EW_AABB;
      case Z:
         return END_ROD_NS_AABB;
      case Y:
         return END_ROD_VERTICAL_AABB;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      EnumFacing enumfacing = p_196258_1_.getFace();
      IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().offset(enumfacing.getOpposite()));
      return iblockstate.getBlock() == this && iblockstate.get(FACING) == enumfacing ? this.getDefaultState().with(FACING, enumfacing.getOpposite()) : this.getDefaultState().with(FACING, enumfacing);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      EnumFacing enumfacing = p_180655_1_.get(FACING);
      double d0 = (double)p_180655_3_.getX() + 0.55D - (double)(p_180655_4_.nextFloat() * 0.1F);
      double d1 = (double)p_180655_3_.getY() + 0.55D - (double)(p_180655_4_.nextFloat() * 0.1F);
      double d2 = (double)p_180655_3_.getZ() + 0.55D - (double)(p_180655_4_.nextFloat() * 0.1F);
      double d3 = (double)(0.4F - (p_180655_4_.nextFloat() + p_180655_4_.nextFloat()) * 0.4F);
      if (p_180655_4_.nextInt(5) == 0) {
         p_180655_2_.spawnParticle(Particles.END_ROD, d0 + (double)enumfacing.getXOffset() * d3, d1 + (double)enumfacing.getYOffset() * d3, d2 + (double)enumfacing.getZOffset() * d3, p_180655_4_.nextGaussian() * 0.005D, p_180655_4_.nextGaussian() * 0.005D, p_180655_4_.nextGaussian() * 0.005D);
      }

   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.NORMAL;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
