package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBubbleColumn extends Block implements IBucketPickupHandler {
   public static final BooleanProperty DRAG = BlockStateProperties.DRAG;

   public BlockBubbleColumn(Block.Properties p_i48783_1_) {
      super(p_i48783_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(DRAG, Boolean.valueOf(true)));
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      IBlockState iblockstate = p_196262_2_.getBlockState(p_196262_3_.up());
      if (iblockstate.isAir()) {
         p_196262_4_.onEnterBubbleColumnWithAirAbove(p_196262_1_.get(DRAG));
         if (!p_196262_2_.isRemote) {
            WorldServer worldserver = (WorldServer)p_196262_2_;

            for(int i = 0; i < 2; ++i) {
               worldserver.spawnParticle(Particles.SPLASH, (double)((float)p_196262_3_.getX() + p_196262_2_.rand.nextFloat()), (double)(p_196262_3_.getY() + 1), (double)((float)p_196262_3_.getZ() + p_196262_2_.rand.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               worldserver.spawnParticle(Particles.BUBBLE, (double)((float)p_196262_3_.getX() + p_196262_2_.rand.nextFloat()), (double)(p_196262_3_.getY() + 1), (double)((float)p_196262_3_.getZ() + p_196262_2_.rand.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         p_196262_4_.onEnterBubbleColumn(p_196262_1_.get(DRAG));
      }

   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      placeBubbleColumn(p_196259_2_, p_196259_3_.up(), getDrag(p_196259_2_, p_196259_3_.down()));
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      placeBubbleColumn(p_196267_2_, p_196267_3_.up(), getDrag(p_196267_2_, p_196267_3_));
   }

   public IFluidState getFluidState(IBlockState p_204507_1_) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public static void placeBubbleColumn(IWorld p_203159_0_, BlockPos p_203159_1_, boolean p_203159_2_) {
      if (canHoldBubbleColumn(p_203159_0_, p_203159_1_)) {
         p_203159_0_.setBlockState(p_203159_1_, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(p_203159_2_)), 2);
      }

   }

   public static boolean canHoldBubbleColumn(IWorld p_208072_0_, BlockPos p_208072_1_) {
      IFluidState ifluidstate = p_208072_0_.getFluidState(p_208072_1_);
      return p_208072_0_.getBlockState(p_208072_1_).getBlock() == Blocks.WATER && ifluidstate.getLevel() >= 8 && ifluidstate.isSource();
   }

   private static boolean getDrag(IBlockReader p_203157_0_, BlockPos p_203157_1_) {
      IBlockState iblockstate = p_203157_0_.getBlockState(p_203157_1_);
      Block block = iblockstate.getBlock();
      if (block == Blocks.BUBBLE_COLUMN) {
         return iblockstate.get(DRAG);
      } else {
         return block != Blocks.SOUL_SAND;
      }
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return 5;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      double d0 = (double)p_180655_3_.getX();
      double d1 = (double)p_180655_3_.getY();
      double d2 = (double)p_180655_3_.getZ();
      if (p_180655_1_.get(DRAG)) {
         p_180655_2_.addOptionalParticle(Particles.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
         if (p_180655_4_.nextInt(200) == 0) {
            p_180655_2_.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_180655_4_.nextFloat() * 0.2F, 0.9F + p_180655_4_.nextFloat() * 0.15F, false);
         }
      } else {
         p_180655_2_.addOptionalParticle(Particles.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
         p_180655_2_.addOptionalParticle(Particles.BUBBLE_COLUMN_UP, d0 + (double)p_180655_4_.nextFloat(), d1 + (double)p_180655_4_.nextFloat(), d2 + (double)p_180655_4_.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (p_180655_4_.nextInt(200) == 0) {
            p_180655_2_.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_180655_4_.nextFloat() * 0.2F, 0.9F + p_180655_4_.nextFloat() * 0.15F, false);
         }
      }

   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.WATER.getDefaultState();
      } else {
         if (p_196271_2_ == EnumFacing.DOWN) {
            p_196271_4_.setBlockState(p_196271_5_, Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, Boolean.valueOf(getDrag(p_196271_4_, p_196271_6_))), 2);
         } else if (p_196271_2_ == EnumFacing.UP && p_196271_3_.getBlock() != Blocks.BUBBLE_COLUMN && canHoldBubbleColumn(p_196271_4_, p_196271_6_)) {
            p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, this.tickRate(p_196271_4_));
         }

         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      Block block = p_196260_2_.getBlockState(p_196260_3_.down()).getBlock();
      return block == Blocks.BUBBLE_COLUMN || block == Blocks.MAGMA_BLOCK || block == Blocks.SOUL_SAND;
   }

   public boolean isCollidable() {
      return false;
   }

   public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
      return 0;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.INVISIBLE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(DRAG);
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, IBlockState p_204508_3_) {
      p_204508_1_.setBlockState(p_204508_2_, Blocks.AIR.getDefaultState(), 11);
      return Fluids.WATER;
   }
}
