package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockButton extends BlockHorizontalFace {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape field_196370_b = Block.makeCuboidShape(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
   protected static final VoxelShape field_196371_c = Block.makeCuboidShape(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
   protected static final VoxelShape field_196376_y = Block.makeCuboidShape(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
   protected static final VoxelShape field_196377_z = Block.makeCuboidShape(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
   protected static final VoxelShape AABB_NORTH_OFF = Block.makeCuboidShape(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape AABB_SOUTH_OFF = Block.makeCuboidShape(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
   protected static final VoxelShape AABB_WEST_OFF = Block.makeCuboidShape(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape AABB_EAST_OFF = Block.makeCuboidShape(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
   protected static final VoxelShape field_196372_E = Block.makeCuboidShape(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
   protected static final VoxelShape field_196373_F = Block.makeCuboidShape(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
   protected static final VoxelShape field_196374_G = Block.makeCuboidShape(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
   protected static final VoxelShape field_196375_H = Block.makeCuboidShape(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
   protected static final VoxelShape AABB_NORTH_ON = Block.makeCuboidShape(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape AABB_SOUTH_ON = Block.makeCuboidShape(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
   protected static final VoxelShape AABB_WEST_ON = Block.makeCuboidShape(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape AABB_EAST_ON = Block.makeCuboidShape(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
   private final boolean wooden;

   protected BlockButton(boolean p_i48436_1_, Block.Properties p_i48436_2_) {
      super(p_i48436_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, AttachFace.WALL));
      this.wooden = p_i48436_1_;
   }

   public int tickRate(IWorldReaderBase p_149738_1_) {
      return this.wooden ? 30 : 20;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      EnumFacing enumfacing = p_196244_1_.get(HORIZONTAL_FACING);
      boolean flag = p_196244_1_.get(POWERED);
      switch(p_196244_1_.get(FACE)) {
      case FLOOR:
         if (enumfacing.getAxis() == EnumFacing.Axis.X) {
            return flag ? field_196374_G : field_196376_y;
         }

         return flag ? field_196375_H : field_196377_z;
      case WALL:
         switch(enumfacing) {
         case EAST:
            return flag ? AABB_EAST_ON : AABB_EAST_OFF;
         case WEST:
            return flag ? AABB_WEST_ON : AABB_WEST_OFF;
         case SOUTH:
            return flag ? AABB_SOUTH_ON : AABB_SOUTH_OFF;
         case NORTH:
         default:
            return flag ? AABB_NORTH_ON : AABB_NORTH_OFF;
         }
      case CEILING:
      default:
         if (enumfacing.getAxis() == EnumFacing.Axis.X) {
            return flag ? field_196372_E : field_196370_b;
         } else {
            return flag ? field_196373_F : field_196371_c;
         }
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_1_.get(POWERED)) {
         return true;
      } else {
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_.with(POWERED, Boolean.valueOf(true)), 3);
         this.playSound(p_196250_4_, p_196250_2_, p_196250_3_, true);
         this.updateNeighbors(p_196250_1_, p_196250_2_, p_196250_3_);
         p_196250_2_.getPendingBlockTicks().scheduleTick(p_196250_3_, this, this.tickRate(p_196250_2_));
         return true;
      }
   }

   protected void playSound(@Nullable EntityPlayer p_196367_1_, IWorld p_196367_2_, BlockPos p_196367_3_, boolean p_196367_4_) {
      p_196367_2_.playSound(p_196367_4_ ? p_196367_1_ : null, p_196367_3_, this.getSoundEvent(p_196367_4_), SoundCategory.BLOCKS, 0.3F, p_196367_4_ ? 0.6F : 0.5F);
   }

   protected abstract SoundEvent getSoundEvent(boolean p_196369_1_);

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (p_196243_1_.get(POWERED)) {
            this.updateNeighbors(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getWeakPower(IBlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, EnumFacing p_180656_4_) {
      return p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, EnumFacing p_176211_4_) {
      return p_176211_1_.get(POWERED) && func_196365_i(p_176211_1_) == p_176211_4_ ? 15 : 0;
   }

   public boolean canProvidePower(IBlockState p_149744_1_) {
      return true;
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote && p_196267_1_.get(POWERED)) {
         if (this.wooden) {
            this.checkPressed(p_196267_1_, p_196267_2_, p_196267_3_);
         } else {
            p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(POWERED, Boolean.valueOf(false)), 3);
            this.updateNeighbors(p_196267_1_, p_196267_2_, p_196267_3_);
            this.playSound(null, p_196267_2_, p_196267_3_, false);
         }

      }
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote && this.wooden && !p_196262_1_.get(POWERED)) {
         this.checkPressed(p_196262_1_, p_196262_2_, p_196262_3_);
      }
   }

   private void checkPressed(IBlockState p_185616_1_, World p_185616_2_, BlockPos p_185616_3_) {
      List<? extends Entity> list = p_185616_2_.getEntitiesWithinAABB(EntityArrow.class, p_185616_1_.getShape(p_185616_2_, p_185616_3_).getBoundingBox().offset(p_185616_3_));
      boolean flag = !list.isEmpty();
      boolean flag1 = p_185616_1_.get(POWERED);
      if (flag != flag1) {
         p_185616_2_.setBlockState(p_185616_3_, p_185616_1_.with(POWERED, Boolean.valueOf(flag)), 3);
         this.updateNeighbors(p_185616_1_, p_185616_2_, p_185616_3_);
         this.playSound(null, p_185616_2_, p_185616_3_, flag);
      }

      if (flag) {
         p_185616_2_.getPendingBlockTicks().scheduleTick(new BlockPos(p_185616_3_), this, this.tickRate(p_185616_2_));
      }

   }

   private void updateNeighbors(IBlockState p_196368_1_, World p_196368_2_, BlockPos p_196368_3_) {
      p_196368_2_.notifyNeighborsOfStateChange(p_196368_3_, this);
      p_196368_2_.notifyNeighborsOfStateChange(p_196368_3_.offset(func_196365_i(p_196368_1_).getOpposite()), this);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, POWERED, FACE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
