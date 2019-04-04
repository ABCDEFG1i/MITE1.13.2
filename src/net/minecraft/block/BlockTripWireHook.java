package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final VoxelShape HOOK_NORTH_AABB = Block.makeCuboidShape(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape HOOK_SOUTH_AABB = Block.makeCuboidShape(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
   protected static final VoxelShape HOOK_WEST_AABB = Block.makeCuboidShape(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape HOOK_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);

   public BlockTripWireHook(Block.Properties p_i48304_1_) {
      super(p_i48304_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch(p_196244_1_.get(FACING)) {
      case EAST:
      default:
         return HOOK_EAST_AABB;
      case WEST:
         return HOOK_WEST_AABB;
      case SOUTH:
         return HOOK_SOUTH_AABB;
      case NORTH:
         return HOOK_NORTH_AABB;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      EnumFacing enumfacing = p_196260_1_.get(FACING);
      BlockPos blockpos = p_196260_3_.offset(enumfacing.getOpposite());
      IBlockState iblockstate = p_196260_2_.getBlockState(blockpos);
      boolean flag = isExceptBlockForAttachWithPiston(iblockstate.getBlock());
      return !flag && enumfacing.getAxis().isHorizontal() && iblockstate.getBlockFaceShape(p_196260_2_, blockpos, enumfacing) == BlockFaceShape.SOLID && !iblockstate.canProvidePower();
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = this.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false));
      IWorldReaderBase iworldreaderbase = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      EnumFacing[] aenumfacing = p_196258_1_.func_196009_e();

      for(EnumFacing enumfacing : aenumfacing) {
         if (enumfacing.getAxis().isHorizontal()) {
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            iblockstate = iblockstate.with(FACING, enumfacing1);
            if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate;
            }
         }
      }

      return null;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      this.calculateState(p_180633_1_, p_180633_2_, p_180633_3_, false, false, -1, null);
   }

   public void calculateState(World p_176260_1_, BlockPos p_176260_2_, IBlockState p_176260_3_, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, @Nullable IBlockState p_176260_7_) {
      EnumFacing enumfacing = p_176260_3_.get(FACING);
      boolean flag = p_176260_3_.get(ATTACHED);
      boolean flag1 = p_176260_3_.get(POWERED);
      boolean flag2 = !p_176260_4_;
      boolean flag3 = false;
      int i = 0;
      IBlockState[] aiblockstate = new IBlockState[42];

      for(int j = 1; j < 42; ++j) {
         BlockPos blockpos = p_176260_2_.offset(enumfacing, j);
         IBlockState iblockstate = p_176260_1_.getBlockState(blockpos);
         if (iblockstate.getBlock() == Blocks.TRIPWIRE_HOOK) {
            if (iblockstate.get(FACING) == enumfacing.getOpposite()) {
               i = j;
            }
            break;
         }

         if (iblockstate.getBlock() != Blocks.TRIPWIRE && j != p_176260_6_) {
            aiblockstate[j] = null;
            flag2 = false;
         } else {
            if (j == p_176260_6_) {
               iblockstate = MoreObjects.firstNonNull(p_176260_7_, iblockstate);
            }

            boolean flag4 = !iblockstate.get(BlockTripWire.DISARMED);
            boolean flag5 = iblockstate.get(BlockTripWire.POWERED);
            flag3 |= flag4 && flag5;
            aiblockstate[j] = iblockstate;
            if (j == p_176260_6_) {
               p_176260_1_.getPendingBlockTicks().scheduleTick(p_176260_2_, this, this.tickRate(p_176260_1_));
               flag2 &= flag4;
            }
         }
      }

      flag2 = flag2 & i > 1;
      flag3 = flag3 & flag2;
      IBlockState iblockstate1 = this.getDefaultState().with(ATTACHED, Boolean.valueOf(flag2)).with(POWERED, Boolean.valueOf(flag3));
      if (i > 0) {
         BlockPos blockpos1 = p_176260_2_.offset(enumfacing, i);
         EnumFacing enumfacing1 = enumfacing.getOpposite();
         p_176260_1_.setBlockState(blockpos1, iblockstate1.with(FACING, enumfacing1), 3);
         this.notifyNeighbors(p_176260_1_, blockpos1, enumfacing1);
         this.playSound(p_176260_1_, blockpos1, flag2, flag3, flag, flag1);
      }

      this.playSound(p_176260_1_, p_176260_2_, flag2, flag3, flag, flag1);
      if (!p_176260_4_) {
         p_176260_1_.setBlockState(p_176260_2_, iblockstate1.with(FACING, enumfacing), 3);
         if (p_176260_5_) {
            this.notifyNeighbors(p_176260_1_, p_176260_2_, enumfacing);
         }
      }

      if (flag != flag2) {
         for(int k = 1; k < i; ++k) {
            BlockPos blockpos2 = p_176260_2_.offset(enumfacing, k);
            IBlockState iblockstate2 = aiblockstate[k];
            if (iblockstate2 != null) {
               p_176260_1_.setBlockState(blockpos2, iblockstate2.with(ATTACHED, Boolean.valueOf(flag2)), 3);
               if (!p_176260_1_.getBlockState(blockpos2).isAir()) {
               }
            }
         }
      }

   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      this.calculateState(p_196267_2_, p_196267_3_, p_196267_1_, false, true, -1, null);
   }

   private void playSound(World p_180694_1_, BlockPos p_180694_2_, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_) {
      if (p_180694_4_ && !p_180694_6_) {
         p_180694_1_.playSound(null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
      } else if (!p_180694_4_ && p_180694_6_) {
         p_180694_1_.playSound(null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
      } else if (p_180694_3_ && !p_180694_5_) {
         p_180694_1_.playSound(null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
      } else if (!p_180694_3_ && p_180694_5_) {
         p_180694_1_.playSound(null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (p_180694_1_.rand.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighbors(World p_176262_1_, BlockPos p_176262_2_, EnumFacing p_176262_3_) {
      p_176262_1_.notifyNeighborsOfStateChange(p_176262_2_, this);
      p_176262_1_.notifyNeighborsOfStateChange(p_176262_2_.offset(p_176262_3_.getOpposite()), this);
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         boolean flag = p_196243_1_.get(ATTACHED);
         boolean flag1 = p_196243_1_.get(POWERED);
         if (flag || flag1) {
            this.calculateState(p_196243_2_, p_196243_3_, p_196243_1_, true, false, -1, null);
         }

         if (flag1) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_, this);
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.offset(p_196243_1_.get(FACING).getOpposite()), this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      if (!p_176211_1_.get(POWERED)) {
         return 0;
      } else {
         return p_176211_1_.get(FACING) == p_176211_4_ ? 15 : 0;
      }
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED, ATTACHED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
