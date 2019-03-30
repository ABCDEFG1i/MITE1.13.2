package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFarmland extends Block {
   public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE_0_7;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

   protected BlockFarmland(Block.Properties p_i48400_1_) {
      super(p_i48400_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(MOISTURE, Integer.valueOf(0)));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == EnumFacing.UP && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.up());
      return !iblockstate.getMaterial().isSolid() || iblockstate.getBlock() instanceof BlockFenceGate;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return !this.getDefaultState().isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos()) ? Blocks.DIRT.getDefaultState() : super.getStateForPlacement(p_196258_1_);
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return p_200011_2_.getMaxLightLevel();
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_1_.isValidPosition(p_196267_2_, p_196267_3_)) {
         turnToDirt(p_196267_1_, p_196267_2_, p_196267_3_);
      } else {
         int i = p_196267_1_.get(MOISTURE);
         if (!hasWater(p_196267_2_, p_196267_3_) && !p_196267_2_.isRainingAt(p_196267_3_.up())) {
            if (i > 0) {
               p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!hasCrops(p_196267_2_, p_196267_3_)) {
               turnToDirt(p_196267_1_, p_196267_2_, p_196267_3_);
            }
         } else if (i < 7) {
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(MOISTURE, Integer.valueOf(7)), 2);
         }

      }
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      if (!p_180658_1_.isRemote && p_180658_1_.rand.nextFloat() < p_180658_4_ - 0.5F && p_180658_3_ instanceof EntityLivingBase && (p_180658_3_ instanceof EntityPlayer || p_180658_1_.getGameRules().getBoolean("mobGriefing")) && p_180658_3_.width * p_180658_3_.width * p_180658_3_.height > 0.512F) {
         turnToDirt(p_180658_1_.getBlockState(p_180658_2_), p_180658_1_, p_180658_2_);
      }

      super.onFallenUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_);
   }

   public static void turnToDirt(IBlockState p_199610_0_, World p_199610_1_, BlockPos p_199610_2_) {
      p_199610_1_.setBlockState(p_199610_2_, func_199601_a(p_199610_0_, Blocks.DIRT.getDefaultState(), p_199610_1_, p_199610_2_));
   }

   private static boolean hasCrops(IBlockReader p_176529_0_, BlockPos p_176529_1_) {
      Block block = p_176529_0_.getBlockState(p_176529_1_.up()).getBlock();
      return block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockAttachedStem;
   }

   private static boolean hasWater(IWorldReaderBase p_176530_0_, BlockPos p_176530_1_) {
      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_176530_1_.add(-4, 0, -4), p_176530_1_.add(4, 1, 4))) {
         if (p_176530_0_.getFluidState(blockpos$mutableblockpos).isTagged(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Blocks.DIRT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(MOISTURE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
