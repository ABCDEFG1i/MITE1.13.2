package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockSkull extends BlockAbstractSkull {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_0_15;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

   protected BlockSkull(BlockSkull.ISkullType p_i48332_1_, Block.Properties p_i48332_2_) {
      super(p_i48332_1_, p_i48332_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(ROTATION, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public VoxelShape getRenderShape(IBlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return VoxelShapes.func_197880_a();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(ROTATION, Integer.valueOf(MathHelper.floor((double)(p_196258_1_.getPlacementYaw() * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(ROTATION, Integer.valueOf(p_185499_2_.rotate(p_185499_1_.get(ROTATION), 16)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.with(ROTATION, Integer.valueOf(p_185471_2_.mirrorRotation(p_185471_1_.get(ROTATION), 16)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(ROTATION);
   }

   public interface ISkullType {
   }

   public static enum Types implements BlockSkull.ISkullType {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;
   }
}
