package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY_1_4;

   protected BlockRedstoneRepeater(Block.Properties p_i48340_1_) {
      super(p_i48340_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(DELAY, Integer.valueOf(1)).with(LOCKED, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)));
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (!p_196250_4_.capabilities.allowEdit) {
         return false;
      } else {
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_.cycle(DELAY), 3);
         return true;
      }
   }

   protected int getDelay(IBlockState p_196346_1_) {
      return p_196346_1_.get(DELAY) * 2;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = super.getStateForPlacement(p_196258_1_);
      return iblockstate.with(LOCKED, Boolean.valueOf(this.isLocked(p_196258_1_.getWorld(), p_196258_1_.getPos(), iblockstate)));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_4_.isRemote() && p_196271_2_.getAxis() != p_196271_1_.get(HORIZONTAL_FACING).getAxis() ? p_196271_1_.with(LOCKED, Boolean.valueOf(this.isLocked(p_196271_4_, p_196271_5_, p_196271_1_))) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isLocked(IWorldReaderBase p_176405_1_, BlockPos p_176405_2_, IBlockState p_176405_3_) {
      return this.getPowerOnSides(p_176405_1_, p_176405_2_, p_176405_3_) > 0;
   }

   protected boolean isAlternateInput(IBlockState p_185545_1_) {
      return isDiode(p_185545_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.get(POWERED)) {
         EnumFacing enumfacing = p_180655_1_.get(HORIZONTAL_FACING);
         double d0 = (double)((float)p_180655_3_.getX() + 0.5F) + (double)(p_180655_4_.nextFloat() - 0.5F) * 0.2D;
         double d1 = (double)((float)p_180655_3_.getY() + 0.4F) + (double)(p_180655_4_.nextFloat() - 0.5F) * 0.2D;
         double d2 = (double)((float)p_180655_3_.getZ() + 0.5F) + (double)(p_180655_4_.nextFloat() - 0.5F) * 0.2D;
         float f = -5.0F;
         if (p_180655_4_.nextBoolean()) {
            f = (float)(p_180655_1_.get(DELAY) * 2 - 1);
         }

         f = f / 16.0F;
         double d3 = (double)(f * (float)enumfacing.getXOffset());
         double d4 = (double)(f * (float)enumfacing.getZOffset());
         p_180655_2_.spawnParticle(RedstoneParticleData.REDSTONE_DUST, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, DELAY, LOCKED, POWERED);
   }
}
