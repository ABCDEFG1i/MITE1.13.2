package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockDoor extends Block {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

   protected BlockDoor(Block.Properties p_i48413_1_) {
      super(p_i48413_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(OPEN, Boolean.valueOf(false)).with(HINGE, DoorHingeSide.LEFT).with(POWERED, Boolean.valueOf(false)).with(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      EnumFacing enumfacing = p_196244_1_.get(FACING);
      boolean flag = !p_196244_1_.get(OPEN);
      boolean flag1 = p_196244_1_.get(HINGE) == DoorHingeSide.RIGHT;
      switch(enumfacing) {
      case EAST:
      default:
         return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
      case SOUTH:
         return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
      case WEST:
         return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
      case NORTH:
         return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
      }
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      DoubleBlockHalf doubleblockhalf = p_196271_1_.get(HALF);
      if (p_196271_2_.getAxis() == EnumFacing.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (p_196271_2_ == EnumFacing.UP)) {
         return p_196271_3_.getBlock() == this && p_196271_3_.get(HALF) != doubleblockhalf ? p_196271_1_.with(FACING, p_196271_3_.get(FACING)).with(OPEN, p_196271_3_.get(OPEN)).with(HINGE, p_196271_3_.get(HINGE)).with(POWERED, p_196271_3_.get(POWERED)) : Blocks.AIR.getDefaultState();
      } else {
         return doubleblockhalf == DoubleBlockHalf.LOWER && p_196271_2_ == EnumFacing.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.getDefaultState(), p_180657_5_, p_180657_6_);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      DoubleBlockHalf doubleblockhalf = p_176208_3_.get(HALF);
      boolean flag = doubleblockhalf == DoubleBlockHalf.LOWER;
      BlockPos blockpos = flag ? p_176208_2_.up() : p_176208_2_.down();
      IBlockState iblockstate = p_176208_1_.getBlockState(blockpos);
      if (iblockstate.getBlock() == this && iblockstate.get(HALF) != doubleblockhalf) {
         p_176208_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         p_176208_1_.playEvent(p_176208_4_, 2001, blockpos, Block.getStateId(iblockstate));
         if (!p_176208_1_.isRemote && !p_176208_4_.isCreative()) {
            if (flag) {
               p_176208_3_.dropBlockAsItem(p_176208_1_, p_176208_2_, 0);
            } else {
               iblockstate.dropBlockAsItem(p_176208_1_, blockpos, 0);
            }
         }
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return p_196266_1_.get(OPEN);
      case WATER:
         return false;
      case AIR:
         return p_196266_1_.get(OPEN);
      default:
         return false;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   private int getCloseSound() {
      return this.material == Material.IRON ? 1011 : 1012;
   }

   private int getOpenSound() {
      return this.material == Material.IRON ? 1005 : 1006;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockPos blockpos = p_196258_1_.getPos();
      if (blockpos.getY() < 255 && p_196258_1_.getWorld().getBlockState(blockpos.up()).isReplaceable(p_196258_1_)) {
         World world = p_196258_1_.getWorld();
         boolean flag = world.isBlockPowered(blockpos) || world.isBlockPowered(blockpos.up());
         return this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing()).with(HINGE, this.getHingeSide(p_196258_1_)).with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)).with(HALF, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      p_180633_1_.setBlockState(p_180633_2_.up(), p_180633_3_.with(HALF, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide getHingeSide(BlockItemUseContext p_208073_1_) {
      IBlockReader iblockreader = p_208073_1_.getWorld();
      BlockPos blockpos = p_208073_1_.getPos();
      EnumFacing enumfacing = p_208073_1_.getPlacementHorizontalFacing();
      BlockPos blockpos1 = blockpos.up();
      EnumFacing enumfacing1 = enumfacing.rotateYCCW();
      IBlockState iblockstate = iblockreader.getBlockState(blockpos.offset(enumfacing1));
      IBlockState iblockstate1 = iblockreader.getBlockState(blockpos1.offset(enumfacing1));
      EnumFacing enumfacing2 = enumfacing.rotateY();
      IBlockState iblockstate2 = iblockreader.getBlockState(blockpos.offset(enumfacing2));
      IBlockState iblockstate3 = iblockreader.getBlockState(blockpos1.offset(enumfacing2));
      int i = (iblockstate.isBlockNormalCube() ? -1 : 0) + (iblockstate1.isBlockNormalCube() ? -1 : 0) + (iblockstate2.isBlockNormalCube() ? 1 : 0) + (iblockstate3.isBlockNormalCube() ? 1 : 0);
      boolean flag = iblockstate.getBlock() == this && iblockstate.get(HALF) == DoubleBlockHalf.LOWER;
      boolean flag1 = iblockstate2.getBlock() == this && iblockstate2.get(HALF) == DoubleBlockHalf.LOWER;
      if ((!flag || flag1) && i <= 0) {
         if ((!flag1 || flag) && i >= 0) {
            int j = enumfacing.getXOffset();
            int k = enumfacing.getZOffset();
            float f = p_208073_1_.getHitX();
            float f1 = p_208073_1_.getHitZ();
            return (j >= 0 || !(f1 < 0.5F)) && (j <= 0 || !(f1 > 0.5F)) && (k >= 0 || !(f > 0.5F)) && (k <= 0 || !(f < 0.5F)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (this.material == Material.IRON) {
         return false;
      } else {
         p_196250_1_ = p_196250_1_.cycle(OPEN);
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 10);
         p_196250_2_.playEvent(p_196250_4_, p_196250_1_.get(OPEN) ? this.getOpenSound() : this.getCloseSound(), p_196250_3_, 0);
         return true;
      }
   }

   public void toggleDoor(World p_176512_1_, BlockPos p_176512_2_, boolean p_176512_3_) {
      IBlockState iblockstate = p_176512_1_.getBlockState(p_176512_2_);
      if (iblockstate.getBlock() == this && iblockstate.get(OPEN) != p_176512_3_) {
         p_176512_1_.setBlockState(p_176512_2_, iblockstate.with(OPEN, Boolean.valueOf(p_176512_3_)), 10);
         this.playSound(p_176512_1_, p_176512_2_, p_176512_3_);
      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      boolean flag = p_189540_2_.isBlockPowered(p_189540_3_) || p_189540_2_.isBlockPowered(p_189540_3_.offset(p_189540_1_.get(HALF) == DoubleBlockHalf.LOWER ? EnumFacing.UP : EnumFacing.DOWN));
      if (p_189540_4_ != this && flag != p_189540_1_.get(POWERED)) {
         if (flag != p_189540_1_.get(OPEN)) {
            this.playSound(p_189540_2_, p_189540_3_, flag);
         }

         p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)), 2);
      }

   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      IBlockState iblockstate = p_196260_2_.getBlockState(p_196260_3_.down());
      if (p_196260_1_.get(HALF) == DoubleBlockHalf.LOWER) {
         return iblockstate.isTopSolid();
      } else {
         return iblockstate.getBlock() == this;
      }
   }

   private void playSound(World p_196426_1_, BlockPos p_196426_2_, boolean p_196426_3_) {
      p_196426_1_.playEvent(null, p_196426_3_ ? this.getOpenSound() : this.getCloseSound(), p_196426_2_, 0);
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
        return (blockCurrentState.get(HALF) == DoubleBlockHalf.UPPER ? Items.AIR : super.getItemDropped(blockCurrentState, worldIn, blockAt, fortuneLevel));
   }

   public EnumPushReaction getPushReaction(IBlockState p_149656_1_) {
      return EnumPushReaction.DESTROY;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_2_ == Mirror.NONE ? p_185471_1_ : p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING))).cycle(HINGE);
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(IBlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getCoordinateRandom(p_209900_2_.getX(), p_209900_2_.down(p_209900_1_.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_209900_2_.getZ());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HALF, FACING, OPEN, HINGE, POWERED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
