package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   private static final Map<EnumFacing, BooleanProperty> field_196537_E = BlockFourWay.FACING_TO_PROPERTY_MAP;
   protected static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
   protected static final VoxelShape TRIP_WRITE_ATTACHED_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final BlockTripWireHook field_196538_F;

   public BlockTripWire(BlockTripWireHook p_i48305_1_, Block.Properties p_i48305_2_) {
      super(p_i48305_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false)).with(DISARMED, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
      this.field_196538_F = p_i48305_1_;
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      return p_196244_1_.get(ATTACHED) ? AABB : TRIP_WRITE_ATTACHED_AABB;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader iblockreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      return this.getDefaultState().with(NORTH, Boolean.valueOf(this.func_196536_a(iblockreader.getBlockState(blockpos.north()), EnumFacing.NORTH))).with(EAST, Boolean.valueOf(this.func_196536_a(iblockreader.getBlockState(blockpos.east()), EnumFacing.EAST))).with(SOUTH, Boolean.valueOf(this.func_196536_a(iblockreader.getBlockState(blockpos.south()), EnumFacing.SOUTH))).with(WEST, Boolean.valueOf(this.func_196536_a(iblockreader.getBlockState(blockpos.west()), EnumFacing.WEST)));
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getAxis().isHorizontal() ? p_196271_1_.with(field_196537_E.get(p_196271_2_), Boolean.valueOf(this.func_196536_a(p_196271_3_, p_196271_2_))) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         this.notifyHook(p_196259_2_, p_196259_3_, p_196259_1_);
      }
   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         this.notifyHook(p_196243_2_, p_196243_3_, p_196243_1_.with(POWERED, Boolean.valueOf(true)));
      }
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, IBlockState p_176208_3_, EntityPlayer p_176208_4_) {
      if (!p_176208_1_.isRemote && !p_176208_4_.getHeldItemMainhand().isEmpty() && p_176208_4_.getHeldItemMainhand().getItem() == Items.SHEARS) {
         p_176208_1_.setBlockState(p_176208_2_, p_176208_3_.with(DISARMED, Boolean.valueOf(true)), 4);
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   private void notifyHook(World p_176286_1_, BlockPos p_176286_2_, IBlockState p_176286_3_) {
      for(EnumFacing enumfacing : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST}) {
         for(int i = 1; i < 42; ++i) {
            BlockPos blockpos = p_176286_2_.offset(enumfacing, i);
            IBlockState iblockstate = p_176286_1_.getBlockState(blockpos);
            if (iblockstate.getBlock() == this.field_196538_F) {
               if (iblockstate.get(BlockTripWireHook.FACING) == enumfacing.getOpposite()) {
                  this.field_196538_F.calculateState(p_176286_1_, blockpos, iblockstate, false, true, i, p_176286_3_);
               }
               break;
            }

            if (iblockstate.getBlock() != this) {
               break;
            }
         }
      }

   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote) {
         if (!p_196262_1_.get(POWERED)) {
            this.updateState(p_196262_2_, p_196262_3_);
         }
      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         if (p_196267_2_.getBlockState(p_196267_3_).get(POWERED)) {
            this.updateState(p_196267_2_, p_196267_3_);
         }
      }
   }

   private void updateState(World p_176288_1_, BlockPos p_176288_2_) {
      IBlockState iblockstate = p_176288_1_.getBlockState(p_176288_2_);
      boolean flag = iblockstate.get(POWERED);
      boolean flag1 = false;
      List<? extends Entity> list = p_176288_1_.func_72839_b(null, iblockstate.getShape(p_176288_1_, p_176288_2_).getBoundingBox().offset(p_176288_2_));
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               flag1 = true;
               break;
            }
         }
      }

      if (flag1 != flag) {
         iblockstate = iblockstate.with(POWERED, Boolean.valueOf(flag1));
         p_176288_1_.setBlockState(p_176288_2_, iblockstate, 3);
         this.notifyHook(p_176288_1_, p_176288_2_, iblockstate);
      }

      if (flag1) {
         p_176288_1_.getPendingBlockTicks().scheduleTick(new BlockPos(p_176288_2_), this, this.tickRate(p_176288_1_));
      }

   }

   public boolean func_196536_a(IBlockState p_196536_1_, EnumFacing p_196536_2_) {
      Block block = p_196536_1_.getBlock();
      if (block == this.field_196538_F) {
         return p_196536_1_.get(BlockTripWireHook.FACING) == p_196536_2_.getOpposite();
      } else {
         return block == this;
      }
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return p_185499_1_.with(NORTH, p_185499_1_.get(SOUTH)).with(EAST, p_185499_1_.get(WEST)).with(SOUTH, p_185499_1_.get(NORTH)).with(WEST, p_185499_1_.get(EAST));
      case COUNTERCLOCKWISE_90:
         return p_185499_1_.with(NORTH, p_185499_1_.get(EAST)).with(EAST, p_185499_1_.get(SOUTH)).with(SOUTH, p_185499_1_.get(WEST)).with(WEST, p_185499_1_.get(NORTH));
      case CLOCKWISE_90:
         return p_185499_1_.with(NORTH, p_185499_1_.get(WEST)).with(EAST, p_185499_1_.get(NORTH)).with(SOUTH, p_185499_1_.get(EAST)).with(WEST, p_185499_1_.get(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return p_185471_1_.with(NORTH, p_185471_1_.get(SOUTH)).with(SOUTH, p_185471_1_.get(NORTH));
      case FRONT_BACK:
         return p_185471_1_.with(EAST, p_185471_1_.get(WEST)).with(WEST, p_185471_1_.get(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
