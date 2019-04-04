package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockHorizontal {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   protected static final VoxelShape AABB_HITBOX_ZAXIS = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape AABB_HITBOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   protected static final VoxelShape AABB_HITBOX_ZAXIS_INWALL = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
   protected static final VoxelShape AABB_HITBOX_XAXIS_INWALL = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
   protected static final VoxelShape field_208068_x = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
   protected static final VoxelShape AABB_COLLISION_BOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
   protected static final VoxelShape field_208069_z = VoxelShapes.func_197872_a(Block.makeCuboidShape(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.makeCuboidShape(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
   protected static final VoxelShape AABB_COLLISION_BOX_ZAXIS = VoxelShapes.func_197872_a(Block.makeCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.makeCuboidShape(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
   protected static final VoxelShape field_208066_B = VoxelShapes.func_197872_a(Block.makeCuboidShape(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.makeCuboidShape(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
   protected static final VoxelShape field_208067_C = VoxelShapes.func_197872_a(Block.makeCuboidShape(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.makeCuboidShape(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

   public BlockFenceGate(Block.Properties p_i48398_1_) {
      super(p_i48398_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)).with(IN_WALL, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      if (p_196244_1_.get(IN_WALL)) {
         return p_196244_1_.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
      } else {
         return p_196244_1_.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
      }
   }

   public IBlockState updatePostPlacement(IBlockState p_196271_1_, EnumFacing p_196271_2_, IBlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      EnumFacing.Axis enumfacing$axis = p_196271_2_.getAxis();
      if (p_196271_1_.get(HORIZONTAL_FACING).rotateY().getAxis() != enumfacing$axis) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         boolean flag = this.isWall(p_196271_3_) || this.isWall(p_196271_4_.getBlockState(p_196271_5_.offset(p_196271_2_.getOpposite())));
         return p_196271_1_.with(IN_WALL, Boolean.valueOf(flag));
      }
   }

   public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
      if (p_196268_1_.get(OPEN)) {
         return VoxelShapes.func_197880_a();
      } else {
         return p_196268_1_.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.Z ? field_208068_x : AABB_COLLISION_BOX_XAXIS;
      }
   }

   public VoxelShape getRenderShape(IBlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      if (p_196247_1_.get(IN_WALL)) {
         return p_196247_1_.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? field_208067_C : field_208066_B;
      } else {
         return p_196247_1_.get(HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X ? AABB_COLLISION_BOX_ZAXIS : field_208069_z;
      }
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
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

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      World world = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      boolean flag = world.isBlockPowered(blockpos);
      EnumFacing enumfacing = p_196258_1_.getPlacementHorizontalFacing();
      EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
      boolean flag1 = enumfacing$axis == EnumFacing.Axis.Z && (this.isWall(world.getBlockState(blockpos.west())) || this.isWall(world.getBlockState(blockpos.east()))) || enumfacing$axis == EnumFacing.Axis.X && (this.isWall(world.getBlockState(blockpos.north())) || this.isWall(world.getBlockState(blockpos.south())));
      return this.getDefaultState().with(HORIZONTAL_FACING, enumfacing).with(OPEN, Boolean.valueOf(flag)).with(POWERED, Boolean.valueOf(flag)).with(IN_WALL, Boolean.valueOf(flag1));
   }

   private boolean isWall(IBlockState p_196380_1_) {
      return p_196380_1_.getBlock() == Blocks.COBBLESTONE_WALL || p_196380_1_.getBlock() == Blocks.MOSSY_COBBLESTONE_WALL;
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_1_.get(OPEN)) {
         p_196250_1_ = p_196250_1_.with(OPEN, Boolean.valueOf(false));
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 10);
      } else {
         EnumFacing enumfacing = p_196250_4_.getHorizontalFacing();
         if (p_196250_1_.get(HORIZONTAL_FACING) == enumfacing.getOpposite()) {
            p_196250_1_ = p_196250_1_.with(HORIZONTAL_FACING, enumfacing);
         }

         p_196250_1_ = p_196250_1_.with(OPEN, Boolean.valueOf(true));
         p_196250_2_.setBlockState(p_196250_3_, p_196250_1_, 10);
      }

      p_196250_2_.playEvent(p_196250_4_, p_196250_1_.get(OPEN) ? 1008 : 1014, p_196250_3_, 0);
      return true;
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      if (!p_189540_2_.isRemote) {
         boolean flag = p_189540_2_.isBlockPowered(p_189540_3_);
         if (p_189540_1_.get(POWERED) != flag) {
            p_189540_2_.setBlockState(p_189540_3_, p_189540_1_.with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)), 2);
            if (p_189540_1_.get(OPEN) != flag) {
               p_189540_2_.playEvent(null, flag ? 1008 : 1014, p_189540_3_, 0);
            }
         }

      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, OPEN, POWERED, IN_WALL);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      if (p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN) {
         return p_193383_2_.get(HORIZONTAL_FACING).getAxis() == p_193383_4_.rotateY().getAxis() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
      } else {
         return BlockFaceShape.UNDEFINED;
      }
   }
}
