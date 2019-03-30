package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class BlockVine extends Block {
   public static final BooleanProperty UP = BlockSixWay.UP;
   public static final BooleanProperty NORTH = BlockSixWay.NORTH;
   public static final BooleanProperty EAST = BlockSixWay.EAST;
   public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
   public static final BooleanProperty WEST = BlockSixWay.WEST;
   public static final Map<EnumFacing, BooleanProperty> field_196546_A = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199782_0_) -> {
      return p_199782_0_.getKey() != EnumFacing.DOWN;
   }).collect(Util.toMapCollector());
   protected static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

   public BlockVine(Block.Properties p_i48303_1_) {
      super(p_i48303_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      VoxelShape voxelshape = VoxelShapes.func_197880_a();
      if (p_196244_1_.get(UP)) {
         voxelshape = VoxelShapes.func_197872_a(voxelshape, UP_AABB);
      }

      if (p_196244_1_.get(NORTH)) {
         voxelshape = VoxelShapes.func_197872_a(voxelshape, NORTH_AABB);
      }

      if (p_196244_1_.get(EAST)) {
         voxelshape = VoxelShapes.func_197872_a(voxelshape, EAST_AABB);
      }

      if (p_196244_1_.get(SOUTH)) {
         voxelshape = VoxelShapes.func_197872_a(voxelshape, SOUTH_AABB);
      }

      if (p_196244_1_.get(WEST)) {
         voxelshape = VoxelShapes.func_197872_a(voxelshape, WEST_AABB);
      }

      return voxelshape;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean isValidPosition(IBlockState p_196260_1_, IWorldReaderBase p_196260_2_, BlockPos p_196260_3_) {
      return this.func_196543_i(this.func_196545_h(p_196260_1_, p_196260_2_, p_196260_3_));
   }

   private boolean func_196543_i(IBlockState p_196543_1_) {
      return this.func_208496_w(p_196543_1_) > 0;
   }

   private int func_208496_w(IBlockState p_208496_1_) {
      int i = 0;

      for(BooleanProperty booleanproperty : field_196546_A.values()) {
         if (p_208496_1_.get(booleanproperty)) {
            ++i;
         }
      }

      return i;
   }

   private boolean func_196541_a(IBlockReader p_196541_1_, BlockPos p_196541_2_, EnumFacing p_196541_3_) {
      if (p_196541_3_ == EnumFacing.DOWN) {
         return false;
      } else {
         BlockPos blockpos = p_196541_2_.offset(p_196541_3_);
         if (this.func_196542_b(p_196541_1_, blockpos, p_196541_3_)) {
            return true;
         } else if (p_196541_3_.getAxis() == EnumFacing.Axis.Y) {
            return false;
         } else {
            BooleanProperty booleanproperty = field_196546_A.get(p_196541_3_);
            IBlockState iblockstate = p_196541_1_.getBlockState(p_196541_2_.up());
            return iblockstate.getBlock() == this && iblockstate.get(booleanproperty);
         }
      }
   }

   private boolean func_196542_b(IBlockReader p_196542_1_, BlockPos p_196542_2_, EnumFacing p_196542_3_) {
      IBlockState iblockstate = p_196542_1_.getBlockState(p_196542_2_);
      return iblockstate.getBlockFaceShape(p_196542_1_, p_196542_2_, p_196542_3_.getOpposite()) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(iblockstate.getBlock());
   }

   protected static boolean isExceptBlockForAttaching(Block p_193397_0_) {
      return p_193397_0_ instanceof BlockShulkerBox || p_193397_0_ instanceof BlockStainedGlass || p_193397_0_ == Blocks.BEACON || p_193397_0_ == Blocks.CAULDRON || p_193397_0_ == Blocks.GLASS || p_193397_0_ == Blocks.PISTON || p_193397_0_ == Blocks.STICKY_PISTON || p_193397_0_ == Blocks.PISTON_HEAD || p_193397_0_.isIn(BlockTags.WOODEN_TRAPDOORS);
   }

   private IBlockState func_196545_h(IBlockState p_196545_1_, IBlockReader p_196545_2_, BlockPos p_196545_3_) {
      BlockPos blockpos = p_196545_3_.up();
      if (p_196545_1_.get(UP)) {
         p_196545_1_ = p_196545_1_.with(UP, Boolean.valueOf(this.func_196542_b(p_196545_2_, blockpos, EnumFacing.DOWN)));
      }

      IBlockState iblockstate = null;

      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BooleanProperty booleanproperty = getPropertyFor(enumfacing);
         if (p_196545_1_.get(booleanproperty)) {
            boolean flag = this.func_196541_a(p_196545_2_, p_196545_3_, enumfacing);
            if (!flag) {
               if (iblockstate == null) {
                  iblockstate = p_196545_2_.getBlockState(blockpos);
               }

               flag = iblockstate.getBlock() == this && iblockstate.get(booleanproperty);
            }

            p_196545_1_ = p_196545_1_.with(booleanproperty, Boolean.valueOf(flag));
         }
      }

      return p_196545_1_;
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == EnumFacing.DOWN) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         IBlockState iblockstate = this.func_196545_h(p_196271_1_, p_196271_4_, p_196271_5_);
         return !this.func_196543_i(iblockstate) ? Blocks.AIR.getDefaultState() : iblockstate;
      }
   }

   public void tick(IBlockState p_196267_1_, World p_196267_2_, BlockPos p_196267_3_, Random p_196267_4_) {
      if (!p_196267_2_.isRemote) {
         IBlockState iblockstate = this.func_196545_h(p_196267_1_, p_196267_2_, p_196267_3_);
         if (iblockstate != p_196267_1_) {
            if (this.func_196543_i(iblockstate)) {
               p_196267_2_.setBlockState(p_196267_3_, iblockstate, 2);
            } else {
               p_196267_1_.dropBlockAsItem(p_196267_2_, p_196267_3_, 0);
               p_196267_2_.removeBlock(p_196267_3_);
            }

         } else if (p_196267_2_.rand.nextInt(4) == 0) {
            EnumFacing enumfacing = EnumFacing.random(p_196267_4_);
            BlockPos blockpos = p_196267_3_.up();
            if (enumfacing.getAxis().isHorizontal() && !p_196267_1_.get(getPropertyFor(enumfacing))) {
               if (this.func_196539_a(p_196267_2_, p_196267_3_)) {
                  BlockPos blockpos4 = p_196267_3_.offset(enumfacing);
                  IBlockState iblockstate5 = p_196267_2_.getBlockState(blockpos4);
                  if (iblockstate5.isAir()) {
                     EnumFacing enumfacing3 = enumfacing.rotateY();
                     EnumFacing enumfacing4 = enumfacing.rotateYCCW();
                     boolean flag = p_196267_1_.get(getPropertyFor(enumfacing3));
                     boolean flag1 = p_196267_1_.get(getPropertyFor(enumfacing4));
                     BlockPos blockpos2 = blockpos4.offset(enumfacing3);
                     BlockPos blockpos3 = blockpos4.offset(enumfacing4);
                     if (flag && this.func_196542_b(p_196267_2_, blockpos2, enumfacing3)) {
                        p_196267_2_.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(enumfacing3), Boolean.valueOf(true)), 2);
                     } else if (flag1 && this.func_196542_b(p_196267_2_, blockpos3, enumfacing4)) {
                        p_196267_2_.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(enumfacing4), Boolean.valueOf(true)), 2);
                     } else {
                        EnumFacing enumfacing1 = enumfacing.getOpposite();
                        if (flag && p_196267_2_.isAirBlock(blockpos2) && this.func_196542_b(p_196267_2_, p_196267_3_.offset(enumfacing3), enumfacing1)) {
                           p_196267_2_.setBlockState(blockpos2, this.getDefaultState().with(getPropertyFor(enumfacing1), Boolean.valueOf(true)), 2);
                        } else if (flag1 && p_196267_2_.isAirBlock(blockpos3) && this.func_196542_b(p_196267_2_, p_196267_3_.offset(enumfacing4), enumfacing1)) {
                           p_196267_2_.setBlockState(blockpos3, this.getDefaultState().with(getPropertyFor(enumfacing1), Boolean.valueOf(true)), 2);
                        } else if ((double)p_196267_2_.rand.nextFloat() < 0.05D && this.func_196542_b(p_196267_2_, blockpos4.up(), EnumFacing.UP)) {
                           p_196267_2_.setBlockState(blockpos4, this.getDefaultState().with(UP, Boolean.valueOf(true)), 2);
                        }
                     }
                  } else if (this.func_196542_b(p_196267_2_, blockpos4, enumfacing)) {
                     p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(getPropertyFor(enumfacing), Boolean.valueOf(true)), 2);
                  }

               }
            } else {
               if (enumfacing == EnumFacing.UP && p_196267_3_.getY() < 255) {
                  if (this.func_196541_a(p_196267_2_, p_196267_3_, enumfacing)) {
                     p_196267_2_.setBlockState(p_196267_3_, p_196267_1_.with(UP, Boolean.valueOf(true)), 2);
                     return;
                  }

                  if (p_196267_2_.isAirBlock(blockpos)) {
                     if (!this.func_196539_a(p_196267_2_, p_196267_3_)) {
                        return;
                     }

                     IBlockState iblockstate4 = p_196267_1_;

                     for(EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                        if (p_196267_4_.nextBoolean() || !this.func_196542_b(p_196267_2_, blockpos.offset(enumfacing2), EnumFacing.UP)) {
                           iblockstate4 = iblockstate4.with(getPropertyFor(enumfacing2), Boolean.valueOf(false));
                        }
                     }

                     if (this.func_196540_x(iblockstate4)) {
                        p_196267_2_.setBlockState(blockpos, iblockstate4, 2);
                     }

                     return;
                  }
               }

               if (p_196267_3_.getY() > 0) {
                  BlockPos blockpos1 = p_196267_3_.down();
                  IBlockState iblockstate1 = p_196267_2_.getBlockState(blockpos1);
                  if (iblockstate1.isAir() || iblockstate1.getBlock() == this) {
                     IBlockState iblockstate2 = iblockstate1.isAir() ? this.getDefaultState() : iblockstate1;
                     IBlockState iblockstate3 = this.func_196544_a(p_196267_1_, iblockstate2, p_196267_4_);
                     if (iblockstate2 != iblockstate3 && this.func_196540_x(iblockstate3)) {
                        p_196267_2_.setBlockState(blockpos1, iblockstate3, 2);
                     }
                  }
               }

            }
         }
      }
   }

   private IBlockState func_196544_a(IBlockState p_196544_1_, IBlockState p_196544_2_, Random p_196544_3_) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         if (p_196544_3_.nextBoolean()) {
            BooleanProperty booleanproperty = getPropertyFor(enumfacing);
            if (p_196544_1_.get(booleanproperty)) {
               p_196544_2_ = p_196544_2_.with(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return p_196544_2_;
   }

   private boolean func_196540_x(IBlockState p_196540_1_) {
      return p_196540_1_.get(NORTH) || p_196540_1_.get(EAST) || p_196540_1_.get(SOUTH) || p_196540_1_.get(WEST);
   }

   private boolean func_196539_a(IBlockReader p_196539_1_, BlockPos p_196539_2_) {
      int i = 4;
      Iterable<BlockPos.MutableBlockPos> iterable = BlockPos.MutableBlockPos.getAllInBoxMutable(p_196539_2_.getX() - 4, p_196539_2_.getY() - 1, p_196539_2_.getZ() - 4, p_196539_2_.getX() + 4, p_196539_2_.getY() + 1, p_196539_2_.getZ() + 4);
      int j = 5;

      for(BlockPos blockpos : iterable) {
         if (p_196539_1_.getBlockState(blockpos).getBlock() == this) {
            --j;
            if (j <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isReplaceable(IBlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      IBlockState iblockstate = p_196253_2_.getWorld().getBlockState(p_196253_2_.getPos());
      if (iblockstate.getBlock() == this) {
         return this.func_208496_w(iblockstate) < field_196546_A.size();
      } else {
         return super.isReplaceable(p_196253_1_, p_196253_2_);
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockState iblockstate = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      boolean flag = iblockstate.getBlock() == this;
      IBlockState iblockstate1 = flag ? iblockstate : this.getDefaultState();

      for(EnumFacing enumfacing : p_196258_1_.func_196009_e()) {
         if (enumfacing != EnumFacing.DOWN) {
            BooleanProperty booleanproperty = getPropertyFor(enumfacing);
            boolean flag1 = flag && iblockstate.get(booleanproperty);
            if (!flag1 && this.func_196541_a(p_196258_1_.getWorld(), p_196258_1_.getPos(), enumfacing)) {
               return iblockstate1.with(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return flag ? iblockstate1 : null;
   }

    public IItemProvider getItemDropped(IBlockState blockCurrentState, World worldIn, BlockPos blockAt, int fortuneLevel) {
      return Items.AIR;
   }

   public void harvestBlock(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      if (!p_180657_1_.isRemote && p_180657_6_.getItem() == Items.SHEARS) {
         p_180657_2_.func_71029_a(StatList.BLOCK_MINED.func_199076_b(this));
         p_180657_2_.addExhaustion(0.005F);
         spawnAsEntity(p_180657_1_, p_180657_3_, new ItemStack(Blocks.VINE));
      } else {
         super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
      }

   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(UP, NORTH, EAST, SOUTH, WEST);
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

   public static BooleanProperty getPropertyFor(EnumFacing p_176267_0_) {
      return field_196546_A.get(p_176267_0_);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return BlockFaceShape.UNDEFINED;
   }
}
