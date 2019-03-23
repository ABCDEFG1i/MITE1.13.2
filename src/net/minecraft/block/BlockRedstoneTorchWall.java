package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneTorchWall extends BlockRedstoneTorch {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty REDSTONE_TORCH_LIT = BlockRedstoneTorch.LIT;

   protected BlockRedstoneTorchWall(Block.Properties p_i48341_1_) {
      super(p_i48341_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(REDSTONE_TORCH_LIT, Boolean.valueOf(true)));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return Blocks.WALL_TORCH.getShape(p_196244_1_, p_196244_2_, p_196244_3_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return Blocks.WALL_TORCH.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return Blocks.WALL_TORCH.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = Blocks.WALL_TORCH.getStateForPlacement(p_196258_1_);
      return iblockstate == null ? null : this.getDefaultState().with(FACING, iblockstate.get(FACING));
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.get(REDSTONE_TORCH_LIT)) {
         EnumFacing enumfacing = p_180655_1_.get(FACING).getOpposite();
         double d0 = 0.27D;
         double d1 = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)enumfacing.getXOffset();
         double d2 = (double)p_180655_3_.getY() + 0.7D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double d3 = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)enumfacing.getZOffset();
         p_180655_2_.spawnParticle(RedstoneParticleData.REDSTONE_DUST, d1, d2, d3, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean shouldBeOff(World p_176597_1_, BlockPos p_176597_2_, IBlockState p_176597_3_) {
      EnumFacing enumfacing = p_176597_3_.get(FACING).getOpposite();
      return p_176597_1_.isSidePowered(p_176597_2_.offset(enumfacing), enumfacing);
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(REDSTONE_TORCH_LIT) && p_180656_1_.get(FACING) != p_180656_4_ ? 15 : 0;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return Blocks.WALL_TORCH.rotate(p_185499_1_, p_185499_2_);
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return Blocks.WALL_TORCH.mirror(p_185471_1_, p_185471_2_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, REDSTONE_TORCH_LIT);
   }
}
