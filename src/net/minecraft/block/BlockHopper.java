package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   public static final DirectionProperty FACING = BlockStateProperties.FACING_EXCEPT_UP;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   private static final VoxelShape INPUT_SHAPE = Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape MIDDLE_SHAPE = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
   private static final VoxelShape INPUT_MIDDLE_SHAPE = VoxelShapes.func_197872_a(MIDDLE_SHAPE, INPUT_SHAPE);
   private static final VoxelShape field_196326_A = VoxelShapes.func_197878_a(INPUT_MIDDLE_SHAPE, IHopper.INSIDE_BOWL_SHAPE, IBooleanFunction.ONLY_FIRST);
   private static final VoxelShape DOWN_SHAPE = VoxelShapes.func_197872_a(field_196326_A, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
   private static final VoxelShape EAST_SHAPE = VoxelShapes.func_197872_a(field_196326_A, Block.makeCuboidShape(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
   private static final VoxelShape NORTH_SHAPE = VoxelShapes.func_197872_a(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
   private static final VoxelShape SOUTH_SHAPE = VoxelShapes.func_197872_a(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
   private static final VoxelShape WEST_SHAPE = VoxelShapes.func_197872_a(field_196326_A, Block.makeCuboidShape(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
   private static final VoxelShape DOWN_RAYTRACE_SHAPE = IHopper.INSIDE_BOWL_SHAPE;
   private static final VoxelShape EAST_RAYTRACE_SHAPE = VoxelShapes.func_197872_a(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
   private static final VoxelShape NORTH_RAYTRACE_SHAPE = VoxelShapes.func_197872_a(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
   private static final VoxelShape SOUTH_RAYTRACE_SHAPE = VoxelShapes.func_197872_a(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
   private static final VoxelShape WEST_RAYTRACE_SHAPE = VoxelShapes.func_197872_a(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

   public BlockHopper(Block.Properties p_i48378_1_) {
      super(p_i48378_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.DOWN).with(ENABLED, Boolean.valueOf(true)));
   }

   public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
      switch(p_196244_1_.get(FACING)) {
      case DOWN:
         return DOWN_SHAPE;
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      case EAST:
         return EAST_SHAPE;
      default:
         return field_196326_A;
      }
   }

   public VoxelShape getRaytraceShape(IBlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      switch(p_199600_1_.get(FACING)) {
      case DOWN:
         return DOWN_RAYTRACE_SHAPE;
      case NORTH:
         return NORTH_RAYTRACE_SHAPE;
      case SOUTH:
         return SOUTH_RAYTRACE_SHAPE;
      case WEST:
         return WEST_RAYTRACE_SHAPE;
      case EAST:
         return EAST_RAYTRACE_SHAPE;
      default:
         return IHopper.INSIDE_BOWL_SHAPE;
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      EnumFacing enumfacing = p_196258_1_.getFace().getOpposite();
      return this.getDefaultState().with(FACING, enumfacing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.DOWN : enumfacing).with(ENABLED, Boolean.valueOf(true));
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new TileEntityHopper();
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity tileentity = p_180633_1_.getTileEntity(p_180633_2_);
         if (tileentity instanceof TileEntityHopper) {
            ((TileEntityHopper)tileentity).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public boolean isTopSolid(IBlockState p_185481_1_) {
      return true;
   }

   public void onBlockAdded(IBlockState p_196259_1_, World p_196259_2_, BlockPos p_196259_3_, IBlockState p_196259_4_) {
      if (p_196259_4_.getBlock() != p_196259_1_.getBlock()) {
         this.updateState(p_196259_2_, p_196259_3_, p_196259_1_);
      }
   }

   public boolean onBlockActivated(IBlockState p_196250_1_, World p_196250_2_, BlockPos p_196250_3_, EntityPlayer p_196250_4_, EnumHand p_196250_5_, EnumFacing p_196250_6_, float p_196250_7_, float p_196250_8_, float p_196250_9_) {
      if (p_196250_2_.isRemote) {
         return true;
      } else {
         TileEntity tileentity = p_196250_2_.getTileEntity(p_196250_3_);
         if (tileentity instanceof TileEntityHopper) {
            p_196250_4_.displayGUIChest((TileEntityHopper)tileentity);
            p_196250_4_.addStat(StatList.INSPECT_HOPPER);
         }

         return true;
      }
   }

   public void neighborChanged(IBlockState p_189540_1_, World p_189540_2_, BlockPos p_189540_3_, Block p_189540_4_, BlockPos p_189540_5_) {
      this.updateState(p_189540_2_, p_189540_3_, p_189540_1_);
   }

   private void updateState(World p_176427_1_, BlockPos p_176427_2_, IBlockState p_176427_3_) {
      boolean flag = !p_176427_1_.isBlockPowered(p_176427_2_);
      if (flag != p_176427_3_.get(ENABLED)) {
         p_176427_1_.setBlockState(p_176427_2_, p_176427_3_.with(ENABLED, Boolean.valueOf(flag)), 4);
      }

   }

   public void onReplaced(IBlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, IBlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof TileEntityHopper) {
            InventoryHelper.dropInventoryItems(p_196243_2_, p_196243_3_, (TileEntityHopper)tileentity);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean isFullCube(IBlockState p_149686_1_) {
      return false;
   }

   public boolean hasComparatorInputOverride(IBlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
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
      p_206840_1_.add(FACING, ENABLED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
      return p_193383_4_ == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
   }

   public void onEntityCollision(IBlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      TileEntity tileentity = p_196262_2_.getTileEntity(p_196262_3_);
      if (tileentity instanceof TileEntityHopper) {
         ((TileEntityHopper)tileentity).onEntityCollision(p_196262_4_);
      }

   }

   public boolean allowsMovement(IBlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
