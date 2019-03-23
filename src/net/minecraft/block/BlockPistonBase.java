package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockPistonBase extends BlockDirectional {
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
   protected static final VoxelShape PISTON_BASE_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_WEST_AABB = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
   protected static final VoxelShape PISTON_BASE_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_UP_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_DOWN_AABB = Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private final boolean isSticky;

   public BlockPistonBase(boolean p_i48281_1_, Block.Properties p_i48281_2_) {
      super(p_i48281_2_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(EXTENDED, Boolean.valueOf(false)));
      this.isSticky = p_i48281_1_;
   }

   public boolean causesSuffocation(IBlockState p_176214_1_) {
      return !p_176214_1_.get(EXTENDED);
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      if (p_196244_1_.get(EXTENDED)) {
         switch((EnumFacing)p_196244_1_.get(FACING)) {
         case DOWN:
            return PISTON_BASE_DOWN_AABB;
         case UP:
         default:
            return PISTON_BASE_UP_AABB;
         case NORTH:
            return PISTON_BASE_NORTH_AABB;
         case SOUTH:
            return PISTON_BASE_SOUTH_AABB;
         case WEST:
            return PISTON_BASE_WEST_AABB;
         case EAST:
            return PISTON_BASE_EAST_AABB;
         }
      } else {
         return VoxelShapes.func_197868_b();
      }
   }

   public boolean isTopSolid(IBlockState p_185481_1_) {
      return !p_185481_1_.get(EXTENDED) || p_185481_1_.get(FACING) == EnumFacing.DOWN;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (!p_180633_1_.isRemote) {
         this.checkForMove(p_180633_1_, p_180633_2_, p_180633_3_);
      }

   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         this.checkForMove(p_189540_2_, p_189540_3_, p_189540_1_);
      }

   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         if (!p_196259_2_.isRemote && p_196259_2_.getTileEntity(p_196259_3_) == null) {
            this.checkForMove(p_196259_2_, p_196259_3_, p_196259_1_);
         }

      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getDefaultState().with(FACING, p_196258_1_.func_196010_d().getOpposite()).with(EXTENDED, Boolean.valueOf(false));
   }

   private void checkForMove(World p_176316_1_, BlockPos p_176316_2_, IBlockState p_176316_3_) {
      EnumFacing enumfacing = p_176316_3_.get(FACING);
      boolean flag = this.shouldBeExtended(p_176316_1_, p_176316_2_, enumfacing);
      if (flag && !p_176316_3_.get(EXTENDED)) {
         if ((new BlockPistonStructureHelper(p_176316_1_, p_176316_2_, enumfacing, true)).canMove()) {
            p_176316_1_.addBlockEvent(p_176316_2_, this, 0, enumfacing.getIndex());
         }
      } else if (!flag && p_176316_3_.get(EXTENDED)) {
         BlockPos blockpos = p_176316_2_.offset(enumfacing, 2);
         IBlockState iblockstate = p_176316_1_.getBlockState(blockpos);
         int i = 1;
         if (iblockstate.getBlock() == Blocks.MOVING_PISTON && iblockstate.get(FACING) == enumfacing) {
            TileEntity tileentity = p_176316_1_.getTileEntity(blockpos);
            if (tileentity instanceof TileEntityPiston) {
               TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;
               if (tileentitypiston.isExtending() && (tileentitypiston.getProgress(0.0F) < 0.5F || p_176316_1_.getTotalWorldTime() == tileentitypiston.func_211146_k() || ((WorldServer)p_176316_1_).isInsideTick())) {
                  i = 2;
               }
            }
         }

         p_176316_1_.addBlockEvent(p_176316_2_, this, i, enumfacing.getIndex());
      }

   }

   private boolean shouldBeExtended(World p_176318_1_, BlockPos p_176318_2_, EnumFacing p_176318_3_) {
      for(EnumFacing enumfacing : EnumFacing.values()) {
         if (enumfacing != p_176318_3_ && p_176318_1_.isSidePowered(p_176318_2_.offset(enumfacing), enumfacing)) {
            return true;
         }
      }

      if (p_176318_1_.isSidePowered(p_176318_2_, EnumFacing.DOWN)) {
         return true;
      } else {
         BlockPos blockpos = p_176318_2_.up();

         for(EnumFacing enumfacing1 : EnumFacing.values()) {
            if (enumfacing1 != EnumFacing.DOWN && p_176318_1_.isSidePowered(blockpos.offset(enumfacing1), enumfacing1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean eventReceived(IBlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      EnumFacing enumfacing = p_189539_1_.get(FACING);
      if (!p_189539_2_.isRemote) {
         boolean flag = this.shouldBeExtended(p_189539_2_, p_189539_3_, enumfacing);
         if (flag && (p_189539_4_ == 1 || p_189539_4_ == 2)) {
            p_189539_2_.setBlockState(p_189539_3_, p_189539_1_.with(EXTENDED, Boolean.valueOf(true)), 2);
            return false;
         }

         if (!flag && p_189539_4_ == 0) {
            return false;
         }
      }

      if (p_189539_4_ == 0) {
         if (!this.doMove(p_189539_2_, p_189539_3_, enumfacing, true)) {
            return false;
         }

         p_189539_2_.setBlockState(p_189539_3_, p_189539_1_.with(EXTENDED, Boolean.valueOf(true)), 67);
         p_189539_2_.playSound((EntityPlayer)null, p_189539_3_, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, p_189539_2_.rand.nextFloat() * 0.25F + 0.6F);
      } else if (p_189539_4_ == 1 || p_189539_4_ == 2) {
         TileEntity tileentity1 = p_189539_2_.getTileEntity(p_189539_3_.offset(enumfacing));
         if (tileentity1 instanceof TileEntityPiston) {
            ((TileEntityPiston)tileentity1).clearPistonTileEntity();
         }

         p_189539_2_.setBlockState(p_189539_3_, Blocks.MOVING_PISTON.getDefaultState().with(BlockPistonMoving.FACING, enumfacing).with(BlockPistonMoving.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
         p_189539_2_.setTileEntity(p_189539_3_, BlockPistonMoving.createTilePiston(this.getDefaultState().with(FACING, EnumFacing.byIndex(p_189539_5_ & 7)), enumfacing, false, true));
         if (this.isSticky) {
            BlockPos blockpos = p_189539_3_.add(enumfacing.getXOffset() * 2, enumfacing.getYOffset() * 2, enumfacing.getZOffset() * 2);
            IBlockState iblockstate = p_189539_2_.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            boolean flag1 = false;
            if (block == Blocks.MOVING_PISTON) {
               TileEntity tileentity = p_189539_2_.getTileEntity(blockpos);
               if (tileentity instanceof TileEntityPiston) {
                  TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;
                  if (tileentitypiston.func_212363_d() == enumfacing && tileentitypiston.isExtending()) {
                     tileentitypiston.clearPistonTileEntity();
                     flag1 = true;
                  }
               }
            }

            if (!flag1) {
               if (p_189539_4_ != 1 || iblockstate.isAir() || !canPush(iblockstate, p_189539_2_, blockpos, enumfacing.getOpposite(), false, enumfacing) || iblockstate.getPushReaction() != EnumPushReaction.NORMAL && block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
                  p_189539_2_.removeBlock(p_189539_3_.offset(enumfacing));
               } else {
                  this.doMove(p_189539_2_, p_189539_3_, enumfacing, false);
               }
            }
         } else {
            p_189539_2_.removeBlock(p_189539_3_.offset(enumfacing));
         }

         p_189539_2_.playSound((EntityPlayer)null, p_189539_3_, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, p_189539_2_.rand.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public static boolean canPush(IBlockState p_185646_0_, World p_185646_1_, BlockPos p_185646_2_, EnumFacing p_185646_3_, boolean p_185646_4_, EnumFacing p_185646_5_) {
      Block block = p_185646_0_.getBlock();
      if (block == Blocks.OBSIDIAN) {
         return false;
      } else if (!p_185646_1_.getWorldBorder().contains(p_185646_2_)) {
         return false;
      } else if (p_185646_2_.getY() >= 0 && (p_185646_3_ != EnumFacing.DOWN || p_185646_2_.getY() != 0)) {
         if (p_185646_2_.getY() <= p_185646_1_.getHeight() - 1 && (p_185646_3_ != EnumFacing.UP || p_185646_2_.getY() != p_185646_1_.getHeight() - 1)) {
            if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
               if (p_185646_0_.getBlockHardness(p_185646_1_, p_185646_2_) == -1.0F) {
                  return false;
               }

               switch(p_185646_0_.getPushReaction()) {
               case BLOCK:
                  return false;
               case DESTROY:
                  return p_185646_4_;
               case PUSH_ONLY:
                  return p_185646_3_ == p_185646_5_;
               }
            } else if (p_185646_0_.get(EXTENDED)) {
               return false;
            }

            return !block.hasTileEntity();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean doMove(World p_176319_1_, BlockPos p_176319_2_, EnumFacing p_176319_3_, boolean p_176319_4_) {
      BlockPos blockpos = p_176319_2_.offset(p_176319_3_);
      if (!p_176319_4_ && p_176319_1_.getBlockState(blockpos).getBlock() == Blocks.PISTON_HEAD) {
         p_176319_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 20);
      }

      BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(p_176319_1_, p_176319_2_, p_176319_3_, p_176319_4_);
      if (!blockpistonstructurehelper.canMove()) {
         return false;
      } else {
         List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
         List<IBlockState> list1 = Lists.newArrayList();

         for(int i = 0; i < list.size(); ++i) {
            BlockPos blockpos1 = list.get(i);
            list1.add(p_176319_1_.getBlockState(blockpos1));
         }

         List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
         int k = list.size() + list2.size();
         IBlockState[] aiblockstate = new IBlockState[k];
         EnumFacing enumfacing = p_176319_4_ ? p_176319_3_ : p_176319_3_.getOpposite();
         Set<BlockPos> set = Sets.newHashSet(list);

         for(int j = list2.size() - 1; j >= 0; --j) {
            BlockPos blockpos2 = list2.get(j);
            IBlockState iblockstate = p_176319_1_.getBlockState(blockpos2);
            iblockstate.dropBlockAsItem(p_176319_1_, blockpos2, 0);
            p_176319_1_.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 18);
            --k;
            aiblockstate[k] = iblockstate;
         }

         for(int l = list.size() - 1; l >= 0; --l) {
            BlockPos blockpos3 = list.get(l);
            IBlockState iblockstate3 = p_176319_1_.getBlockState(blockpos3);
            blockpos3 = blockpos3.offset(enumfacing);
            set.remove(blockpos3);
            p_176319_1_.setBlockState(blockpos3, Blocks.MOVING_PISTON.getDefaultState().with(FACING, p_176319_3_), 68);
            p_176319_1_.setTileEntity(blockpos3, BlockPistonMoving.createTilePiston(list1.get(l), p_176319_3_, p_176319_4_, false));
            --k;
            aiblockstate[k] = iblockstate3;
         }

         if (p_176319_4_) {
            PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            IBlockState iblockstate1 = Blocks.PISTON_HEAD.getDefaultState().with(BlockPistonExtension.FACING, p_176319_3_).with(BlockPistonExtension.TYPE, pistontype);
            IBlockState iblockstate4 = Blocks.MOVING_PISTON.getDefaultState().with(BlockPistonMoving.FACING, p_176319_3_).with(BlockPistonMoving.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            set.remove(blockpos);
            p_176319_1_.setBlockState(blockpos, iblockstate4, 68);
            p_176319_1_.setTileEntity(blockpos, BlockPistonMoving.createTilePiston(iblockstate1, p_176319_3_, true, true));
         }

         for(BlockPos blockpos4 : set) {
            p_176319_1_.setBlockState(blockpos4, Blocks.AIR.getDefaultState(), 66);
         }

         for(int i1 = list2.size() - 1; i1 >= 0; --i1) {
            IBlockState iblockstate2 = aiblockstate[k++];
            BlockPos blockpos5 = list2.get(i1);
            iblockstate2.updateDiagonalNeighbors(p_176319_1_, blockpos5, 2);
            p_176319_1_.notifyNeighborsOfStateChange(blockpos5, iblockstate2.getBlock());
         }

         for(int j1 = list.size() - 1; j1 >= 0; --j1) {
            p_176319_1_.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock());
         }

         if (p_176319_4_) {
            p_176319_1_.notifyNeighborsOfStateChange(blockpos, Blocks.PISTON_HEAD);
         }

         return true;
      }
   }

   public IBlockState rotate(IBlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.with(FACING, p_185499_2_.rotate(p_185499_1_.get(FACING)));
   }

   public IBlockState mirror(IBlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(FACING, EXTENDED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_2_.get(FACING) != p_193383_4_.getOpposite() && p_193383_2_.get(EXTENDED) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }

   public int getOpacity(IBlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      return 0;
   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
