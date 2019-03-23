package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockHorizontal {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected BlockRedstoneDiode(Block.Properties p_i48416_1_) {
      super(p_i48416_1_);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).isTopSolid();
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!this.isLocked(p_196267_2_, p_196267_3_, p_196267_1_)) {
         boolean flag = p_196267_1_.get(POWERED);
         boolean flag1 = this.shouldBePowered(p_196267_2_, p_196267_3_, p_196267_1_);
         if (flag && !flag1) {
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag) {
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(POWERED, Boolean.valueOf(true)), 2);
            if (!flag1) {
               p_196267_2_.getPendingBlockTicks().scheduleTick(p_196267_3_, this, this.getDelay(p_196267_1_), TickPriority.HIGH);
            }
         }

      }
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      if (!p_180656_1_.get(POWERED)) {
         return 0;
      } else {
         return p_180656_1_.get(HORIZONTAL_FACING) == p_180656_4_ ? this.getActiveSignal(p_180656_2_, p_180656_3_, p_180656_1_) : 0;
      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (p_189540_1_.isValidPosition(p_189540_2_, p_189540_3_)) {
         this.updateState(p_189540_2_, p_189540_3_, p_189540_1_);
      } else {
         p_189540_1_.dropBlockAsItem(p_189540_2_, p_189540_3_, 0);
         p_189540_2_.removeBlock(p_189540_3_);

         for(EnumFacing enumfacing : EnumFacing.values()) {
            p_189540_2_.notifyNeighborsOfStateChange(p_189540_3_.offset(enumfacing), this);
         }

      }
   }

   protected void updateState(World p_176398_1_, BlockPos p_176398_2_, IBlockState p_176398_3_) {
      if (!this.isLocked(p_176398_1_, p_176398_2_, p_176398_3_)) {
         boolean flag = p_176398_3_.get(POWERED);
         boolean flag1 = this.shouldBePowered(p_176398_1_, p_176398_2_, p_176398_3_);
         if (flag != flag1 && !p_176398_1_.getPendingBlockTicks().isTickPending(p_176398_2_, this)) {
            TickPriority tickpriority = TickPriority.HIGH;
            if (this.isFacingTowardsRepeater(p_176398_1_, p_176398_2_, p_176398_3_)) {
               tickpriority = TickPriority.EXTREMELY_HIGH;
            } else if (flag) {
               tickpriority = TickPriority.VERY_HIGH;
            }

            p_176398_1_.getPendingBlockTicks().scheduleTick(p_176398_2_, this, this.getDelay(p_176398_3_), tickpriority);
         }

      }
   }

   public boolean isLocked(IWorldReaderBase p_176405_1_, BlockPos p_176405_2_, IBlockState p_176405_3_) {
      return false;
   }

   protected boolean shouldBePowered(World p_176404_1_, BlockPos p_176404_2_, IBlockState p_176404_3_) {
      return this.calculateInputStrength(p_176404_1_, p_176404_2_, p_176404_3_) > 0;
   }

   protected int calculateInputStrength(World p_176397_1_, BlockPos p_176397_2_, IBlockState p_176397_3_) {
      EnumFacing enumfacing = p_176397_3_.get(HORIZONTAL_FACING);
      BlockPos blockpos = p_176397_2_.offset(enumfacing);
      int i = p_176397_1_.getRedstonePower(blockpos, enumfacing);
      if (i >= 15) {
         return i;
      } else {
         IBlockState iblockstate = p_176397_1_.getBlockState(blockpos);
         return Math.max(i, iblockstate.getBlock() == Blocks.REDSTONE_WIRE ? iblockstate.get(BlockRedstoneWire.POWER) : 0);
      }
   }

   protected int getPowerOnSides(IWorldReaderBase p_176407_1_, BlockPos p_176407_2_, IBlockState p_176407_3_) {
      EnumFacing enumfacing = p_176407_3_.get(HORIZONTAL_FACING);
      EnumFacing enumfacing1 = enumfacing.rotateY();
      EnumFacing enumfacing2 = enumfacing.rotateYCCW();
      return Math.max(this.getPowerOnSide(p_176407_1_, p_176407_2_.offset(enumfacing1), enumfacing1), this.getPowerOnSide(p_176407_1_, p_176407_2_.offset(enumfacing2), enumfacing2));
   }

   protected int getPowerOnSide(IWorldReaderBase p_176401_1_, BlockPos p_176401_2_, EnumFacing p_176401_3_) {
      IBlockState iblockstate = p_176401_1_.getBlockState(p_176401_2_);
      Block block = iblockstate.getBlock();
      if (this.isAlternateInput(iblockstate)) {
         if (block == Blocks.REDSTONE_BLOCK) {
            return 15;
         } else {
            return block == Blocks.REDSTONE_WIRE ? iblockstate.get(BlockRedstoneWire.POWER) : p_176401_1_.getStrongPower(p_176401_2_, p_176401_3_);
         }
      } else {
         return 0;
      }
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (this.shouldBePowered(p_180633_1_, p_180633_2_, p_180633_3_)) {
         p_180633_1_.getPendingBlockTicks().scheduleTick(p_180633_2_, this, 1);
      }

   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      this.notifyNeighbors(p_196259_2_, p_196259_3_, p_196259_1_);
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         this.func_211326_a(p_196243_2_, p_196243_3_);
         this.notifyNeighbors(p_196243_2_, p_196243_3_, p_196243_1_);
      }
   }

   protected void func_211326_a(World p_211326_1_, BlockPos p_211326_2_) {
   }

   protected void notifyNeighbors(World p_176400_1_, BlockPos p_176400_2_, IBlockState p_176400_3_) {
      EnumFacing enumfacing = p_176400_3_.get(HORIZONTAL_FACING);
      BlockPos blockpos = p_176400_2_.offset(enumfacing.getOpposite());
      p_176400_1_.neighborChanged(blockpos, this, p_176400_2_);
      p_176400_1_.notifyNeighborsOfStateExcept(blockpos, this, enumfacing);
   }

   protected boolean isAlternateInput(IBlockState p_185545_1_) {
      return p_185545_1_.canProvidePower();
   }

   protected int getActiveSignal(IBlockReader p_176408_1_, BlockPos p_176408_2_, IBlockState p_176408_3_) {
      return 15;
   }

   public static boolean isDiode(IBlockState p_185546_0_) {
      return p_185546_0_.getBlock() instanceof BlockRedstoneDiode;
   }

   public boolean isFacingTowardsRepeater(IBlockReader p_176402_1_, BlockPos p_176402_2_, IBlockState p_176402_3_) {
      EnumFacing enumfacing = p_176402_3_.get(HORIZONTAL_FACING).getOpposite();
      IBlockState iblockstate = p_176402_1_.getBlockState(p_176402_2_.offset(enumfacing));
      return isDiode(iblockstate) && iblockstate.get(HORIZONTAL_FACING) != enumfacing;
   }

   protected abstract int getDelay(IBlockState p_196346_1_);

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isSolid(IBlockState p_200124_1_) {
      return true;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }
}
